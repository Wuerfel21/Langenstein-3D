package net.wuerfel21.langenstein3D.render;

import java.awt.image.*;
import java.util.Arrays;
import static java.lang.Math.min;

/**
 * Palette Definition, ported from a previous attempt of 3D. In Allegro format e.g. 6 bits per channel.
 * 
 * @author Wuerfel_21
 */
public abstract class Palette {
	private static IndexColorModel playpal = null;
	private static byte[][] lightMap, transMap, fogMap, redMap, xorMap, additiveMap, subtractiveMap, multiplyMap, hueshiftMap, desarurateMap;
	private static byte[] negativeMap, grayscaleMap, redscaleMap;
	private static byte[] rampMap;
	private static int[] col_diff;
	
	public static final float hue256th = 360f/256f;

	public static IndexColorModel get() {
		if (playpal == null) {
			playpal = convert();
		}
		return playpal;
	}

	private static IndexColorModel convert() {
		int[] pal = new int[256];
		for (int i = 0; i < 256; i++) {
			byte[] col = playpal_vga[i];
			pal[i] = (col[2] << 2) | ((col[2] >> 4) & 0x3) | (col[1] << 10) | ((col[1] << 4) & 0x300) | (col[0] << 18)
					| ((col[0] << 12) & 0x30000);
		}
		return new IndexColorModel(8, 256, pal, 0, false, 0, DataBuffer.TYPE_BYTE);
	}

	public static byte[][] getLightMap() {
		if (lightMap == null) {
			lightMap = generateFadeTable(0, 0, 0, lighting_discourage);
		}
		return lightMap;
	}

	public static byte[][] getTransMap() {
		if (transMap == null) {
			transMap = generateTransTable(127, 127, 127);
		}
		return transMap;
	}

	public static byte[][] getFogMap() {
		if (fogMap == null) {
			fogMap = generateFadeTable(52, 52, 52, fog_discourage);
		}
		return fogMap;
	}

	public static byte[][] getRedMap() {
		if (redMap == null) {
			redMap = generateFadeTable(63, 0, 0, red_discourage);
		}
		return redMap;
	}

	public static byte[][] getXORMap() {
		if (xorMap == null) {
			xorMap = generateXORTable2d();
		}
		return xorMap;
	}

	public static byte[][] getAdditiveMap() {
		if (additiveMap == null) {
			additiveMap = generateAdditiveTable();
		}
		return additiveMap;
	}

	public static byte[][] getMultiplyMap() {
		if (multiplyMap == null) {
			multiplyMap = generateMultiplyTable();
		}
		return multiplyMap;
	}

	public static byte[][] getSubtractiveMap() {
		if (subtractiveMap == null) {
			subtractiveMap = generateSubtractiveTable();
		}
		return subtractiveMap;
	}
	
	public static byte[][] getHueshiftMap() {
		if (hueshiftMap == null) {
			hueshiftMap = generateHueshiftTable2d();
		}
		return hueshiftMap;
	}
	
	public static byte[][] getDesaturateMap() {
		if (desarurateMap == null) {
			desarurateMap = generateDesaturateTable();
		}
		return desarurateMap;
	}

	public static byte[] getNegativeMap() {
		if (negativeMap == null) {
			negativeMap = generateXORTable1d(63, 63, 63);
		}
		return negativeMap;
	}

	public static byte[] getGrayscaleMap() {
		if (grayscaleMap == null) {
			grayscaleMap = generateGrayscaleTable();
		}
		return grayscaleMap;
	}

	public static byte[] getRedscaleMap() {
		if (redscaleMap == null) {
			redscaleMap = generateRedscaleTable();
		}
		return redscaleMap;
	}

	/**
	 * Extends a color component from 6 to 8 bit
	 */
	public static final int extendColor(int c) {
		return (c << 2) | (c >> 4);
	}

	/**
	 * Reduces a color component from 8 to 6 bit
	 */
	public static final int reduceColor(int c) {
		return c >>> 2;
	}

