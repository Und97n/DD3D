package org.zizitop.game.sprites.abilities;

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

	public HealthAbility(double hp, double maxHp) {
		this.hp = hp;
		this.maxHp = maxHp;
	}

	public void damage(double damageValue) {
		hp -= damageValue;
		hp = hp < 0 ? 0 : hp;
	}

	public void heal(double healValue) {
		hp += healValue;
		hp = hp > maxHp ? maxHp : hp;
	}

	public double getHp() {
		return hp;
	}

	public double getMaxHp() {
		return maxHp;
	}

	@Override
	public boolean ownerIsDead() {
		return hp <= 0;
	}
}
