package net.irq_interactive.langenstein3D.game;

/**
 * Write a description of class Position here.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Position extends DoubleXY {
	public Position() {
		super();
	}

	public Position(double x, double y) {
		super(x, y);
	}

	public Position(DoubleXY d) {
		super(d);
	}

	@Override
	public Position copy() {
		return new Position(this);
	}
}
