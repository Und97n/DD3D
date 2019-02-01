package org.zizitop.game.sprites;

public abstract class Shell extends Entity {
	public Shell(double x, double y, double z, double HP, double dx, double dy) {
		super(x, y, z, HP, dx, dy);
	}
//
//	private static final long serialVersionUID = 1L;
//
//	protected Object shooter;
//
//	protected double dz;
//
//	public Shell(double x, double y, double z, double HP, double dx, double dy, Object shooter, double zDelta) {
//		super(x, y, z, HP, dx, dy);
//
//		this.shooter = shooter;
//		this.dz = zDelta;
//	}
//
//	@Override
//	public boolean move(World w) {
//		super.move(w);
//
//		dz -= 0.001;
//
//		z += dz;
//
//		return HP <= 0 || (Math.hypot(dx, dy) == 0) || z <= -0.5;
//	}
//
//	@Override
//	public void collisionWithWall(World world, Wall wall, int wallX, int wallY, double oldDx, double oldDy) {
//		if(wall instanceof ActionBlock) {
//			((ActionBlock) wall).attackAction(wallX, wallY, shooter, world);
//		}
//
//		dx = dy = 0;
//	}
//
//	@Override
//	public void collisionWithStructure(World world, Structure e, double oldDx, double oldDy) {
//		dx = dy = 0;
//	}
//
//	@Override
//	public void entityCollide(Entity e, World world, double oldDx, double oldDy) {
//		if(hitting(Math.hypot(oldDx, oldDy), world, e)) {
//			dx = dy = 0;
//		}
//	}
//
//	/**
//	 *
//	 * @param speed
//	 * @param world
//	 * @param victim
//	 * @return true if attack is succes
//	 */
//	public abstract boolean hitting(double speed, World world, BreakableStructure victim);
//
//	@Override
//	public double getFriction(Floor f) {
//		return AIR_FRICTION;
//	}
}
