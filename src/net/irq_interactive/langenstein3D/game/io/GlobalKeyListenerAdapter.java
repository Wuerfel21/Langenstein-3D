package net.irq_interactive.langenstein3D.game.io;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class GlobalKeyListenerAdapter implements KeyEventDispatcher {
	List<KeyListener> listeners;
	
	public GlobalKeyListenerAdapter(List<KeyListener> listeners) {
		this.listeners = listeners;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		for (KeyListener listener:listeners){
			switch(e.getID()) {
			case KeyEvent.KEY_PRESSED:
				listener.keyPressed(e);
				break;
			case KeyEvent.KEY_RELEASED:
				listener.keyReleased(e);
				break;
			case KeyEvent.KEY_TYPED:
				listener.keyTyped(e);
				break;
			default:
				break;
			}
		}
		return false;
	}

}
