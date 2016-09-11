package net.irq_interactive.langenstein3D.game.render;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.*;
import javax.swing.*;

import net.irq_interactive.langenstein3D.FixedPoint;
import net.irq_interactive.langenstein3D.game.Input;
import net.irq_interactive.langenstein3D.game.Int2D;
import net.irq_interactive.langenstein3D.game.Loader;
import net.irq_interactive.langenstein3D.game.Position;
import net.irq_interactive.langenstein3D.game.Vector;
import net.irq_interactive.langenstein3D.game.Input.Keys;
import net.irq_interactive.langenstein3D.game.render.VisSprite.ZComparator;

import javax.imageio.ImageIO;
import static java.lang.Math.sqrt;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.abs;
import static java.lang.Math.PI;
import static java.lang.System.out;
import static net.irq_interactive.langenstein3D.FixedPoint.FIXMULTI;

/**
 * This class handles raycasting and misc rendering. Currently it also contains game logic, but this will be moved out later.
 * 
 * @author Wuerfel_21
 */
public class Caster {
	public enum CasterState {
		PLAYING, QUIT
	}

	/**
	 * Enum of Super Digital Differential Analysis algorithm states.
	 * 
	 * @author Wuerfel_21
	 *
	 */
	protected enum SDDAState {
		REGULAR, THINWALL
	}

	public static final int texWrapBit = 6;
	public static final int texFixShift = -(texWrapBit - 16);
	public static final int texSize = 1 << texWrapBit;
	public static final int texMask = texSize - 1;

	public static final double PI2 = 2 * PI;

	protected CasterState state;
	protected RenderWindow screen;
	protected Dimension dim;
	protected IndexColorModel playpal;
	protected byte[][] lightMap, transMap, fogMap, redMap, xorMap, additiveMap, subtractiveMap, multiplyMap, hueshiftMap, desarurateMap;
	protected byte[] negativeMap, grayscaleMap, redscaleMap;
	public Input input;
	protected BufferedImage cursorImg;
	public Cursor blankCursor;
	public JLabel fpsLabel;
	public JLabel maxZLabel;
	protected double frameTime;

	protected double[] camXLookup;
	protected BufferedImage bufferImg;
	protected Raster bufferRaster;
	protected byte[] buffer;
	protected Graphics2D graph;
	protected double[] floorDist;
	protected double[] zBuf;
	protected double maxZ;
	protected static final ZComparator zComparator = new VisSprite.ZComparator();

	public byte[][][] textures;

	protected Position pos; // player position
	protected long time, oldTime, frame = 0;

	protected Vector dir; // camera vector
	protected Vector plane; // camera plane
	public static final double planeLength = 0.66; // length of the camera plane

	protected List<VisSprite> vissprites;

	public Caster(int width, int height, boolean fullscreen) {
		dim = new Dimension(width, height);
		fpsLabel = new JLabel();
		maxZLabel = new JLabel();
		screen = new RenderWindow(dim, fullscreen);
		// screen.steuerungNord.add(fpsLabel); TODO fix this
		// screen.steuerungNord.add(maxZLabel);
		state = CasterState.PLAYING;
		pos = new Position(22, 12);
		dir = new Vector(-1, 0);
		plane = new Vector(0, planeLength);
		playpal = Palette.get();
		lightMap = Palette.getLightMap();
		transMap = Palette.getTransMap();
		fogMap = Palette.getFogMap();
		redMap = Palette.getRedMap();
		xorMap = Palette.getXORMap();
		additiveMap = Palette.getAdditiveMap();
		subtractiveMap = Palette.getSubtractiveMap();
		multiplyMap = Palette.getMultiplyMap();
		hueshiftMap = Palette.getHueshiftMap();
		desarurateMap = Palette.getDesaturateMap();
		negativeMap = Palette.getNegativeMap();
		grayscaleMap = Palette.getGrayscaleMap();
		redscaleMap = Palette.getRedscaleMap();

		// Initialize screen column to camera-relative X coordinate LUT
		camXLookup = new double[dim.width];
		for (int x = 0; x < dim.width; x++) {
			camXLookup[x] = 2 * x / (double) dim.width - 1;
		}

		// Initialize screen buffer
		bufferImg = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_BYTE_INDEXED, playpal);
		bufferRaster = bufferImg.getRaster();
		buffer = ((DataBufferByte) bufferRaster.getDataBuffer()).getData();
		graph = bufferImg.createGraphics();

