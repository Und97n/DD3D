package org.zizitop.pshell;

import org.zizitop.pshell.resources.SoundEngine;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.window.InputSource;
import org.zizitop.pshell.window.Window;

public interface Platform {
	/**
	 * Create an input source information from integer, that we hold in config file.
	 * @throws IllegalArgumentException if id is incorrect.
	 */
	InputSource inputSourceFromID(int id);

	Window createWindow(String name, String upperRightText, int windowWidth, int windowHeight,
	                    org.zizitop.pshell.window.DisplayMode dm, boolean fullscreen);

	int getUserScreenWidth();
	int getUserScreenHeight();

	SoundEngine getSoundEngine();

	/**
	 * Make a sound
	 */
	void beep();

	/**
	 * Load bitmap from graphics file.
	 * Supported formats: JPEG, PNG, GIF, BMP, WBMP
	 *
	 * @param filePath - path to graphics file
	 * @return bitmap with hraphics data.
	 * @throws FileLoadingException if problems with file loading
	 */
	Bitmap loadImage(String filePath) throws FileLoadingException;

	/**
	 * Show small window with message for user.
	 */
	void showMessageDialog(String message);

}
