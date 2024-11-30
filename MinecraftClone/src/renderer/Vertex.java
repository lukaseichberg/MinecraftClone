package renderer;

import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;

public class Vertex {
	
	public Vector4f pos, col;

	public Vector2f[] vec2;
	public Vector3f[] vec3;
	public Vector4f[] vec4;
	
	public Vertex(int vec2s, int vec3s, int vec4s) {
		pos = new Vector4f();
		col = new Vector4f();
		vec2 = new Vector2f[vec2s];
		vec3 = new Vector3f[vec3s];
		vec4 = new Vector4f[vec4s];
	}
	
	public Vertex lerp(Vertex v, float value) {
		Vertex tmp = new Vertex(vec2.length, vec3.length, vec4.length);
		tmp.pos = pos.lerp(v.pos, value);
		tmp.col = col.lerp(v.col, value);
		for (int i = 0; i < vec4.length; i++) {
			tmp.vec4[i] = vec4[i].lerp(v.vec4[i], value);
		}
		for (int i = 0; i < vec3.length; i++) {
			tmp.vec3[i] = vec3[i].lerp(v.vec3[i], value);
		}
		for (int i = 0; i < vec2.length; i++) {
			tmp.vec2[i] = vec2[i].lerp(v.vec2[i], value);
		}
		return tmp;
	}
	
	public void perspectiveDivide(boolean perspectiveCorrection) {
		float z = 1 / pos.z;
		pos._mul(z);
		if (perspectiveCorrection) {
			col._mul(z);
			for (int i = 0; i < vec4.length; i++) {
				vec4[i]._mul(z);
			}
			for (int i = 0; i < vec3.length; i++) {
				vec3[i]._mul(z);
			}
			for (int i = 0; i < vec2.length; i++) {
				vec2[i]._mul(z);
			}
		}
		pos.z = z;
	}
	
	public void perspectiveCorrect(boolean perspectiveCorrection) {
		float z = 1 / pos.z;
		pos._mul(z);
		if (perspectiveCorrection) {
			col._mul(z);
			for (int i = 0; i < vec4.length; i++) {
				vec4[i]._mul(z);
			}
			for (int i = 0; i < vec3.length; i++) {
				vec3[i]._mul(z);
			}
			for (int i = 0; i < vec2.length; i++) {
				vec2[i]._mul(z);
			}
		}
		pos.w = 1 / (pos.w * z);
	}
	
	public void toScreenSpace(float halfWidth, float halfHeight) {
		pos.x = pos.x * halfWidth + halfWidth;
		pos.y = -pos.y * halfHeight + halfHeight;
	}
	
	public void swap(Vertex v) {
		pos.swap(v.pos);
		col.swap(v.col);
		for (int i = 0; i < vec4.length; i++) {
			vec4[i].swap(v.vec4[i]);
		}
		for (int i = 0; i < vec3.length; i++) {
			vec3[i].swap(v.vec3[i]);
		}
		for (int i = 0; i < vec2.length; i++) {
			vec2[i].swap(v.vec2[i]);
		}
	}
	
	public Vertex clone() {
		Vertex tmp = new Vertex(vec2.length, vec3.length, vec4.length);
		tmp.pos = pos.clone();
		tmp.col = col.clone();
		for (int i = 0; i < vec4.length; i++) {
			tmp.vec4[i] = vec4[i].clone();
		}
		for (int i = 0; i < vec3.length; i++) {
			tmp.vec3[i] = vec3[i].clone();
		}
		for (int i = 0; i < vec2.length; i++) {
			tmp.vec2[i] = vec2[i].clone();
		}
		return tmp;
	}

}
