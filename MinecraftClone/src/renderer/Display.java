package renderer;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Display {
	
	public JFrame frame;
	private BufferedImage image;
	private BufferStrategy bufferStrategy;
	private Graphics graphics;
	
	private int width, height, scale;
	private Buffer buffer;
	public boolean up, down, left, right, mb_left, mb_right, escape, space, shift, k, l, o, p;
	public int mouse_x, mouse_y;
	
	public Canvas canvas;
	
	public Display(int width, int height, int scale, String title) {
		this.width 	= width;
		this.height = height;
		this.scale 	= scale;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		buffer = new Buffer(width, height);
		buffer.bindColorBufferArray(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
		
		Dimension size = new Dimension(width * scale, height * scale);
		
		canvas = new Canvas();
		canvas.setPreferredSize(size);
		canvas.setMinimumSize(size);
		canvas.setMaximumSize(size);
		canvas.createBufferStrategy(1);
		setInputHandler();
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		bufferStrategy = canvas.getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
	}
	
	private void setInputHandler() {
		canvas.requestFocus();
		canvas.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}

			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					up = true;
					break;
				case KeyEvent.VK_S:
					down = true;
					break;
				case KeyEvent.VK_A:
					left = true;
					break;
				case KeyEvent.VK_D:
					right = true;
					break;
				case KeyEvent.VK_SPACE:
					space = true;
					break;
				case KeyEvent.VK_SHIFT:
					shift = true;
					break;
				case KeyEvent.VK_K:
					k = true;
					break;
				case KeyEvent.VK_L:
					l = true;
					break;
				case KeyEvent.VK_O:
					o = true;
					break;
				case KeyEvent.VK_P:
					p = true;
					break;
				case KeyEvent.VK_ESCAPE:
					escape = true;
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					up = false;
					break;
				case KeyEvent.VK_S:
					down = false;
					break;
				case KeyEvent.VK_A:
					left = false;
					break;
				case KeyEvent.VK_D:
					right = false;
					break;
				case KeyEvent.VK_SPACE:
					space = false;
					break;
				case KeyEvent.VK_SHIFT:
					shift = false;
					break;
				case KeyEvent.VK_K:
					k = false;
					break;
				case KeyEvent.VK_L:
					l = false;
					break;
				case KeyEvent.VK_O:
					o = false;
					break;
				case KeyEvent.VK_P:
					p = false;
					break;
				case KeyEvent.VK_ESCAPE:
					escape = false;
					break;
				}
			}
		});
		canvas.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mb_left = false;
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					mb_right = false;
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mb_left = true;
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					mb_right = true;
				}
			}
			
			public void mouseExited(MouseEvent e) {
				mb_left = false;
				mb_right = false;
			}
			
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				mouse_x = e.getX();
				mouse_y = e.getY();
			}
			
			public void mouseDragged(MouseEvent e) {
				mouse_x = e.getX();
				mouse_y = e.getY();
			}
		});
	}
	
	public Point getCanvasPos() {
		return canvas.getLocationOnScreen();
	}
	
	public int getCanvasWidth() {
		return canvas.getWidth();
	}
	
	public int getCanvasHeight() {
		return canvas.getHeight();
	}
	
	public void update() {
		graphics.drawImage(image, 0, 0, width * scale, height * scale, null);
		bufferStrategy.show();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getScale() {
		return scale;
	}
	
	public Buffer getBuffer() {
		return buffer;
	}

}
