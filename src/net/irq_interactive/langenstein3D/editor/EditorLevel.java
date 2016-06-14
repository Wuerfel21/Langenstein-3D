/**
 * 
 */
package net.irq_interactive.langenstein3D.editor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.google.common.io.LittleEndianDataOutputStream;

import net.irq_interactive.langenstein3D.game.GameConstants;

/**
 * @author Wuerfel_21
 *
 */
public class EditorLevel {

	protected int xsize, ysize;

	public Tile[][] map;

	public static class Tile {
		public byte flatTex, wallTex, light, tileType, misc1, misc2, tag;
		public short meta;
		public boolean statLight, selfLight;

		public Tile(byte flatTex, byte wallTex, byte light, byte tileType, byte misc1, byte misc2, short meta, boolean statLight, boolean selfLight, byte tag) {
			this.flatTex = flatTex;
			this.wallTex = wallTex;
			this.light = light;
			this.tileType = tileType;
			this.misc1 = misc1;
			this.misc2 = misc2;
			this.meta = meta;
			this.statLight = statLight;
			this.selfLight = selfLight;
			this.tag = tag;
		}

		public void save(DataOutput out) throws IOException {
			out.writeByte(flatTex);
			out.writeByte(light);
			out.writeByte(tileType);
			out.writeByte(wallTex);
			out.writeByte((misc1 & 0x7F) | (statLight ? 0x80 : 0));
			out.writeByte((misc2 & 0x7F) | (selfLight ? 0x80 : 0));
			out.writeShort(meta);
			out.writeByte(tag);
		}

		public static Tile load(DataInput in) throws IOException {
			byte flatTex = in.readByte();
			byte light = in.readByte();
			byte tileType = in.readByte();
			byte wallTex = in.readByte();
			byte bit1 = in.readByte();
			byte misc1 = (byte) (bit1 & 0x7F);
			boolean statLight = (bit1 & 0x80) != 0;
			byte bit2 = in.readByte();
			byte misc2 = (byte) (bit1 & 0x7F);
			boolean selfLight = (bit2 & 0x80) != 0;
			short meta = in.readShort();
			byte tag = in.readByte();
			return new Tile(flatTex, wallTex, light, tileType, misc1, misc2, meta, statLight, selfLight, tag);
		}
	}

	public EditorLevel(int xsize, int ysize, Tile[][] map, boolean copy) {
		this.xsize = xsize & 0xFFFF;
		this.ysize = ysize & 0xFFFF;
		if (copy) {
			this.map = new Tile[this.xsize][];
			for (int i = 0; i < this.xsize; i++) {
				this.map[i] = Arrays.copyOf(map[i], this.ysize);
			}
		} else this.map = map;
	}

	public int getXsize() {
		return xsize;
	}

	public void setXsize(int xsize) {
		this.xsize = (int) xsize & 0xFFFF; // TODO: Resizing code here
	}

	public int getYsize() {
		return ysize;
	}

	public void setYsize(int ysize) {
		this.ysize = (int) ysize & 0xFFFF; // TODO: Resizing code here
	}

	public void saveToStream(OutputStream out) throws IOException {
		save(new LittleEndianDataOutputStream(out));
	}

	public void save(DataOutput out) throws IOException {
		out.writeInt(GameConstants.MAGICNUM); // Magic Number
		out.writeShort(0); // Format version
		out.writeShort(xsize);
		out.writeShort(ysize);
		for (int y = 0; y < ysize; y++) {
			for (int x = 0; x < xsize; x++) {
				map[x][y].save(out);
			}
		}
	}

	public static EditorLevel load(DataInput in) throws IOException {
		int magic = in.readInt();
		if (magic != GameConstants.MAGICNUM) throw new IOException("Invalid magic number: " + Integer.toHexString(magic));
		short version = in.readShort();
		if (version != GameConstants.MAGICNUM) throw new IOException("Unknown format version: " + Integer.toHexString(version));
		int xsize = (int) in.readShort() & 0xFFFF;
		int ysize = (int) in.readShort() & 0xFFFF;
		Tile[][] map = new Tile[xsize][ysize];
		for (int y = 0; y < ysize; y++)
			for (int x = 0; x < xsize; x++)
				map[x][y] = Tile.load(in);
		return new EditorLevel(xsize, ysize, map, false);
	}

}
