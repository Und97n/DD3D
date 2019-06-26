package org.zizitop.game.sprites;

/**
 * Just utility class for extending it in another classes.
 * <br><br>
 * Created 30.01.19 22:26
 *
 * @author Zizitop
 */
public abstract class LinkedListElement extends Point {
	// Next object in linked list. If not needed - can be null
	public LinkedListElement listNext;

	public LinkedListElement(double x, double y, double z) {
		super(x, y, z);
	}
}
