package game;

public class FPSCounter {
	
	private int frames, fps;
	private long lastTime;
	
	public FPSCounter() {
		lastTime = System.currentTimeMillis();
		frames = 0;
		fps = 0;
	}
	
	public void tick() {
		if (System.currentTimeMillis() - lastTime >= 1000) {
			fps = frames;
			frames = 0;
			lastTime = System.currentTimeMillis();
			System.out.println("FPS: " + fps);
		} else {
			frames++;
		}
	}
	
	public int getFPS() {
		return fps;
	}

}
