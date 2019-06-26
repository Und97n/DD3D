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
	
//	protected Inventory inv;
	
	public Player(double x, double y) {
		super(x, y, 0, 0, 0);

//		this.inv = inv;
	}

	@Override
	public void update(World world, double dt) {

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