	/*
	 * * Stolen from Allegro!
	 */
	private static byte[][] generateFadeTable(int r, int g, int b, double[][] discourage) {
		byte[][] tab = new byte[256][256];
		int r1, g1, b1, r2, g2, b2, t1, t2;
		for (int x = 0; x < 255; x++) {
			t1 = x * 0x010101;
			t2 = 0xFFFFFF - t1;

			r1 = (1 << 23) + r * t2;
			g1 = (1 << 23) + g * t2;
			b1 = (1 << 23) + b * t2;

			tab[x][0] = 0;
			for (int y = 1; y < 256; y++) {
				r2 = (r1 + playpal_vga[y][0] * t1) >> 24;
				g2 = (g1 + playpal_vga[y][1] * t1) >> 24;
				b2 = (b1 + playpal_vga[y][2] * t1) >> 24;

				tab[x][y] = bestfit_color(r2, g2, b2, discourage[getRampMap()[y]]);
			}
		}
		for (int y = 0; y < 256; y++)
			tab[255][y] = (byte) y;
		return tab;
	}

	/*
	 * * Stolen from Allegro!
	 */
	private static byte[][] generateTransTable(int r, int g, int b) {
		int[] tmp = new int[768];
		int q; // dis be int pointer
		int x, y, i, j, k;
		byte[] p; // dis be byte pointer
		int tr, tg, tb;
		byte[][] table = new byte[256][256];

		/*
		 * This is a bit ugly, but accounts for the solidity parameters being in the range 0-255 rather than 0-256. Given that the precision of r,g,b components
		 * is only 6 bits it shouldn't do any harm.
		 */
		if (r > 128)
			r++;
		if (g > 128)
			g++;
		if (b > 128)
			b++;

		for (x = 0; x < 256; x++) {
			tmp[x * 3] = playpal_vga[x][0] * (256 - r) + 127;
			tmp[x * 3 + 1] = playpal_vga[x][1] * (256 - g) + 127;
			tmp[x * 3 + 2] = playpal_vga[x][2] * (256 - b) + 127;
		}

		for (x = 1; x < 256; x++) {
			i = playpal_vga[x][0] * r;
			j = playpal_vga[x][1] * g;
			k = playpal_vga[x][2] * b;

			p = table[x];
			q = 0;

			for (y = 0; y < 256; y++) {
				tr = (i + tmp[q++]) >> 8;
				tg = (j + tmp[q++]) >> 8;
				tb = (k + tmp[q++]) >> 8;
				p[y] = bestfit_color(tr, tg, tb, default_discourage);
			}
		}

		for (y = 0; y < 256; y++) {
			table[0][y] = (byte) y;
			table[y][y] = (byte) y;
		}

		return table;
	}
	
	private static byte[][] generateHueshiftTable2d() {
		byte[][] table = new byte[256][];
		
		for (int i=0;i<256;i++) {
			table[i] = generateHueshiftTable(hue256th*i);
		}
		
		return table;
	}

	private static byte[] generateHueshiftTable(float shift) {
		byte[] table = new byte[256];

		for (int x = 1; x < 256; x++) {
				float[] hsv = rgb_to_hsv(extendColor(playpal_vga[x][0]), extendColor(playpal_vga[x][1]), extendColor(playpal_vga[x][2]));
				int[] rgb = hsv_to_rgb(hsv[0] + shift, hsv[1], hsv[2]);
				table[x] = bestfit_color(reduceColor(rgb[0]), reduceColor(rgb[1]), reduceColor(rgb[2]), default_discourage);
		}
		return table;
	}

	
	private static byte[][] generateDesaturateTable() {
		byte[][] table = new byte[256][256];

		for (int i = 0; i < 256; i++) {
			table[255][i] = (byte) i;
			table[i][0] = 0;
		}
		for (int x = 0; x < 255; x++) {
			float fract = (1/256f) * x;
			for (int y = 1; y < 256; y++) {
				float[] hsv = rgb_to_hsv(extendColor(playpal_vga[y][0]), extendColor(playpal_vga[y][1]), extendColor(playpal_vga[y][2]));
				int[] rgb = hsv_to_rgb(hsv[0], hsv[1]*fract, hsv[2]);
				table[x][y] = bestfit_color(reduceColor(rgb[0]), reduceColor(rgb[1]), reduceColor(rgb[2]), default_discourage);
			}
		}
		return table;
	}
	
