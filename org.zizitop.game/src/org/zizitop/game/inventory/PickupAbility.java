package org.zizitop.game.inventory;

import org.zizitop.game.sprites.abilities.Ability;

/**
 * Ability to pickup items
 * <br><br>
 * Created 04.02.19 15:02
 *
 * @author Zizitop
 */
public interface PickupAbility extends Ability {
	/**
	 * Try to add item to entity.
	 * @param it some item
	 * @return If item is picked fully - true, else - false.
	 */
	boolean addItem(Item it);
}
