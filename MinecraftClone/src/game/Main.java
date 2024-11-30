package game;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Random;

import maths.Matrix4f;
import maths.Vector3f;
import renderer.Buffer;
import renderer.Display;
import renderer.Renderer;
import renderer.ShaderProgram;
import renderer.Uniform;

public class Main {
	
	static boolean SAVE_ON_CLOSE = true;
	
	static Robot robot = null;
	static boolean updateModel = false;
	static boolean showBuffer = false;
	static float yVel;
	static Vector3f pos;
	static Vector3f vel;
	static long seed;
	
	public static void main(String[] args) {
		int scale = 4;
		int width = 1280 / scale;
		int height = 720 / scale;
		Display display = new Display(width, height, scale, "Minecraft Clone v0.3(29.11.2020) | Software Renderer v0.3(29.11.2020)");
		
		Buffer buffer = display.getBuffer();
		buffer.enable(Buffer.DEPTH_BUFFER);
		
		Camera cam = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		cam.setProjectionMatrix(70f, (float) width / height, 0.03f, 32);
		
		Uniform uniform = new Uniform();
		uniform.mat4f = new Matrix4f[2];
		uniform.vec3 = new Vector3f[2];

		Matrix4f projection = cam.getProjection();
		
		if (!load()) {
			Random r = new Random();
			seed = r.nextLong() & 0xFFFFFF;
			PerlinNoise n = new PerlinNoise(seed);
			pos = new Vector3f(0, n.getHeight(0, 0) * 256 + 0.5f, 0);
			vel = new Vector3f(0, 0, 0);
		}

		Camera lightCam = new Camera(pos, new Vector3f(30, 40, 0));
		uniform.vec3[1] = lightCam.getViewDir();
		
		ShaderProgram sp = new ShaderProgram(new VS(), new FS(), uniform);
		
		Renderer renderer = new Renderer(buffer, sp, 0.03f, 32);
		renderer.backfaceCulling = true;
		renderer.frontfaceCulling = false;
		
		
		FPSCounter counter = new FPSCounter();
		
		World world = new World(seed);

		yVel = 0;
		
		try {
			robot = new Robot();
			Point p = display.canvas.getLocationOnScreen();
			robot.mouseMove(p.x + display.getWidth() * scale / 2, p.y + display.getHeight() * scale / 2);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		display.canvas.setCursor(display.canvas.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null));

		float speed = 0;
		long lastTime = System.nanoTime();
		
		System.out.println(world.countChunks());
		
		boolean running = true;
		while (running) {
			long delta = System.nanoTime() - lastTime;
			lastTime = System.nanoTime();
			speed = (float) delta / 10000000;
			

			int halfWidth = display.getCanvasWidth() / 2;
			int halfHeight = display.getCanvasHeight() / 2;
			int dx = display.mouse_x - halfWidth;
			int dy = display.mouse_y - halfHeight;
			
			cam.rot.y += (float) dx / 10;
			cam.rot.x += (float) dy / 10;

			Robot robot;
			try {
				robot = new Robot();
				Point point = display.getCanvasPos();
				robot.mouseMove(point.x + halfWidth, point.y + halfHeight);
			} catch (AWTException e) {
				e.printStackTrace();
			}

			vel.y -= 0.003f * speed;
			vel.x *= 0.5f;
			vel.z *= 0.5f;
			
			if (vel.y > 0) {
				if (
					world.getBlock(
						(int) Math.floor(pos.x),
						(int) Math.floor(pos.y + 1.8f + vel.y * speed),
						(int) Math.floor(pos.z)
					) > 0
				) {
					vel.y = -0.0000001f;
				}
			} else {
				if (world.getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y + vel.y * speed), (int) Math.floor(pos.z)) > 0) {
					pos.y = (int) (pos.y + vel.y * speed) + 1;
					vel.y = 0;
					while (world.getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)) > 0) {
						pos.y += 1;
					}
				}
			}
			
			if (display.up) {
				vel.x += (float) Math.sin(Math.toRadians(cam.rot.y)) * 0.05f;
				vel.z += (float) Math.cos(Math.toRadians(cam.rot.y)) * 0.05f;
			}
			if (display.down) {
				vel.x -= (float) Math.sin(Math.toRadians(cam.rot.y)) * 0.05f;
				vel.z -= (float) Math.cos(Math.toRadians(cam.rot.y)) * 0.05f;
			}
			if (display.left) {
				vel.x -= (float) Math.cos(Math.toRadians(cam.rot.y)) * 0.05f;
				vel.z += (float) Math.sin(Math.toRadians(cam.rot.y)) * 0.05f;
			}
			if (display.right) {
				vel.x += (float) Math.cos(Math.toRadians(cam.rot.y)) * 0.05f;
				vel.z -= (float) Math.sin(Math.toRadians(cam.rot.y)) * 0.05f;
			}
			
			if (world.getBlock((int) Math.floor(pos.x + vel.x * speed + Math.signum(vel.x) * 0.3), (int) Math.floor(pos.y), (int) Math.floor(pos.z)) > 0 ||
				world.getBlock((int) Math.floor(pos.x + vel.x * speed + Math.signum(vel.x) * 0.3), (int) Math.floor(pos.y + 1f), (int) Math.floor(pos.z)) > 0 ||
				world.getBlock((int) Math.floor(pos.x + vel.x * speed + Math.signum(vel.x) * 0.3), (int) Math.floor(pos.y + 1.8f), (int) Math.floor(pos.z)) > 0) {
				
				vel.x = 0;
			}
			
			if (world.getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z + vel.z * speed + Math.signum(vel.z) * 0.3f)) > 0 ||
				world.getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y + 1f), (int) Math.floor(pos.z + vel.z * speed + Math.signum(vel.z) * 0.3f)) > 0 ||
				world.getBlock((int) Math.floor(pos.x), (int) Math.floor(pos.y + 1.8f), (int) Math.floor(pos.z + vel.z * speed + Math.signum(vel.z) * 0.3f)) > 0) {
				vel.z = 0;
			}
			
			if (display.space && vel.y == 0) {
				vel.y += 0.1;
			}
			if (display.escape) {
				running = false;
			}
			if (display.p) {
				renderer.perspectiveCorrection = !renderer.perspectiveCorrection;
				display.p = false;
			}
			
			pos._add(vel.mul(speed));
			
			cam.pos = new Vector3f(pos.x, pos.y + 1.7f, pos.z);

			if (display.mb_left) {
				for (float i = 0; i < 5; i += 0.01f) {
					Vector3f rayPos = cam.pos.add(cam.getViewDir().mul(i));
					rayPos.x = (float) Math.floor(rayPos.x);
					rayPos.y = (float) Math.floor(rayPos.y);
					rayPos.z = (float) Math.floor(rayPos.z);
					
					int blockId = world.getBlock((int) rayPos.x, (int) rayPos.y, (int) rayPos.z);
					if (blockId > 0 && blockId != 8) {
						world.setBlock((int) rayPos.x, (int) rayPos.y, (int) rayPos.z, 0, true);
						break;
					}
				}
				display.mb_left = false;
			}
			if (display.mb_right) {
				for (float i = 0; i < 5; i += 0.01f) {
					Vector3f rayPos = cam.pos.add(cam.getViewDir().mul(i));
					
					if (world.getBlock((int) Math.floor(rayPos.x), (int) Math.floor(rayPos.y), (int) Math.floor(rayPos.z)) > 0) {
						rayPos = rayPos.sub(cam.getViewDir().mul(0.01f));
						rayPos.x = (int) Math.floor(rayPos.x);
						rayPos.y = (int) Math.floor(rayPos.y);
						rayPos.z = (int) Math.floor(rayPos.z);
						
						world.setBlock((int) rayPos.x, (int) rayPos.y, (int) rayPos.z, 1, true);
						break;
					}
				}
				display.mb_right = false;
			}
			
			counter.tick();
			
			Matrix4f viewMatrix = cam.getViewMatrix();
			
			
			buffer.fillDepth(0);
			buffer.fillColor(0xAADDFF);

			uniform.mat4f[0] = projection.mul(viewMatrix);
			uniform.vec3[0] = cam.pos;
			renderer.bindShaderProgram(sp);
			world.render(Math.floorDiv((int) cam.pos.x, 16), Math.floorDiv((int) cam.pos.z, 16), 2, renderer);
			display.update();
		}
		if (SAVE_ON_CLOSE) {
			world.save();
			save();
		}
		System.exit(0);
	}
	
	private static void save() {
		byte[] byteArray = new byte[32];
		int[] bits = {
				Float.floatToIntBits(pos.x),
				Float.floatToIntBits(pos.y + 1),
				Float.floatToIntBits(pos.z),
				Float.floatToIntBits(0),
				Float.floatToIntBits(0),
				Float.floatToIntBits(0)
		};
		
		for (int i = 0; i < bits.length; i++) {
			byteArray[i*4] = (byte) (bits[i] >> 24);
			byteArray[i*4+1] = (byte) (bits[i] >> 16);
			byteArray[i*4+2] = (byte) (bits[i] >> 8);
			byteArray[i*4+3] = (byte) (bits[i]);
		}

		byte[] longBytes = longToBytes(seed);
		for (int i = 0; i < 8; i++) {
			byteArray[24 + i] = longBytes[i];
		}
		
		File f = new File("res/world/player.dat");
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			fos.write(byteArray);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean load() {
		File f = new File("res/world/player.dat");
		if (f.exists()) {
			try {
				byte[] byteArray = Files.readAllBytes(f.toPath());
				float[] floats = new float[6];
				for (int i = 0; i < floats.length; i++) {
					int bits = byteArray[i*4] << 24 | (byteArray[i*4+1] & 0xFF) << 16 | (byteArray[i*4+2] & 0xFF) << 8 | (byteArray[i*4+3] & 0xFF);
					floats[i] = Float.intBitsToFloat(bits);
				}
				byte[] longBytes = new byte[8];
				for (int i = 0; i < 8; i++) {
					longBytes[i] = byteArray[24 + i];
				}
				seed = bytesToLong(longBytes);
				
				pos = new Vector3f(floats[0], floats[1], floats[2]);
				vel = new Vector3f(floats[3], floats[4], floats[5]);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}

}
