package org.zizitop.dd3d.content.inventory;

import org.zizitop.dd3d.content.scenes.InventoryScene;
import org.zizitop.game.GameScene;
import org.zizitop.game.inventory.Inventory;
import org.zizitop.game.inventory.Item;
import org.zizitop.game.inventory.StackableItem;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

import java.io.Serializable;

/**
 * Utility class for smart hotbar manipulations
 * @author Zizitop
 *
 */
public class Hotbar implements Serializable {
	private static final InputOption input_number1 = InputOption.getInputOption("inventory.number1");
	private static final InputOption input_number2 = InputOption.getInputOption("inventory.number2");
	private static final InputOption input_number3 = InputOption.getInputOption("inventory.number3");
	private static final InputOption input_number4 = InputOption.getInputOption("inventory.number4");
	private static final InputOption input_number5 = InputOption.getInputOption("inventory.number5");
	private static final InputOption input_number6 = InputOption.getInputOption("inventory.number6");

	private static final InputOption input_next = InputOption.getInputOption("inventory.next");
	private static final InputOption input_previous = InputOption.getInputOption("inventory.previous");
	private static final InputOption input_openInventory = InputOption.getInputOption("game.player.openInventory");
	private static final InputOption input_useItem = InputOption.getInputOption("game.player.useItem");
	private static final InputOption input_dropItem = InputOption.getInputOption("game.player.dropItem");
	private static final InputOption input_workWithAllStack =
			InputOption.getInputOption("game.player.workWithAllStack");


	public static final int HOTBAR_SIZE = 6;
	public static final int BAG_SIZE = 18;

	public static final int SLOTS = 6;

	public static Bitmap[] healthTexture;
	public static final Bitmap interfaceTexture = ResourceLoader.getInstance().getTexture("interface/gameInterface");

	static {
		//Make health texture array of lines(of texture) for
		//Shine HP drawing
		Bitmap healthTexture = ResourceLoader.getInstance().getTexture("interface/gameInterfaceHealth");

		Hotbar.healthTexture = new Bitmap[healthTexture.height];

		for(int i = 0; i < healthTexture.height; ++i) {
			int[] arr = new int[healthTexture.width];

			System.arraycopy(healthTexture.pixels, healthTexture.pixels.length - healthTexture.width * (i + 1), arr, 0, arr.length);

			Hotbar.healthTexture[i] = new Bitmap(arr, healthTexture.width, 1);
		}
	}

	private static final long serialVersionUID = 1L;


	/**
	 * Selected item or weapon
	 */
	private int currentItem = 0, oldCurrentItem;

	private final Inventory playerInventory;

	/**
	 * If no selected item
	 * good choice - hands
	 */
	private final Item nullItem;

	private boolean useItemAction, useItemWasReleased, workWithAllStack, dropItemAction;

	public Hotbar(Item nullItem, Inventory playerInventory) {
		this.playerInventory = playerInventory;

		this.nullItem = nullItem;
	}

	public boolean hasSelectedItem() {
		return getCurrentItem() != null;
	}

	public void drawItemBar(Entity owner, Bitmap canvas, DisplayMode dm) {
		if(getCurrentItem() != null) {
			getCurrentItem().drawInHands(dm, canvas, 0, dm.getViewportHeight()-dm.getContentHeight());
		}

		double scaleX = (double)canvas.width / dm.getBaseWidth();
		double scaleY = (double)canvas.height / dm.getBaseHeight();

		int itWidth = (int) (interfaceTexture.width * scaleX);
		int itHeight = (int) (interfaceTexture.height * scaleY);

		canvas.draw(interfaceTexture, 0, dm.getBaseViewportHeight(), dm.getContentWidth(), dm.getContentHeight() - dm.getBaseViewportHeight());

		/*canvas.draw(healthTexture, (Main.MIN_WIDTH - healthTexture.width - 2) * scaleX,
				(Main.MIN_HEIGHT - healthTexture.height - 3) * scaleY,
				healthTexture.width * scaleX, healthTexture.height * scaleY);*/


		{
			double x = (dm.getBaseWidth() - healthTexture[0].width - 2) * scaleX;

			int heroHealthLimit = 0;

			if(owner != null) {
				HealthAbility[] he = owner.getAbilityHolder().getAbilities(HealthAbility.class);

				if(he.length > 0) {
					heroHealthLimit = (int) (healthTexture.length * (he[0].getHp() / he[0].getMaxHp()));
				}

				//Make borders for heroHealthLimit: [0; healthTexture.length]
				heroHealthLimit = heroHealthLimit < 0 ? 0 : (heroHealthLimit > healthTexture.length ?
						healthTexture.length : heroHealthLimit);
			}

			for(int i = 0; i < heroHealthLimit; ++i) {
				//Draw lines of a health texture
				canvas.draw(healthTexture[i], x, (dm.getBaseHeight() - 5 + healthTexture[0].height - i) * scaleY,
						healthTexture[0].width * scaleX, scaleY);
			}
		}

		for(int i = 0; i < SLOTS; ++i) {
			double x = (81 + 34 * i) * scaleX;
			double y = canvas.height - itHeight + 6 * scaleY;

			double width = 28 * scaleX;
			double height = 28 * scaleY;

			if(i == currentItem) {
				canvas.fillRect(0x9f9f9f, x, y, width, height);
			}

			Item item = playerInventory.getItem(i);

			if(item != null) {
				item.drawIcon(canvas, x / canvas.width, y / canvas.height, width / canvas.width, height / canvas.height);
			}
		}
	}

