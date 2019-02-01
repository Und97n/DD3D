package org.zizitop.game.sprites;

import org.zizitop.game.world.World;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class BreakableStructure extends Structure {
	private static final long serialVersionUID = 1L;

	public double HP;

	public BreakableStructure(double x, double y, double z, double HP) {
		super(x, y, z);

		this.HP = HP;
	}

	public abstract double getMaxHP();

	public double heal(double healValue, World world) {
		if (healValue < 0) {
			return healValue;
		}

		HP += healValue;

		double maxHP = getMaxHP();

		double delta = HP > maxHP ? HP - maxHP : 0;

		HP -= delta;

		return healValue - delta;
	}

	public double damage(double damageValue, World world) {
		if (damageValue < 0) {
			return damageValue;
		}

		HP -= damageValue;

		double delta = HP < 0 ? -HP : 0;

		HP += delta;

		if (HP <= 0) {
			dead(world);
		}

		return damageValue - delta;
	}

	public boolean isDead() {
		return HP <= 0;
	}

	protected void dead(World w) {
		throw null;
		// w.deleteStructure(this);
	}
}
