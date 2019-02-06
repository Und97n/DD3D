package org.zizitop.game.inventory;

import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.TexturedObject;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Rectangle;
import org.zizitop.pshell.window.DisplayMode;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Item implements Serializable, TexturedObject {
	private static final long serialVersionUID = 1L;

	public static final int defaultWidthInHands = 64;
	
	public static final Sound pickupSound = ResourceLoader.getInstance().getSound("pickup");
	
	public Item() {}
	
	/**
	 * Update item(used for updating selected item in hotbar)
	 * @param hero - owner
	 * @param w - universe with this item
	 */
	public void update(Entity hero, World w) {}
	
	/**
	 * This method runs when player take in hands another item
	 */
	public void changeSelectedItem(Entity hero, World w) {}
		
	
	/**
	 * This method runs when player select this item and use it
	 * @param hero - player with this item in hands
	 * @param w - current universe
	 */
	public void action(Entity hero, World w) {}
	
	/**
	 * This method runs when player select this item and release action button
	 * @param hero - player with this item in hands
	 * @param w - current universe
	 */
	public void onKeyReleased(Entity hero, World w) {}
	
	/**
	 * Get localized name of item
	 */
	public String getName() {
		return Lang.getText("object." + getClass().getSimpleName());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public void drawIcon(Bitmap canvas, double x, double y, double width, double height) {
		canvas.draw_SC(getItemTexture(), x, y, width, height);
	}

	public void drawInHands(DisplayMode dm, Bitmap canvas, int xOffset, int yOffset) {
		Bitmap tx = getItemTexture();

		int w = (int) (canvas.width * (double)defaultWidthInHands / dm.getBaseViewportWidth());
		int h = (int) (canvas.height * (defaultWidthInHands * (tx.width / (double)tx.height)) / dm.getBaseViewportHeight());

		canvas.draw(tx, xOffset + canvas.width / 2 - w / 2, canvas.height - h + yOffset, w, h);
	}

	public void addToTheWorld(World w, double x, double y) {
		addToTheWorld(w, x, y, false);
	}
	
	public void addToTheWorld(World w, double x, double y, boolean dropedFromMainActor) {
		ItemHolder i = new ItemHolder(x, y, this, dropedFromMainActor);
		
		w.add(i);
	}
	
	public abstract double getZPosition();
	public abstract Bitmap getItemTexture();
	
	@Override
	public final Bitmap getTexture() {
		return getItemTexture();
	}
	
	/**
	 * This method runs after every item update
	 * @return true if need remove item from inventory
	 */
	public boolean needDeleteFromInventory() {
		return false;
	}
}
