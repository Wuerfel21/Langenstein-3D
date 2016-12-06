package net.irq_interactive.langenstein3D.launcher;

import java.util.Map;

public abstract class Launchable {
	
	public Launchable() {
		//This is a dummy class
	}

	public abstract int launch(Map<String,Object> args) throws Exception;
	
}
