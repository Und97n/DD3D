package org.zizitop.game.sprites.abilities;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.world.World;

/**
 * Atom of high-level abstraction for entities. Thing, that represents one property of an {@link org.zizitop.game.sprites.Entity}.
 * <br><br>
 * Created 04.02.19 13:45
 *
 * @author Zizitop
 */
public interface Ability {
	/**
	 * Update method. Run`s on each game tick.
	 * @param owner owner of this ability
	 * @param world owner of owner of this ability
	 */
	default void update(Entity owner, World world) {}

	/**
	 * If return value is true - this ability will be removed from the holder on the next game tick.
	 */
	default boolean needRemove() {
		return false;
	}

	/**
	 * This method invokes when entity is removing from the world.
	 */
	default void onEntityRemoving(Entity owner, World world) {}

}
