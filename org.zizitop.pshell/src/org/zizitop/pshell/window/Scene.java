package org.zizitop.pshell.window;

import org.zizitop.pshell.DefaultContext;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.controlling.Context;

/**
 * Use this class for content filling on the screen
 * @author Zizitop
 *
 */
public interface Scene {
	
	/**
	 * Proceed input, update physics.
	 * @param w - window
	 */
	void tick(Window w);
	
	/**
	 * Draw content on the screen
	 * @param canvas - draw your content here
	 */
	void draw(Bitmap canvas);

	default boolean lockMouse() {
		return false;
	}
	
	/**
	 * Get custom cursor id for this scene
	 * '0' for default, '-1' for transparent
	 * @return
	 */
	default int getCustomCursor() {
		return 0;
	}

	default Context[] getContext() {
		return Utils.asArray(DefaultContext.defaultContext);
	}
}
