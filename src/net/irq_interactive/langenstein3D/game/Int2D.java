package net.irq_interactive.langenstein3D.game;

/**
 * Two ints in one object.
 * 
 * @author Wuerfel_21
 */
public class Int2D extends FinalInt2D {
	
	public int x;
	public int y;
	
	/**
	 * Create a new initialized Int2D.
	 */
	public Int2D() {
		super(0,0);
		// void constructor
	}
	

	public Int2D(DoubleXY d) {
		super(d);
		// TODO Auto-generated constructor stub
	}


	public Int2D(FinalInt2D i2d) {
		super(i2d);
		// TODO Auto-generated constructor stub
	}


	public Int2D(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
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
}
