package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

/**
 * 
 * @author Zizitop
 *
 */
public class EmptyScene implements Scene {

	public static final EmptyScene emptyScene = new EmptyScene();
	
	private EmptyScene() {}
	
	@Override
	public void tick(Window w) {}

	@Override
	public void draw(Bitmap canvas, DisplayMode displayMode) {
		canvas.fillRect_SC(0x0, 0, 0, 1, 1);
		DDFont.DEFAULT_FONT.draw(canvas, 0.5, 0.5, "Empty Scene", -1);
	}

}