	/**
	 * Get selected item(copy, not take)
	 */
	public Item getCurrentItem() {
		Item ret = playerInventory.getItem(currentItem);

		if(ret == null) {
			ret = nullItem;
		}

		return ret;
	}

	/**
	 * Take selected item(and remove it from hotbar)
	 */
	public Item takeCurrentItem() {
		return playerInventory.takeItem(currentItem);
	}

	/**
	 * Need update inventory every tick
	 */
	public void update(World w, Entity hero) {
		if(oldCurrentItem != currentItem) {
			Item i = playerInventory.getItem(oldCurrentItem);

			if(i != null) {
				i.changeSelectedItem(hero, w);
			}
		}

		updateSelectedItem(w, hero);
	}

	private void updateSelectedItem(World w, Entity hero) {
		Item i = getCurrentItem();

		if(i == null) {
			return;
		}

		i.update(hero, w);

		if(i.needDeleteFromInventory()) {
			playerInventory.replaceItem(null, currentItem);
			return;
		}

		if(useItemWasReleased) {
			i.onKeyReleased(hero, w);
		} else if(useItemAction) {
			i.action(hero, w);
		}

		if(dropItemAction && i != nullItem) {
			double x = hero.x;
			double y = hero.y;

			if(!workWithAllStack && i instanceof StackableItem) {
				final StackableItem droped = ((StackableItem)i).split(1);

				if(droped != null && !droped.isEmpty()) {
					droped.addToTheWorld(w, x, y, true);

					if(((StackableItem)i).isEmpty()) {
						playerInventory.replaceItem(null, currentItem);
					}
				}
			} else {
				i.addToTheWorld(w, x, y, true);

				playerInventory.replaceItem(null, currentItem);
			}
		}
	}

	public void checkInput(Window window, Entity hero) {
		useItemWasReleased = window.wasReleased(input_useItem);
		useItemAction = window.getActionsCount(input_useItem) > 0;
		workWithAllStack = window.isPressed(input_workWithAllStack);
		dropItemAction = window.getActionsCount(input_dropItem) > 0;

		oldCurrentItem = currentItem;

		if(window.getActionsCount(input_next) > 0) {
			++currentItem;
		}

		if(window.getActionsCount(input_previous) > 0) {
			--currentItem;
		}

		if(window.getActionsCount(input_number1) > 0){
			currentItem = 0;
		} else if(window.getActionsCount(input_number2) > 0) {
			currentItem = 1;
		} else if(window.getActionsCount(input_number3) > 0) {
			currentItem = 2;
		} else if(window.getActionsCount(input_number4) > 0) {
			currentItem = 3;
		} else if(window.getActionsCount(input_number5) > 0) {
			currentItem = 4;
		} else if(window.getActionsCount(input_number6) > 0) {
			currentItem = 5;
		}

		if(currentItem >= SLOTS) {
			currentItem -= SLOTS;
		} else if(currentItem < 0) {
			currentItem += SLOTS;
		}

		if(window.getActionsCount(input_openInventory) > 0) {
			if(!(window.getCurrentScene() instanceof GameScene)) {
				Log.write("Can't open inventory(scene is not a GameScene)", 1);
			} else {
				InventoryScene.openInventory(window, (GameScene) window.getCurrentScene(), hero, Utils.asArray(playerInventory));
			}
		}
	}
}