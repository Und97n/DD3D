package org.zizitop.dd3d.content.inventory;

import org.zizitop.game.inventory.Inventory;
import org.zizitop.game.inventory.Item;
import org.zizitop.game.inventory.ItemStorageAbility;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.abilities.PlayerSpecialAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Rectangle;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Window;

/**
 *
 * @author Zizitop
 *
 */
public class InventoryPlayerBag extends Inventory implements ItemStorageAbility, PlayerSpecialAbility {
	private static final long serialVersionUID = 1L;

	public static final Bitmap bagInterfaceTexture =
			ResourceLoader.getInstance().getTexture("interface/bagInterface");

	private static final int SLOTS_X_COUNT = 6;
	private static final int SLOTS_Y_COUNT = 4;
	private static final SlotBox[] SLOTS = new SlotBox[SLOTS_X_COUNT * SLOTS_Y_COUNT];
	private static final Rectangle CONTENT_AREA = new Rectangle(0, 0, bagInterfaceTexture.width, bagInterfaceTexture.height);

	static {
		//Screen coords - 320x240
		final int startX = 4;
		final int startY = 4;

		final int cellSize = 34;

		final int distanceBetweenCellsX = 0;
		final int distanceBetweenCellsY = 0;

		for(int y = 0; y < SLOTS_Y_COUNT; ++y) {
			for(int x = 0; x < SLOTS_X_COUNT; ++x) {
				SLOTS[(x + ((SLOTS_Y_COUNT - y - 1) * SLOTS_X_COUNT))] = new SlotBox(startX + (cellSize + distanceBetweenCellsX) * x,
						startY + (cellSize + distanceBetweenCellsY) * y, cellSize);
			}
		}
	}

	private Hotbar hotbar;

	public InventoryPlayerBag() {
		super(SLOTS_X_COUNT, SLOTS_Y_COUNT);

		hotbar = new Hotbar(new WeaponFist(), this);
	}

	@Override
	public void drawInterface(Entity owner, DisplayMode dm, Bitmap canvas) {
		hotbar.drawItemBar(owner, canvas, dm);
	}

	@Override
	public void update(Entity owner, World world) {
		hotbar.update(world, owner);
	}

	public Item getSelectedItem() {
		return hotbar.getCurrentItem();
	}

	@Override
	public Bitmap getInterfaceTexture() {
		return bagInterfaceTexture;
	}

	@Override
	public SlotBox[] getSlotsRectangles() {
		return SLOTS;
	}

	@Override
	public Rectangle getContentArea() {
		return CONTENT_AREA;
	}

	@Override
	public void proceedInput(Entity owner, Window window) {
		hotbar.checkInput(window, owner);
	}

	@Override
	public Inventory getInventory() {
		return this;
	}
}

