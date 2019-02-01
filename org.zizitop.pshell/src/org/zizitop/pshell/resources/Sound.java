package org.zizitop.pshell.resources;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.ResourceManager;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;

/**
 * Class for easy sound playing.
 * @author Zizitop
 *
 */
public abstract class Sound implements ResourceManager {
	public enum SoundType {
		MenuSound(1f), Effects(1f), Music(1f);
		
		private float typeVolume;
		
		SoundType(float typeVolume) {
			this.typeVolume = typeVolume;
		}
		
		public float getTypeVolume() {
			return typeVolume;
		}
		
		public void setTypeVolume(float newVolume) {
			this.typeVolume = newVolume;
		}
	}

	public abstract void play();

	public abstract void stop();

	public abstract void updateVolume();

	private static SoundEngine currentSoundEngine = ShellApplication.getPlatform().getSoundEngine();

	public static double getMainVolume() {
		return currentSoundEngine.getMainVolume();
	}
	
	public static void setMainVolume(double newVolume) {
		currentSoundEngine.setMainVolume(newVolume);
	}

	static Sound loadSound(String path) throws FileLoadingException {
		return currentSoundEngine.loadSound(path);
	}

	static Sound getNullSound() {
		return currentSoundEngine.getNullSound();
	}
}
