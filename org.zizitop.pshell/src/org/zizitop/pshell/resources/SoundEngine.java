package org.zizitop.pshell.resources;

import org.zizitop.pshell.utils.exceptions.FileLoadingException;

/**
 * 
 * @author Zizitop
 *
 */
public interface SoundEngine {
	void updateVolume(Sound.SoundType updated);

	double getMainVolume();
	void setMainVolume(double newVolume);

	Sound loadSound(String path) throws FileLoadingException;
	Sound getNullSound();
}