	private static byte[][] generateAdditiveTable() {
		byte[][] table = new byte[256][256];

		for (int y = 0; y < 256; y++) {
			table[0][y] = (byte) y;
		}
		for (int x = 1; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				int r = Math.min(63, playpal_vga[x][0] + playpal_vga[y][0]);
				int g = Math.min(63, playpal_vga[x][1] + playpal_vga[y][1]);
				int b = Math.min(63, playpal_vga[x][2] + playpal_vga[y][2]);
				table[x][y] = bestfit_color(r, g, b, default_discourage);
			}
		}
		return table;
	}

	private static byte[][] generateSubtractiveTable() {
		byte[][] table = new byte[256][256];

		for (int y = 0; y < 256; y++) {
			table[0][y] = (byte) y;
		}
		for (int x = 1; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				int r = Math.max(0, playpal_vga[y][0] - playpal_vga[x][0]);
				int g = Math.max(0, playpal_vga[y][1] - playpal_vga[x][1]);
				int b = Math.max(0, playpal_vga[y][2] - playpal_vga[x][2]);
				table[x][y] = bestfit_color(r, g, b, default_discourage);
			}
		}
		return table;
	}

	private static byte[][] generateMultiplyTable() {
		byte[][] table = new byte[256][256];

		for (int y = 0; y < 256; y++) {
			table[0][y] = (byte) y;
		}
		for (int x = 1; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				int r = (int) Math.min(63, (playpal_vga[x][0] / 63d) * (playpal_vga[y][0] / 63d) * 63);
				int g = (int) Math.min(63, (playpal_vga[x][1] / 63d) * (playpal_vga[y][1] / 63d) * 63);
				int b = (int) Math.min(63, (playpal_vga[x][2] / 63d) * (playpal_vga[y][2] / 63d) * 63);
				table[x][y] = bestfit_color(r, g, b, default_discourage);
			}
		}
		return table;
	}

	private static byte[][] generateXORTable2d() {
		byte[][] table = new byte[256][];

		table[0] = new byte[256];
		for (int i = 0; i < 256; i++) {
			table[0][i] = (byte) i;
		}

		for (int i = 1; i < 256; i++) {
			table[i] = generateXORTable1d(playpal_vga[i][0], playpal_vga[i][1], playpal_vga[i][2]);
		}

		return table;
	}

	private static byte[] generateXORTable1d(int r, int g, int b) {
		byte[] table = new byte[256];

		for (int i = 1; i < 256; i++) {
			table[i] = bestfit_color(r ^ playpal_vga[i][0], g ^ playpal_vga[i][1], b ^ playpal_vga[i][2], default_discourage);
		}

		return table;
	}

	private static byte[] generateGrayscaleTable() {
		byte[] table = new byte[256];

		for (int i = 1; i < 256; i++) {
			int gray = (int) (playpal_vga[i][0] * 0.2126 + playpal_vga[i][1] * 0.7152 + playpal_vga[i][2] * 0.0722);
			table[i] = bestfit_color(gray, gray, gray, grayscale_discourage);
		}
		return table;
	}

	private static byte[] generateRedscaleTable() {
		byte[] table = new byte[256];

		for (int i = 1; i < 256; i++) {
			int gray = (int) (playpal_vga[i][0] * 0.4252 + playpal_vga[i][1] * 1.4304 + playpal_vga[i][2] * 0.1444);
			int r = Math.min(63, gray);
			int gb = Math.max(0, gray - 63);
			table[i] = bestfit_color(r, gb, gb, redscale_discourage);
		}
		return table;
	}

	/*
	 * * Stolen from Allegro!
	 */
	private static byte bestfit_color(int r, int g, int b, double[] discourage) {
		if (col_diff == null) {// initialize lookup
			col_diff = new int[3 * 128];
			for (int i = 1; i < 64; i++) {
				int k = i * i;
				col_diff[0 + i] = col_diff[0 + 128 - i] = k * (59 * 59);
				col_diff[128 + i] = col_diff[128 + 128 - i] = k * (30 * 30);
				col_diff[256 + i] = col_diff[256 + 128 - i] = k * (11 * 11);
			}
		}
		byte bestfit = 0;
		double lowest = Integer.MAX_VALUE;
		for (int i = 1; i < 256; i++) {
			byte[] rgb = playpal_vga[i];
			double dis = discourage[getRampMap()[i]];
			dis *= dis * dis;
			// if ((r == g && r == b) && !(rgb[0] == rgb[1] && rgb[0] ==
			// rgb[2])) continue; //Keep greys gray!
			// if (rampMap[i] != ramp) continue; //keep color in same ramp
			double coldiff = col_diff[0 + ((rgb[1] - g) & 0x7F)] * dis;
			if (coldiff < lowest) {
				coldiff += col_diff[128 + ((rgb[0] - r) & 0x7F)] * dis;
				if (coldiff < lowest) {
					coldiff += col_diff[256 + ((rgb[2] - b) & 0x7F)] * dis;
					if (coldiff < lowest) {
						bestfit = (byte) i;
						if (coldiff == 0)
							return bestfit;
						lowest = coldiff;
					}
				}
			}
		}
		return bestfit;
	}

	public static byte[] getRampMap() {
		if (rampMap == null) {
			rampMap = generateRampMap();
		}
		return rampMap;
	}

	private static byte[] generateRampMap() {
		byte[] rm = new byte[256];
		for (int i = 0; i < playpal_ramps.length; i++) {
			for (int j = playpal_ramps[i][0]; j <= playpal_ramps[i][1]; j++) {
				rm[j] = (byte) i;
			}
		}
		return rm;
	}

	public static float[] rgb_to_hsv(byte[] rgb) {
		return rgb_to_hsv(rgb[0], rgb[1], rgb[2]);
	}

	/*
	 * rgb_to_hsv: Converts an RGB value into the HSV colorspace.
	 */
	public static float[] rgb_to_hsv(int r, int g, int b) {
		int delta;
		float h, s, v;

		if (r > g) {
			if (b > r) {
				/* b>r>g */
				delta = b - g;
				h = 240.0f + ((r - g) * 60) / (float) delta;
				s = (float) delta / (float) b;
				v = (float) b * (1.0f / 255.0f);

			} else {
				/* r>g and r>b */
				delta = r - min(g, b);
				h = ((g - b) * 60) / (float) delta;
				if (h < 0.0f)
					h += 360.0f;
				s = (float) delta / (float) r;
				v = (float) r * (1.0f / 255.0f);
			}
		} else {
			if (b > g) {
				/* b>g>=r */
				delta = b - r;
				h = 240.0f + ((r - g) * 60) / (float) delta;
				s = (float) delta / (float) b;
				v = (float) b * (1.0f / 255.0f);
			} else {
				/* g>=b and g>=r */
				delta = g - min(r, b);
				if (delta == 0) {
					h = 0.0f;
					if (g == 0)
						s = v = 0.0f;
					else {
						s = (float) delta / (float) g;
						v = (float) g * (1.0f / 255.0f);
					}
				} else {
					h = 120.0f + ((b - r) * 60) / (float) delta;
					s = (float) delta / (float) g;
					v = (float) g * (1.0f / 255.0f);
				}
			}
		}
		return new float[] { h, s, v };
	}

	public static int[] hsv_to_rgb(float h, float s, float v) {
		float f, x, y, z;
		int i, r, g, b;

		v *= 255.0f;

		if (s == 0.0f) { /* ok since we don't divide by s, and faster */
			r = g = b = (int) (v + 0.5f);
		} else {
			h = (h % 360.0f) / 60.0f;
			if (h < 0.0f)
				h += 6.0f;

			i = (int) h;
			f = h - i;
			x = v * s;
			y = x * f;
			v += 0.5f; /* round to the nearest integer below */
			z = v - x;

			switch (i) {

			default: // Added so rgb is always initialized
			case 6:
			case 0:
				r = (int) v;
				g = (int) (z + y);
				b = (int) z;
				break;

			case 1:
				r = (int) (v - y);
				g = (int) v;
				b = (int) z;
				break;

			case 2:
				r = (int) z;
				g = (int) v;
				b = (int) (z + y);
				break;

			case 3:
				r = (int) z;
				g = (int) (v - y);
				b = (int) v;
				break;

			case 4:
				r = (int) (z + y);
				g = (int) z;
				b = (int) v;
				break;

			case 5:
				r = (int) v;
				g = (int) z;
				b = (int) (v - y);
				break;
			}
		}
		return new int[] { r, g, b };
	}

	public static void findDupes() {
		for (int x = 0; x < 256; x++) {
			byte[] a = playpal_vga[x];
			for (int y = 0; y < 256; y++) {
				byte[] b = playpal_vga[y];
				if (x != y && Arrays.equals(a, b)) {
					System.out.println(
							"Color " + Integer.toString(x) + " is a duplicate of color " + Integer.toString(y) + "!");
				}
			}
		}
	}

	// @formatter:off

	public static final short[][] playpal_ramps = {
	        //first color, last color
	        {  0,  0}, //transparent
	        {  1,  8}, //very dark gray
	        {  7, 31}, //gray
	        { 32, 52}, //purple-ish
	        { 53, 68}, //saturated brown
	        { 69, 80}, //semi-saturated green
	        { 81, 88}, //fire ramp (will this ever be lighted, anyways?)
	        { 89,118}, //cosmic latte-yellow
	        {119,147}, //skin tones
	        {148,177}, //blue
	        {178,205}, //off-green
	        {206,234}, //red
	        {235,250}, //yellow-green-ish something
	        {251,255}, //cyan
	    };
	    
	    public static final double H = 10000; //Very high constant for use in the table below
	    
	    public static final double[] default_discourage = 
	    	{   H,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1};
	    
	    public static final double[] grayscale_discourage = 
	    	{   H,   1,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H};
	    
	    public static final double[] redscale_discourage = 
	    	{   H, 1.5,   H,   H,   H,   H,   1,   H,   H,   H,   H,   1,   H,   H};
	    
	    public static final double[][] lighting_discourage = {
	        //Color difference is multiplied by the cube of this
	        //Vertical: source range
	        //Horizontal: target range
	        {   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //transparent
	        {   H,   1,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //very dark gray
	        {   H, 1.5,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //gray
	        {   H, 1.5,  10,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //purple-ish
	        {   H, 1.5,  15,   H,   1,   H,   H,   5,   4,   H,   H,   H,   H,   H}, //saturated brown
	        {   H, 1.5,  15,   H,   H,   1,   H,   H,   H,   H,  40,   H,  50,   H}, //semi-saturated green
	        {   H, 1.5,   3,   2,   2,   2,   1, 1.2,1.75,  10,   3, 1.5,   2,   H}, //fire ramp (will this ever be lighted, anyways?)
	        {   H, 1.5,  19,   H,   H,   H,   3,   1,   H,   H,   H,   H,   H,   H}, //cosmic latte-yellow
	        {   H, 1.5,  19,   H,   8,  10,   5,   3,   1,   H,   H,   H,   H,   H}, //skin tones
	        {   H, 1.5,  30,   H,   H,   H,   H,   H,   H,   1,   H,   H,   H,   H}, //blue
	        {   H, 1.5,  23,   H,   H,   H,   H,   H,   H,   H,   1,   H,   H,1.75}, //off-green
	        {   H, 1.5,  10,   H,   H,   H,   3,   H,   H,   H,   H,   1,   H,   H}, //red
	        {   H, 1.5,  17,   H,   H,   3,   H,   H,   H,   H,   6,   H,   1,   H}, //yellow-green-ish something
	        {   H, 1.5,  15,   H,   H,   H,   H,   H,   H, 1.5,   H,   H,   H,   1}, //cyan
	    };
	    
	    public static final double[][] fog_discourage = {
	    	//Color difference is multiplied by the cube of this
		    //Vertical: source range
	    	//Horizontal: target range
		    {   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //transparent
		    {   H,   1,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //very dark gray
		    {   H,   H,   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //gray
		    {   H,   H, 1.8,   1,   H,   H,   H,   H,   H, 1.4,   H,   H,   H,   H}, //purple-ish
		    {   H,   H, 1.9,   H,   1,   H,   H,   H, 1.3,   H,   H,   H,   H,   H}, //saturated brown
		    {   H,   H, 1.5,   H,   H,   1,   H,   H,   H,   H, 1.3,   H, 1.3,   H}, //semi-saturated green
		    {   H,   H, 1.8,   H,   2,   H,   1, 1.3, 1.4,  10,   2, 1.3,   2,   H}, //fire ramp (will this ever be lighted, anyways?)
		    {   H,   H,   2,   H,   H,   H,   2,   1, 1.6,   H,   H,   H, 1.7,   H}, //cosmic latte-yellow
		    {   H,   H, 1.9,   H,   3,  10,   H, 1.7,   1,   H,   H,   H,   H,   H}, //skin tones
		    {   H,   H, 1.8,   H,   H,   H,   H,   H,   H,   1,   H,   H,   H,1.25}, //blue
		    {   H,   H, 1.8,   H,   H,   2,   H,   H,   H,   H,   1,   H,1.42,1.55}, //off-green
		    {   H,   H, 1.5,   H, 1.5,   H,   1,   H, 1.4,   H,   H,   1,   H,   H}, //red
		    {   H,   H, 1.8,   H,   H,   3,   H,   H,   H,   H, 1.5,   H,   1,   H}, //yellow-green-ish something TODO: continue from this line onwards
		    {   H,   H, 1.5,   H,   H,   H,   H,   H,   H, 1.1,   H,   H,   H,   1}, //cyan
		};
	    
	    public static final double[][] red_discourage = {
	    	//Color difference is multiplied by the cube of this
		    //Vertical: source range
	    	//Horizontal: target range
	    	{   1,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H,   H}, //transparent
		    {   H,   1,   1,   H,   H,   H, 1.2,   H,   H,   H,   H,   1,   H,   H}, //very dark gray
		    {   H, 1.2,   1,   H, 1.6,   H, 1.2,   H, 1.5,   H,   H, 1.1,   H,   H}, //gray
		    {   H, 1.5,   5,   1,   H,   H, 1.2,   2, 1.5, 1.8, 1.6, 1.1,   H,   H}, //purple-ish
		    {   H, 1.5,  10,   H,   1,   H, 1.2,   2,   2,   H,   H, 1.1,   H,   H}, //saturated brown
		    {   H, 1.5,  10,   H,   H,   1, 1.2,   2,   2,   H,  40, 1.1,  50,   H}, //semi-saturated green
		    {   H, 1.5,   3,   2,   2,   2,   1, 1.2,1.75,  10,   3, 1.1,   2,   H}, //fire ramp (will this ever be lighted, anyways?)
		    {   H, 1.5,  14,   H,   2,   H, 1.2,   1, 1.5,   H,   H, 1.1,   H,   H}, //cosmic latte-yellow
		    {   H, 1.5,  14,   H, 1.3,  10, 1.2,   3,   1,   H,   H, 1.1,   H,   H}, //skin tones
		    {   H, 1.5,  25, 1.3, 1.5,   H, 1.2,   H,1.65,   1,   H, 1.1,   H,   H}, //blue
		    {   H, 1.5,  18,   H,   H,   H, 1.2, 1.3, 1.4,   H,   1, 1.1,   H,1.75}, //off-green
		    {   H, 1.5,   5,   H,   H,   H, 1.3,   H,   H,   H,   H,   1,   H,   H}, //red
		    {   H, 1.5,  12,   H,   H,   3, 1.2,   H,   H,   H,   6, 1.1,   1,   H}, //yellow-green-ish something
		    {   H, 1.5,  10, 1.2,   H,   H, 1.2,   H,   H, 1.2,   H,   H,   H,   1}, //cyan
		};
	    
	    public static final byte[][] playpal_vga = {
	        { 0, 0, 0}, //transparent
	        //grey ramp
	        { 0, 0, 0}, //black
	        { 1, 1, 1},
	        { 2, 2, 2},
	        { 3, 3, 3},
	        { 4, 4, 4},
	        { 6, 6, 6},
	        { 8, 8, 8},
	        {13,13,13},
	        {14,14,14},
	        {17,17,17},
	        {21,21,21},
	        {23,23,23},
	        {25,25,25},
	        {27,27,27},
	        {29,29,29},
	        {30,30,30},
	        {34,34,34},
	        {36,36,36},
	        {38,38,38},
	        {40,40,40},
	        {42,42,42},
	        {44,44,44},
	        {46,46,46},
	        {50,50,50},
	        {52,52,52},
	        {55,55,55},
	        {57,57,57},
	        {59,59,59},
	        {60,60,60},
	        {62,62,62},
	        {63,63,63},
	        
	        //purple-ish ramp
	        { 0, 0, 1},
	        { 3, 1, 4},
	        { 5, 3, 7},
	        { 7, 4,10},
	        { 9, 5,13},
	        {11, 6,16},
	        {13, 8,19},
	        {15, 9,21},
	        {19,12,27},
	        {22,13,30},
	        //light part
	        {24,14,33},
	        {28,17,39},
	        {32,20,45},
	        {36,26,47},
	        {41,32,50},
	        {45,38,52},
	        {49,44,55},
	        {53,49,57},
	        //almost-white part
	        {56,52,59},
	        {58,55,60},
	        {60,58,61},
	        
	        //brown ramp
	        {45,23,10},
	        {41,21,10},
	        {38,21,10},
	        {35,20,10},
	        {32,18, 9},
	        {28,17, 9},
	        {25,15, 9},
	        {23,14, 9},
	        {21,14, 8},
	        {18,12, 8},
	        {15,11, 7},
	        {13,10, 7},
	        {11, 9, 6},
	        { 8, 6, 5},
	        { 6, 5, 4},
	        { 5, 4, 4},
	        
	        //green ramp
	        { 1, 2, 1},
	        { 3, 6, 3},
	        { 4, 9, 4},
	        { 6,12, 6},
	        { 7,15, 7},
	        {10,21,10},
	        {12,26,12},
	        {15,31,15},
	        {18,37,18},
	        {21,42,21},
	        {25,50,25},
	        {31,63,31},
	        
	        //Misc colors
	        {63,45, 5}, //Berkeley Gold
	        
	        //Heretic Flame Ramp
	        {63,55, 0},
	        {63,47, 0},
	        {63,39, 0},
	        {63,31, 0},
	        {63,23, 0},
	        {63,15, 0},
	        {61, 3, 1},
	        
	        //Yellow ramp (based on Cosmic Latte)
	        {63,63,62},
	        {63,62,57}, //cosmic latte
	        {63,60,53},
	        {63,59,49},
	        {63,58,45},
	        {63,57,40},
	        {63,55,36},
	        {63,54,32},
	        {63,53,28},
	        {63,52,23},
	        {63,50,19},
	        {63,49,15},
	        {63,48,11},
	        {63,47, 6},
	        {63,46, 2},
	        {62,44, 0},
	        {57,41, 0},
	        {53,38, 0},
	        {49,35, 0},
	        {45,32, 0},
	        {40,29, 0},
	        {36,26, 0},
	        {32,23, 0},
	        {28,20, 0},
	        {23,17, 0},
	        {19,14, 0},
	        {15,11, 0}, //It's more brown than dark yellow at this point, but whatever
	        {11, 7, 0},
	        { 6, 4, 0},
	        { 2, 1, 0},
	        
	        //Skin tones (base color(9D7660) is eyedropped from my favorite webcomic, can you guess which one?)
	        //The base color is actually a mid-dark-ish skin tone. I try to be politically correct with the diversity of my game's cast. Or have people who spend waaay to much time at the beach.
	        //Could also be used as Coffee (Damage floor maybe?)
	        {62,61,60},
	        {60,59,58},
	        {58,56,55},
	        {57,54,52},
	        {55,52,50},
	        {53,49,47},
	        {52,47,45},
	        {50,45,41},
	        {49,43,39},
	        {47,40,37},
	        {45,38,34},
	        {44,36,31},
	        {42,34,29},
	        {41,31,26},
	        {39,29,24}, //Base tone.
	        {36,27,22},
	        {34,25,20},
	        {31,23,19},
	        {28,21,17},
	        {26,19,16},
	        {23,17,14},
	        {20,15,12},
	        {18,13,11},
	        {15,11, 9},
	        {13, 9, 8},
	        {10, 7, 6},
	        { 7, 5, 4},
	        { 5, 3, 3},
	        { 2, 1, 1},
	        
	        //blue ramp(based on 2C75FF)
	        {62,62,63},
	        {57,59,63},
	        {53,57,63},
	        {49,54,63},
	        {45,51,63},
	        {40,48,63},
	        {36,46,63},
	        {32,43,63},
	        {28,40,63},
	        {23,37,63},
	        {19,34,63},
	        {15,32,63},
	        {11,29,63}, //base tone
	        { 6,26,63},
	        { 2,23,63},
	        { 0,21,62},
	        { 0,20,57},
	        { 0,18,53},
	        { 0,17,49},
	        { 0,15,45},
	        { 0,14,40},
	        { 0,12,36},
	        { 0,11,32},
	        { 0, 9,28},
	        { 0, 8,23},
	        { 0, 6,19},
	        { 0, 5,15},
	        { 0, 3,11},
	        { 0, 2, 6},
	        { 0, 1, 2},
	        
	        //Off green ramp (based on 6E7846)
	        //This is supposed to represent the color(s) of Castle Langensteins interior walls and window frames.
	        //The base color was gathered by finding a photograph of Castle Langenstein and using the eyedropper on it.
	        {60,60,58},
	        {58,59,56},
	        {57,57,53},
	        {55,56,50},
	        {53,54,48},
	        {51,53,45},
	        {49,51,42},
	        {48,50,40},
	        {46,48,37},
	        {44,46,34},
	        {42,45,32},
	        {40,43,29},
	        {38,42,26},
	        {37,40,24},
	        {34,38,22},
	        {32,35,20},
	        {30,32,19},
	        {27,30,17},
	        {25,27,16},
	        {22,24,14},
	        {20,22,12},
	        {17,19,11},
	        {15,16, 9},
	        {12,14, 8},
	        {10,11, 6},
	        { 7, 8, 5},
	        { 5, 5, 3},
	        { 3, 3, 1},
	        
	        //red ramp (based on 7D110C)
	        {63,56,56},
	        {62,52,52},
	        {62,49,48},
	        {62,45,44},
	        {61,41,40},
	        {61,37,36},
	        {60,34,33},
	        {60,30,29},
	        {60,26,25},
	        {59,23,21},
	        {59,19,17},
	        {59,15,13},
	        {58,11, 9},
	        {58, 8, 5},
	        {54, 7, 5},
	        {50, 7, 4},
	        {46, 6, 4},
	        {42, 5, 4},
	        {39, 5, 3},
	        {35, 4, 3},
	        {31, 4, 3}, //base tone
	        {27, 3, 2},
	        {23, 3, 2},
	        {19, 2, 2},
	        {15, 2, 1},
	        {12, 1, 1},
	        { 8, 1, 0},
	        { 4, 0, 1},
	        { 1, 0, 0},
	        
	        //green-yellow brige mini ramp (based on BCEC3E)
	        {57,62,46},
	        {55,61,38},
	        {51,60,27},
	        {48,59,27},
	        {47,59,19},
	        {44,58, 7},
	        {39,52, 5},
	        {33,44, 4},
	        {27,37, 3},
	        {22,29, 2},
	        {16,21, 2},
	        {13,17, 1},
	        {10,13, 1},
	        { 7, 9, 1},
	        { 4, 6, 0},
	        { 1, 2, 0},
	        
	        //Cyan extra (not recommended for lighted textures...)
	        { 0,15,15},
	        { 0,30,30},
	        { 0,45,45},
	        { 0,60,60},
	        {12,63,63},
	        
	    };
	// @formatter:on
}
