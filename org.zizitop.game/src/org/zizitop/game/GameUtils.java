package org.zizitop.game;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Sprite;
import org.zizitop.game.sprites.Structure;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

/**
 * Just simple class for different utils. Like {@link Utils}, but only for game.
 * <br><br>
 * Created 23.08.2017 22:11:12
 * @author Zizitop
 */
public class GameUtils {
	/**
	 * Function for melee attack.
	 * @param actor - agressive entity
	 * @param w - current universe
	 * @param damage - damage of the attack
	 * @param rectX - x postion of attack rectangle top left corner
	 * @param rectY - y postion of attack rectangle top left corner
	 * @param rectWidth - width of the attack rectange
	 * @param rectHeight - width of the attack rectange
	 * @param condition - condition of attack for every structure(don't put collision checking, it included!)
	 * @return true if attack succes
	 */
	public static final boolean meleeAttack(Entity actor, World w, double damage, double rectX, double rectY,
	                                        double rectWidth, double rectHeight, Predicate<Structure> condition) {

		// For changing in lambda
		final AtomicBoolean attackSucces = new AtomicBoolean(false);

		Consumer<Entity> action = s -> {
			if(!attackSucces.get() && s != actor && condition.test(s)) {
				if(s.intersects(rectX, rectY, rectWidth, rectHeight)) {
					HealthAbility[] ha = s.getAbilityHolder().getAbilities(HealthAbility.class);

					if(ha.length > 0 && condition.test(s)) {
						ha[0].damage(damage);
						attackSucces.set(true);
					}
				}
			}
		};

		w.getLevel().proceedNearbyEntities(action, actor.sectorId);
//      TODO
//		// Get walls with collision
//		if(true) {
//			final int xBegin = Utils.floor(rectX);
//			;
//			final int xEnd = Utils.ceil(rectX + rectWidth);
//
//			final int yBegin = Utils.floor(rectY);
//			final int yEnd = Utils.ceil(rectY + rectHeight);
//
//			for (int yy = yBegin; yy < yEnd; ++yy) {
//				for (int xx = xBegin; xx < xEnd; ++xx) {
//					if (w.checkCoordinates(xx, yy)) {
//						Wall wall = w.getWall(xx, yy);
//
//						if (wall != null && wall.isSolid() && wall instanceof ActionBlock) {
//							ActionBlock ab = (ActionBlock) wall;
//
//							if (ab.attackAction(xx, yy, actor, w)) {
//								attackSucces.set(true);
//							}
//						}
//					}
//				}
//			}
//		}

		return attackSucces.get();
	}
	
	/**
	 * Add sprite to the needed list. Use this for adding any sprite.
	 * @param s - sprite to add
	 * @param sprites - list of sprites
	 * @param structures - list of structure s
	 * @param entities - list of entities
	 * @return true if sprite added
	 */
	public static boolean addSprite(Sprite s, List<Sprite> sprites, List<Structure> structures, List<Entity> entities) {
		if(s instanceof Structure) {
			if(s instanceof Entity) {
				return entities.add((Entity) s);
			} else {
				return structures.add((Structure) s);
			}
		} else {
			return sprites.add(s);
		}
	}
	
	/**
	 * Remove sprite from the needed list. Use this for removing any sprite.
	 * @param s - sprite to remove
	 * @param sprites - list of sprites
	 * @param structures - list of structure s
	 * @param entities - list of entities
	 * @return true if sprite removed
	 */
	public static boolean removeSprite(Sprite s, List<Sprite> sprites, List<Structure> structures, List<Entity> entities) {
		if(s instanceof Structure) {
			if(s instanceof Entity) {
				return entities.remove((Entity) s);
			} else {
				return structures.remove((Structure) s);
			}
		} else {
			return sprites.remove(s);
		}
	}
	
	/**
	 * Get {@link GameScene} from current window
	 * @param gameWindow - window with GameScene
	 * @param outMessage - array to write problem description, lenght is 1 or bigger, else no out message
	 * @return needed {@link GameScene}
	 */
	public static GameScene getGameScene(Window gameWindow, String[] outMessage) {		
		Scene scene;
		
		String message = "";
		
		GameScene ret = null;
		
		if(gameWindow == null) {
			message = "No fucking window! Seriously?";
		} else if((scene = gameWindow.getCurrentScene()) == null) {
			message = "No scene. I can't work without scene.";
		} else if(!(scene instanceof GameScene)) {
			message = "Please, join in the game.";
		} else {
			ret = (GameScene)scene;
		}
		
		//Write problem description
		if(outMessage != null && outMessage.length > 0) {
			outMessage[0] = message;
		}
		
		return ret;
	}
}
