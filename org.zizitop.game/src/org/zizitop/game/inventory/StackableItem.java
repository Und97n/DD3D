package org.zizitop.game.inventory;

import java.util.function.Consumer;

import org.zizitop.game.sprites.Structure;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

public abstract class StackableItem extends Item {
	private static final long serialVersionUID = 1L;

	protected int count;
	
	public StackableItem(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return count;
	}
	
	public StackableItem split(int count) {
		if(count > this.count) {
			//throw new IllegalArgumentException("Too many items for spliting!");
			return null;
		} else {
			this.count -= count;
			
			return getCopy(count);
		}
	}
	
	public int takeItems(int max) {
		if(max <= 0) {
			return max;
		}
		
		int ret = Math.min(max, count);
		
		count -= ret;
		
		return ret;
	}
	
	public boolean isEmpty() {
		return count <= 0;
	}
	
	public abstract int getMaxCount();
	public abstract StackableItem getCopy(int countInCopy);
	
	/**
	 * Add items to the "stack"
	 * @param i - items to add
	 * @return true if can add Items to the stack, else false(if no space or if classes are different)
	 */
	public boolean addItems(StackableItem i) {
		if(i.getClass() != this.getClass()) {
			return false;
		} else {
			count += i.takeItems(getMaxCount() - count);
			
			return true;
		}
	}
	
	@Override
	public boolean needDeleteFromInventory() {
		return isEmpty();
	}
	
	@Override
	public void drawIcon(Bitmap canvas, double x, double y, double width, double height) {
		canvas.draw_SC(getItemTexture(), x, y, width, height);
		
		/*DDFont.CONSOLE_FONT.draw(canvas, x + 1.0 / Main.MIN_WIDTH, y + 1.0 / Main.MIN_HEIGHT, 
				String.valueOf(count), 0x2f2f2f);*/
		DDFont.INVENRORY_FONT.draw(canvas, x, y, String.valueOf(count), -1);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + count;
	}
	
	@Override
	public void addToTheWorld(World w, double x, double y, boolean dropedFromMainActor) {
		int sectorId = w.getLevel().getSectorId(x, y);

		if(sectorId < 0) {
			// Problems!
			throw new RuntimeException("No sector for coors " + x + " " + y + ". Can`t add item.");
		}

		ItemHolder ih = new ItemHolder(x, y, this, dropedFromMainActor);
		
		Consumer<Structure> action = s -> {
			if(s.intersects(ih) && s instanceof ItemHolder) {
				Item i = ((ItemHolder) s).getItem();
				
				if(i instanceof StackableItem) {
					addItems((StackableItem) i);
				}
			}
		};

		w.getLevel().proceedNearbyStructures(action, sectorId);

		w.add(ih);
	}
}
