package renderer;

import java.util.ArrayList;
import java.util.List;

import maths.Vector3f;
import maths.Vector4f;

public class Renderer {
	
	private ShaderProgram shaderProgram;
	private Buffer buffer;
	
	private float nearPlane, farPlane;
	
	private int width, height;
	
	public boolean perspectiveCorrection;
	
	public boolean backfaceCulling;
	public boolean frontfaceCulling;
	
	public Renderer(Buffer buffer, ShaderProgram shaderProgram, float nearPlane, float farPlane) {
		this.buffer = buffer;
		this.shaderProgram = shaderProgram;
		width = buffer.getWidth();
		height = buffer.getHeight();
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		perspectiveCorrection = false;
		backfaceCulling = true;
	}
	
	public void renderModel(Model model) {
		int faces = model.vertices.length / 3;
		for (int i = 0; i < faces; i++) {
			renderTriangle(model.vertices[i * 3], model.vertices[i * 3 + 1], model.vertices[i * 3 + 2]);
		}
	}
	
	public void renderTriangle(Vertex v0, Vertex v1, Vertex v2) {
		List<Vertex> vertices = new ArrayList<>();

		Vertex tmp0 = shaderProgram.processVertex(v0);
		Vertex tmp1 = shaderProgram.processVertex(v1);
		Vertex tmp2 = shaderProgram.processVertex(v2);

		Vector3f t0 = tmp0.pos.getVector3f();
		Vector3f t1 = tmp1.pos.getVector3f();
		Vector3f t2 = tmp2.pos.getVector3f();
		
		float dot = 0;
		dot = t0.dot(t1.sub(t0).crossProduct(t2.sub(t0)));

//		boolean cullBack = (dot < 0) && frontfaceCulling;
		boolean isBack = (dot > 0);
		boolean cull = (isBack && backfaceCulling) || (!isBack && frontfaceCulling);
		
		if (!cull) {
			vertices.add(tmp0);
			vertices.add(tmp1);
			vertices.add(tmp2);
			
			vertices = clipNearPlane(vertices);
			vertices = clipFarPlane(vertices);
			perspectiveDivide(vertices);
			vertices = clipLeft(vertices);
			vertices = clipBottom(vertices);
			vertices = clipRight(vertices);
			vertices = clipTop(vertices);
	
			toScreenSpace(vertices);
			
			fillTriangleFan(vertices);
		}
	}
	
	private void fillTriangleFan(List<Vertex> vertices) {
		if (vertices.size() > 2) {
			for (int i = 1; i < vertices.size() - 1; i++) {
				fillTriangle(vertices.get(0), vertices.get(i), vertices.get(i + 1));
			}
		}
	}
	
	public float ccw(Vector4f a, Vector4f b, Vector4f c) {
		return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
	}
	
	private void fillTriangle(Vertex v0, Vertex v1, Vertex v2) {
		Vertex vt0 = v0.clone();
		Vertex vt1 = v1.clone();
		Vertex vt2 = v2.clone();
		
		if (vt0.pos.y > vt1.pos.y) vt0.swap(vt1);
		if (vt1.pos.y > vt2.pos.y) vt1.swap(vt2);
		if (vt0.pos.y > vt1.pos.y) vt0.swap(vt1);
		
		float value = (vt1.pos.y - vt0.pos.y) / (vt2.pos.y - vt0.pos.y);
		Vertex v = vt0.lerp(vt2, value);
		
		if (vt1.pos.x > v.pos.x) vt1.swap(v);
		
		fillBottomFlatTriangle(vt0, vt1, v);
		fillTopFlatTriangle(vt1, v, vt2);
	}
	
	private void fillBottomFlatTriangle(Vertex v0, Vertex v1, Vertex v2) {
		int startY = (int) Math.ceil(v0.pos.y - 0.5f);
		int endY = (int) Math.ceil(v2.pos.y - 0.5f);

		for (int y = startY; y < endY; y++) {
			float value = (float) (y + 0.5f - v0.pos.y) / (v2.pos.y - v0.pos.y);
			Vertex left = v0.lerp(v1, value);
			Vertex right = v0.lerp(v2, value);
			
			fillHorizontalLine(left, right);
		}
	}
	
	private void fillTopFlatTriangle(Vertex v0, Vertex v1, Vertex v2) {
		int startY = (int) Math.ceil(v0.pos.y - 0.5f);
		int endY = (int) Math.ceil(v2.pos.y - 0.5f);
		
		for (int y = startY; y < endY; y++) {
			float value = (float) (y + 0.5f - v0.pos.y) / (v2.pos.y - v0.pos.y);
			Vertex left = v0.lerp(v2, value);
			Vertex right = v1.lerp(v2, value);
			
			fillHorizontalLine(left, right);
		}
	}
	
