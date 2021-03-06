/**
 * 
 */
package net.irq_interactive.langenstein3D.game.things;

import net.irq_interactive.langenstein3D.FixedPoint;
import net.irq_interactive.langenstein3D.game.FinalInt2D;
import net.irq_interactive.langenstein3D.game.Int2D;

/**
 * @author Wuerfel_21
 *
 */
public abstract strictfp class Thing {
	
	protected int angle;
	protected int x,y; //Fixed point
	
	/**
	 * This method is called every frame for all Things on the Thing list.
	 * 
	 * @param now the current frame number
	 * @return whether to remove this Thing from the Thing list.
	 */
	public boolean tick(long now) {
		//Check for and correct invalid states
		if (x<0) {
			x = x<-1073741824?FixedPoint.MAX_VALUE:0;
		}
		if (y<0) {
			y = y<-1073741824?FixedPoint.MAX_VALUE:0;
		}
		
		return false;
	}
	
	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public FinalInt2D getXY() {
		return new Int2D(x, y);
	}
	
	public void setXY(FinalInt2D xy) {
		this.x = xy.x;
		this.y = xy.y;
	}
	
	public int getZ() { // Z is for moving up/down, which most things don't do.
		return 0;
	}
	
	public void setZ(){};
	
	public double getXfp() {
		return FixedPoint.fixToDouble(getX());
	}

	public double getYfp() {
		return FixedPoint.fixToDouble(getY());
	}
	
	public double getZfp() {
		return FixedPoint.fixToDouble(getZ());
	}
	
	public abstract int getRadius();
	
	/**
	 * Returns whether this Thing is "virtual", e.g. not related to actual gameplay, instead serving a utility purpose.
	 * Virtual things will neither be rendered nor be checked for collision and such.
	 * @return
	 */
	public abstract boolean isVirtual();

	
}
