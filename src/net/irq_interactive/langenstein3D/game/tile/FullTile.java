package net.irq_interactive.langenstein3D.game.tile;

import net.irq_interactive.langenstein3D.game.Tileset;

public class FullTile extends Tile {

	public FullTile(Tileset parent,Object... params) {
		super(parent,params);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getSolid(boolean render, int x, int y, int granularity, int subx, int suby) {
		return 1;
	}

}
