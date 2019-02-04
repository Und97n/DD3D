package org.zizitop.game.inventory;

import org.zizitop.pshell.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract thing for storing items
 * @author Zizitop
 *
 */
public abstract class ItemStorage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final Item[] storage;
	
	public ItemStorage(int storageSize) {
		storage = new Item[storageSize];
	}
	
	/**
	 * Get item from storage(copy, not take)
	 * @param index - index of item
	 * @return needed item
	 */
	public Item getItem(int index) {
		if(index < 0 || index >= storage.length) {
			throw new IllegalArgumentException("Bad index for item slot: " + index + 
					(index < 0 ? "(less that zero)." : "(greater than size of item storage)."));
		}
		
		return storage[index];
	}
	
	/**
	 * Remove item from storage and give you
	 * @param index - index of item
	 * @return needed item
	 */
	public Item takeItem(int index) {		
		return replaceItem(null, index);
	}
	
	/**
	 * Take several items from stack
	 * @param index - index of item
	 * @return needed item(null if no item)
	 */
	public StackableItem takeStackableItem(int index, int count) {
		Item i = getItem(index);
		
		if(i != null && i instanceof StackableItem) {
			StackableItem ret = ((StackableItem)i).split(count);
			
			if(((StackableItem)i).isEmpty()) {
				storage[index] = null;
			}
			
			if(ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		
		return null;
	}
	
	/**
	 * Replace item in storage to new  item
	 * @param index - slot index
	 * @param newItem - new item...
	 * @return old item
	 */
	public Item replaceItem(Item newItem, int index) {
		Item old = getItem(index);
		
		storage[index] = newItem;
		
		return old;
	}
	
	/**
	 * Attempt to add item to storage
	 * @return true if item added, else false
	 */
	public boolean addItem(Item item) {
		if(item == null) {
			//We allways have space for nothing
			return true;
		}
		
		if(item instanceof StackableItem) {
			StackableItem stItem = (StackableItem) item;
			
			if(stItem.isEmpty()) {
				return true;
			}
			
			for(int i = 0; i < storage.length; ++i) {
				Item currentItem = storage[i];
				
				if(currentItem != null && currentItem instanceof StackableItem &&((StackableItem) currentItem).addItems(stItem)) {
					return ((StackableItem) item).isEmpty();
				}
			}
		}
		
		for(int i = 0; i < storage.length; ++i) {
			if(storage[i] == null) {
				storage[i] = item;
				
				return true;
			}
		}
		
		return false;
	}
	
	public Item putItem(Item item, int index) {
		Item ret = getItem(index);
		
		storage[index] = item;
		
		return ret;
	}

	public List<Item> takeAllItems() {
		var list = new ArrayList<Item>();

		for(int i = 0; i < storage.length; ++i) {
			Item it = storage[i];

			if(it != null) {
				list.add(it);
				storage[i] = null;
			}
		}

		return list;
	}

	
	public int findItem(Class<? extends Item> classForFinding, boolean useSuperclasses) {
		for(int i = 0; i < storage.length; ++i) {
			Item it = storage[i];
			
			if(it != null && (it.getClass() == classForFinding || (useSuperclasses && it.getClass().isAssignableFrom(classForFinding)))) {
				return i;
			}
		}
		
		return -1;
	}

}
