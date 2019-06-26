package org.zizitop.game.sprites;

import org.zizitop.game.sprites.abilities.DeathAbility;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.animation.Animation;
import org.zizitop.pshell.utils.animation.StateManager;

/**
 * <br><br>
 * Created 09.02.19 17:50
 *
 * @author Zizitop
 */
public abstract class EnemyWithStateAnimation extends Enemy implements StateManager.StateManagerHead, HealthAbility.HealthListener {
	public static final double attackDistance = 0.7;

	private static final long serialVersionUID = 1L;

	protected StateManager animation;

	private double attackDirectionX, attackDirectionY;
	private double attackWaiter;

	public EnemyWithStateAnimation(double x, double y, double z, double dx, double dy) {
		super(x, y, z, dx, dy);

		animation = new StateManager(this);
	}

	protected abstract int getDefaultStateId();
	protected abstract int getDamageStateId();
	protected abstract int getAttackStateId();
	protected abstract int getMovingStateId();

	protected abstract int getAttackDeltaTime();
	protected abstract int getAttackStateManagerAttackFrameTopBorder();
	protected abstract int getAttackStateManagerAttackFrameBottomBorder();
	protected abstract double getMinDistanceToTargetForAttack();

	protected abstract Animation getDeathAnimation();

	@Override
	public void update(World world, double dt) {
		if(DeathAbility.entityIsDead(this)) {
			world.add(new Corpse());
		}

		if(animation.getCurrentStateId() == getDefaultStateId() && isMoving()) {
			animation.switchState(getMovingStateId());
		}

		animation.update(dt);

		if(animation.getCurrentStateId() != getAttackStateId()) {
			--attackWaiter;

			if(attackWaiter < 0) {
				attackWaiter = 0;

				if(ai.isTargetVisible()) {
					Point p = ai.getCurrentTarget();

					if(isVictim(p) && distance(p) <= getMinDistanceToTargetForAttack()) {
						animation.switchState(getAttackStateId());

						attackDirectionX = directionX;
						attackDirectionY = directionY;
					}
				}
			}
		} else {
			int statePointer = animation.getStateTime();

			if(attackDirectionX == attackDirectionX && attackDirectionY == attackDirectionY &&
					isVictim(ai.getCurrentTarget()) &&
					statePointer < getAttackStateManagerAttackFrameTopBorder() &&
					statePointer > getAttackStateManagerAttackFrameBottomBorder()) {
				attack(attackDirectionX, attackDirectionY, world);

				attackDirectionX = attackDirectionY = Double.NaN;

				attackWaiter = getAttackDeltaTime();
			}
		}

		super.update(world, dt);
	}

	@Override
	public void onDamage(double newHp, double damageValue, Entity source) {
		if(damageValue != 0) {
			animation.switchState(getDamageStateId());
		}
	}

	@Override
	public Bitmap getTexture() {
		return animation.getImage();
	}

	public boolean canMove() {
		int state = animation.getCurrentStateId();

		return state != getAttackStateId() && state != getDamageStateId();
	}

	public void attack(double attackDirectionX, double attackDirectionY, World w) {}

	@Override
	public void proceedStateChange(StateManager as, int oldState, int newState) {
		if(!(oldState == getAttackStateId() && newState != getDamageStateId())) {
			attackDirectionX = attackDirectionY = Double.NaN;
		}
	}

	private class Corpse extends Entity {
		double animationPtr;
		public Corpse() {
			super(EnemyWithStateAnimation.this.x, EnemyWithStateAnimation.this.y,
					EnemyWithStateAnimation.this.z, 0, 0);
		}

		@Override
		public void update(World world, double dt) {
			animationPtr = EnemyWithStateAnimation.this.getDeathAnimation().update(animationPtr, dt);
			super.update(world, dt);
		}

		@Override
		public double getElasticity() {
			return 0;
		}

		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public double getSizeX() {
			return EnemyWithStateAnimation.this.getSizeX();
		}

		@Override
		public double getSizeY() {
			return EnemyWithStateAnimation.this.getSizeY();
		}

		@Override
		public Bitmap getTexture() {
			return EnemyWithStateAnimation.this.getDeathAnimation().getCurrentFrame(animationPtr);
		}

		@Override
		public double getMaxSpeed() {
			return 0;
		}

		@Override
		public double getMass() {
			return EnemyWithStateAnimation.this.getMass();
		}

		@Override
		public double getKneeHeight() {
			return 0;
		}

		@Override
		public double getHeight() {
			return 0;
		}
	}
}
