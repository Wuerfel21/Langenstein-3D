package net.wuerfel21.langenstein3D.game;

/**
 * Two doubles in one one class.
 * 
 * @author Wuerfel_21
 */
public abstract class DoubleXY {
	public double x, y;

	public DoubleXY() {
		// void constructor
	}

	public DoubleXY(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public DoubleXY(DoubleXY xy) {
		this.x = xy.x;
		this.y = xy.y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void set(DoubleXY xy) {
		this.x = xy.x;
		this.y = xy.y;
	}

	public boolean equals(DoubleXY d) {
		return d.x == x && d.y == y;
	}

	public abstract DoubleXY copy();
}
