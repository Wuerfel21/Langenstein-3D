/**
 * 
 */
package net.irq_interactive.langenstein3D.game.render;

import static net.irq_interactive.langenstein3D.game.render.Caster.texFixShift;
import static net.irq_interactive.langenstein3D.game.render.Caster.texMask;
import static net.irq_interactive.langenstein3D.game.render.Caster.texSize;
import static net.irq_interactive.langenstein3D.game.render.Caster.texWrapBit;

import java.util.Arrays;

/**
 * Represents a 64x64 texture in 8-bit indexed, column-primary format; Includes data for optimized sprite drawing.
 * 
 * @author Wuerfel_21
 *
 */
@SuppressWarnings("unused")
public class Texture {
	/**
	 * Holds the texture data
	 */
	public final byte[][] data;
	/**
	 * Holds the metatexture data
	 */
	public final byte[][] metaData;
	/**
	 * First non-transparent column
	 */
	public final int startColumn;
	/**
	 * Last non-transparent column
	 */
	public final int stopColumn;
	/**
	 * First non-transparent pixel in each column
	 */
	public final int[] startRows;
	/**
	 * Last non-transparent pixel in each column
	 */
	public final int[] stopRows;

	/**
	 * Creates a new Texture object with a completely transparent(meta false) image and default start/stop data.
	 */
	public Texture() {
		this(new byte[texSize][texSize], false, false);
	}

	/**
	 * Creates a new Texture object from the provided array. The passed array is copied and can be used otherwise.
	 * The metatexture is completely false.
	 * 
	 * @param data
	 */
	public Texture(byte[][] data) {
		this(data, true, true);
	}
	
	public Texture(byte[][] data, boolean genStartStop, boolean copyData) {
		this(data,genStartStop,copyData,new byte[texSize][texSize]);
	}

	/**
	 * Creates a new Texture object from the provided array. Also, spaghetti code alert.
	 * 
	 * @param data
	 * @param genStartStop
	 *            whether to generate optimized start/stop info or use defaults
	 * @param copyData
	 *            whether to copy the data array or use the passed instance
	 * @param metaData
	 */
	public Texture(byte[][] data, boolean genStartStop, boolean copyData, byte[][] metaData) {
		this.data = copyData ? data.clone() : data;
		this.metaData = copyData ? metaData.clone() : metaData;
		startRows = new int[texSize];
		stopRows = new int[texSize];
		done:
		if (genStartStop) {
			int i = 0;
			boolean foundColumnStart = false;
			// Count fully transparent columns
			start:
			for (; i < texSize; i++) {
				for (int j = 0; j < texSize; j++) {
					if (data[i][j] != 0) {
						foundColumnStart = true;
						break start;
					}
				}
			}
			if (foundColumnStart) {
				startColumn = i;
			} else { // Completely empty! skip further processing.
				startColumn = 0;
				stopColumn = 0;
				break done;
			}
			// Generate row data and keep track of the last non-transparent column
			boolean wasColumnGap = true;
			int columnGapStart = i - 1;
			for (; i < texSize; i++) {
				int j = 0;
				int rowStart = 0;
				int rowGapStart = 0;
				boolean wasRowGap = true;
				boolean foundRowStart = false;
				for (; j < texSize; j++) { // find first
					if (data[i][j] != 0) {
						if (!foundRowStart) {
							rowStart = j;
							foundRowStart = true;
						}
						wasRowGap = false;
					} else {
						if (!wasRowGap) {
							wasRowGap = true;
							rowGapStart = j;
						}
					}
				}

				if (!foundRowStart) { // gap!
					startRows[i] = 0;
					stopRows[i] = 0;
					if (!wasColumnGap) {
						wasColumnGap = true;
						columnGapStart = i;
					}
				} else {
					startRows[i] = rowStart;
					stopRows[i] = wasRowGap ? rowGapStart : texMask;
					wasColumnGap = false;
				}
			}
			stopColumn = wasColumnGap ? columnGapStart : texMask;
		} else {
			startColumn = 0;
			stopColumn = texSize;
			Arrays.fill(startRows, 0);
			Arrays.fill(stopRows, texSize);
		}
	}

}
