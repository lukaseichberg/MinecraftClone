package maths;

public class Vector4f {
	
	public float x, y, z, w;
	
	public Vector4f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}
	
	public Vector4f(Vector3f v, float w) {
		this(v.x, v.y, v.z, w);
	}
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(float value) {
		x = value;
		y = value;
		z = value;
		w = value;
	}

	public Vector4f lerp(Vector4f v, float value) {
		return new Vector4f(
			Maths.lerp(x, v.x, value),
			Maths.lerp(y, v.y, value),
			Maths.lerp(z, v.z, value),
			Maths.lerp(w, v.w, value)
		);
	}
	
	public void swap(Vector4f v) {
		Vector4f tmpV = new Vector4f(x, y, z, w);
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
		v.x = tmpV.x;
		v.y = tmpV.y;
		v.z = tmpV.z;
		v.w = tmpV.w;
	}

	public Vector4f add(Vector4f v) {
		return new Vector4f(
			x + v.x,
			y + v.y,
			z + v.z,
			w + v.w
		);
	}
	
	public void _add(Vector4f v) {
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
	}
	
	public Vector4f sub(Vector4f v) {
		return new Vector4f(
			x - v.x,
			y - v.y,
			z - v.z,
			w - v.w
		);
	}

	public Vector4f colorClip() {
		return new Vector4f(
			Math.min(Math.max(x, 0), 1),
			Math.min(Math.max(y, 0), 1),
			Math.min(Math.max(z, 0), 1),
			Math.min(Math.max(w, 0), 1)
		);
	}
	
	public Vector4f mul(Matrix4f mat) {
		return new Vector4f(
			x * mat.m00 + y * mat.m01 + z * mat.m02 + w * mat.m03,
			x * mat.m10 + y * mat.m11 + z * mat.m12 + w * mat.m13,
			x * mat.m20 + y * mat.m21 + z * mat.m22 + w * mat.m23,
			x * mat.m30 + y * mat.m31 + z * mat.m32 + w * mat.m33
		);
	}

	public Vector4f mul(float scalar) {
		return new Vector4f(
			x * scalar,
			y * scalar,
			z * scalar,
			w * scalar
		);
	}

	public void _mul(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		w *= scalar;
	}
	
	public Vector4f div(float scalar) {
		return new Vector4f(
			x / scalar,
			y / scalar,
			z / scalar,
			w / scalar
		);
	}
	
	public void _div(float scalar) {
		x /= scalar;
		y /= scalar;
		z /= scalar;
		w /= scalar;
	}
	
	public Vector4f clone() {
		return new Vector4f(x, y, z, w);
	}
	
	public Vector3f getVector3f() {
		return new Vector3f(x, y, z);
	}
	
}
