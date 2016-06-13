package net.irq_interactive.langenstein3D.game;

/**
 * Write a description of class Vector here.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Vector extends DoubleXY {
	public Vector() {
		super();
	}

	public Vector(double x, double y) {
		super(x, y);
	}

	public Vector(DoubleXY d) {
		super(d);
	}

	@Override
	public Vector copy() {
		return new Vector(this);
	}
}
