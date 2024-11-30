package maths;

public class Variables {
	
	public static float lerp(float f0, float f1, float value) {
		return f0 + (f1 - f0) * value;
	}
	
	public static Vector4f toColorVec(int color) {
		float r = (float) ((color >> 16) & 0xFF) / (float) 0xFF;
		float g = (float) ((color >> 8) & 0xFF) / (float) 0xFF;
		float b = (float) (color & 0xFF) / (float) 0xFF;
		float a = (float) ((color >> 24) & 0xFF) / (float) 0xFF;
		return new Vector4f(r, g, b, a);
	}

	public static void swap(float f0, float f1) {
		float f = f0;
		f0 = f1;
		f1 = f;
	}

}
