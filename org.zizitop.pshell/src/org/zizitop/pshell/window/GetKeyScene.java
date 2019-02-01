package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

/**
 * Cool class to get pressed by user button
 * @author Zizitop
 *
 */
public class GetKeyScene implements Scene, Window.GetKeyResultReceiver {
	private static final String MESSAGE = "Please, press button";
	private static final DDFont FONT = DDFont.DEFAULT_FONT;
	
	private Bitmap background;
	private Scene previousScene;
	private Window.GetKeyResultReceiver reciever;
	
	private InputSource input = null;
	
	private GetKeyScene() {}
	
	public static void getPressedKey(Scene previousScene, Window w, Window.GetKeyResultReceiver reciever) {
		if(previousScene == null || w == null || reciever == null) {
			throw new IllegalArgumentException("Argument(s) for GetKeyScene creating is null.");
		}
		
		GetKeyScene gks = new GetKeyScene();
		
		gks.background = w.copyScreen();
		gks.previousScene = previousScene;
		gks.reciever = reciever;
		
		w.changeScene(gks);

		w.getFirstPressedKey(gks);
	}
	
	@Override
	public void tick(Window w) {
		if(input != null) {
			w.changeScene(previousScene);
			
			reciever.receive(input);
		}
	}

	@Override
	public void draw(Bitmap canvas) {
		canvas.drawBackground(background);
		
		double height = FONT.getHeight() * 2;
		double width = FONT.getWidth(MESSAGE);
		
		double x = (1.0 - width) / 2.0;
		double y = (1.0 - height) / 2.0;
		
		FONT.draw(canvas, x, y, MESSAGE, -1);
	}

	@Override
	public void receive(InputSource inputSource) {
		input = inputSource;
	}
}
