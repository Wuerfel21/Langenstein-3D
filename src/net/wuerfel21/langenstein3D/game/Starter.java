package net.wuerfel21.langenstein3D.game;

import java.util.concurrent.TimeUnit;

import net.wuerfel21.langenstein3D.game.audio.MusicPlayer;
import net.wuerfel21.langenstein3D.game.render.Caster;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

/**
 * Abstrakte Klasse Starter - beschreiben Sie hier die Klasse
 * 
 * @author (Ihr Name)
 * @version (eine Version-Nummer oder ein Datum)
 */
public final class Starter {
	public static Caster caster;
	public static MusicPlayer music;

	private Starter() {
		// Dummy
	}
	
	/**
	 * Improves timing precision on M$ Windows
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

	public static void main(String[] args) { // TODO: Make launcher dialog, move timing somewhere else
		System.setProperty("sun.java2d.opengl", "True"); //This allows using VSYNC and page flipping. TODO: Make configurable
		//create sleeper, always a good idea!
		new Sleeper().start();
		switch (args.length) {
		case 3:
			caster = new Caster(Integer.decode(args[0]), Integer.decode(args[1]),!args[2].equals("window"));
			break;
		case 2:
			caster = new Caster(Integer.decode(args[0]), Integer.decode(args[1]),true);
			break;
		case 1:
			if (args[0].equals("colormap")) {
				testColorMap();
				return;
			} else if (args[0].equals("window")) {
				caster = new Caster(640, 400,false);
				break;
			}
		default:
			caster = new Caster(640, 400,true);
			break;
		}
		music = new MusicPlayer();
		music.play("KingOfTheDesert");
		long frameStart, frameStop;
		while (!caster.doQuit()) {
			frameStart = System.nanoTime();
			frameStop = frameStart + (1000000000l / 35);
			caster.run();
			if (System.nanoTime() < frameStop) try {
				if ((frameStop-System.nanoTime()) > 1500000)
					Thread.sleep(Math.max(0,(frameStop-System.nanoTime()-1500000)/1000000)); //Wait until theres 1.5 ms left
				while (System.nanoTime() < frameStop); //busy wait the remaining time
			} catch (InterruptedException e) {
			}
		}
		System.exit(0);
	}

	public static void testColorMap() {
		caster = new Caster(1280, 513,false);
		caster.test();
	}
}
