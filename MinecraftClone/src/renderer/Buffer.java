package renderer;

import maths.Vector4f;

public class Buffer {

	private int width, height;
	private int[] colorBuffer;
	private float[] depthBuffer;

	public static final int COLOR_BUFFER = 0;
	public static final int DEPTH_BUFFER = 1;

	public Buffer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void fillColor(int color) {
		for (int i = 0; i < colorBuffer.length; i++)
			colorBuffer[i] = color;
	}
	
	public void fillDepth(float depth) {
		for (int i = 0; i < depthBuffer.length; i++)
			depthBuffer[i] = depth;
	}
	
	public void setColor(int x, int y, Vector4f color) {
		int r = (int) (color.x * 0xFF) & 0xFF;
		int g = (int) (color.y * 0xFF) & 0xFF;
		int b = (int) (color.z * 0xFF) & 0xFF;
		int c = r << 16 | g << 8 | b;
		
		setColor(x, y, c);
	}
	
	public void setColor(int x, int y, int color) {
		colorBuffer[x + y * width] = color;
	}
	
	public void setDepth(int x, int y, float depth) {
		depthBuffer[x + y * width] = depth;
	}
	
	public int getColor(int x, int y) {
		return colorBuffer[x + y * width];
	}

	public Vector4f getPixelColor(int x, int y) {
		int color = getColor(x, y);
		float r = (float) ((color >> 16) & 0xFF) / 0xFF;
		float g = (float) ((color >> 8) & 0xFF) / 0xFF;
		float b = (float) (color & 0xFF) / 0xFF;
		return new Vector4f(r, g, b, 1);
	}
	
	public float getDepth(int x, int y) {
		return depthBuffer[x + y * width];
	}

	public void enable(int bufferType) {
		switch (bufferType) {
			case COLOR_BUFFER:
				colorBuffer = new int[width * height];
				break;
			case DEPTH_BUFFER:
				depthBuffer = new float[width * height];
				break;
		}
	}
	
	public void diable(int bufferType) {
		switch (bufferType) {
			case COLOR_BUFFER:
				colorBuffer = null;
				break;
			case DEPTH_BUFFER:
				depthBuffer = null;
				break;
		}
	}
	
	public void drawBuffer(Buffer b) {
		for (int y = 0; y < b.height; y++) {
			for (int x = 0; x < b.width; x++) {
				setColor(x, y, b.getColor(x, y));
			}
		}
	}
	
	public void bindColorBufferArray(int[] buffer) {
		colorBuffer = buffer;
	}
	
	public void bindDepthBufferArray(float[] buffer) {
		depthBuffer = buffer;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

}
