package maths;

public class Vector2f {
	
	public float x, y;
	
	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f lerp(Vector2f v, float value) {
		return new Vector2f(
			Maths.lerp(x, v.x, value),
			Maths.lerp(y, v.y, value)
		);
	}
	
	public void swap(Vector2f v) {
		Vector2f tmpV = new Vector2f(x, y);
		x = v.x;
		y = v.y;
		v.x = tmpV.x;
		v.y = tmpV.y;
	}
	
	public Vector2f add(Vector2f v) {
		return new Vector2f(
			x + v.x,
			y + v.y
		);
	}
	
	public void _add(Vector2f v) {
		x += v.x;
		y += v.y;
	}

	public Vector2f div(float scalar) {
		return new Vector2f(
			x / scalar,
			y / scalar
		);
	}
	
	public void _div(float scalar) {
		x /= scalar;
		y /= scalar;
	}

	public Vector2f mul(float scalar) {
		return new Vector2f(
			x * scalar,
			y * scalar
		);
	}
	
	public void _mul(float scalar) {
		x *= scalar;
		y *= scalar;
	}
	
	public Vector2f sub(Vector2f v) {
		return new Vector2f(
			x - v.x,
			y - v.y
		);
	}

	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public Vector2f clone() {
		return new Vector2f(x, y);
	}

}
