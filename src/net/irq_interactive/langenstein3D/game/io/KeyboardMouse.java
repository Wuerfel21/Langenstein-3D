package net.irq_interactive.langenstein3D.game.io;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import net.irq_interactive.langenstein3D.MiscUtil;
import net.irq_interactive.langenstein3D.game.FinalInt2D;
import net.irq_interactive.langenstein3D.game.Int2D;
import net.irq_interactive.langenstein3D.game.Vector;

/**
 * Handles Input.
 * 
 * @author Wuerfel_21
 */
public class KeyboardMouse extends InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	protected boolean[] states;
	public int[] keyCodes = { 0, KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, 0, 0, 0,
			0, 0, 0, 0 };
	public int rotSpeed = 58591309;//(1 / 35f)* 3.0
	protected int rotation;
	public int turnLeft = KeyEvent.VK_LEFT, turnRight = KeyEvent.VK_RIGHT;
	protected boolean turningLeft,turningRight;
	public double mouseSpeed = 1;
	private final Object lock = new Object();

	private final Point mouseAbsolute;
	private final Vector mouseRelative;
	private Robot robot;

	public KeyboardMouse(int p) {
		super(p);
		if (p != 0) throw new IllegalArgumentException("Only one Keyboard!!!!");
		states = new boolean[InputUtil.Keys.values().length];
		mouseRelative = new Vector();
		// prevMouse = new Int2D();
		mouseAbsolute = new Point();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// bhlerg
		}

	}

	private void setTo(int code, boolean state) {
		if (code == turnLeft)
			turningLeft = state;
		else if (code == turnRight)
			turningRight = state;
		for (int i = 0; i < keyCodes.length; i++) {
			if (keyCodes[i] == code) {
				synchronized (lock) {
					states[i] = state;
					break;
				}
			}
		}
	}

	public FinalInt2D getMouseAbsolute() {
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
	
	public int getRotation() {
		int r;
		synchronized (lock){
			r = rotation;
			rotation = 0;
		}
		return r;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.setTo(e.getKeyCode(), true);
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
		double delta = 0;
		synchronized (mouseAbsolute) {
			// prevMouse.set(mouseAbsolute);
			mouseAbsolute.setLocation(e.getPoint());
			if (!(mouseAbsolute.x == bounds.width / 2 && mouseAbsolute.y == bounds.height / 2)) {
				robot.mouseMove(off.x + bounds.width / 2, off.y + bounds.height / 2);
				delta = mouseAbsolute.x - bounds.width / 2;
				synchronized (mouseRelative) {
					mouseRelative.x += (delta) * mouseSpeed;
					mouseRelative.y += (mouseAbsolute.y - bounds.height / 2) * mouseSpeed;
				}
			}
		}
		synchronized (lock) {
		rotation += (int)(((long) ((delta / 1024.0) * 4294967295.0)) & 0xFFFFFFFFl);
		}
	}

	@Override
	public long getInput() {
		long in;
		int r = getRotation()+(turningLeft?-rotSpeed:0)+(turningRight?rotSpeed:0);
		in = r & 0xFFFFFFFFl;
		// TODO: Weapon switching
		// TODO: Mouse buttons
		in |= MiscUtil.toBitField(states) << 40;
		in &= 0x01FFFFFFFFFFFFFFl;
		return in;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
