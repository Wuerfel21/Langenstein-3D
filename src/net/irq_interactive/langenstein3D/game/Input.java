package net.irq_interactive.langenstein3D.game;

import java.awt.event.*;
import java.awt.Robot;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.AWTException;

/**
 * Handles Input.
 * 
 * @author Wuerfel_21
 */
public class Input implements KeyListener, MouseMotionListener {
	public static enum Keys {
		EXIT, FORWARD, BACKWARD, LEFT, RIGHT, TLEFT, TRIGHT, SPRINT
	}

	protected boolean[] states;
	public int[] codes = { KeyEvent.VK_ESCAPE, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
			KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_SHIFT, };
	public double mouseSpeed = 0.6;
	private volatile boolean screenshot;
	private final Object lock = new Object();

	// private final Int2D prevMouse;
	private final Point mouseAbsolute;
	private final Vector mouseRelative;
	private Robot robot;

	public Input() {
		states = new boolean[Keys.values().length];
		mouseRelative = new Vector();
		// prevMouse = new Int2D();
		mouseAbsolute = new Point();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// bhlerg
		}

	}

	public boolean isPressed(Keys k) {
		synchronized (lock) {
			return states[k.ordinal()];
		}
	}

	private void setTo(int code, boolean state) {
		for (int i = 0; i < codes.length; i++) {
			if (codes[i] == code) {
				synchronized (lock) {
					states[i] = state;
					break;
				}
			}
		}
	}

	public boolean isScreenshot() {
		synchronized (lock) {
			if (screenshot) {
				screenshot = false;
				return true;
			} else
				return false;
		}
	}

	public Int2D getMouseAbsolute() {
		synchronized (mouseAbsolute) {
			return new Int2D(mouseAbsolute.x, mouseAbsolute.y);
		}
	}

	public Vector getMouseRelative() {
		Vector n;
		synchronized (mouseRelative) {
			n = mouseRelative.copy();
			mouseRelative.set(0, 0);
		}
		return n;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.setTo(e.getKeyCode(), true);
		if (e.getKeyCode() == KeyEvent.VK_F2)
			synchronized (lock) {
			screenshot = true;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.setTo(e.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// bhlerg
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Component parent = e.getComponent();
		Rectangle bounds = parent.getBounds();
		Point off = parent.getLocationOnScreen();
		synchronized (mouseAbsolute) {
			// prevMouse.set(mouseAbsolute);
			mouseAbsolute.setLocation(e.getPoint());
			if (!(mouseAbsolute.x == bounds.width / 2 && mouseAbsolute.y == bounds.height / 2)) {
				robot.mouseMove(off.x + bounds.width / 2, off.y + bounds.height / 2);
				synchronized (mouseRelative) {
					mouseRelative.x += (mouseAbsolute.x - bounds.width / 2) * mouseSpeed;
					mouseRelative.y += (mouseAbsolute.y - bounds.height / 2) * mouseSpeed;
				}
			}
		}
	}
}
