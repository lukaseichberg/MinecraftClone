package game;

import maths.Matrix4f;
import maths.Vector3f;
import maths.Vector4f;

public class Camera {
	
	public Vector3f pos, rot;
	private Matrix4f projection;
	
	public Camera(Vector3f pos, Vector3f rot) {
		this.pos = pos;
		this.rot = rot;
	}
	
	public Vector3f getViewDir() {
		Vector4f dir = new Vector4f(0, 0, 1, 0);
		return dir.mul(Matrix4f.rotateZ(rot.z).mul(Matrix4f.rotateY(rot.y).mul(Matrix4f.rotateX(rot.x)))).getVector3f();
	}
	
	public Matrix4f getViewMatrix() {
		Matrix4f viewMatrix = Matrix4f.rotateZ(-rot.z).mul(Matrix4f.rotateX(-rot.x)).mul(Matrix4f.rotateY(-rot.y)).mul(Matrix4f.translate(-pos.x, -pos.y, -pos.z));
		return viewMatrix;
	}

	public void setProjectionMatrix(float fov, float aspect, float near, float far) {
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2))));
		float x_scale = y_scale / aspect;
		
		Matrix4f projectionMatrix = Matrix4f.identity();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		
		projection = projectionMatrix;
	}
	
	public void setOrthographicMatrix(float left, float right, float top, float bottom, float near, float far) {
		float y_scale = (float) 1 / (right - left);
		float x_scale = (float) 1 / (top - bottom);
		
		Matrix4f matrix = Matrix4f.identity();
		matrix.m00 = x_scale;
		matrix.m11 = y_scale;
		matrix.m03 = 0;
		matrix.m13 = 0;
		matrix.m22 = 0;
		matrix.m32 = 1;
		matrix.m23 = 1;
		matrix.m33 = (far - near) / 2;
		
		projection = matrix;
	}
	
	public Matrix4f getProjection() {
		return projection;
	}

}
