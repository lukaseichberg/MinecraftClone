package maths;

public class Vector2i {
	
	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2i add(Vector2i v) {
		return new Vector2i(x + v.x, y + v.y);
	}
	
	public Vector2i sub(Vector2i v) {
		return new Vector2i(x - v.x, y - v.y);
	}

}
