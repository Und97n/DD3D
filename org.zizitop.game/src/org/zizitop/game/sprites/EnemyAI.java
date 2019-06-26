package org.zizitop.game.sprites;

import java.io.Serializable;
import java.util.Stack;

import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.abilities.DeathAbility;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;

/**
 * 
 * @author Zizitop
 *
 */
public class EnemyAI implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Stack<Point> targets;

	private double targetX, targetY;

	private boolean targetVisible, hasTarget;

	private final Enemy actor;

	public EnemyAI(Enemy actor) {
		this.actor = actor;

		targets = new Stack<Point>();
	}

	public void updateAI(World w) {
		MainActor hero = w.getMainActor();

		if(targets.isEmpty() && hero instanceof Entity) {
			if(DeathAbility.entityIsDead((Entity) hero)) {
//				targets.push(new Point(actor.x, actor.y, actor.z));
			} else {
				targets.push((Sprite) hero);
			}
		}

		Point target = targets.peek();

		if(target instanceof TargetPoint) {
			if(--((TargetPoint) target).limiter <= 0) {
				targets.pop();

				return;
			}
		}

		if(target instanceof Entity && DeathAbility.entityIsDead((Entity) target)) {
			targets.pop();
			return;
		}

		if(w.isPointVisible(actor, target) && !(target instanceof Sprite && (!((Sprite)target).isVisible()))) {
			targetVisible = true;
			hasTarget = true;

			if(actor.isVictim(target) && actor.distance(target) < actor.getMinimumVictimDistance()) {
				targetX = actor.x;
				targetY = actor.y;
			} else {
				targetX = target.x;
				targetY = target.y;

				actor.directionX = targetX - actor.x;
				actor.directionY = targetY - actor.y;

				//Normalize vector
				{
					double lenght = Math.hypot(actor.directionX, actor.directionY);

					actor.directionX /= lenght;
					actor.directionY /= lenght;
				}
			}
		} else {
			targetVisible = false;
		}

		if(movingNotHunting()) {
			if(hero instanceof Sprite) {
				Sprite hr = (Sprite) hero;

				if((Math.abs(targetX - actor.x) < 0.5 && Math.abs(targetY - actor.y) < 0.5) ||
						(((TargetPoint)target).deleteIfSeeEnemy && w.isPointVisible(actor, hr) && hr.isVisible())) {
					targets.pop();
				}
			}
		}
	}

	public Point getCurrentTarget() {
		if(targets.isEmpty()) {
			return null;
		}

		return this.targets.peek();
	}

	public void addTarget(Sprite newTarget) {
		targets.push(newTarget);
	}

	public double getTargetX() {
		return targetX;
	}

	public double getTargetY() {
		return targetY;
	}

	public boolean isTargetVisible() {
		return targetVisible && !targets.isEmpty();
	}

	public boolean hasTagret() {
		return hasTarget;
	}

	public void moveTo(double x, double y) {
		TargetPoint p = new TargetPoint(x, y, 0, false, 60);
		targets.push(p);
	}

	public boolean movingNotHunting() {
		return targets.isEmpty() || targets.peek() instanceof TargetPoint;
	}

	private static class TargetPoint extends Point {
		private static final long serialVersionUID = 1L;
		public boolean deleteIfSeeEnemy;
		public int limiter;

		public TargetPoint(double x, double y, double z, boolean deleteIfSeeEnemy, int limiter) {
			super(x, y, z);

			this.deleteIfSeeEnemy = deleteIfSeeEnemy;

			this.limiter = limiter;
		}
	}
}
