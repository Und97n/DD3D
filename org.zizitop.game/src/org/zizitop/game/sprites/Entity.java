package org.zizitop.game.sprites;

import org.zizitop.game.sprites.abilities.AbilityHolder;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Utils;

/**
 * {@link Structure}, that is movable and each-tick-updatable.
 * Each tick world must invoke such methods in such order:<br>
 *     - {@link #update(World, double)}<br>
 *     - {@link #moveStart(World, double)}<br>
 *     - {@link #proceedCollision(World, double)}<br>
 *     - {@link #moveEnd(World, double)}<br>
 * @author Zizitop
 *
 */
public abstract class Entity extends Structure {
	private static final long serialVersionUID = 1L;
	
	public static final double AIR_FRICTION = 0.999;

	protected final AbilityHolder abilityHolder = new AbilityHolder();

	public double dx, dy, accX, accY;
	
	public Entity(double x, double y, double z, double dx, double dy) {
		super(x, y, z);
		
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * This method calls on every tick. Place here your AI and some another things.
	 * @param world - current universe
	 */
	public void update(World world, double dt) {
		abilityHolder.update(this, world);
	}

	/**
	 * Update entity speed.
	 * @param world - our universe
	 * @param dt - difference in time between callings this methods.
	 */
	public final void moveStart(World world, double dt) {
		double speed = getSpeed(), maxSpeed = getMaxSpeed();

		if(speed > 0) {
			double friction = getFrictionConst(world, sectorId)*speed / dt;

			accX -= friction * (dx / speed);
			accY -= friction * (dy / speed);
		}

		// Intregrator
		dx += accX * dt;
		dy += accY * dt;

		accX = accY = 0;

		double speedMultiplier = 1;

		if(speed > maxSpeed) {
			speedMultiplier *= maxSpeed / speed;
		}

		dx *= speedMultiplier;
		dy *= speedMultiplier;
	}

	protected double getFrictionConst(World world, int sectorId) {
		double frictionC = world.getLevel().getFriction(sectorId);

		return frictionC;
	}

	public final void proceedCollision(World world, double dt) {
		if(!isSolid() || sectorId < 0) {
			return;
		}

		Utils.DoubleVector dd = new Utils.DoubleVector(dx*dt, dy*dt);

		world.getLevel().collideEntity(this, sectorId, dd);

		dx = dd.x/dt;
		dy = dd.y/dt;

		world.getLevel().proceedNearbyStructures((s) -> proceedCollision(world, s, dt), sectorId);
		world.getLevel().proceedNearbyEntities((e) -> proceedCollision(world, e, this, dt), sectorId);
	}

	/**
	 * Move entity.
	 * @param world - our universe
	 * @param dt - difference in time between callings this methods.
	 */
	public final void moveEnd(World world, double dt) {
		final double oldX = x, oldY = y;

		x += dx * dt;
		y += dy * dt;
	}

	/**
	 * Add some acceleration to our object.
	 * Acceleration sets to 0 after calling move() method.
	 * @param ax - acceleration x component
	 * @param ay - acceleration y component
	 */
	public void addAcceleration(double ax, double ay) {
		accX += ax;
		accY += ay;
	}

	private void proceedCollision(World w, Structure s, double dt) {
		if(!s.isSolid()) {
			return;
		}

		if(!s.isSolid()) {
			if(s.intersects(this)) {
				onStructureCollision(s, w);
			}
			return;
		}

		final double x = this.x + dx*dt, y = this.y + dy*dt;
		final double oldX = this.x, oldY = this.y;

		final double sizeX = getSizeX(), sizeY = getSizeY(), ssizeX = s.getSizeX(), ssizeY = s.getSizeY();

		// S means Struct, E means end cord(right border)
		final double xe = x + sizeX, ye = y + sizeY;
		final double sx = s.x, sy = s.y, sxe = s.x + ssizeX, sye = s.y + ssizeY;

		final double elasticity = (this.getElasticity() + s.getElasticity()) * 0.5;

		// X axis
		if(x > oldX) {
			//     _____
			//    | Struct
			//  [ |]
			//    |_____
			if(xe > sx && oldX < sxe) {
				// Abstract time, when we collide struct
				double collisionTime = (sx - (oldX + sizeX)) / (x - oldX);

				if(collisionTime >= 0) {
					double yy = oldY + (y - oldY) * collisionTime, yye = yy + sizeY;

					if(yye > sy && yy < sye) {
						onStructureCollision(s, w);

						this.x = sx - sizeX;
						this.dx = -Math.abs(dx) * elasticity;
					}
				}
			}
		} else if(x < oldX) {
			//_______
			// Struct|
			//      [| ]
			//_______|
			if(x < sxe && (oldX + sizeX) > sx) {
				// Abstract time, when we collide struct
				double collisionTime = (oldX - sxe) / (oldX - x);

				if(collisionTime >= 0) {
					double yy = oldY + (y - oldY) * collisionTime, yye = yy + sizeY;

					if(yye > sy && yy < sye) {
						onStructureCollision(s, w);

						this.x = sxe;
						this.dx = Math.abs(dx) * elasticity;
					}
				}
			}
		}

		// Y axis
		if(y > oldY) {
			//    __
			// __|__|__
			//|  |__|
			//|Struct
			if(ye > sy && oldY < sye) {
				// Abstract time, when we collide struct
				double collisionTime = (sy - (oldY + sizeY)) / (y - oldY);

				if(collisionTime >= 0) {
					double xx = oldX + (x - oldX) * collisionTime, xxe = xx + sizeX;

					if(xxe > sx && xx < sxe) {
						onStructureCollision(s, w);

						this.y = sy - sizeY;
						this.dy = -Math.abs(dy) * elasticity;
					}
				}
			}
		} else if(y < oldY) {
			//|Struct
			//|    __
			//|___|__|__
			//    |__|
			//
			if(y < sye && (oldY + sizeY) > sy) {
				// Abstract time, when we collide struct
				double collisionTime = (oldY - sye) / (oldY - y);

				if(collisionTime >= 0) {
					double xx = oldX + (x - oldX) * collisionTime, xxe = xx + sizeX;

					if(xxe > sx && xx < sxe) {
						onStructureCollision(s, w);

						this.y = sye;
						this.dy = Math.abs(dy) * elasticity;
					}
				}
			}
		}
	}

	private static void proceedCollision(World w, Entity s1, Entity s2, double dt) {
		if(!s1.isSolid() || !s2.isSolid() || s1 == s2) {
			return;
		}

		final double m1 = s1.getMass(), m2 = s2.getMass();

		final double oldX1 = s1.x, oldY1  = s1.y, oldX2 = s2.x, oldY2 = s2.y;

		final double x1 = s1.x + s1.dx * dt;
		final double y1 = s1.y + s1.dy * dt;
		final double x2 = s2.x + s2.dx * dt;
		final double y2 = s2.y + s2.dy * dt;

		final double sizeX1 = s1.getSizeX();
		final double sizeY1 = s1.getSizeY();
		final double sizeX2 = s2.getSizeX();
		final double sizeY2 = s2.getSizeY();

		// x and y speed between entities
		double ddvx, ddvy;

		ddvx = s1.dx - s2.dx;

		if((s1.dx >= 0) == (s2.dx >= 0)) {
			ddvx *= 2;
		}

		ddvy = s1.dy - s2.dy;

		if((s1.dy >= 0) == (s2.dy >= 0)) {
			ddvy *= 2;
		}

//		final double ddvx1 = +s1.dx - (((s1.dx >= 0) == (s2.dx >= 0)) ? 0 : s2.dx);
//		final double ddvx2 = -s2.dx + (((s1.dx >= 0) == (s2.dx >= 0)) ? 0 : s1.dx);
//		final double ddvy1 = +s1.dy - (((s1.dy >= 0) == (s2.dy >= 0)) ? 0 : s2.dy);
//		final double ddvy2 = -s2.dy + (((s1.dy >= 0) == (s2.dy >= 0)) ? 0 : s1.dy);
//
//		final boolean rightS2 = (x2 + sizeX2/2) > (x1 + sizeX1);
//		final boolean downS2 = (y2 + sizeY2/2) > (y1 + sizeY1);
//		final boolean leftS2 = !rightS2;
//		final boolean upS2 = !downS2;
//
//		// If sings was different, choices makes indifferent
//		final boolean moveRigth = s1.dx > 0;
//		final boolean moveDown = s1.dy > 0;
//
//		final double ddvx = (rightS2 ^ moveRigth) ? ddvx1 : ddvx2;
//		final double ddvy = (downS2 ^ moveDown) ? ddvy1 : ddvy2;

		final boolean xIntOld = (oldX1 + sizeX1 >= oldX2) && (oldX1 <= oldX2 + sizeX2);
		final boolean yIntOld = (oldY1 + sizeY1 >= oldY2) && (oldY1 <= oldY2 + sizeY2);

		final boolean xInt = (x1 + sizeX1 >= x2) && (x1 <= x2 + sizeX2);
		final boolean yInt = (y1 + sizeY1 >= y2) && (y1 <= y2 + sizeY2);

		final double elasticity = (s1.getElasticity() + s2.getElasticity()) * 0.5;

		// Abstract time, when we collide struct
		final double collisionTimeX1 = (oldX2 - (oldX1 + sizeX1)) / ddvx;
		final double collisionTimeX2 = ((oldX2 + sizeX2) - oldX1) / ddvx;

		final double collisionTimeY1 = (oldY2 - (oldY1 + sizeY1)) / ddvy;
		final double collisionTimeY2 = ((oldY2 + sizeY2) - oldY1 ) / ddvy;

		final boolean timeX1Correct = collisionTimeX1 >= 0 && collisionTimeX1 <= dt;
		final boolean timeX2Correct = collisionTimeX2 >= 0 && collisionTimeX2 <= dt;
		final boolean timeY1Correct = collisionTimeY1 >= 0 && collisionTimeY1 <= dt;
		final boolean timeY2Correct = collisionTimeY2 >= 0 && collisionTimeY2 <= dt;

		final boolean xIntersection = timeX1Correct || timeX2Correct || xInt || xIntOld;
		final boolean yIntersection = timeY1Correct || timeY2Correct || yInt || yIntOld;

		if(xIntersection && yIntersection) {
			s1.onEntityCollision(s2, w);
			s2.onEntityCollision(s1, w);

			s1.onEntityCollision(s2, w);
			s2.onEntityCollision(s1, w);

			if(!s1.isSolid() || !s2.isSolid()) {
				return;
			}
		}

		double powerX = 0, powerY = 0;

		if(yIntersection) {
			if(timeX1Correct) {
				//     _____
				//    | S2
				//  [ |]
				//    |_____
				double multiplier = collisionTimeX1/dt;

				if(s1.dx > 0) {
					double oldDx = s1.dx;
					s1.dx *= multiplier;

					powerX -= (oldDx - s1.dx) * m1;
				}

				if(s2.dx < 0) {
					double oldDx = s2.dx;
					s2.dx *= multiplier;

					powerX += (oldDx - s1.dx) * m2;
				}
			} else if(timeX2Correct) {
				//_______
				//     S2|
				//      [| ]
				//_______|
				double multiplier = collisionTimeX2/dt;

				if(s1.dx < 0) {
					double oldDx = s1.dx;
					s1.dx *= multiplier;

					powerX -= (oldDx - s1.dx) * m1;
				}

				if(s2.dx > 0) {
					double oldDx = s2.dx;
					s2.dx *= multiplier;

					powerX += (oldDx - s1.dx) * m2;
				}
			}
		}

		if(xIntersection) {
			if(timeY1Correct) {
				//    __
				// __|__|__
				//|  |__|
				//|S2
				double multiplier = collisionTimeY1/dt;

				if(s1.dy > 0) {
					double oldDy = s1.dy;
					s1.dy *= multiplier;

					powerY -= (oldDy - s1.dy) * m1;
				}

				if(s2.dy < 0) {
					double oldDy = s2.dy;
					s2.dy *= multiplier;

					powerY += ( oldDy - s1.dy) * m2;
				}
			} else if(timeY2Correct) {
				//|S2
				//|    __
				//|___|__|__
				//    |__|
				//
				double multiplier = collisionTimeY2/dt;

				if(s1.dy < 0) {
					double oldDy = s1.dy;
					s1.dy *= multiplier;

					powerY -= (oldDy - s1.dy) * m1;
				}

				if(s2.dy > 0) {
					double oldDy = s2.dy;
					s2.dy *= multiplier;

					powerY += (oldDy - s1.dy) * m2;
				}
			}
		}

		powerX *= elasticity / dt;
		powerY *= elasticity / dt;

		s1.addAcceleration(+powerX / m1, +powerY / m1);
		s2.addAcceleration(-powerX / m2, -powerY / m2);

		if(yIntOld && xIntOld) {
//			double ddx = 0, ddy = 0;
//
//			if(s1.y < s2.y) {
//				ddy = y2 - (y1 + sizeY1);
//			} else {
//				ddy = (y2 + sizeY2) - y1;
//			}
//
//			if(s1.x < s2.x) {
//				ddx = x2 - (x1 + sizeX1);
//			} else {
//				ddx = (x2 + sizeX2) - x1;
//			}
//
//			s1.x += +ddx/2;
//			s2.x += -ddx/2;
//
//			s1.y += +ddy/2;
//			s2.y += -ddy/2;
		}
	}

	public void onStructureCollision(Structure s, World w) {}
	
	public abstract double getMaxSpeed();
	
	public boolean canMove() {
		return true;
	}

	public double getSpeed() {
		return Math.hypot(dx, dy);
	}

	public abstract double getMass();

	public AbilityHolder getAbilityHolder() {
		return abilityHolder;
	}

	@Override
	public void onRemoving(World world) {
		abilityHolder.proceedAbilities(a -> a.onEntityRemoving(Entity.this, world));
	}

	public abstract double getKneeHeight();
	public abstract double getHeight();
}