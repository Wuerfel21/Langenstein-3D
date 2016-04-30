/**
 * 
 */
package net.wuerfel21.langenstein3D.game;

import static net.wuerfel21.langenstein3D.game.render.Caster.texSize;
import static net.wuerfel21.langenstein3D.game.render.Caster.texWrapBit;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.wuerfel21.langenstein3D.game.render.Texture;;

/**
 * @author Wuerfel_21
 *
 */
public class Loader {
	protected static final long CACHE_SIZE = 100; // TODO: Add config
	public final LoadingCache<String, Texture> textureCache;
	private CacheBuilder<Object, Object> builder;

	public class TextureLoader extends CacheLoader<String, Texture> {

		@Override
		public Texture load(String key) throws Exception {
			InputStream in = null,meta = null;
			for (String format : textureExtensions) { // Try multiple formats
				in = getClass().getResourceAsStream("/assets/internal/textures/" + key + format); // TODO: Load external/localized data
				if (in != null) {
					meta = getClass().getResourceAsStream("/assets/internal/textures/" + key + ".meta" + format); //Load meta texture if possible
					break;
				}
			}
			if (in == null) throw new FileNotFoundException("No texture found for name " + key + " !!!");
			BufferedImage img = ImageIO.read(in);
			if (img.getType() != BufferedImage.TYPE_BYTE_INDEXED || img.getHeight() != texSize || img.getWidth() != texSize)
				throw new IOException("Invalid texture!");
			DataBuffer buf = img.getRaster().getDataBuffer();
			byte[] src = ((DataBufferByte) buf).getData();
			byte[][] texData = new byte[texSize][texSize];
			for (int x = 0; x < texSize; x++) {
				for (int y = 0; y < texSize; y++) {
					texData[x][y] = src[x + (y << texWrapBit)];
				}
			}
			
			Texture tex;
			
			if (meta != null) {
				BufferedImage metaImg = ImageIO.read(meta);
				if (metaImg.getType() != BufferedImage.TYPE_BYTE_INDEXED || metaImg.getHeight() != texSize || metaImg.getWidth() != texSize)
					throw new IOException("Invalid texture!");
				DataBuffer metaBuf = metaImg.getRaster().getDataBuffer();
				byte[] metaSrc = ((DataBufferByte) metaBuf).getData();
				boolean[][] metaTexData = new boolean[texSize][texSize];
				for (int x = 0; x < texSize; x++) {
					for (int y = 0; y < texSize; y++) {
						metaTexData[x][y] = metaSrc[x + (y << texWrapBit)] >= 2; //Not black or transparent is true
					}
				}
				tex = new Texture(texData, true, false, metaTexData);
			} else {
				tex = new Texture(texData, true, false);
			}
			return tex;
		}

	}

	public Loader() {
		builder = CacheBuilder.newBuilder().softValues().maximumSize(CACHE_SIZE);
		textureCache = builder.build(new TextureLoader());

	}

	public Texture getTexture(String name) {
		try {
			return textureCache.get(name);
		} catch (ExecutionException e) {
			System.err.println("Texture loading error: " + e.toString());
			return null;
		}
	}

	private static final String[] textureExtensions = { ".png", ".bmp", ".gif" };

}
