package org.zizitop.pshell.menu;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.Window;

/**
 * Nice class for all objects on menu screen.
 * @author Zizitop
 *
 */
public interface Component {	
	int DEFAULT_BACKGROUND_COLOR = 0x404030;
	int DEFAULT_SELECTED_COLOR = DEFAULT_BACKGROUND_COLOR / 2;
	
	Sound menuSound = ResourceLoader.getInstance().getSound("menu.wiw");
	
	double getX();
	double getY();
	double getSizeX();
	double getSizeY();
	
	Component setX(double newX);
	Component setY(double newY);
	Component setPosition(double newX, double newY);
	
	void updateComponent(Window window);
	void drawComponent(Bitmap canvas);
	
	default boolean containsPoint(double pointX, double pointY) {
		final double x1 = getX();
		final double x2 = x1 + getSizeX();
		final double y1 = getY();
		final double y2 = y1 + getSizeY();
		
		return (pointX > x1 && pointX < x2) && (pointY > y1 && pointY < y2);
	}
}
