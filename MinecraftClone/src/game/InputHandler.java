package game;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import maths.Vector2i;
import renderer.Display;

public class InputHandler {
	
	public boolean[] key;
	public boolean[] mb;
	public Vector2i mouse;
	
	public InputHandler(Display display) {
		key = new boolean[KeyEvent.KEY_LAST];
		mb = new boolean[MouseEvent.MOUSE_LAST];
		
		mouse = new Vector2i(0, 0);
		
		display.canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				key[arg0.getKeyCode()] = true;
			}

			public void keyReleased(KeyEvent arg0) {
				key[arg0.getKeyCode()] = false;
			}

			public void keyTyped(KeyEvent arg0) {}
		});
		
		display.canvas.setCursor(display.canvas.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null));
		
		display.canvas.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				mouse.x = e.getX();
				mouse.y = e.getY();
			}
			
			public void mouseDragged(MouseEvent e) {}
		});
		
		display.canvas.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				mb[e.getButton()] = false;
			}
			
			public void mousePressed(MouseEvent e) {
				mb[e.getButton()] = true;
			}
			
			public void mouseExited(MouseEvent e) {}
			
			public void mouseEntered(MouseEvent e) {}
			
			public void mouseClicked(MouseEvent e) {}
		});
	}

}
