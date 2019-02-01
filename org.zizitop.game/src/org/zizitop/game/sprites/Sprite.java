package org.zizitop.game.sprites;

import org.zizitop.pshell.utils.Bitmap;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Sprite extends LinkedListElement implements TexturedObject, Serializable {
	private static final long serialVersionUID = 1L;

	public double x, y, z;

	public Sprite(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '(' + x +  ';' + y + ';' + z + ')';
	}

	public double distance(Point p) {
		return Math.hypot(p.x - x, p.y - y);
	}
	
	public boolean isVisible() {
		return true;
	}
	
	public abstract Bitmap getTexture();

	/**
	 * This method run`s when sector, that contains this object, is changed.
	 * @param oldSectorId - old sector. If no old sector - -1
	 * @param newSectorId - new sector. If no new sector - -1
	 */
	public void onSectorChange(int oldSectorId, int newSectorId) {}
}
