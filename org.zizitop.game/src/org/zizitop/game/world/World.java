package org.zizitop.game.world;

import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Sprite;
import org.zizitop.game.sprites.Structure;
import org.zizitop.game.sprites.abilities.Ability;
import org.zizitop.game.sprites.abilities.DeathAbility;
import org.zizitop.pshell.DefaultContext;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.window.Window;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * <br><br>
 * Created 30.01.19 19:15
 *
 * @author Zizitop
 */
public class World {
	private final Level level;
	private final MainActor mainActor;

	// List for sprites, structures and entities, that must be removed.
	private transient ArrayList<Sprite> removedObjects;
	// List for sprites, structures and entities, that must be added.
	private transient ArrayList<Sprite> addedObjects;

	public World(Level level, MainActor mainActor) {
		this.level = level;
		this.mainActor = mainActor;
	}

	public Level getLevel() {
		return level;
	}

	public void update(double dt, Window w) {
		// Because transient
		if(removedObjects == null) removedObjects = new ArrayList<>();
		if(addedObjects == null) addedObjects = new ArrayList<>();

		mainActor.proceedInput(w);
		mainActor.mainActorTick(this);

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.update(this, dt);
				e.moveStart(this, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.proceedCollision(this, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.moveEnd(this, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				Entity ee = e;
				e = (Entity) e.listNext;

				if(!level.updatePosition(ee, i)) {
					Log.write("Entity " + ee + " is outside the map. It will be removed.", 2);
				} else {
					DeathAbility[] deathAbilities = ee.getAbilityHolder().getAbilities(DeathAbility.class);
					// If our Entity is dead.
					for(DeathAbility a: deathAbilities) {
						if(a.ownerIsDead()) {
							remove(ee);
							break;
						}
					}
				}
			}
		}

		// Remove all needed
		for(Sprite s: removedObjects) {
			int sectorID = level.getSectorId(s.x, s.y, s.sectorId);

			if(sectorID >= 0) {
				if(!level.sectors[sectorID].remove(s)) {
					// Problems!
					throw new RuntimeException("Can`t remove object " + s + ": no such object.");
				} else {
					// Removed successfully
					// Proceed onRemoving method
					s.onRemoving(this);
				}
			} else {
				// Problems!
				throw new RuntimeException("No sector for " + s +  ". Can`t remove object.");
			}
		}

		removedObjects.clear();

		// Add all needed
		for(Sprite s: addedObjects) {
			int sectorID = level.getSectorId(s.x, s.y);

			if(sectorID >= 0) {
				level.sectors[sectorID].add(s);
			} else {
				// Problems!
				throw new RuntimeException("No sector for " + s +  ". Can`t add object.");
			}
		}

		addedObjects.clear();
	}

	/**
	 * Remove some object from the world at the next tick.
	 */
	public void remove(Sprite s) {
		removedObjects.add(s);
	}

	/**
	 * Add some object to the world at the next tick.
	 */
	public void add(Sprite s) {
		addedObjects.add(s);
	}

	public MainActor getMainActor() {
		return mainActor;
	}
}
