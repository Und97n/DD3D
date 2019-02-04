package org.zizitop.game.world;

import org.zizitop.pshell.utils.Bitmap;

/**
 * Abstract class, that represents some thing, that can render {@link World}. May be named as "3D graphics engine".
 * <br><br>
 * Created 30.01.19 22:56
 *
 * @author Zizitop
 */
public abstract class WorldRenderer {
	/**
	 * 2^TX_SIZE_2_LOG = TX_SIZE
	 */
	public static final int TX_SIZE_2_LOG = 5;

	/**
	 * Size of wall and floor textures in pixels
	 */
	public static final int TX_SIZE = 1 << TX_SIZE_2_LOG;

	/**
	 * Target for drawing.
	 */
	protected Bitmap canvas;

	/**
	 * Sets true if engine is broken and we need to create a new
	 */
	private boolean broken;

	public WorldRenderer(int screenWidth, int screenHeight) {
		reshape(screenWidth, screenHeight);
	}

	/**
	 * Draw world to the canvas.
	 * @param px - camera x position
	 * @param py - camera y posttion
	 * @param pz - camera z position
	 * @param hAngle - camera horizontal angle
	 * @param vAngle - camera vertical angle
	 * @param pSectorId - id of sector, that contains camera
	 * @param world - world to draw.
	 */
	public abstract void render(double px, double py, double pz, double hAngle, double vAngle, int pSectorId, World world);

	/**
	 * Get result of drawing
	 * @return
	 */
	public Bitmap getCanvas() {
		return canvas;
	}

	/**
	 * Change width and height for 3D engine
	 * @param newWidth
	 * @param newHeight
	 */
	public void reshape(int newWidth, int newHeight) {
		if(newWidth <= 0 || newHeight <= 0) {
			// My old code have many funny exceptions.
			throw new IllegalArgumentException("The new width or height is 0 or less. That is impossible.");
		}

		canvas = new Bitmap(newWidth, newHeight);
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken() {
		this.broken = true;
	}
}
