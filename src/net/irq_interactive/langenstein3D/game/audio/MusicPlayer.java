package net.irq_interactive.langenstein3D.game.audio;

import javax.sound.midi.*;

import net.irq_interactive.langenstein3D.game.Loader;

import java.io.BufferedInputStream;

public class MusicPlayer {
	private Sequencer player;
	private Synthesizer synth;
	private Soundbank bank;
	private boolean paused = false;
	private boolean ready = false;

	public MusicPlayer() {
		try {
			// Initialize MIDI output
			player = MidiSystem.getSequencer(false);
			synth = MidiSystem.getSynthesizer();
			// The BufferedInputStream is required for launching from jar, as the decompression thing can't seek. TODO: Use proper Loader
			bank = MidiSystem.getSoundbank(new BufferedInputStream(Loader.getInternalloader().get("/assets/internal/Langenstein 3D.SF2")));
			player.open();
			synth.open();
			player.getTransmitter().setReceiver(synth.getReceiver());
			synth.loadAllInstruments(bank);
		} catch (Exception e) {
			System.err.println("MUSIC INITIALIZATION ERROR: \n" + e.toString());
		}
	}

	/**
	 * Starts playing a new track
	 */
	public void play(String name) {
		try {
			Sequence track = Loader.getInternalloader().getSong(name);// TODO: Use proper Loader
			player.stop();
			player.setSequence(track);
			player.setMicrosecondPosition(0); // Rewind if needed
			player.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			paused = false;
			ready = true;
			player.start(); // And here we go! MusicPlayer.party();!
		} catch (Exception e) {
			System.err.println("MUSIC PLAY ERROR: \n" + e.toString());
			return;
		}
	}

	/**
	 * Pauses the currently playing track
	 */
	public void pause() {
		if (!paused && ready) {
			player.stop();
			paused = true;
		}
	}

	/**
	 * Unpauses the currently playing track
	 */
	public void unpause() {
		if (paused && ready) {
			player.start();
			paused = false;
		}
	}
}
