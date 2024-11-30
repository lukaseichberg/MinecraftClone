package maths;

public class Vector3f {
	
	public float x, y, z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Vector3f lerp(Vector3f v, float value) {
		return new Vector3f(
			Maths.lerp(x, v.x, value),
			Maths.lerp(y, v.y, value),
			Maths.lerp(z, v.z, value)
		);
	}
	
	public void swap(Vector3f v) {
		Vector3f tmpV = new Vector3f(x, y, z);
		x = v.x;
		y = v.y;
		z = v.z;
		v.x = tmpV.x;
		v.y = tmpV.y;
		v.z = tmpV.z;
	}

	public Vector3f add(Vector3f v) {
		return new Vector3f(
			x + v.x,
			y + v.y,
			z + v.z
		);
	}
	
	public void _add(Vector3f v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public Vector3f reflect(Vector3f normal) {
		return sub(normal.mul(2 * dot(normal)));
	}

	public Vector3f sub(Vector3f v) {
		return new Vector3f(
			x - v.x,
			y - v.y,
			z - v.z
		);
	}

	public Vector3f normalize() {
		float mag = magnitude();
		return new Vector3f(
			x / mag,
			y / mag,
			z / mag
		);
	}
	
	public Vector3f crossProduct(Vector3f v) {
		return new Vector3f(
			y * v.z - z * v.y,
			z * v.x - x * v.z,
			x * v.y - y * v.x
		);
	}
	
	public float dot(Vector3f v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3f mul(float scalar) {
		return new Vector3f(
			x * scalar,
			y * scalar,
			z * scalar
		);
	}

	public void _mul(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public Vector3f div(float scalar) {
		return new Vector3f(
			x / scalar,
			y / scalar,
			z / scalar
		);
	}
	
	public void _div(float scalar) {
		x /= scalar;
		y /= scalar;
		z /= scalar;
	}
	
	public Vector2f getVector2f() {
		return new Vector2f(
			x,
			y
		);
	}
	
	public float max() {
		float ax = Math.abs(x);
		float ay = Math.abs(y);
		float az = Math.abs(z);
		return Math.max(ax, Math.max(ay, az));
	}
	
	public Vector3f clone() {
		return new Vector3f(x, y, z);
	}
	
}
