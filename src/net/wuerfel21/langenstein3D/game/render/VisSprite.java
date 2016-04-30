/**
 * 
 */
package net.wuerfel21.langenstein3D.game.render;

import java.util.Comparator;

/**
 * Represents a sprite that is ready to be rendered
 * 
 * @author Wuerfel_21
 *
 */
public class VisSprite {
	public final Sprite spr;
	public double dist;
	public int x, y, width, height, rotation;
	public int light; //should be byte, but saves a cast this way

	/**
	 * @param spr
	 */
	public VisSprite(Sprite spr) {
		this.spr = spr;
	}

	/**
	 * @param spr
	 * @param dist
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param rotation
	 */
	public VisSprite(Sprite spr, double dist, int x, int y, int width, int height, int rotation,int light) {
		this.spr = spr;
		this.dist = dist;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.light = light;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dist);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + height;
		result = prime * result + rotation;
		result = prime * result + ((spr == null) ? 0 : spr.hashCode());
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + light;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		VisSprite other = (VisSprite) obj;
		if (Double.doubleToLongBits(dist) != Double.doubleToLongBits(other.dist)) return false;
		if (height != other.height) return false;
		if (rotation != other.rotation) return false;
		if (spr == null) {
			if (other.spr != null) return false;
		} else if (!spr.equals(other.spr)) return false;
		if (width != other.width) return false;
		if (x != other.x) return false;
		if (y != other.y) return false;
		if (light != other.light) return false;
		return true;
	}

	public static class ZComparator implements Comparator<VisSprite> {

		@Override
		public int compare(VisSprite o1, VisSprite o2) {
			return Double.compare(o2.dist, o1.dist); // Reversed because further away sprites get drawn first.
		}

	}

}
