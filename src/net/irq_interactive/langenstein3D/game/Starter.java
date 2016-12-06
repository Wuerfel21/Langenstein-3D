package net.irq_interactive.langenstein3D.game;

import java.lang.reflect.Constructor;
import java.util.Map;

import net.irq_interactive.langenstein3D.game.audio.MusicPlayer;
import net.irq_interactive.langenstein3D.game.render.Caster;
import net.irq_interactive.langenstein3D.launcher.Launchable;

/**
 * Abstrakte Klasse Starter - beschreiben Sie hier die Klasse
 * 
 * @author (Ihr Name)
 * @version (eine Version-Nummer oder ein Datum)
 */
public final class Starter extends Launchable {
	public static Caster caster;
	public static MusicPlayer music;

	/**
	 * Improves timing precision on M$ Windows
	 * 
	 * @author Wuerfel_21
	 *
	 */
	protected static class Sleeper extends Thread {

		public Sleeper() {
			this.setDaemon(true);
			this.setName("Chungus");
		}

		@Override
		public void run() {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			}
		}

	}

	@SuppressWarnings("unused")
	public int launch(Map<String,Object> args) { // TODO: no longer pass values as strings, move timing somewhere else
		System.setProperty("sun.java2d.opengl", "True"); // This allows using VSYNC and page flipping. TODO: Make configurable
		// create sleeper, always a good idea!
		new Sleeper().start();
		if((Boolean)args.getOrDefault("debug.colormap", Boolean.FALSE)) {
			testColorMap();
			return 0;
		}
		caster = new Caster((Integer)args.getOrDefault("render.width", 640), (Integer)args.getOrDefault("render.height", 480), (Boolean)args.getOrDefault("render.fullscreen", true));
		if ((Boolean)args.getOrDefault("sound.musicEnabled", Boolean.FALSE)) {
			music = new MusicPlayer();
			music.play("KingOfTheDesert");
		}
		
		
		
		long frameStart, frameStop;
		while (!caster.doQuit()) {
			frameStart = System.nanoTime();
			frameStop = frameStart + (1000000000l / 35);
			caster.run();
			if (System.nanoTime() < frameStop) try {
				if ((frameStop - System.nanoTime()) > 1500000)
					Thread.sleep(Math.max(0, (frameStop - System.nanoTime() - 1500000) / 1000000)); // Wait until theres 1.5 ms left
				while (System.nanoTime() < frameStop)
					; // busy wait the remaining time
			} catch (InterruptedException e) {
			}
		}
		return 0;
	}

	public static void testColorMap() {
		caster = new Caster(1280, 513, false);
		caster.test();
	}
}
