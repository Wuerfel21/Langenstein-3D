package net.wuerfel21.langenstein3D;

import java.util.concurrent.TimeUnit;

import net.wuerfel21.langenstein3D.audio.MusicPlayer;
import net.wuerfel21.langenstein3D.render.Caster;
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
		//create sleeper, always a good idea!
		new Sleeper().start();
		switch (args.length) {
		case 2:
			caster = new Caster(Integer.decode(args[0]), Integer.decode(args[1]));
			break;
		case 1:
			if (args[0].equals("colormap")) {
				testColorMap();
				return;
			}
		default:
			caster = new Caster(640, 400);
			break;
		}
		// music = new MusicPlayer();
		// music.play("DefianceNew");
		long frameStart, frameStop;
		while (!caster.doQuit()) {
			/*frameStart = System.nanoTime();
			frameStop = frameStart + 33333333l;*/
			caster.run();
			/*if (System.nanoTime() < frameStop) try {
				if ((frameStop-System.nanoTime()) > 1500000)
					Thread.sleep(Math.max(0,(frameStop-System.nanoTime()-1500000)/1000000)); //Wait until theres 1.5 ms left
				while (System.nanoTime() < frameStop); //busy wait the remaining time
			} catch (InterruptedException e) {
			}*/
		}
		System.exit(0);
	}

	public static void testColorMap() {
		caster = new Caster(1280, 513);
		caster.test();
	}
}
