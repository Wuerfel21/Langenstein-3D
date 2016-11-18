package net.irq_interactive.langenstein3D.game.io;

public final strictfp class InputUtil {

	public static enum Keys {
		FIRE, USE, FORWARD, BACKWARD, LEFT, RIGHT, SPRINT, SNEAK, FIRE_ALT, AUX1, AUX2, AUX_UP, AUX_DOWN, AUX_LEFT, AUX_RIGHT;

		public boolean isPressed(long in) {
			return (in & (1l << (40 + this.ordinal()))) != 0;
		}
	}

	private InputUtil() {
	}

	public static final int getRotationFract(long in) {
		return (int) in;
	}

	public static final double getRotationRadians(long in) {
		return (((double) getRotationFract(in))/4294967295.0) * 2 * Math.PI;
	}

}
