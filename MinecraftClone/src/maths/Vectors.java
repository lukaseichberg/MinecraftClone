package maths;

public class Vectors {
	
	public static int toColorInt(Vector4f color) {
		int a = (int) (color.w * 0xFF);
		int r = (int) (color.x * 0xFF);
		int g = (int) (color.y * 0xFF);
		int b = (int) (color.z * 0xFF);
		return a << 24 | r << 16 | g << 8 | b;
	}

}
