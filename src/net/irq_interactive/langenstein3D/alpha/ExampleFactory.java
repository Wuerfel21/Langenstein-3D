package net.irq_interactive.langenstein3D.alpha;

import static java.lang.Math.max;

import java.io.InputStream;
import java.util.Arrays;

import net.irq_interactive.langenstein3D.game.Game;
import net.irq_interactive.langenstein3D.game.Loader;
import net.irq_interactive.langenstein3D.game.Tileset;
import net.irq_interactive.langenstein3D.game.render.Texture;
import net.irq_interactive.langenstein3D.game.tile.EmptyTile;
import net.irq_interactive.langenstein3D.game.tile.FullTile;
import net.irq_interactive.langenstein3D.game.tile.Tile;

import static net.irq_interactive.langenstein3D.game.render.Caster.texSize;

public final class ExampleFactory {

	private ExampleFactory() {
	}

	@SuppressWarnings({ "unchecked" })
	public static Tileset exampleTileset(Game game, Loader loader) {
		Class<? extends Tile>[] tileClasses = (Class<? extends Tile>[]) new Class[256];
		Arrays.fill(tileClasses, EmptyTile.class);
		tileClasses[1] = FullTile.class;

		// Generate Textures
		byte[][][] genTex = new byte[3][texSize][texSize];
		for (int x = 0; x < texSize; x++) {
			for (int y = 0; y < texSize; y++) {
				// textures[2][x][y] = (byte) ((x != y && x != texSize - y) ? 9 : 1);
				genTex[0][x][y] = (byte) max(1, x + (y * texSize));//3
				genTex[1][x][y] = (byte) (209 + (y >> 2));//4
				// textures[5][x][y] = (byte) max(1, x + y);
				genTex[2][x][y] = (byte) ((x & y) != 0 ? 1 : 15);//7
			}
		}

		Texture[] flat, wall;
		wall = new Texture[256];
		Arrays.fill(wall, new Texture());
		wall[1] = loader.getTexture("dhgWall/clean");
		wall[2] = loader.getTexture("checker/blackwhite/big");
		wall[3] = new Texture(genTex[0], false, false);
		wall[4] = new Texture(genTex[1], false, false);
		wall[5] = loader.getTexture("brkWall0/normal");
		wall[6] = loader.getTexture("carpet/0");
		wall[7] = new Texture(genTex[2], false, false);
		wall[8] = loader.getTexture("dhgWall/cross");
		wall[9] = loader.getTexture("dhgWall/crossBlood");
		wall[10] = loader.getTexture("dhgWall/blood0");
		wall[11] = loader.getTexture("dhgWall/dirty0");
		wall[12] = loader.getTexture("dhgWall/dirty1");
		wall[13] = loader.getTexture("dhgWall/dirty2");
		wall[14] = loader.getTexture("brkWall0/tiny");
		wall[15] = loader.getTexture("brkWall0/huge");
		wall[16] = loader.getTexture("brkWall0/big");
		wall[17] = loader.getTexture("checker/blackwhite/huge0");
		wall[18] = loader.getTexture("checker/blackwhite/huge1");
		wall[19] = loader.getTexture("checker/redwhite/huge");
		flat = wall.clone();
		return new Tileset(game, tileClasses, new Object[256][0], wall, flat);
	}
	
	public static InputStream exampleLevel() {
		return null;
	}

}
