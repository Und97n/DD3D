package org.zizitop.dd3d.content.scenes;

import org.zizitop.game.GameScene;
import org.zizitop.game.inventory.Inventory;
import org.zizitop.game.inventory.Inventory.SlotBox;
import org.zizitop.game.inventory.Item;
import org.zizitop.game.inventory.StackableItem;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.*;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

/**
 *
 * @author Zizitop
 *
 */
public class InventoryScene implements Scene {
	protected static final InputOption input_openInventory = InputOption.getInputOption("game.player.openInventory");
	protected static final InputOption input_escape = InputOption.getInputOption("menu.escape");
	protected static final InputOption input_actionOnMenuItem = InputOption.getInputOption("menu.actionOnMenuItem");
	protected static final InputOption input_actionOnMenuItemRB =
			InputOption.getInputOption("menu.actionOnMenuItemRB");

	public static final DDFont ITEMS_NAMES_FONT = DDFont.CONSOLE_FONT;
	public static final int SLOT_CONTENT_SIZE = 28;

	private Scene previousScene;
	private Bitmap background;

	private World world;
	private Entity observer;

	private final Inventory[] inventories;

	private final Rectangle[] inventoriesData;

	private IntPoint selectedCellData;

	private Item itemInHands;
	private Boolean itemInHandsButton;

	private IntPoint itemInHandsData;
	private IntPoint mousePosition, itemInHandsMousePosition;

	public static InventoryScene openInventory(Window w, GameScene previousScene, Entity observer, Inventory[] inventories) {
		InventoryScene inventoryScene = new InventoryScene(w, previousScene.getWorld(), previousScene, observer, inventories);

		w.changeScene(inventoryScene);

		return inventoryScene;
	}

	public InventoryScene(Window w, World world, Scene previousScene, Entity observer, Inventory[] inventories) {
		this.previousScene = previousScene;
		background = w.copyScreen();

		this.world = world;

		this.observer = observer;

		this.inventories = inventories;

		inventoriesData = new Rectangle[inventories.length];

		//Good data storage - point
		selectedCellData = new IntPoint(-1, -1);
		itemInHandsData = new IntPoint(-1, -1);

		mousePosition = new IntPoint(0, 0);
		itemInHandsMousePosition = new IntPoint(0, 0);


		int inventoryHeight = 0;

		for(int i = 0; i < inventories.length; ++i) {
			inventoryHeight += inventories[i].getContentArea().height;
		}

		int delta = w.getDisplayMode().getBaseHeight() - inventoryHeight;

		if(delta < inventories.length + 1) {
			Log.write("Too many inventories in inventory scene. Can't work normally.", 2);
		}

		delta /= inventories.length + 1;

		int yy = 0;

		for(int i = 0; i < inventories.length; ++i) {
			Rectangle contentArea = inventories[i].getContentArea();

			yy += delta;
			inventoriesData[i] = new Rectangle((w.getDisplayMode().getBaseWidth() - contentArea.width) / 2, yy, contentArea.width, contentArea.height);
			yy += contentArea.height;

		}
	}

