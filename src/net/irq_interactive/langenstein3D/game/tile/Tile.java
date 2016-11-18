package net.irq_interactive.langenstein3D.game.tile;

import static net.irq_interactive.langenstein3D.FixedPoint.MAX_FRACT;

import net.irq_interactive.langenstein3D.CompassDir;
import net.irq_interactive.langenstein3D.game.Game;
import net.irq_interactive.langenstein3D.game.Tileset;

public abstract class Tile {

	protected final Tileset parent;
	
	public Tile(Tileset parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns whether the Tile is valid for the passed game.
	 * @param game The current(?) game.
	 * @return true when valid
	 */
	public boolean valid(Game game){
		return parent.valid(game);
	}
	
	/**
	 * Returns the horizontal texture offset on this tile, if it were at map XY, in world units (fixed point)
	 */
	public int getTextureOffset(int x,int y) {
		return 0;
	}
	
	/**
	 * returns whether this tile, if it were at map XY, is solid, at the specified granularity and sub XY.
	 */
	/**
	 * @param render whether the Renderer is calling. This can be used to create invisible walls/fake walls.
	 * @param x The X coordinate on the map (integer)
	 * @param y The Y coordinate on the map (integer)
	 * @param granularity the granularity level to use. level 0 is the full tile. 1 is quarters, 2 is sixteenths and so on.
	 * @param subx The X coordinate of the sub-tile. Range depends on granularity. (integer)
	 * @param suby The Y coordinate of the sub-tile. Range depends on granularity. (integer)
	 * @return Zero: (sub-)tile is empty. Positive: Solid. Negative: Higher granularity needed. 
	 */
	public abstract int getSolid(boolean render,int x, int y, int granularity, int subx, int suby);
	

	public int getSolidAt(boolean render,int x, int y) {
		int t,mapX = x >> 16,mapY = y >> 16,subX = x & MAX_FRACT,subY = y & MAX_FRACT;
		for(int i=0;i<=16;i++) {
			int shift = 16-i;
			t = getSolid(render,mapX,mapY,i,subX>>>shift,subY>>>shift);
			if (t<=0) return t;
			// Were going deeper, Leo!
		}
		return 1; //That's too deep for me! Bail out!
	}
	
	public int getWallTexture(boolean render,int x, int y, int granularity, int subx, int suby, CompassDir dir) {
		return parent.game.getWallTex(x, y);
	}
	
	/**
	 * The grand epitome of magic numbers, isn't it?
	 */
	public int getWallLightMulti(boolean render,int x, int y, int granularity, int subx, int suby, CompassDir dir) {
		switch(dir) {
		default:
		case EAST:
		case WEST:
			return 0x10000;
		case NORTH:
		case SOUTH:
			return 0x9249;
		}
	}
	
	public int getFlatTexture(boolean render,int x, int y, int granularity, int subx, int suby) {
		return parent.game.getFlatTex(x, y);
	}
}
