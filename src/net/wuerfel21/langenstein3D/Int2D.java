package net.wuerfel21.langenstein3D;

/**
 * Two ints in an object.
 * 
 * @author Wuerfel_21
 */
public class Int2D {
	public int x, y;

	/**
	 * Create a new unitialized Int2D.
	 */
	public Int2D() {
		// void constructor
	}

	/**
	 * Create a new Int2D from 2 ints.
	 */
	public Int2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Int2D from another Int2D.
	 */
	public Int2D(Int2D i2d) {
		this.x = i2d.x;
		this.y = i2d.y;
	}

	/**
	 * Create a new Int2D by casting a DoubleXY.
	 */
	public Int2D(DoubleXY d) {
		this.x = (int) d.x;
		this.y = (int) d.y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void set(Int2D i2d) {
		this.x = i2d.x;
		this.y = i2d.y;
	}

	public void set(DoubleXY d) {
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
}
