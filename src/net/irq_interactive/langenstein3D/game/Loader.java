/**
 * 
 */
package net.irq_interactive.langenstein3D.game;

import static net.irq_interactive.langenstein3D.game.render.Caster.texSize;
import static net.irq_interactive.langenstein3D.game.render.Caster.texWrapBit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.irq_interactive.langenstein3D.game.render.Texture;

/**
 * @author Wuerfel_21
 *
 */
public class Loader {
	protected static final long CACHE_SIZE = 100; // TODO: Add config
	protected LoadingCache<String, Texture> textureCache;
	protected LoadingCache<String, Sequence> musicCache;
	protected CacheBuilder<Object, Object> softBuilder;
	protected final ArrayList<ResourceSource> sources;
	protected static List<Image> icons;
	protected static final Loader internalLoader;

	public static abstract class ResourceSource {

		/**
		 * Gets the resource at a specific path in this source
		 * 
		 * @return The InputStream for reading the resource at the specified path or null if there is no resource at that path
		 */
		public abstract InputStream get(String path);

	}
	
	public static class InternalSource extends ResourceSource {

		@Override
		public InputStream get(String path) {
			return Loader.class.getResourceAsStream(path);
		}
		
	}

	public class TextureLoader extends CacheLoader<String, Texture> {

		@Override
		public Texture load(String key) throws Exception {
			InputStream in = null, meta = null;
			for (String format : textureExtensions) { // Try multiple formats
				in = get("/assets/internal/textures/" + key + format); // TODO: Load external/localized data
				if (in != null) {
					meta = get("/assets/internal/textures/" + key + ".meta" + format); // Load meta texture if possible
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
				byte[][] metaTexData = new byte[texSize][texSize];
				for (int x = 0; x < texSize; x++) {
					for (int y = 0; y < texSize; y++) {
						metaTexData[x][y] = metaSrc[x + (y << texWrapBit)];
					}
				}
				tex = new Texture(texData, true, false, metaTexData);
			} else {
				tex = new Texture(texData, true, false);
			}
			return tex;
		}

	}

	public class MusicLoader extends CacheLoader<String, Sequence> {

		@Override
		public Sequence load(String key) throws Exception {
			InputStream in = get("/assets/internal/music/" + key + ".mid"); // TODO: Load external/localized data
			if (in == null) throw new FileNotFoundException("No song found for name " + key + " !!!");
			return MidiSystem.getSequence(in);
		}
	}

	public Loader(List<ResourceSource> sources) {
		this.sources = new ArrayList<>(sources);
		softBuilder = CacheBuilder.newBuilder().softValues().maximumSize(CACHE_SIZE);
		textureCache = softBuilder.build(new TextureLoader());
		musicCache = softBuilder.build(new MusicLoader());
	}
	
	static {
		ArrayList<ResourceSource> intern = new ArrayList<>(1);
		intern.add(new InternalSource());
		internalLoader = new Loader(intern);
	}

	public Texture getTexture(String name) {
		try {
			return textureCache.get(name);
		} catch (ExecutionException e) {
			System.err.println("Texture loading error: " + e.toString());
			return null;
		}
	}

	public Sequence getSong(String name) {
		try {
			return musicCache.get(name);
		} catch (ExecutionException e) {
			System.err.println("Music loading error: " + e.toString());
			return null;
		}
	}

	private static final String[] textureExtensions = { ".png", ".bmp", ".gif" };

	/**
	 * Returns an InputStream for loading a resource. NOT CACHED!
	 * 
	 * @param path
	 *            the path of the resource (in the virtual/JAR file system)
	 * @return the input stream of the resource at the specified path, or null
	 */
	public InputStream get(String path) {
		InputStream in = null;
		for (ResourceSource src:sources) {
			in = src.get(path);
			if (in!=null) break;
		}
		return in;
	}
	
	public static Loader getInternalloader() {
		return internalLoader;
	}

	/**
	 * Returns (and loads if nercassy) the game icons (currently the IRQ logo). TODO: Make an Icon specific to the game.
	 * TODO 2: Load Icons from external files, somehow.
	 * 
	 * @return a list of icons of varying sizes
	 */
	public static List<Image> getIcons() {
		if (icons != null) return icons;
		List<Image> list = new ArrayList<>();
		Loader loader = getInternalloader();
		try {
			list.add(ImageIO.read(loader.get("/assets/internal/irq_tiny.png")));
			list.add(ImageIO.read(loader.get("/assets/internal/irq_micro.png")));
			list.add(ImageIO.read(loader.get("/assets/internal/irq_mini.png")));
			list.add(ImageIO.read(loader.get("/assets/internal/irq_square.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		icons = list;
		return list;
	}

}
