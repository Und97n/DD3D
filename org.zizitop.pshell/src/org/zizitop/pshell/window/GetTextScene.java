package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

import java.util.function.Consumer;

/**
 * Cool class to get pressed by user button
 * @author Zizitop
 *
 */
public class GetTextScene implements Scene {
	private static final DDFont FONT = DDFont.DEFAULT_FONT;

	protected static final InputOption input_enter = InputOption.getInputOption("enter");
	protected static final InputOption input_erase = InputOption.getInputOption("erase");

	private Bitmap background;
	private Scene previousScene;
	private Consumer<String> receiver;
	private String[] message;
	private StringBuilder text = new StringBuilder();

	private InputSource input = null;

	private GetTextScene() {}

	public static void getUserText(String message, Scene previousScene, Window w, Consumer<String> receiver) {
		if(previousScene == null || w == null || receiver == null) {
			throw new IllegalArgumentException("Argument(s) for GetTextScene creating is null.");
		}

		GetTextScene gks = new GetTextScene();

		gks.background = w.copyScreen();
		gks.previousScene = previousScene;
		gks.receiver = receiver;
		gks.message = message.split("\n");

		w.changeScene(gks);
	}

	@Override
	public void tick(Window w) {
		String inputS = w.getLastEnteredText();

		for(char c: inputS.toCharArray()) {
			// If char is printable - add it
			if(c >= 32) {
				text.append(c);
			}
		}

		if(w.getActionsCount(input_erase) > 0 && text.length() > 0) {
			// Erase one char
			text.setLength(text.length() - 1);
		}

		if(w.getActionsCount(input_enter) > 0) {
			receiver.accept(text.toString());
			w.changeScene(previousScene);
		}
	}

	@Override
	public void draw(Bitmap canvas) {
		canvas.drawBackground(background);

		double height = (FONT.getHeight() * 2) * message.length;

		double y = (1.0 - height) / 2.0;

		for(String s: message) {
			double width = FONT.getWidth(s);

			double x = (1.0 - width) / 2.0;

			FONT.draw(canvas, x, y, s, -1);

			y += FONT.getHeight();
		}

		DDFont smallFont = DDFont.INVENRORY_FONT;

		smallFont.draw(canvas, (1.0 - smallFont.getWidth(text)) / 2.0, y + FONT.getHeight(), text, -1);
	}
}
