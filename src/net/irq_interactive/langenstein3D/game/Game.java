package net.irq_interactive.langenstein3D.game;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;

import net.irq_interactive.langenstein3D.GameConstants;
import net.irq_interactive.langenstein3D.MiscUtil;
import net.irq_interactive.langenstein3D.game.things.Thing;

/**
 * A Game, e.g. a single round. You know what i mean.
 * 
 * @author Wuerfel_21
 *
 */
public class Game {

	protected final LinkedList<Thing> things;
	protected final int[][] map0;
	protected final long[][] map1, map2;
	protected final int centrx, centry, minX, minY, maxX, maxY;

	public static Game newGame(InputStream level) throws IOException {
		DataInput in = MiscUtil.makeDataInput(level);
		if (in.readInt() != GameConstants.MAGICNUM) throw new IOException("Invalid level file!");
		int version = in.readUnsignedShort();
		if (version != 0) throw new IOException("Unknown format version " + MiscUtil.makeHexString(version, 4));
		int xsize = in.readUnsignedShort();
		int ysize = in.readUnsignedShort();
		int centrx = in.readUnsignedShort();
		int centry = in.readUnsignedShort();
		int[][] map0 = new int[xsize][ysize];
		long[][] map1 = new long[xsize][ysize], map2 = new long[xsize][ysize];

		for (int y = 0; y < ysize; y++)
			for (int x = 0; x < xsize; x++) {
				int flatTex = in.readUnsignedByte();
				int light = in.readUnsignedByte();
				int tile = in.readUnsignedByte();
				int wallTex = in.readUnsignedByte();
				int bit1 = in.readUnsignedByte();
				int bit2 = in.readUnsignedByte();
				int meta1 = in.readUnsignedShort();
				int meta2 = in.readUnsignedShort();
				int tag = in.readUnsignedByte() & 0x7F;
				map0[x][y] = (flatTex) | (light << 8) | (tile << 16) | (tag << 24) | ((bit1 & 0x80) << 31);
				map1[x][y] = (bit2 >>> 7) | ((bit1 & 0x7F) << 1) | (wallTex << 8) | (meta1 << 32) | (meta1 << 32) | (meta2 << 48);
				map2[x][y] = (flatTex) | (light << 8) | (tile << 16) | ((bit1 & 0x80) << 17) | ((bit2 & 0x80) << 18) | ((bit1 & 0x7F) << 26)
						| ((bit2 & 0x7F) << 33) | (wallTex << 40) | (meta1 << 48);
			}

		return new Game(map0, map1, map2, centrx, centry, xsize, ysize);
	}

	public Game(int[][] map0, long[][] map1, long[][] map2, int centrx, int centry, int xsize, int ysize) {
		// TODO: Stub
		things = new LinkedList<>();
		this.map0 = map0;
		this.map1 = map1;
		this.map2 = map2;
		this.centrx = centrx;
		this.centry = centry;
		this.minX = -centrx;
		this.minY = -centry;
		this.maxX = xsize - centrx - 1;
		this.maxY = ysize - centry - 1;

	}

	public int transformX(int x) {
		return x + centrx;
	}

	public int transformY(int y) {
		return y + centry;
	}

	public int getMap0(int x, int y) {
		return map0[transformX(x)][transformY(y)];
	}

	public long getMap1(int x, int y) {
		return map1[transformX(x)][transformY(y)];
	}

	public void setMap0(int x, int y, int m0) {
		map0[transformX(x)][transformY(y)] = m0;
	}

	public void setMap1(int x, int y, long m1) {
		map1[transformX(x)][transformY(y)] = m1;
	}

	public long getMap2(int x, int y) {
		return map2[transformX(x)][transformY(y)];
	}

	public int getWallTex(int x, int y) {
		return (int) ((getMap1(x, y) >>> 8) & 0xFF);
	}

	public int getFlatTex(int x, int y) {
		return getMap0(x, y) & 0xFF;
	}

	protected static int cnt = 0;
	protected static final SecureRandom secureRandom;
	protected static final Random auxRandom;

	static {
		secureRandom = new SecureRandom();
		auxRandom = new Random(secureRandom.nextLong());
	}

	protected static long getNewGameID() {
		return ((System.currentTimeMillis() >>> 16) & 0x0FFFFFFFFl) | (((long) cnt & 0x0FFFFl) << 32) | ((int) secureRandom.nextInt(0xFFFF) << 48);
	}

}
