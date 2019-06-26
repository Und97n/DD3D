package org.zizitop.game.inventory;

import org.zizitop.game.GameUtils;
import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Structure;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.Sound;

import java.util.function.Predicate;

/**
 *
 * @author Zizitop
 *
 */
public abstract class WeaponNear extends Weapon {
	private static final long serialVersionUID = 1L;

	/**
	 * For comfortable war
	 */
	private boolean nextSwing = false;
	private boolean needAttack = false;

	protected void attack(Entity hero, World w) {
		needAttack = false;

		// TODO add directions
		double ax = hero.x/* + hero.getDirectionX() * 0.5*/;
		double ay = hero.y/* + hero.getDirectionY() * 0.5*/;



		int delay = animation.getStateTime();

		if(delay < 0) {
			delay = 0;
		}

		delay = getSwingDuration() - delay;

		double multiplier = (double)delay / getSwingDuration();

		Predicate<Structure> condition = s -> !(s instanceof MainActor);

		double damage = getMinDamage() * (1 - multiplier) + getMaxDamage() * multiplier;

		if(GameUtils.meleeAttack(hero, w, damage, ax, ay, getAttackDistance(), getAttackDistance(), condition)) {
			getPunchSound().play();
		}
	}

	@Override
	public void onKeyReleased(Entity hero, World w) {
		nextSwing = false;

		if(animation.getCurrentStateId() == getSwingStateId()) {
			animation.switchState(getAttackStateId());
			needAttack = true;

			getSwingSound().play();
		}
	}

	@Override
	public void update(Entity hero, World w, double dt) {
		super.update(hero, w, dt);

		final int state = animation.getCurrentStateId();
		final int stateDefault = getDefaultStateId();
		final int stateSwing = getSwingStateId();
		final int stateAttack = getAttackStateId();

		if(state == stateDefault) {
			//If player press attack button when was swing
			if(nextSwing) {
				animation.switchState(getSwingStateId());
				nextSwing = false;
			}
		} else if(state == stateSwing) {
			//Make action if end of swing
			if(animation.getStateTime() <= 0) {
				animation.switchState(getAttackStateId());
				needAttack = true;
				getSwingSound().play();
			}
		} else if(state == stateAttack) {
			//If now is time for attack
			if(needAttack && animation.getAnimationPointer() == getAttackFrameId()) {
				attack(hero, w);
			}
		}
	}

	@Override
	public void action(Entity hero, World w) {
		int animationState = animation.getCurrentStateId();

		if(animationState == getDefaultStateId()) {
			animation.switchState(getSwingStateId());
		} else if(animationState == getAttackStateId()) {
			nextSwing = true;
		}
	}

	public abstract int getSwingStateId();
	public abstract int getAttackStateId();

	public abstract int getSwingDuration();
	public abstract int getAttackFrameId();

	public abstract double getAttackDistance();

	public abstract double getMinDamage();
	public abstract double getMaxDamage();

	public abstract Sound getSwingSound();
	public abstract Sound getPunchSound();
}