		// Initialize VisSprite list
		vissprites = new ArrayList<VisSprite>(64); // 64 should be enough to not cause immense lag on level start

		// Initialize Inputs
		input = new Input();
		screen.addKeyListener(input); // TODO: Move all this somewhere else
		screen.canvas.addKeyListener(input);
		screen.canvas.addMouseMotionListener(input);

		// Generate Textures
		textures = new byte[17][texSize][texSize];
		for (int x = 0; x < texSize; x++) {
			for (int y = 0; y < texSize; y++) {
				textures[0][x][y] = (byte) 0;
				// textures[2][x][y] = (byte) ((x != y && x != texSize - y) ? 9 : 1);
				textures[3][x][y] = (byte) max(1, x + (y * texSize));
				textures[4][x][y] = (byte) (209 + (y >> 2));
				// textures[5][x][y] = (byte) max(1, x + y);
				textures[7][x][y] = (byte) ((x & y) != 0 ? 1 : 15);
			}
		}
		Loader loader = Loader.getInternalloader(); // TODO: Get a proper Loader

		textures[1] = loader.getTexture("dhgWall/clean").data;
		textures[2] = loader.getTexture("brkWall0/plate").data;
		textures[5] = loader.getTexture("brkWall0/normal").data;
		textures[6] = loader.getTexture("carpet/0").data;
		textures[8] = loader.getTexture("dhgWall/cross").data;
		textures[9] = loader.getTexture("dhgWall/crossBlood").data;
		textures[10] = loader.getTexture("dhgWall/blood0").data;
		textures[11] = loader.getTexture("dhgWall/dirty0").data;
		textures[12] = loader.getTexture("dhgWall/dirty1").data;
		textures[13] = loader.getTexture("dhgWall/dirty2").data;
		textures[14] = loader.getTexture("brkWall0/tiny").data;
		textures[15] = loader.getTexture("brkWall0/huge").data;
		textures[16] = loader.getTexture("brkWall0/big").data;

		// Hide mouse cursor
		// Transparent 16 x 16 pixel cursor image.
		cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		screen.canvas.setCursor(blankCursor);

