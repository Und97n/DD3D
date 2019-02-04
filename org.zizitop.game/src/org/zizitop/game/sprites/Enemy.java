 package org.zizitop.game.sprites;

import org.zizitop.game.world.World;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class Enemy extends Entity {
	private static final long serialVersionUID = 1L;
	
	private static final int TICKS_PER_AI_UPDATE = 5;
	
	protected EnemyAI ai;
	
	private int aiTimeCounter = 0;
	
	public double directionX, directionY;
	
	public Enemy(double x, double y, double z, double dx, double dy) {
		super(x, y, z, dx, dy);
		
		aiTimeCounter = (int) (Math.random() * TICKS_PER_AI_UPDATE);
		
//		ai = new EnemyAI(this);
	}

	@Override
	public void update(World world, double dt) {
		++aiTimeCounter;

		while(aiTimeCounter >= TICKS_PER_AI_UPDATE ) {
//			ai.updateAI(world);

			aiTimeCounter -= TICKS_PER_AI_UPDATE;
		}

//		if(ai.hasTagret()) {
//			dx = ai.getTargetX() - x;
//			dy = ai.getTargetY() - y;
//		}
	}

	public boolean isMoving() {
		final double tmp = Math.hypot(dx, dy);
		return (tmp / getMaxSpeed()) > 0.1;
	}

	public abstract boolean isVictim(Point p);
	public abstract double getMinimumVictimDistance();
}
