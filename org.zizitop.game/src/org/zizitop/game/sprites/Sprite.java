package org.zizitop.game.sprites;

import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Sprite extends LinkedListElement implements TexturedObject, Serializable {
	// Sector, that contains this object
	public int sectorId;

	private static final long serialVersionUID = 1L;

	public Sprite(double x, double y, double z) {
		super(x, y, z);
	}

	public boolean isVisible() {
		return true;
	}
	
	public abstract Bitmap getTexture();


	/**
	 * This mehtod invokes when world deletes a Sprite
	 */
	public void onRemoving(World world) {}

	/**
	 * This method run`s when sector, that contains this object, is changed.
	 * @param oldSectorId - old sector. If no old sector - -1
	 * @param newSectorId - new sector. If no new sector - -1
	 */
	public final void onSectorChange(int oldSectorId, int newSectorId) {
		if(newSectorId >= 0) {
			sectorId = newSectorId;
		}
	}
}