		testSprites = new Sprite[] {
				new Sprite(loader.getTexture("carpet/damaged"), false, 255, redscaleMap, null, FIXMULTI, FIXMULTI / 2, -FIXMULTI / 2, 0, 1),
				new Sprite(loader.getTexture("test"), false, 255, null, transMap, FIXMULTI * 2, FIXMULTI, 0, 0, 1),
				new Sprite(loader.getTexture("test3"), true, 255, null, null, FIXMULTI, FIXMULTI, 0, 0, 1),
				new Sprite(
						new Texture[] { loader.getTexture("x/0"), loader.getTexture("x/1"), loader.getTexture("x/2"), loader.getTexture("x/3"),
								loader.getTexture("x/4"), loader.getTexture("x/5"), loader.getTexture("x/6"), loader.getTexture("x/7") },
						true, 255, null, additiveMap, FIXMULTI, FIXMULTI, 0, 1, 4) };
		testSpritesPos = new Position[] { new Position(10, 10.75), new Position(2.3, 4.5), new Position(2.5, 10.5), new Position(20, 10.5) };
	}

	public boolean doQuit() {
		return state == CasterState.QUIT;
	}

	public void run() {
		switch (state) {
		case PLAYING:
			cast();
			logic();
			frame++;
			break;
		case QUIT:
			// keeiiiihhhl mmmeeeeeeeeh!!! BRAAAAINS!!!
			break;
		}
	}

	public void cast() {
		// Due to floor rendering, the buffer doesn't need to be cleared.
		/*
		 * for (int i=0;i<buffer.length;i++) { buffer[i]=1; }
		 */
		final Position rayPos = new Position();
		final Int2D mapPos = new Int2D(); // which box of the map we're in
		// final Int2D step = new Int2D();
		// final Vector rayDir = new Vector();
		final Vector sideDist = new Vector(); // length of ray from current
												// position to next x or y-side
		// final Position floorWall = new Position();
		// final Int2D floorTex = new Int2D();
		// final Position currentFloor = new Position();
		final int w = dim.width;
		final int h = dim.height;
		maxZ = 0;

		if (floorDist == null || floorDist.length != (h >> 1) + 1) {
			// Precalculate floor distance
			double[] floorDistProto = new double[(h >> 1) + 1];
			for (int y = h >> 1; y <= h; y++) {
				floorDistProto[y - (h >> 1)] = h / (2.0 * y - h + 2);
			}
			this.floorDist = floorDistProto;
		}
		final double[] floorDist = this.floorDist;
		if (zBuf == null || zBuf.length != w) {
			zBuf = new double[w];
		}

		for (int x = 0; x < w; x++) { // Iterate over screen columns
			// Variable definitions
			int stepX, stepY; // what direction to step in x or y-direction
								// (either +1 or -1)
			double currentFloorX, currentFloorY;
			final double camX = camXLookup[x]; // X of the current column
												// relative to the camera
			rayPos.set(pos);
			final double rayDirX = dir.x + plane.x * camX;
			final double rayDirY = dir.y + plane.y * camX;
			mapPos.set(rayPos);
			double perpWallDist = 1; // Initialize to not annoy the compiler
			boolean hit = false;
			// far
			boolean side = false; // true: North/South side false: East/West
									// side
			double floorWallX, floorWallY; // x, y position of the floor texel
											// at the bottom of the wall

			// length of ray from one x or y-side to next x or y-side
			final double deltaDistX = sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			final double deltaDistY = sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));

			byte[][] texture = null; // Initialize to not annoy the compiler

			// calculate step and initial sideDist
			if (rayDirX < 0) {
				stepX = -1;
				sideDist.x = (rayPos.x - mapPos.x) * deltaDistX;
			} else {
				stepX = 1;
				sideDist.x = (mapPos.x + 1.0 - rayPos.x) * deltaDistX;
			}
			if (rayDirY < 0) {
				stepY = -1;
				sideDist.y = (rayPos.y - mapPos.y) * deltaDistY;
			} else {
				stepY = 1;
				sideDist.y = (mapPos.y + 1.0 - rayPos.y) * deltaDistY;
			}

			// perform DDA (Digital Differential Analysis)
			do {
				// jump to next map square, OR in x-direction, OR in y-direction
				if (sideDist.x < sideDist.y) {
					sideDist.x += deltaDistX;
					mapPos.x += stepX;
					side = false;
				} else {
					sideDist.y += deltaDistY;
					mapPos.y += stepY;
					side = true;
				}
				// Check if ray has hit a wall
				int m = map[mapPos.x][mapPos.y];
				if (m != 0) {
					hit = true;
					texture = textures[m % textures.length];
					// Calculate distance projected on camera direction (oblique
					// distance will give fisheye effect!)
					if (side)
						perpWallDist = (mapPos.y - rayPos.y + (1 - stepY) / 2) / rayDirY;

					else
						perpWallDist = (mapPos.x - rayPos.x + (1 - stepX) / 2) / rayDirX;

				}
			} while (!hit);

			// Calculate height of line to draw on screen
			int lineHeight = (int) (h / perpWallDist);

			// calculate lowest and highest pixel to fill in current stripe
			int drawStartUnclipped = (-lineHeight >> 1) + (h >> 1);
			int drawStart = drawStartUnclipped < 0 ? 0 : drawStartUnclipped;
			int drawEnd = (lineHeight >> 1) + (h >> 1);
			if (drawEnd >= h)
				drawEnd = h - 1;
			int drawLength = drawEnd - drawStart + 1;

			// calculate value of wallX
			double wallX; // where exactly the wall was hit
			if (side == false)
				wallX = rayPos.y + perpWallDist * rayDirY;
			else
				wallX = rayPos.x + perpWallDist * rayDirX;
			wallX -= Math.floor((wallX));

			// x coordinate on the texture
			int texX = (int) (wallX * (double) (texSize));
			if (side == false && rayDirX > 0)
				texX = texSize - texX - 1;
			if (side == true && rayDirY < 0)
				texX = texSize - texX - 1;

			// texture pixels per screen pixel
			int texRatio = ((int) ((texSize / (double) lineHeight) * 0x10000)) - 1;
			int texPos = drawStartUnclipped < 0 ? texRatio * (-drawStartUnclipped) : 0;

			// draw the wall
			// byte color = wallColors[hit%wallColors.length][side?1:0];
			int drawPointer = x + (drawStart * w);
			int left = drawLength;
			int light = max(0, min(255, (int) (((255 / perpWallDist) * 5) / (side ? 1.75 : 1)))); // TODO: Improve lighting algorithm
			while (left > 0) {
				buffer[drawPointer] = lightMap[light][(int) (texture[texX][((texPos) >> 16) & texMask]) & 0xFF];
				drawPointer += w;
				texPos += texRatio;
				left--;
			}

			// Write to Z-Buffer and find max distance
			zBuf[x] = perpWallDist;
			maxZ = max(maxZ, perpWallDist);

			// FLOOR CASTING BEGINS HERE

			// 4 different wall directions possible
			if (!side && rayDirX > 0) {
				floorWallX = mapPos.x;
				floorWallY = mapPos.y + wallX;
			} else if (!side && rayDirX < 0) {
				floorWallX = mapPos.x + 1;
				floorWallY = mapPos.y + wallX;
			} else if (side && rayDirY > 0) {
				floorWallX = mapPos.x + wallX;
				floorWallY = mapPos.y;
			} else {
				floorWallX = mapPos.x + wallX;
				floorWallY = mapPos.y + 1;
			}

			double distWall, currentDist;

			distWall = perpWallDist;

			// draw the floor from drawEnd to the bottom of the screen
			if (drawEnd != h - 1 && drawStart != 0) {
				for (int y = (drawEnd < 0 ? h : drawEnd); y < h; y++) {
					currentDist = floorDist[y - (h >> 1)];

					double weight = (currentDist / distWall);

					currentFloorX = weight * floorWallX + (1.0 - weight) * pos.x;
					currentFloorY = weight * floorWallY + (1.0 - weight) * pos.y;
					// final int floorTexture = (((int)currentFloorX +
					// (int)currentFloorY)&1)!=0?1:2;
					final int floorTexture = (map[(int) currentFloorX][(int) currentFloorY] - 2) & 0x7;

					final int floorTexX = (int) (currentFloorX * texSize) & texMask;
					final int floorTexY = (int) (currentFloorY * texSize) & texMask;

					// lighting
					light = min(255, (int) (((255 / currentDist) * 5)));
					// floor
					buffer[x + (y * w)] = lightMap[light][(int) (textures[floorTexture][floorTexX][floorTexY]) & 0xFF];
					// ceiling (symmetrical!)
					buffer[x + ((h - y - 1) * w)] = lightMap[light][(int) (textures[2][floorTexX][floorTexY]) & 0xFF];
				}
			}
		}
		// Background finished, phew!

		// SPRITE CASTING
		// get all sprites and generate distance, clipping away sprites guaranteed to be invisible
		final double invDet = 1.0 / (plane.x * dir.y - dir.x * plane.y); // required for correct matrix multiplication (moved outside of loop because constant)
		vissprites.clear();
		for (int i = 0; i < testSprites.length; i++) { // TODO: Get useful sprites here
			Sprite spr = testSprites[i];
			// Translate sprite position to camera space
			double relX = testSpritesPos[i].x - pos.x;
			double relY = testSpritesPos[i].y - pos.y;

			// I don't even pretend to know what is going on here:
			// transform sprite with the inverse camera matrix
			// @formatter:off
		    // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
		    // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
		    // [ planeY   dirY ]                                          [ -planeY  planeX ]
			// @formatter:on

			double transformY = invDet * (-plane.y * relX + plane.x * relY); // this is actually the depth inside the screen, that what Z is in 3D
			if (transformY <= 0 || transformY > maxZ) continue; // clip n' snip
			double transformX = invDet * (dir.y * relX - dir.x * relY);

			int spriteScreenX = (int) ((w / 2) * (1 + transformX / transformY));
			// calculate height of the sprite on screen
			int spriteHeight = abs((int) ((h / (transformY)) * FixedPoint.fixToDouble(spr.scaleY))); // using "transformY" instead of the real distance prevents
																										// fisheye
			// calculate lowest and highest pixel to fill in current stripe
			int offsetY = (spr.offsetY * spriteHeight) >> 16;
			int drawStartY = (-spriteHeight / 2 + h / 2) + offsetY;
			// if (drawStartY < 0) drawStartY = 0;
			int drawEndY = (spriteHeight / 2 + h / 2) - offsetY;
			if (drawEndY >= h) drawEndY = h - 1;

			// calculate width of the sprite
			int spriteWidth = abs((int) (w * FixedPoint.fixToDouble(spr.scaleX) * (66.0 / 90.0) / (transformY))); // TODO: Make this better
			int drawStartX = -spriteWidth / 2 + spriteScreenX;
			// if (drawStartX < 0) drawStartX = 0;
			int drawEndX = spriteWidth / 2 + spriteScreenX;
			if (drawEndX >= w) drawEndX = w - 1;

			int light = spr.staticLighting ? spr.staticLightLevel : max(0, min(255, (int) (((255 / transformY) * 5)))); // TODO: Improve lighting algorithm

			int rotation; // add half rotation to sprite angle.substarct player angle from sprite angle

			if (spr.rotations.length == 1) {
				rotation = 0;
			} else {
				double spriteAngle = 0;// ((frame % 320) / 320.0) * 2 * PI; // TODO: Use thing object here
				double playerAngle = Math.atan2(relY, relX);
				double relRotation = spriteAngle + playerAngle;
				while (relRotation < 0)
					relRotation += PI2;
				// rotation = (((int) Math.floor(((relRotation / (-2 * PI)) *
				// spr.repeatRotations) * spr.rotations.length)) % spr.rotations.length + spr.rotations.length) % spr.rotations.length;

				rotation = (int) Math.round(relRotation / (PI2) * spr.rotations.length * spr.repeatRotations) % spr.rotations.length;
			}

			vissprites.add(new VisSprite(spr, transformY, drawStartX, drawStartY, spriteWidth, spriteHeight, rotation, light));
		}
		// Sort sprites
		vissprites.sort(zComparator);

		// Render Sprites
		for (VisSprite v : vissprites) {
			final int texRatio = ((int) ((texSize / (double) v.height) * 0x10000)) - 1;
			final int texRatioInv = ((int) ((v.height / (double) texSize) * 0x10000)) - 1;
			final int endX = v.x + v.width;
			final double dist = v.dist;
			final Texture tex = v.spr.rotations[v.rotation];
			int renderType;
			final int light = v.light;
			byte[] coltab1d = null, coltab2d[] = null; // This line is close to the epitome of syntactic sugar
			int metatype = v.spr.metatextureMode;
			if (light == 255) { // fullbight
				renderType = 0b1;
			} else {
				renderType = 0b0;
			}
			if (v.spr.colorTable1d != null) {
				renderType |= 0b10;
				coltab1d = v.spr.colorTable1d;
			}
			if (v.spr.colorTable2d != null) {
				renderType |= 0b100;
				coltab2d = v.spr.colorTable2d;
			}
			renderType |= metatype << 4; // One unused bit
			final byte[][] texData = tex.data;
			final byte[][] metaData = tex.metaData;
			for (int x = v.x; x < endX; x++) {
				// PLEASE, I BEG YOU, DO NOT CHANGE THIS! IT TOOK AGES TO FIGURE OUT HOW TO HAVE STARTROWS NOT MESS UP!!!!!!!
				final int texX = (int) (256 * (x - v.x) * texSize / v.width) / 256; // TODO: Is the 256 stuff required here?
				if (x >= w || texX > tex.stopColumn) break;
				if (x < 0 || texX < tex.startColumn || zBuf[x] < dist) continue;
				int spriteTop = v.y + ((tex.startRows[texX] * texRatioInv) >> 16);
				int missing = spriteTop < 0 ? -spriteTop : 0;
				int screenStart = spriteTop + missing;
				int texPos = (tex.startRows[texX] << 16) + (missing * texRatio);
				int stop = (tex.stopRows[texX] << 16) + 0x10000;
				int drawPointer = x + (screenStart * w);
				switch (renderType) {
				default:
					throw new IndexOutOfBoundsException("Invalid render type: " + Integer.toHexString(renderType));
				case 0x00: // Lighted, no color tables
					while (texPos < stop && drawPointer < buffer.length) {
						int texel = (texData[texX][((texPos) >> 16) & texMask]) & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = lightMap[light][texel];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x01: // Fullbright, no color tables
				case 0x11: // Fullbright, no color tables, metamode 1(useless)
				case 0x31: // Fullbright, no color tables, metamode 3(useless)
					while (texPos < stop && drawPointer < buffer.length) {
						byte texel = texData[texX][((texPos) >> 16) & texMask];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x02: // Lighted, 1d color table
					while (texPos < stop && drawPointer < buffer.length) {
						byte texel = lightMap[light][(int) coltab1d[(int) (texData[texX][((texPos) >> 16) & texMask]) & 0xFF] & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x03: // Fullbright, 1d color table
				case 0x33: // Fullbright, 1d color table, metamode 3(useless)
					while (texPos < stop && drawPointer < buffer.length) {
						byte texel = coltab1d[(texData[texX][((texPos) >> 16) & texMask]) & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x04: // Lighted, 2d color table
					while (texPos < stop && drawPointer < buffer.length) {
						int texel = lightMap[light][(int) (texData[texX][((texPos) >> 16) & texMask]) & 0xFF] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x05: // Fullbright, 2d color table
				case 0x35: // Fullbright, 2d color table, metamode 3(useless)
					while (texPos < stop && drawPointer < buffer.length) {
						int texel = texData[texX][((texPos) >> 16) & texMask] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x06: // Lighted, both color tables.
					while (texPos < stop && drawPointer < buffer.length) {
						int texel = lightMap[light][(int) coltab1d[(int) (texData[texX][((texPos) >> 16) & texMask]) & 0xFF] & 0xFF] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x07: // Fullbright, both color tables
				case 0x37: // Fullbright, both color tables, metamode 3(useless)
					while (texPos < stop && drawPointer < buffer.length) {
						int texel = (int) coltab1d[(int) (texData[texX][((texPos) >> 16) & texMask]) & 0xFF] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x10: // Lighted, no color tables, metamode 1
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte texel = (metaData[texX][texY] & 0x80) != 0 ? texData[texX][texY] : lightMap[light][(int) (texData[texX][texY]) & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x12: // Lighted, 1d color table, metamode 1
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte meta = metaData[texX][texY];
						byte texel = (meta & 0x20) == 0 ? texData[texX][texY] : coltab1d[(int) (texData[texX][texY]) & 0xFF];
						if ((meta & 0x80) == 0)
							texel = lightMap[light][(int) texel % 0xFF];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x13: // Fullbright, 1d color table, metamode 1
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte texel = (metaData[texX][texY] & 0x20) == 0 ? texData[texX][texY] : coltab1d[(int) (texData[texX][texY]) & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = texel;

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x14: // Lighted, 2d color table, metamode 1
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte meta = metaData[texX][texY];
						int texel = (meta & 0x80) != 0 ? texData[texX][texY] : lightMap[light][(int) (texData[texX][texY]) & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = (meta & 0x40) == 0 ? (byte) texel : coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x15: // Fullbright, 2d color table, metamode 1
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte meta = metaData[texX][texY];
						byte texel = texData[texX][texY];
						if (texel != 0)
							buffer[drawPointer] = (meta & 0x40) == 0 ? texel : coltab2d[(int) texel & 0xFF][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x16: // Lighted, both color tables, metamode 1. This is madness. Or sparta. Depends on how you view it.
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte meta = metaData[texX][texY];
						byte texel = (meta & 0x20) == 0 ? texData[texX][texY] : coltab1d[(int) (texData[texX][texY]) & 0xFF];
						if ((meta & 0x80) == 0)
							texel = lightMap[light][(int) texel % 0xFF];
						if (texel != 0)
							buffer[drawPointer] = (meta & 0x40) == 0 ? texel : coltab2d[(int) texel & 0xFF][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x17: // Fullbright, both color tables.
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						byte meta = metaData[texX][texY];
						byte texel = (meta & 0x20) == 0 ? texData[texX][texY] : coltab1d[(int) (texData[texX][texY]) & 0xFF];
						if (texel != 0)
							buffer[drawPointer] = (meta & 0x40) == 0 ? texel : coltab2d[(int) texel & 0xFF][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x30: // Lighted, no color tables, metamode 3
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						int texel = (texData[texX][texY]) & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = lightMap[min(light + (((int) metaData[texX][texY]) & 0xFF), 0xFF)][texel];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x32: // Lighted, 1d color table, metamode 3
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						int texel =  coltab1d[(int) (texData[texX][((texPos) >> 16) & texMask]) & 0xFF] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = lightMap[min(light + (((int) metaData[texX][texY]) & 0xFF), 0xFF)][texel];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				case 0x34: // Lighted, 2d color table, metamode 3
					while (texPos < stop && drawPointer < buffer.length) {
						int texY = ((texPos) >> 16) & texMask;
						int texel = lightMap[min(light + (((int) metaData[texX][texY]) & 0xFF), 0xFF)][(int) (texData[texX][texY]) & 0xFF] & 0xFF;
						if (texel != 0)
							buffer[drawPointer] = coltab2d[texel][buffer[drawPointer] & 0xFF];

						drawPointer += w;
						texPos += texRatio;
					}
					break;
				// TODO: Implement the other metamodes
				}

			}
		}

		// Done! Just some extra stuff left:
		oldTime = time;
		time = System.nanoTime();
		frameTime = (time - oldTime) / 1000000000.0;
		double fps = 1 / frameTime;
		// out.printf("Frame %d done! Time: %f FPS:
		// %f\n",frame++,frameTime,fps);

		/*
		 * From the Javadoc of nanoTime: "Differences in successive calls that span greater than approximately 292 years (2^63 nanoseconds) will not correctly
		 * compute elapsed time due to numerical overflow." The More You Know!
		 */
		// out.printf("W pressed? "+(input.states[Input.Keys.FORWARD.ordinal()]?"Yes":"No")+"%n");
		graph.setColor(Color.CYAN);
		graph.drawString(Double.toString(fps), 0, h);
		screen.showFrame(bufferImg);
		fpsLabel.setText("FPS: " + Double.toString(fps));
		maxZLabel.setText("Max Z: " + Double.toString(maxZ));
	}

	@SuppressWarnings("unused")
	public void logic() {
		// Game Logic
		final int w = dim.width;
		final int h = dim.height;
		// speed modifiers
		double moveSpeed = (1 / 35f)/* frameTime */ * 5.0; // the constant value is in
		// squares/second
		double rotSpeed = (1 / 35f)/* frameTime */ * 3.0; // the constant value is in
		// radians/second
		if (input.isPressed(Keys.SPRINT))
			moveSpeed *= 2;

		// move forward if no wall in front of you
		if (input.isPressed(Keys.FORWARD)) {
			if (map[(int) (pos.x + dir.x * moveSpeed)][(int) (pos.y)] == 0)
				pos.x += dir.x * moveSpeed;
			if (map[(int) (pos.x)][(int) (pos.y + dir.y * moveSpeed)] == 0)
				pos.y += dir.y * moveSpeed;
		}
		// move backwards if no wall behind you
		if (input.isPressed(Keys.BACKWARD)) {
			if (map[(int) (pos.x - dir.x * moveSpeed)][(int) (pos.y)] == 0)
				pos.x -= dir.x * moveSpeed;
			if (map[(int) (pos.x)][(int) (pos.y - dir.y * moveSpeed)] == 0)
				pos.y -= dir.y * moveSpeed;
		}

		if (input.isPressed(Keys.RIGHT)) {
			if (map[(int) (pos.x + dir.y * moveSpeed)][(int) (pos.y)] == 0)
				pos.x += dir.y * moveSpeed;
			if (map[(int) (pos.x)][(int) (pos.y - dir.x * moveSpeed)] == 0)
				pos.y -= dir.x * moveSpeed;
		}

		if (input.isPressed(Keys.LEFT)) {
			if (map[(int) (pos.x - dir.y * moveSpeed)][(int) (pos.y)] == 0)
				pos.x -= dir.y * moveSpeed;
			if (map[(int) (pos.x)][(int) (pos.y + dir.x * moveSpeed)] == 0)
				pos.y += dir.x * moveSpeed;
		}

		// Get mouse input
		Vector mouseRel = input.getMouseRelative();
		Int2D mouseAbs = input.getMouseAbsolute();
		// out.println("Mouse Relative! X:"+mouseRel.x+" Y:"+mouseRel.y+"\nMouse
		// Absolute! X: "+mouseAbs.x+" Y:"+mouseAbs.y);
		double rotAmount = -mouseRel.x / 100;
		// rotate to the right
		if (input.isPressed(Keys.TRIGHT))
			rotAmount -= rotSpeed;
		// rotate to the left
		if (input.isPressed(Keys.TLEFT))
			rotAmount += rotSpeed;
		if (rotAmount != 0) {
			// both camera direction and camera plane must be rotated
			double oldDirX = dir.x;
			dir.x = dir.x * cos(rotAmount) - dir.y * sin(rotAmount);
			dir.y = oldDirX * sin(rotAmount) + dir.y * cos(rotAmount);
			double oldPlaneX = plane.x;
			plane.x = plane.x * cos(rotAmount) - plane.y * sin(rotAmount);
			plane.y = oldPlaneX * sin(rotAmount) + plane.y * cos(rotAmount);
		}
		// Take Screenshot
		if (input.isScreenshot()) {
			try {
				ImageIO.write(bufferImg, "png",
						new File("screenshot " + Long.toHexString(System.currentTimeMillis()) + ".png"));
				out.println("Screenshot written!");
			} catch (Exception e) {
				out.println("Error writing screenshot: " + e.toString());
			}
		}

		// Exit?
		if (input.isPressed(Input.Keys.EXIT))
			state = CasterState.QUIT;

	}

	public void test() {
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				buffer[x + (y * dim.width)] = lightMap[255 - y][x];
				buffer[x + (y * dim.width) + 256] = transMap[x][y];
				buffer[x + ((y + 256) * dim.width)] = fogMap[255 - y][x];
				buffer[x + 256 + ((y + 256) * dim.width)] = redMap[255 - y][x];
				buffer[x + (y * dim.width) + 512] = xorMap[x][y];
				buffer[x + ((y + 256) * dim.width) + 512] = additiveMap[x][y];
				buffer[x + (y * dim.width) + 768] = subtractiveMap[x][y];
				buffer[x + ((y + 256) * dim.width) + 768] = multiplyMap[x][y];
				buffer[x + (y * dim.width) + 1024] = hueshiftMap[255 - y][x];
				buffer[x + ((y + 256) * dim.width) + 1024] = desarurateMap[255 - y][x];

				buffer[x + (512 * dim.width)] = negativeMap[x];
				buffer[x + (512 * dim.width) + 256] = grayscaleMap[x];
				buffer[x + (512 * dim.width) + 512] = redscaleMap[x];
			}
		}
		screen.showFrame(bufferImg);
	}

	/*
	 * private static final byte[][] wallColors = {//Light first, then Dark {0,0}, //Transparent, should be never used {9,6}, //Red {3,2}, //Green {5,4}, //Blue
	 * {15,14}, //white {11,10}, //yellow };
	 */

	// @formatter:off
	protected short[][] map = {
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,13},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 0, 0, 0, 0, 7, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 0, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1,11, 1, 1, 1, 1, 1, 1, 1, 8, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 5, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 5, 0, 0, 2, 0, 0, 0, 0, 5, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 1},
			{ 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 5, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,16,16,16,16,16,16, 1},
			{ 1, 0, 3, 0, 0, 0, 3, 4, 3, 0, 5, 0, 0, 0, 5, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1,16,16,16,16,16, 1},
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 5, 0, 0, 0, 5, 1, 9, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,14, 0, 0, 0, 0, 0, 2},
			{ 1, 0, 2, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,14, 0, 0, 0, 0, 0, 2},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 5, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 7},
			{ 1, 0, 1, 0, 0, 0, 0, 0, 5, 2, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,14, 0, 0, 0, 0, 0, 2},
			{ 1, 0, 0, 0, 0, 0, 0, 0, 5, 3, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,14, 0, 0, 0, 0, 0, 2},
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5, 1,10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,15,15,15,15,15, 2},
		};
	// @formatter:on

	private Sprite[] testSprites;
	private Position[] testSpritesPos;
}
