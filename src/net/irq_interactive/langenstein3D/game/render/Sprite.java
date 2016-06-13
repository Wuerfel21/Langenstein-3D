/**
 * 
 */
package net.wuerfel21.langenstein3D.game.render;

import java.util.Arrays;

/**
 * Represents a Sprite, without position info.
 * 
 * @author Wuerfel_21
 *
 */
public class Sprite {

	/**
	 * Textures for the sprite. If there's more than one, the angle relative to the camera will be calculated and the texture will be picked based on that.
	 */
	public final Texture[] rotations;

	/**
	 * Whether staticLightLevel should be used as the light level instead of computing it based on position and distance.
	 */
	public final boolean staticLighting;
	/**
	 * Light level when staticLighting is true. Legal values are from 0 to 255. Is an int for convenience.
	 */
	public final int staticLightLevel;

	/**
	 * 1D color table to be applied to the sprite.
	 */
	public final byte[] colorTable1d;

	/**
	 * 2D color table to be applied to the sprite. The sprite pixel is the first index, the background pixel is the second (1D color table is applied first).
	 */
	public final byte[][] colorTable2d;
	
	public final boolean useMetatexture;

	public final double scaleX, scaleY, offsetY; //TODO: Implement these
	
	/**
	 * Whether the metatexture should be used.
	 * If this is true, the 2D color table is only applied to pixels where the metatexture is true
	 */
	public final double repeatRotations;

	/**
	 * Constructs a new Sprite object. The parameters are the values for the respective fields.
	 * 
	 * @param rotations
	 * @param staticLighting
	 * @param staticLightLevel
	 * @param colorTable1d
	 * @param colorTable2d
	 * @param scaleX
	 * @param scaleY
	 * @param offsetY
	 */
	public Sprite(Texture[] rotations, boolean staticLighting, int staticLightLevel, byte[] colorTable1d, byte[][] colorTable2d, double scaleX, double scaleY,
			double offsetY, boolean useMetatexture, double repeatRotations) {
		this.rotations = rotations;
		this.staticLighting = staticLighting;
		this.staticLightLevel = staticLightLevel;
		this.colorTable1d = colorTable1d;
		this.colorTable2d = colorTable2d;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.offsetY = offsetY;
		this.useMetatexture = useMetatexture;
		this.repeatRotations = repeatRotations;
	}

	public Sprite(Texture tex, boolean staticLighting, int staticLightLevel, byte[] colorTable1d, byte[][] colorTable2d, double scaleX, double scaleY,
			double offsetY, boolean useMetatexture, double repeatRotations) {
		this(new Texture[] { tex }, staticLighting, staticLightLevel, colorTable1d, colorTable2d, scaleX, scaleY, offsetY, useMetatexture, repeatRotations);
	}

	public Sprite(Texture tex) {
		this(new Texture[] { tex }, false, 255, null, null, 1, 1, 0, false, 1);
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
		Sprite other = (Sprite) obj;
		if (colorTable1d == other.colorTable1d) return false;
		if (colorTable2d == other.colorTable2d) return false;
		if (offsetY != other.offsetY) return false;
		if (!Arrays.equals(rotations, other.rotations)) return false;
		if (scaleX != other.scaleX) return false;
		if (scaleY != other.scaleY) return false;
		if (staticLightLevel != other.staticLightLevel) return false;
		if (staticLighting != other.staticLighting) return false;
		return true;
	}

}
