package org.zizitop.game.world;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Sprite;
import org.zizitop.game.sprites.Structure;
import org.zizitop.pshell.utils.Rectangle;
import org.zizitop.pshell.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <br><br>
 * Created 30.01.19 19:09
 *
 * @author Zizitop
 */
public class Level {
	final double[] verticies;
	final Sector[] sectors;

	private static final class Wall {
		final double x1, y1, x2, y2;

		double nx, ny;

		private Wall(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public boolean collideVector(double x, double y, Utils.DoubleVector velocity) {
			Utils.DoubleVector tmp = new Utils.DoubleVector();

			if (!Utils.lineSegmentIntersection(x1, y1, x2, y2, x, y, x + velocity.x, y + velocity.y, tmp)) {
				return false;
			}

			if (nx == 0 && ny == 0) {
				nx = y2 - y1;
				ny = x1 - x2;

				double nn = Math.hypot(nx, ny);
				nx /= nn;
				ny /= nn;
			}

			double clippedX = (x + velocity.x - tmp.x);

			if (clippedX * nx > 0) {
				clippedX = 0;
			}

			double clippedY = (y + velocity.y - tmp.y);

			if (clippedY * ny > 0) {
				clippedY = 0;
			}

			if (clippedX == 0 && clippedY == 0) {
				return false;
			}

			double p = (clippedX * ny - clippedY * nx);

			velocity.x -= clippedX * 1.1;
			velocity.y -= clippedY * 1.1;
			velocity.x += +p * ny;
			velocity.y += -p * nx;

			return true;
		}
	}

	public Level(double[] verticies, Sector[] sectors) {
		this.verticies = verticies;
		this.sectors = sectors;

		for(int i = 0; i < sectors.length; ++i) {
			sectors[i].init(this, i);
		}
	}

	public void collideEntity(Entity e, int sectorId, Utils.DoubleVector velocity) {
		if(velocity.x == 0 && velocity.y == 0) {
			return;
		}

		final double ssx = e.getSizeX()/2, ssy = e.getSizeY()/2;

		Sector s = sectors[sectorId];

		double speed = Math.hypot(velocity.x, velocity.y);

		double aabbSizeX = speed + ssx;
		double aabbSizeY = speed + ssy;

		ArrayList<Wall> walls = new ArrayList<>();

		double ex1 = e.x - aabbSizeX, ey1 = e.y - aabbSizeY, ex2 = e.x + aabbSizeX, ey2 = e.y + aabbSizeY;

		proceedNearWalls(walls, e, s, ex1, ey1, ex2, ey2);

		for(Sector ss: s.neighbours) {
			proceedNearWalls(walls, e, ss, ex1, ey1, ex2, ey2);
		}

		int iterations = 0, maxIterations = 5;
		boolean c;

		do {
			c = false;
			for(Wall w: walls) {
				c = c || w.collideVector(e.x + ssx, e.y + ssy, velocity);
				c = c || w.collideVector(e.x - ssx, e.y + ssy, velocity);
				c = c || w.collideVector(e.x + ssx, e.y - ssy, velocity);
				c = c || w.collideVector(e.x - ssx, e.y - ssy, velocity);
			}

			if(c && ++iterations >= maxIterations) {
				velocity.x = velocity.y = 0;
				break;
			}
		} while(c);
	}

	private void proceedNearWalls(List<Wall> wallsList, Entity e, Sector ss, double ex1, double ey1, double ex2, double ey2) {
		int i, j;

		for(i = 1, j = 0; i < ss.verticies.length; j = i++) {
			if(ss.walls[j] < 0) {
				continue;
			}

			int iv = ss.verticies[i];
			int jv = ss.verticies[j];

			double ix = verticies[iv*2 + 0], iy = verticies[iv*2 + 1];
			double jx = verticies[jv*2 + 0], jy = verticies[jv*2 + 1];

			double wx1 = Math.min(ix, jx), wx2 = Math.max(ix, jx);
			double wy1 = Math.min(iy, jy), wy2 = Math.max(iy, jy);

			if(
					(ex2 >= wx1 && ex1 <= wx2) &&
					(ey2 >= wy1 && ey1 <= wy2)
			) {
				wallsList.add(new Wall(ix, iy, jx, jy));
			}
		}
	}

