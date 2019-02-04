package org.zizitop.game.inventory;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.abilities.Ability;
import org.zizitop.game.world.World;

/**
 * Ability to store some items.
 * <br><br>
 * Created 04.02.19 16:58
 *
 * @author Zizitop
 */
public interface ItemStorageAbility extends Ability {
	Inventory getInventory();

	@Override
	default void onEntityRemoving(Entity owner, World world) {
		var items = getInventory().takeAllItems();

		// Drop inventory to the world
		for(Item item: items) {
			item.addToTheWorld(world, owner.x, owner.y);
		}
	}
}
