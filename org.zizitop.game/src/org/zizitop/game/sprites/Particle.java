package org.zizitop.game.sprites;

import org.zizitop.game.world.World;

public abstract class Particle extends Sprite {
	private static final long serialVersionUID = 1L;

	public Particle(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Every tick need update particles
	 * @param w
	 * @return true if need remove particle
	 */
	public abstract boolean update(World w);
}
