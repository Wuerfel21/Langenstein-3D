/**
 * 
 */
package net.irq_interactive.langenstein3D;

/**
 * @author Wuerfel_21
 *
 */
public final strictfp class FixedPoint {

	public static final int FIXMULTI = 0x10000;
	public static final double FIXMULTIfp = 0x10000;
	public static final int FIXDIV = 1;
	public static final double FIXDIVfp = 1 / FIXMULTIfp;

	public static final int MAX_VALUE = 0x7FFFFFFF;

	public static final int intToFix(int i) {
		return i << 16;
	}

	public static final int fixToInt(int f) {
		return f >> 16;
	}

	public static final double fixToDouble(int f) {
		return f / FIXMULTIfp;
	}

	public static final int doubleToFix(double d) {
		return (int) (d * FIXMULTI);
	}

	/**
	 * WARNING: results >= 65536 will overflow!!!
	 */
	public static final int fixmult(int f1, int f2) {
		return (int) (((long) f1 * (long) f2) >> 16);
	}

	public static final long fixmultL(int f1, int f2) {
		return ((long) f1 * (long) f2) >> 16;
	}

	/**
	 * Divides f1 by f2, e.g. f1/f2.
	 */
	public static final int fixdiv(int f1, int f2) {
		return (int) ((((long) f1) << 16) / f2);
	}

	public static final int fixSqrt(int x) {
		int testDiv;
		int root = 0; /* Clear root */
		int remHi = 0; /* Clear high part of partial remainder */
		int remLo = x; /* Get argument into low part of partial remainder */
		int count = 23; /* Load loop counter */
		do {
			remHi = (remHi << 2) | (remLo >>> 30);
			remLo <<= 2; /* get 2 bits of arg */
			root <<= 1; /* Get ready for the next bit in the root */
			testDiv = (root << 1) + 1; /* Test radical */
			if (remHi >= testDiv) {
				remHi -= testDiv;
				root++;
			}
		} while (count-- != 0);
		return (root);
	}

	// Object wrapper code:

	private final int x;

	public FixedPoint(int x) {
		this.x = x;
	}

	public int get() {
		return x;
	}

}
