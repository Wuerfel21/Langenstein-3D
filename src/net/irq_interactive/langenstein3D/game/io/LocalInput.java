package net.irq_interactive.langenstein3D.game.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LocalInput implements KeyListener {

	public LocalInput() {
	}

	protected static boolean escape, screenshot;
	public static final Object lock = new Object();

	public static boolean screenshotPending() {
		synchronized (lock) {
			boolean r = screenshot;
			screenshot = false;
			return r;
		}
	}

	public static boolean escapePending() {
		synchronized (lock) {
			boolean r = escape;
			escape = false;
			return r;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			synchronized (lock) {
				escape = true;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_F2) {
			synchronized (lock) {
				screenshot = true;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// bhlerg
	}

}
