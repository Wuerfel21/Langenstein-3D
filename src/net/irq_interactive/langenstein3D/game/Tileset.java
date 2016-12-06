package net.irq_interactive.langenstein3D.game;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.irq_interactive.langenstein3D.game.render.Texture;
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
	public final Texture[] flats,walls;
	
	
	public Tileset(Game game,Class<? extends Tile>[] tileClasses,Object[][] params,Texture[] wallTex,Texture[] flatTex) {
		this.game = game;
		tiles = new Tile[256];
		flats = new Texture[256];
		walls = new Texture[256];
		Arrays.fill(tiles, new EmptyTile(this));
		Texture empty = new Texture();
		Arrays.fill(walls, empty);
		Arrays.fill(flats, empty);
		
		for (int i=0;i<256;i++) {
			try{
				Class<? extends Tile> tileClass = tileClasses[i];
				Constructor<? extends Tile> con = tileClass.getConstructor(Tileset.class,Object[].class);
				tiles[i] = con.newInstance(this,params[i]);
			} catch (Exception e) {
				System.err.println("Error constucting tile "+i);
				e.printStackTrace();
				tiles[i] = new EmptyTile(this);
			}
			Texture ft = flatTex[i];
			flats[i] = ft!=null?ft:empty;
			Texture wt = wallTex[i];
			walls[i] = wt!=null?wt:empty;
		}
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
