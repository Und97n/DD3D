package org.zizitop.pshell.window;


/**
 * Cool class for selecting width and height of the content.
 * @author Zizitop
 *
 */
public abstract class DisplayMode {
	public abstract int getContentWidth();
	public abstract int getContentHeight();

	public abstract int getBaseWidth();
	public abstract int getBaseHeight();

	public abstract double getFPS();
}
