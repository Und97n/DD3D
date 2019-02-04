package org.zizitop.game.sprites;

import org.zizitop.game.MainActor;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.Window;

/**
 * TODO REWRITE OR REMOVE THIS CLASS
 * @author Zizitop
 *
 */
public abstract class Player extends Entity implements MainActor {
	private static final long serialVersionUID = 1L;

	protected double zSpeed = 0.02;

	protected double floorZ = 0;
	protected double walkZ = 0;
	protected double walkZDelta = 0.11;
	
//	protected Inventory inv;
	
	public Player(double x, double y) {
		super(x, y, 0, 0, 0);

//		this.inv = inv;
	}

	@Override
	public void update(World world, double dt) {
		z = getEyeHeight();

		if(walkZ <= -0.1) {
			walkZDelta = Math.abs(walkZDelta);
		} else if(walkZ >= 0.1) {
			walkZDelta = -Math.abs(walkZDelta);
		}
		walkZ += walkZDelta * getSpeed()*dt;
		
		z += walkZ;

		final double realZ = world.getLevel().getFloorLevel(sectorId);

		final double dzSpeed = 10*dt;

		if(floorZ > realZ) {
			if(floorZ - realZ >= dzSpeed) {
				floorZ -= dzSpeed;
			} else {
				floorZ = realZ;
			}
		} else if(floorZ < realZ) {
			if(realZ - floorZ >= dzSpeed) {
				floorZ += dzSpeed;

			} else {
				floorZ = realZ;
			}
		}

		z += floorZ;

//		if(z <= (-0.5 + zSpeed)) z = (-0.5 + zSpeed);
//		else if(z >= (0.5 - zSpeed)) z = (0.5 - zSpeed);
		
//		inv.update(world, this);
	}

	@Override
	public Bitmap getTexture() {
		return null;
	}

	@Override
	public double getPosX() {
		return x;
	}

	@Override
	public double getPosY() {
		return y;
	}

	@Override
	public boolean lockUserMouse() {
		return true;
	}

	@Override
	public int getSectorId() {
		return sectorId;
	}

	public abstract double getEyeHeight();
}
