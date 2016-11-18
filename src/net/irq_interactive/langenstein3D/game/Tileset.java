package net.irq_interactive.langenstein3D.game;

import java.util.Arrays;

import net.irq_interactive.langenstein3D.game.tile.EmptyTile;
import net.irq_interactive.langenstein3D.game.tile.Tile;

/**
 * It manages tiles. duh.
 * 
 * @author Wuerfel_21
 *
 */
public class Tileset {
	
	public final Game game;
	
	protected final Tile[] tiles;
	
	
	public Tileset(Game game) {
		this.game = game;
		tiles = new Tile[256];
		Arrays.fill(tiles, new EmptyTile(this));
		//TODO: stub
	}
	
	/**
	 * Returns whether the Tileset is valid for the passed game.
	 * @param game The current(?) game.
	 * @return true when valid
	 */
	public boolean valid(Game game){
		return this.game == game;
	}
	
	/**
	 * Returns whether this tile is to be rendered empty
	 * @param x X coordinate of the tile (integer)
	 * @param y Y coordinate of the tile (integer)
	 * @param map0 Map0 entry for the tile
	 * 
	 * This function might yield false negatives!
	 */
	public boolean renderEmpty(int x,int y, int map0) {
		return true; //TODO: stub
	}
	

	
	
}
