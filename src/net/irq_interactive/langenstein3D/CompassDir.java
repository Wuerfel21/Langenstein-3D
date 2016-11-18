package net.irq_interactive.langenstein3D;

import net.irq_interactive.langenstein3D.game.FinalInt2D;

public enum CompassDir {
	NORTH, EAST, SOUTH, WEST;

	private static final FinalInt2D[] dir2d = { new FinalInt2D(0, -1), new FinalInt2D(1, 0), new FinalInt2D(0, 1), new FinalInt2D(-1, 0) };
	
	public static FinalInt2D getXY(CompassDir d) {
		return dir2d[ d.ordinal()];
	}
	
	public static int getX(CompassDir d) {
		return dir2d[ d.ordinal()].x;
	}
	
	public static int getY(CompassDir d) {
		return dir2d[ d.ordinal()].y;
	}
}
