package org.zizitop.game.sprites;

import org.zizitop.game.MainActor;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Player extends Entity implements MainActor {
	private static final long serialVersionUID = 1L;

	protected double movingSpeed = 0.04;

	protected double zSpeed = 0.02;

	protected double floorZ = 0;
	protected double realZ = 0.1;
	protected double walkZ = 0;
	protected double walkZDelta = 0.18;
	protected int sectorId;
	
//	protected Inventory inv;
	
	public Player(double x, double y) {
		super(x, y, 0, 50, 0, 0);

//		this.inv = inv;
	}

	@Override
	public void update(World world, int sectorId, double dt) {
		z = realZ;

		if(walkZ <= -0.05) {
			walkZDelta = Math.abs(walkZDelta);
		} else if(walkZ >= 0.05) {
			walkZDelta = -Math.abs(walkZDelta);
		}
		walkZ += walkZDelta * getSpeed();
		
		z += walkZ;
		
		//Get floor delta z
//		//For example: player height is small, when it is in the water
//		if(world.checkCoordinates((int)x, (int)y)) {
//			Floor ground = world.getFloors()[(int)x + (int)(y) * world.getMapWidth()];
//
//			if(ground != null) {
//				final double realZ = ground.getDeltaZ();
//
//				final double dzSpeed = 0.03;
//
//				if(floorZ > realZ) {
//					if(floorZ - realZ >= dzSpeed) {
//						floorZ -= dzSpeed;
//					} else {
//						floorZ = realZ;
//					}
//				} else if(floorZ < realZ) {
//					if(realZ - floorZ >= dzSpeed) {
//						floorZ += dzSpeed;
//
//					} else {
//						floorZ = realZ;
//					}
//				}
//			}
//		}
		
		z -= floorZ;
		
		if(z <= (-0.5 + zSpeed)) z = (-0.5 + zSpeed);
		else if(z >= (0.5 - zSpeed)) z = (0.5 - zSpeed);
		
//		inv.update(world, this);
	}

	@Override
	public Bitmap getTexture() {
		return null;
	}

//	@Override
//	public ItemStorage getItemStorage() {
//		return inv;
//	}

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
	public void onSectorChange(int oldSectorId, int newSectorId) {
		sectorId = newSectorId;
	}

	@Override
	public int getSectorId() {
		return sectorId;
	}
}
