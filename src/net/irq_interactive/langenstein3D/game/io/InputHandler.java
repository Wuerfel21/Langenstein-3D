package net.irq_interactive.langenstein3D.game.io;

public abstract class InputHandler {

	/**
	 * 
	 * @param playerNum the local player number to get the Input handler for.
	 */
	public InputHandler(int playerNum) {
	}
	
	public abstract long getInput();
	

}
