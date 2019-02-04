package org.zizitop.pshell.window;

import java.util.Arrays;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

/**
 * 
 * @author Zizitop
 *
 */
public class TestScene implements Scene {

	private static DDFont font = DDFont.DEFAULT_FONT;
	private static Bitmap test = ResourceLoader.getInstance().getTexture("test").getSubImage(32, 32, 32, 32);
	
	private double x, y;
	
	@Override
	public void tick(Window w) {
		x = w.getMouseX();
		y = w.getMouseY();
	}

	@Override
	public void draw(Bitmap canvas, DisplayMode displayMode) {
		Arrays.fill(canvas.pixels, 0);
		test.draw(canvas, x, y, 128, 128);
		font.draw(canvas, x / canvas.width, y / canvas.height, "Hello World!", -1);
	}

}
