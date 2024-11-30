package game;

import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;
import renderer.Image;
import renderer.Shader;
import renderer.Vertex;

public class FS extends Shader {
	
	Image img;
	float specularStrength = 0.8f;
	
	public FS() {
		img = new Image("res/terrain.png");
	}

	@Override
	public void main() {
		Vector2f uv = in.vec2[0];
		Vector3f normal = in.vec3[0].normalize();
		Vector3f light = uniform.vec3[1];
		
		out = new Vertex(0, 0, 0);
		
		float brightness = Math.max(-light.normalize().dot(normal), 0.2f);
		float fog = Math.max(Math.min(((1 / in.pos.w)-16) / 16, 1), 0);
		
		int x = (int) (uv.x * img.getWidth());
		int y = (int) (uv.y * img.getHeight());

		brightness = Math.max(Math.min(brightness, 1), 0);
		Vector4f color = img.getPixelColor(x, y).mul(brightness);
		color.w = 1;
		color = color.colorClip();
		out.col = color.lerp(new Vector4f((float) 0xAA / 0xFF, (float) 0xDD / 0xFF, (float) 0xFF / 0xFF, 1), fog);
	}

}
