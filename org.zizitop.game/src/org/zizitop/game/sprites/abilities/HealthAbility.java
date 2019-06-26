package org.zizitop.game.sprites.abilities;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Ability for entity to have an Health Points
 * <br><br>
 * Created 04.02.19 16:00
 *
 * @author Zizitop
 */
public class HealthAbility implements DeathAbility, Ability {
	protected double hp;
	protected final double maxHp;

	protected final List<HealthEvent> events = new ArrayList<>();

	public HealthAbility(double hp, double maxHp) {
		this.hp = hp;
		this.maxHp = maxHp;
	}

	@Override
	public void update(Entity owner, World world, double dt) {
		if(owner instanceof HealthListener) {
			for(HealthEvent he: events) {
				if(he.hpChange < 0) {
					((HealthListener) owner).onDamage(hp, -he.hpChange, he.reason);
				} else {
					((HealthListener) owner).onHeal(hp, +he.hpChange, he.reason);
				}
			}
		}
	}

	/**
	 * Damage to the entity
	 * @param damageSource entity, that maked this damage. Or null.
	 */
	public void damage(double damageValue, Entity damageSource) {
		if(damageValue < 0) {
			damage(-damageValue, damageSource);
		}

		hp -= damageValue;
		hp = hp < 0 ? 0 : hp;

		events.add(new HealthEvent(-damageValue, damageSource));
	}

	/**
	 * Heal entity
	 * @param healSource entity, that maked this heal. Or null.
	 */
	public void heal(double healValue, Entity healSource) {
		if(healValue < 0) {
			damage(-healValue, healSource);
		}

		hp += healValue;
		hp = hp > maxHp ? maxHp : hp;

		events.add(new HealthEvent(+healValue, healSource));
	}

	public double getHp() {
		return hp;
	}

	public double getMaxHp() {
		return maxHp;
	}

	private static class HealthEvent {
		final double hpChange;
		final Entity reason;

		private HealthEvent(double hpChange, Entity reason) {
			this.hpChange = hpChange;
			this.reason = reason;
		}
	}

	@Override
	public boolean ownerIsDead() {
		return hp <= 0;
	}

	/**
	 * HP change listener. Implement it in owner(Entity).
	 */
	public interface HealthListener {
		default void onDamage(double newHp, double damageValue, Entity source) {}
		default void onHeal(double newHp, double healValue, Entity source) {}
	}
}
