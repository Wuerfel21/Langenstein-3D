package net.irq_interactive.langenstein3D;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

public final class MiscUtil {

	private MiscUtil(){};
	
	public static DataInput makeDataInput(InputStream stream) {
		if (stream instanceof DataInput)
			return (DataInput) stream;
		else
			return new DataInputStream(stream);
	}
	
	
	public static String makeBinString(int i, int minbits) {
		return String.format("%" + minbits + "s", Integer.toBinaryString(i)).replace(' ', '0');
	}

	public static String makeHexString(int i, int minnibbles) {
		return String.format("%0" + minnibbles + "X", i);
	}
	
	/**
	 * Turns the first (up to) 64 bits of an boolean array into a long
	 */
	public static long toBitField(boolean[] arr) {
		long l=0;
		for (int i=0;i<Math.min(arr.length,64);i++) {
			l|=arr[i]?(1l<<i):0;
		}
		return l;
	}
}