	@Override
	public void tick(Window w) {
		if(w.getActionsCount(input_escape) > 0 || w.getActionsCount(input_openInventory) > 0) {
			w.changeScene(previousScene);
		}

		mousePosition.x = (int) (w.getDisplayMode().getBaseWidth() * w.getMouseX() / w.getContentWidth());
		mousePosition.y = (int) (w.getDisplayMode().getBaseHeight()* w.getMouseY() / w.getContentHeight());

		selectedCellData.x = selectedCellData.y = -1;

		int mouseCellX = 0, mouseCellY = 0;

		for(int i = 0; i < inventories.length; ++i) {
			Inventory inv = inventories[i];
			Rectangle contentArea = inventoriesData[i];

			final int mouseX = mousePosition.x - contentArea.x;
			final int mouseY = mousePosition.y - contentArea.y;

			if(contentArea.contains(mousePosition.x, mousePosition.y)) {
				selectedCellData.y = i;

				SlotBox[] slots = inv.getSlotsRectangles();

				for(int j = 0; j < slots.length; ++j) {
					if(slots[j].containsPoint(mouseX, mouseY)) {
						mouseCellX = mouseX - slots[j].x;
						mouseCellY = mouseY - slots[j].y;

						selectedCellData.x = j;
						break;
					}
				}

				break;
			}
		}

		if(w.getActionsCount(input_actionOnMenuItem) > 0) {
			if(selectedCellData.x >= 0 && selectedCellData.y >= 0) {
				if(itemInHands == null) {
					itemInHands = inventories[selectedCellData.y].takeItem(selectedCellData.x);

					if(itemInHands != null) {
						itemInHandsButton = true;
						itemInHandsMousePosition.x = mouseCellX;
						itemInHandsMousePosition.y = mouseCellY;
						itemInHandsData.x = selectedCellData.x;
						itemInHandsData.y = selectedCellData.y;
					}
				}
			}
		}

		if(w.getActionsCount(input_actionOnMenuItemRB) > 0) {

			if(selectedCellData.x >= 0 && selectedCellData.y >= 0) {
				Inventory inv = inventories[selectedCellData.y];

				if(itemInHands == null) {
					Item i = inv.getItem(selectedCellData.x);
					if(i != null) {
						if(!(i instanceof StackableItem)) {
							itemInHands = inv.takeItem(selectedCellData.x);

							itemInHandsButton = false;
							itemInHandsMousePosition.x = mouseCellX;
							itemInHandsMousePosition.y = mouseCellY;
							itemInHandsData.x = selectedCellData.x;
							itemInHandsData.y = selectedCellData.y;
						} else {
							StackableItem ii = (StackableItem) i;

							if(ii.isEmpty()) {
								inv.takeItem(selectedCellData.x);
							} else if(ii.getCount() == 1) {
								itemInHands = inv.takeItem(selectedCellData.x);
							} else {
								itemInHands = inv.takeStackableItem(selectedCellData.x, ii.getCount() / 2);
							}

							if(itemInHands != null) {
								itemInHandsButton = false;
								itemInHandsMousePosition.x = mouseCellX;
								itemInHandsMousePosition.y = mouseCellY;
								itemInHandsData.x = selectedCellData.x;
								itemInHandsData.y = selectedCellData.y;
							}
						}
					}
				}
			}
		}

		if(itemInHands != null) {
			if((itemInHandsButton && !w.isPressed(input_actionOnMenuItem)) ||
					(!itemInHandsButton && !w.isPressed(input_actionOnMenuItemRB))) {
				if(selectedCellData.x >= 0 && selectedCellData.y >= 0) {
					Inventory inv = inventories[selectedCellData.y];
					Item selItem = inv.getItem(selectedCellData.x);

					tst: if(selItem != null) {
						if(selItem instanceof StackableItem && itemInHands instanceof StackableItem) {

							((StackableItem) selItem).addItems((StackableItem) itemInHands);

							if(((StackableItem) itemInHands).isEmpty()) {
								break tst;
							}
						}

						Inventory oldInv = inventories[itemInHandsData.y];
						Item oldItem = oldInv.getItem(itemInHandsData.x);

						if(oldItem == null) {
							oldInv.putItem(itemInHands, itemInHandsData.x);
						} else {
							if(!oldInv.addItem(itemInHands)) {
								dropItem(itemInHands, true);
							}
						}
					} else {
						inv.putItem(itemInHands, selectedCellData.x);
					}

					itemInHands = null;
					itemInHandsButton = null;

					itemInHandsData.x = -1;
					itemInHandsData.y = -1;
				} else {
					if(dropItem(itemInHands, !w.isPressed(input_actionOnMenuItemRB))) {
						itemInHands = null;
						itemInHandsButton = null;

						itemInHandsData.x = -1;
						itemInHandsData.y = -1;
					}
				}
			}
		}
	}