	/**
	 * Function for updating position of some {@link Sprite}. If it is moved, then we need
	 * to check it`s position and, if needed, place it to another sector.
	 * @param obj object to check
	 * @param sectorId previous sector
	 * @return true if all is good, false if object is outside the map
	 */
	boolean updatePosition(Sprite obj, int sectorId) {
		int newSector = getSectorId(obj.x, obj.y, sectorId);

		if(newSector == -1) {
			return false;
		}

		if(newSector != sectorId) {
			sectors[sectorId].remove(obj);
			sectors[newSector].add(obj);
			obj.sectorId = newSector;
			obj.onSectorChange(sectorId, newSector);
		}

		return true;
	}

	public void add(Sprite object) {
		sectors[getSectorId(object.x, object.y, 0)].add(object);
	}

	/**
	 * Get if of sector, that contains this point.
	 * @param x point x coordinate.
	 * @param y point y coordinate.
	 * @param oldSectorId old sector. If no old sector - place -1
	 * @return id of sector, that contains point. If no such sector - return -1.
	 */
	public int getSectorId(double x, double y, int oldSectorId) {
		Sector s = sectors[oldSectorId];

		if(pointInSector(x, y, s)) {
			// Sector not changed
			return oldSectorId;
		}

		// Check neighbours
		for(int i = 0; i < s.neighbours.length; ++i){
			if(pointInSector(x, y, s.neighbours[i])) {
				return s.neighbours[i].id;
			}
		}

		// Check all sectors
		return getSectorId(x, y);
	}

	/**
	 * Get if of sector, that contains this point.
	 * @param x point x coordinate.
	 * @param y point y coordinate.
	 * @return id of sector, that contains point. If no such sector - return -1.
	 */
	public int getSectorId(double x, double y) {
		// Check all sectors
		for(int i = 0; i < sectors.length; ++i){
			if(pointInSector(x, y, sectors[i])) {
				return i;
			}
		}

		// No sector!
		return -1;
	}


	/**
	 * Return true if point is inside needed sector
	 */
	public boolean pointInSector(double x, double y, Sector s) {
		// PIP(point in polygon) algorithm
		// Trace a ray to the left and count intersected walls
		// If count is pair number - we outside, else - inside

		// First and second vertex of wall
		int i, j;
		int nvert = s.verticies.length;
		boolean c = false;

		for(i = 0, j = nvert - 1; i < nvert; j = i++) {
			int iv = s.verticies[i];
			int jv = s.verticies[j];

			double ix = verticies[iv*2 + 0], iy = verticies[iv*2 + 1];
			double jx = verticies[jv*2 + 0], jy = verticies[jv*2 + 1];

			if(
					(iy >= y) != (jy >= y) &&
					(x <= (jx-ix)*(y-iy)/(jy-iy) + ix)) {

				c = !c;
			}
		}

		return c;
	}

	public double getFriction(int sectorId) {
		return sectors[sectorId].getFriction();
	}

	public double getFloorLevel(int sectorId) {
		return sectors[sectorId].floor;
	}

	public double getCeilingLevel(int sectorId) {
		return sectors[sectorId].ceiling;
	}

	public void proceedSectorEntities(Consumer<Entity> c, int sectorId) {
		Sector s = sectors[sectorId];

		Entity e = (Entity) s.entitiesList;

		while(e != null) {
			c.accept(e);
			e = (Entity) e.listNext;
		}
	}

	public void proceedNearbyEntities(Consumer<Entity> c, int sectorId) {
		Sector s = sectors[sectorId];

		for(int i = 0; i < s.neighbours.length; ++i) {
			proceedSectorEntities(c, s.neighbours[i].id);
		}
	}

	public void proceedSectorStructures(Consumer<Structure> c, int sectorId) {
		Sector s = sectors[sectorId];

		Structure ss = (Structure) s.structuresList;

		while(ss != null) {
			c.accept(ss);
			ss = (Structure) ss.listNext;
		}
	}

	public void proceedNearbyStructures(Consumer<Structure> c, int sectorId) {
		Sector s = sectors[sectorId];

		for(int i = 0; i < s.neighbours.length; ++i) {
			proceedSectorStructures(c, s.neighbours[i].id);
		}
	}
}
