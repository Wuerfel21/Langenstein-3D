package net.irq_interactive.langenstein3D.game;

public class FinalInt2D {

	public final int x;
	public final int y;
	
	/**
	 * Create a new FinalInt2D from 2 ints.
	 */
	public FinalInt2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new FinalInt2D from another Int2D.
	 */
	public FinalInt2D(FinalInt2D i2d) {
		this.x = i2d.x;
		this.y = i2d.y;
	}

	/**
	 * Create a new Int2D by casting a DoubleXY.
	 */
	public FinalInt2D(DoubleXY d) {
		this.x = (int) d.x;
		this.y = (int) d.y;
	}

	public boolean equals(Int2D i) {
		return i.x == x && i.y == y;
	}

	/**
	 * Create a new Int2D from this one.
	 */
	public Int2D copy() {
		return new Int2D(this);
	}

	public String toString() {
		return Integer.toString(x)+";"+Integer.toString(y);
	}

}