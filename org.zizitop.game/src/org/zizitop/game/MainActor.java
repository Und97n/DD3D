package org.zizitop.game;

import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.Window;

/**
 * Camera and interface holder for each world.
 */
public interface MainActor {
	void drawInterface(Bitmap canvas, Bitmap viewport, World w);

	/**
	 * Set some option for a hero. Option list may change from one MainActor type to another.
	 * @return true if success, false if option not found
	 */
	boolean setOption(String name, int value);

	void proceedInput(Window window);
	default void mainActorTick(World world) {}


	double getPosX();
	double getPosY();
	double getPosZ();
	double getHorizontalViewAngle();
	double getVerticalViewAngle();
	int getSectorId();

	default boolean lockUserMouse() {
		return false;
	}
}
