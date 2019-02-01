package org.zizitop.pshell.resources;

import org.zizitop.pshell.Platform;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.ResourceManager;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class for easy {@link Bitmap} loading. Just run {@link #load()} method and you have all resources in memory.
 * <br><br>
 * Created 18.03.2018 1:04
 *
 * @author Zizitop
 */
public class ResourceLoader implements ResourceManager {
	public static final String TEXTURES_DIRECTORY = "res/textures", SOUNDS_DIRECTORY = "res/sfx";
	public static final String TEXTURES_EXTENSION = "png", SOUNDS_EXTENSION = "wav";

	private static ResourceLoader instance;

	private Map<String, Bitmap> textures = new HashMap<>();
	private Map<String, Sound> sounds = new HashMap<>();

	private final Bitmap nullTexture;
	private final Sound nullSound;

	static {
		// Create a null texture
		int[] nullTextureData = new int[64 * 64];

		for(int i = 0; i < 64 * 64; ++i) {
			if((i % 64) < 32) {
				if((i / 64) < 32) {
					nullTextureData[i] = 0xFF_00_00_00;
				} else {
					//Not 0xFF_FF_00_FF, because 0xFF_FF_00_FF - transparent color
					nullTextureData[i] = 0xFF_FB_00_FF;
				}
			} else {
				if((i / 64) < 32) {
					nullTextureData[i] = 0xFF_FB_00_FF;
				} else {
					nullTextureData[i] = 0xFF_00_00_00;
				}
			}
		}

		instance = new ResourceLoader(new Bitmap(nullTextureData, 64, 64), Sound.getNullSound());
	}

	private ResourceLoader(Bitmap nullTexture, Sound nullSound) {
		this.nullTexture = nullTexture;
		this.nullSound = nullSound;

		ShellApplication.addResourceManager(this);
	}

	public Bitmap getTexture(String identifier) {
		if(identifier.equals("null")) {
			return nullTexture;
		}

		Bitmap tx = textures.get(identifier);

		if(tx == null) {
			try {
				String path = TEXTURES_DIRECTORY + "/" + identifier.replace('.', '/') + "." + TEXTURES_EXTENSION;

				Bitmap txx = ShellApplication.getPlatform().loadImage(path);

				textures.put(identifier, txx);

				Log.write("Texture \"" + identifier + "\" loaded successfully.", 0);

				return txx;
			} catch (FileLoadingException e) {
				Log.write("Texture \"" + identifier + "\" not found.", 2);
				Log.writeException(e);
				return nullTexture;
			}
		} else {
			return tx;
		}
	}

	public Sound getSound(String identifier) {
		Sound s = sounds.get(identifier);

		if(s == null) {
			try {
				String path = SOUNDS_DIRECTORY + "/" + identifier.replace('.', '/') + "." + SOUNDS_EXTENSION;

				Sound sx = Sound.loadSound(path);

				sounds.put(identifier, sx);

				Log.write("Sound \"" + identifier + "\" loaded successfully.", 0);

				return sx;
			} catch (FileLoadingException e) {
				Log.write("Sound \"" + identifier + "\" not found.", 2);
				Log.writeException(e);
				return nullSound;
			}
		} else {
			return s;
		}
	}

	/**
	 * Load ALL media. Not effective. Now we dynamically load data when it is needed.
	 */
	@Deprecated
	public static void load() throws FileLoadingException {
		instance.loadTextures(TEXTURES_DIRECTORY, "");
		instance.loadSounds(SOUNDS_DIRECTORY, "");
	}

	private void loadTextures(String mainPath, String path) throws FileLoadingException {
		File file = new File(mainPath + path);

		if(file.isDirectory()) {
			String[] fln = file.list();

			for(int i = 0; i < fln.length; ++i) {
				loadTextures(mainPath, path + "/" + fln[i]);
			}
		} else if(Utils.getFileExtension(file).equals(TEXTURES_EXTENSION)) {
			String identifier = path.substring(1, path.indexOf('.')).replace('/', '.');

			textures.put(identifier, ShellApplication.getPlatform().loadImage(mainPath + path));

			Log.write("Texture \"" + identifier + "\" loaded successfully.", 0);
		}
	}

	private void loadSounds(String mainPath, String path) throws FileLoadingException {
		File file = new File(mainPath + path);

		if(file.isDirectory()) {
			String[] fln = file.list();

			for(int i = 0; i < fln.length; ++i) {
				loadSounds(mainPath, path + "/" + fln[i]);
			}
		} else if(Utils.getFileExtension(file).equals(SOUNDS_EXTENSION)) {
			String identifier = path.substring(1, path.indexOf('.')).replace('/', '.');

			sounds.put(identifier, Sound.loadSound(mainPath + path));
		}
	}

	public static ResourceLoader getInstance() {
		if(instance == null) {
			throw new IllegalStateException("Resource loader is not initialized now.");
		} else {
			return instance;
		}
	}

	@Override
	public void freeResources() {
//		Iterator it = textures.entrySet().iterator();
//
//		while(it.hasNext()) {
//			Map.Entry<String, Bitmap> pair = (Map.Entry)it.next();
//
//			pair.getValue().();
//		}

		textures.clear();

		Iterator is = sounds.entrySet().iterator();

		while(is.hasNext()) {
			Map.Entry<String, Sound> pair = (Map.Entry)is.next();

			pair.getValue().freeResources();
		}

		sounds.clear();
	}
}