	@Override
	public void draw(Bitmap canvas, DisplayMode displayMode) {
		canvas.drawBackground(background);

		final int minWidth = displayMode.getBaseWidth(), minHeight = displayMode.getBaseHeight();

		for(int i = 0; i < inventories.length; ++i) {
			Inventory inv = inventories[i];
			Rectangle contentArea = inventoriesData[i];

			//Draw inventory texture
			{
				final Bitmap inventoryInterfaceTexture = inv.getInterfaceTexture();

				final double w = (double)inventoryInterfaceTexture.width / minWidth;
				final double h = (double)inventoryInterfaceTexture.height / minHeight;

				canvas.draw_SC(inventoryInterfaceTexture,
						(double)(contentArea.x) / minWidth, (double)(contentArea.y) / minHeight, w, h);
			}

			SlotBox[] slots = inv.getSlotsRectangles();

			final double cellSizeX = (double)(SLOT_CONTENT_SIZE) / minWidth;
			final double cellSizeY = (double)(SLOT_CONTENT_SIZE) / minHeight;

			for(int j = 0; j < slots.length; ++j) {
				SlotBox sb = slots[j];

				int delta = sb.size - SLOT_CONTENT_SIZE;

				final double x = (double)(sb.x + contentArea.x + delta / 2) / minWidth;
				final double y = (double)(sb.y + contentArea.y + delta / 2) / minHeight;

				final boolean selectedCell = i == selectedCellData.y && j == selectedCellData.x;

				if(selectedCell) {
					canvas.fillRect_SC(0x9f9f9f, x, y, cellSizeX, cellSizeY);
				}

				Item item = inv.getItem(j);

				if(item != null) {
					item.drawIcon(canvas, x, y, cellSizeX, cellSizeY);

					if(selectedCell && itemInHands == null) {
						final String itemName = item.getName();
						final double width = ITEMS_NAMES_FONT.getWidth(itemName);
						final double height = ITEMS_NAMES_FONT.getHeight();

						final double yy = y - height;

						canvas.fillRect_SC(0x00006f, x, yy, width, height);
						canvas.drawRect_SC(0, x, yy, width, height, canvas.getPixelScSize());

						ITEMS_NAMES_FONT.draw(canvas, x, yy, itemName, -1);
					}
				}
			}
		}

		if(itemInHands != null) {
			final double x = (double)(mousePosition.x - itemInHandsMousePosition.x) / minWidth;
			final double y = (double)(mousePosition.y - itemInHandsMousePosition.y) / minHeight;

			final String itemName = itemInHands.getName();
			final double width = ITEMS_NAMES_FONT.getWidth(itemName);
			final double height = ITEMS_NAMES_FONT.getHeight();

			final double yy = y - height;

			canvas.fillRect_SC(0x00006f, x, yy, width, height);
			canvas.drawRect_SC(0, x, yy, width, height, canvas.getPixelScSize());

			ITEMS_NAMES_FONT.draw(canvas, x, yy, itemName, -1);

			itemInHands.drawIcon(canvas, x, y,
					(double)SLOT_CONTENT_SIZE / minWidth, (double)SLOT_CONTENT_SIZE / minHeight);
		}
	}

	private boolean dropItem(Item item, boolean dropAll) {
		if(item != null) {
			if(!dropAll && item instanceof StackableItem) {
				StackableItem st = ((StackableItem)item);

				if(st.isEmpty()) {
					return true;
				}

				StackableItem dropped = st.split(1);
				dropped.addToTheWorld(world, observer.x, observer.y, true);

				if(st.isEmpty()) {
					return true;
				}

				return false;
			} else {
				item.addToTheWorld(world, observer.x, observer.y, true);
				return true;
			}
		}

		return true;
	}
}