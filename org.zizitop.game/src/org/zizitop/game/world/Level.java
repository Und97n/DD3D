package org.zizitop.game.world;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Sprite;
import org.zizitop.game.sprites.Structure;

import java.util.function.BiConsumer;
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

	public Level(double[] verticies, Sector[] sectors) {
		this.verticies = verticies;
		this.sectors = sectors;

		for(int i = 0; i < sectors.length; ++i) {
			sectors[i].init(this, i);
		}
	}

	/**
	 * Function for updating position of some object. If it is moved, then we need
	 * to check it`s position and, if needed, place it to another sector.
	 * @param obj object to check
	 * @param sectorId previous sector
	 * @return true if all is good, false if object is outside the map
	 */
	boolean updateObjectPosition(Sprite obj, int sectorId) {
		int newSector = getSectorId(obj.x, obj.y, sectorId);

		if(newSector == -1) {
			return false;
		}

		if(newSector != sectorId) {
			sectors[sectorId].remove(obj);
			sectors[newSector].add(obj);
			obj.onSectorChange(sectorId, newSector);
		}

		return true;
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
			proceedSectorEntities(c, i);
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
			proceedSectorStructures(c, i);
		}
	}
}
