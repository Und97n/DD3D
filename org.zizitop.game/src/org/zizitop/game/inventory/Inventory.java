package org.zizitop.game.inventory;

import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.IntPoint;
import org.zizitop.pshell.utils.Rectangle;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Inventory extends ItemStorage {	
	private static final long serialVersionUID = 1L;
	
	public Inventory(final int cellsXCount, final int cellsYCount) {
		super(cellsXCount * cellsYCount);
	}
	
	/**
	 * @return true if item added fully
	 */
	public boolean addItem(Item item) {
		if(item instanceof StackableItem) {
			for(int j = 0; j < storage.length; ++j) {
				if(storage[j] != null && storage[j] instanceof StackableItem) {
					((StackableItem)storage[j]).addItems((StackableItem)item);
					
					if(((StackableItem) item).isEmpty()) {
						return true;
					}
				}
			}
		}
		
		//If not stackable item or no special space for stackable item
		return addNonStackableItem(item);
	}
	
	/**
	 * @return true if item added
	 */
	protected boolean addNonStackableItem(Item item) {
		for(int i = 0; i < storage.length; ++i) {
			if(storage[i] == null) {
				storage[i] = item;
				
				return true;
			}
		}
		
		return false;
	}

	public abstract Bitmap getInterfaceTexture();
	
	public abstract SlotBox[] getSlotsRectangles();
	public abstract Rectangle getContentArea();

	public static class SlotBox {
		public int x, y, size;

		public SlotBox(int x, int y, int size) {
			this.x = x;
			this.y = y;

			this.size = size;
		}

		public boolean containsPoint(int px, int py) {
			int dx = px - x;
			int dy = py - y;

			return dx >= 0 && dy >= 0 && dx <= size && dy <= size;
		}

		public boolean containsPoint(IntPoint point) {
			return containsPoint(point.x, point.y);
		}
	}
}
