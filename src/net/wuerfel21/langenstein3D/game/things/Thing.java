/**
 * 
 */
package net.wuerfel21.langenstein3D.game.things;

/**
 * @author Wuerfel_21
 *
 */
public abstract strictfp class Thing {
	
	protected double angle;
	
	/**
	 * This method is called every frame for all Things on the Thing list.
	 * 
	 * @param now the current frame number
	 * @return whether to remove this Thing from the Thing list.
	 */
	public abstract boolean tick(long now);
	
	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	
}
