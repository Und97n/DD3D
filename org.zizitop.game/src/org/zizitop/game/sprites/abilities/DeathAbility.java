package org.zizitop.game.sprites.abilities;

import org.zizitop.game.sprites.Entity;

/**
 * Ability, that represents possibility of dying.
 * <br><br>
 * Created 04.02.19 14:05
 *
 * @author Zizitop
 */
public interface DeathAbility extends Ability {
	default boolean ownerIsDead() {
		return false;
	}

	public static boolean entityIsDead(Entity e) {
		DeathAbility[] da = e.getAbilityHolder().getAbilities(DeathAbility.class);

		for(DeathAbility a: da) {
			if(a.ownerIsDead()) {
				return true;
			}
		}

		return false;
	}
}
