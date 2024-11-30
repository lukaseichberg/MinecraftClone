package renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import maths.Vector4f;

public class Image {
	
	private BufferedImage img;
	
	public Image(String fileName) {
		File file = new File(fileName);
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Vector4f getPixelColor(int x, int y) {
		int color = img.getRGB(x, y);
		float r = (float) ((color >> 16) & 0xFF) / 0xFF;
		float g = (float) ((color >> 8) & 0xFF) / 0xFF;
		float b = (float) (color & 0xFF) / 0xFF;
		return new Vector4f(r, g, b, 1);
	}
	
	public int getWidth() {
		return img.getWidth();
	}
	
	public int getHeight() {
		return img.getWidth();
	}
	
	public int getPixel(int x, int y) {
		return img.getRGB(x, y);
	}

}