	private void fillHorizontalLine(Vertex v0, Vertex v1) {
		int startX = (int) Math.ceil(v0.pos.x - 0.5f);
		int endX = (int) Math.ceil(v1.pos.x - 0.5f);

		for (int x = startX; x < endX; x++) {
			float value = (float) (x + 0.5f - v0.pos.x) / (v1.pos.x - v0.pos.x);
			Vertex v = v0.lerp(v1, value);
			fillPixel(v);
		}
	}
	
	private void fillPixel(Vertex v) {
		int x = (int) v.pos.x;
		int y = (int) v.pos.y;
		v.perspectiveCorrect(perspectiveCorrection);
		if (v.pos.w > buffer.getDepth(x, y)) {
			buffer.setDepth(x, y, v.pos.w);
			v = shaderProgram.processFragment(v);
			
			int color = buffer.getColor(x, y);
			float r = (float) ((color >> 16) & 0xFF) / 0xFF;
			float g = (float) ((color >> 8) & 0xFF) / 0xFF;
			float b = (float) (color & 0xFF) / 0xFF;
			
			
			buffer.setColor(x, y, new Vector4f(r, g, b, 1).lerp(v.col, v.col.w));
		}
	}
	
	public void toScreenSpace(List<Vertex> vertices) {
		for (Vertex v:vertices) {
			v.toScreenSpace(width / 2f, height / 2f);
		}
	}
	
	public void perspectiveDivide(List<Vertex> vertices) {
		for (Vertex v:vertices) {
			v.perspectiveDivide(perspectiveCorrection);
		}
	}

	private List<Vertex> clipLeft(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();
		
		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.x < -1) {
				if (nextV.pos.x >= -1) {
					float value = (-1f - v.pos.x) / (nextV.pos.x - v.pos.x);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.x >= -1) {
					tmp.add(nextV);
				} else {
					float value = (-1 - v.pos.x) / (nextV.pos.x - v.pos.x);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}
	
	private List<Vertex> clipRight(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();

		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.x > 1) {
				if (nextV.pos.x <= 1) {
					float value = (1f - v.pos.x) / (nextV.pos.x - v.pos.x);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.x <= 1) {
					tmp.add(nextV);
				} else {
					float value = (1 - v.pos.x) / (nextV.pos.x - v.pos.x);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}
	
	private List<Vertex> clipBottom(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();
		
		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.y < -1) {
				if (nextV.pos.y >= -1) {
					float value = (-1f - v.pos.y) / (nextV.pos.y - v.pos.y);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.y >= -1) {
					tmp.add(nextV);
				} else {
					float value = (-1 - v.pos.y) / (nextV.pos.y - v.pos.y);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}
	
	private List<Vertex> clipTop(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();
		
		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.y > 1) {
				if (nextV.pos.y <= 1) {
					float value = (1f - v.pos.y) / (nextV.pos.y - v.pos.y);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.y <= 1) {
					tmp.add(nextV);
				} else {
					float value = (1 - v.pos.y) / (nextV.pos.y - v.pos.y);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}
	
	private List<Vertex> clipNearPlane(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();

		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.z < nearPlane) {
				if (nextV.pos.z >= nearPlane) {
					float value = (nearPlane - v.pos.z) / (nextV.pos.z - v.pos.z);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.z >= nearPlane) {
					tmp.add(nextV);
				} else {
					float value = (nearPlane - v.pos.z) / (nextV.pos.z - v.pos.z);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}

	private List<Vertex> clipFarPlane(List<Vertex> vertices) {
		List<Vertex> tmp = new ArrayList<>();

		for (int i = 0; i < vertices.size(); i++) {
			int nextI = (i + 1) % vertices.size();
			
			Vertex v = vertices.get(i);
			Vertex nextV = vertices.get(nextI);
			
			if (v.pos.z > farPlane) {
				if (nextV.pos.z <= farPlane) {
					float value = (farPlane - v.pos.z) / (nextV.pos.z - v.pos.z);
					tmp.add(v.lerp(nextV, value));
					tmp.add(nextV);
				}
			} else {
				if (nextV.pos.z <= farPlane) {
					tmp.add(nextV);
				} else {
					float value = (farPlane - v.pos.z) / (nextV.pos.z - v.pos.z);
					tmp.add(v.lerp(nextV, value));
				}
			}
		}
		
		return tmp;
	}
	
	public void bindShaderProgram(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

}
