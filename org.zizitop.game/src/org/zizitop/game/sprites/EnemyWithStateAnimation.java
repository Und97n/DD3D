package org.zizitop.game.sprites;

import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.animation.Animation;
import org.zizitop.pshell.utils.animation.StateManager;

public abstract class EnemyWithStateAnimation extends Enemy implements StateManager.StateManagerHead {
	public static final double attackDistance = 0.7;
	
	private static final long serialVersionUID = 1L;

	protected StateManager animation;
	
	private double attackDirectionX, attackDirectionY;
	private double attackWaiter;
	
	public EnemyWithStateAnimation(double x, double y, double z, double HP, double dx, double dy) {
		super(x, y, z, HP, dx, dy);
		
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

	protected abstract Animation getCorpseAnimation();

	@Override
	public void update(World world, int sectorId, double dt) {
		if(animation.getCurrentStateId() == getDefaultStateId() && isMoving()) {
			animation.switchState(getMovingStateId());
		}

		if(animation.getCurrentStateId() == getMovingStateId()) {
			animation.update();
		} else {
			animation.update();
		}

		if(animation.getCurrentStateId() != getAttackStateId()) {
			--attackWaiter;

			if(attackWaiter < 0) {
				attackWaiter = 0;

//				if(ai.isTargetVisible()) {
//					Point p = ai.getCurrentTarget();
//
//					if(isVictim(p) && distance(p) <= getMinDistanceToTargetForAttack()) {
//						animation.switchState(getAttackStateId());
//
//						attackDirectionX = directionX;
//						attackDirectionY = directionY;
//					}
//				}
			}
		} else {
			int statePointer = animation.getStateTime();

			if(attackDirectionX == attackDirectionX && attackDirectionY == attackDirectionY &&
//					isVictim(ai.getCurrentTarget()) &&
					statePointer < getAttackStateManagerAttackFrameTopBorder() &&
					statePointer > getAttackStateManagerAttackFrameBottomBorder()) {
				attack(attackDirectionX, attackDirectionY, world);

				attackDirectionX = attackDirectionY = Double.NaN;

				attackWaiter = getAttackDeltaTime();
			}
		}

	}

	@Override
	public double damage(double damageValue, World world) {
		double ret = super.damage(damageValue, world);

		if(ret != 0) {
			animation.switchState(getDamageStateId());
		}

//		if(HP <= 0) {
//			world.add(new Corpse());
//		}

		return ret;
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

//	private class Corpse extends Entity {
//		private double animPointer =
//				EnemyWithStateAnimation.this.getCorpseAnimation().restartAnimation();
//
//		public Corpse() {
//			super(EnemyWithStateAnimation.this.x, EnemyWithStateAnimation.this.y,
//					EnemyWithStateAnimation.this.z, Double.MAX_VALUE, 0, 0);
//		}
//
//		@Override
//		public boolean move(World w) {
//			animPointer = EnemyWithStateAnimation.this.getCorpseAnimation().update(animPointer);
//
//			return super.move(w);
//		}
//
//		@Override
//		public double getMaxSpeed() {
//			return 0;
//		}
//
//		@Override
//		public double getMaxHP() {
//			return Double.MAX_VALUE;
//		}
//
//		@Override
//		public boolean isSolid() {
//			return false;
//		}
//
//		@Override
//		public double getSizeX() {
//			return EnemyWithStateAnimation.this.getSizeX();
//		}
//
//		@Override
//		public double getSizeY() {
//			return EnemyWithStateAnimation.this.getSizeY();
//		}
//
//		@Override
//		public Bitmap getTexture() {
//			return EnemyWithStateAnimation.this.getCorpseAnimation().getCurrentFrame(animPointer);
//		}
//
//		@Override
//		public int getScreenSizeX() {
//			return getTexture().width;
//		}
//
//		@Override
//		public int getScreenSizeY() {
//			return getTexture().height;
//		}
//	}
}
