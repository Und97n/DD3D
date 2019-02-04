package org.zizitop.game.sprites.abilities;

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
}
