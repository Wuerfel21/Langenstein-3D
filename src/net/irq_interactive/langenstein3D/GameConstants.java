/**
 * 
 */
package net.irq_interactive.langenstein3D;

import net.irq_interactive.langenstein3D.derived.Version;

/**
 * @author Wuerfel_21
 *
 */
public final class GameConstants {
	
	public static final String GAME = "Langenstein 3D";
	public static final String VERSION = Version.version;
	public static final boolean dirty = VERSION.endsWith("-dirty");
	public static final long VERSION_HASH = Version.hash;
	public static final int MAGICNUM = 0x22022001;

	private GameConstants() {}
	
}
