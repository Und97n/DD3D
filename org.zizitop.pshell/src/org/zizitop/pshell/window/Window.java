package org.zizitop.pshell.window;


import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Bitmap;

/**
 * <br><br>
 * Created 26.08.18 16:00
 *
 * @author Zizitop
 */
public interface Window {
	String SCREENSHOT_EXTENSION = "png";
	String SCREENSHOT_DIRECTORY =
			ShellApplication.getProgramConfig().getOption("screenshotDirectory");

	double TICK_FPS = 60.0;

	void startMainLoop() throws Exception;

	Bitmap copyScreen();

	/**
	 * You can do this in any time without problems.
	 */
	void changeScene(Scene newScene);

	double getMouseX();
	double getMouseY();

	double getMouseDx();
	double getMouseDy();

	String getLastEnteredText();

	int getContentWidth();
	int getContentHeight();

	int getWidth();
	int getHeight();

	double getAspectRatio();

	void exitMainLoop();

	boolean isPressed(InputOption io);

	/**
	 * True, if this button was released last tick.
	 */
	boolean wasReleased(InputOption io);

	int getActionsCount(InputOption io);

	Scene getCurrentScene();

	DisplayMode getDisplayMode();

	WindowContext getContext();

	void getFirstPressedKey(GetKeyResultReceiver gkrr);

	default boolean mouseMoved() {
		return getMouseDx() == 0 && getMouseDy() == 0;
	}

	@FunctionalInterface
	public interface GetKeyResultReceiver {
		void receive(InputSource inputSource);
	}
}
