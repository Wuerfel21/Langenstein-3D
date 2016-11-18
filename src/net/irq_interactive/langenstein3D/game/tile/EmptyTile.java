/**
 * 
 */
package net.irq_interactive.langenstein3D.game.tile;

import net.irq_interactive.langenstein3D.game.Tileset;

/**
 * @author Wuerfel_21
 *
 */
public class EmptyTile extends Tile {

	/**
	 * @param parent
	 */
	public EmptyTile(Tileset parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.irq_interactive.langenstein3D.game.tile.Tile#getSolid(boolean, int, int, int, int, int)
	 */
	@Override
	public int getSolid(boolean render, int x, int y, int granularity, int subx, int suby) {
		return 0; //just plain 'ole nothing.
	}

}
