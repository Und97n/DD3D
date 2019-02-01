package org.zizitop.game.world;

import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.Entity;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.window.Window;

/**
 * <br><br>
 * Created 30.01.19 19:15
 *
 * @author Zizitop
 */
public class World {
	private final Level level;
	private final MainActor mainActor;

	public World(Level level, MainActor mainActor) {
		this.level = level;
		this.mainActor = mainActor;
	}

	public Level getLevel() {
		return level;
	}

	public void update(double dt, Window w) {
		mainActor.proceedInput(w);
		mainActor.mainActorTick(this);

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.update(this, i, dt);
				e.moveStart(this, i, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.proceedCollision(this, i, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				e.moveEnd(this, i, dt);
				e = (Entity) e.listNext;
			}
		}

		for(int i = 0; i < level.sectors.length; ++i) {
			Sector s = level.sectors[i];

			Entity e = (Entity) s.entitiesList;

			while(e != null) {
				Entity ee = e;
				e = (Entity) e.listNext;

				if(!level.updateObjectPosition(ee, i)) {
					Log.write("Entity " + ee + " is outside the map. It will be removed.", 2);
				} else if(e.isDead()) {
					// Remove shit
					s.removeEntity(ee);
				}
			}
		}
	}

	public MainActor getMainActor() {
		return mainActor;
	}
}
