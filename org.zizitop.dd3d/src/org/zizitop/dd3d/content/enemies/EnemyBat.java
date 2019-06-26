package org.zizitop.dd3d.content.enemies;

import org.zizitop.game.GameUtils;
import org.zizitop.game.sprites.*;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.animation.Animation;
import org.zizitop.pshell.utils.animation.StateManager;

import java.util.function.Predicate;

/**
 * <br><br>
 * Created 09.02.19 17:49
 *
 * @author Zizitop
 */
public class EnemyBat extends EnemyWithStateAnimation {
	public static final Animation deathAnimation =  new Animation("entities.enemies.bat.death", 3, 0.15, false);

	private static final long serialVersionUID = 1L;

	public static final StateManager.State

			DEFAULT_STATE = new StateManager.State("Default", 20, new Animation(new Bitmap[] {
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/0"),
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/1"),
	}, 0.1, true)),

	DAMAGE_STATE = new StateManager.State("Damage", 20, new Animation(new Bitmap[] {
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/damage"),
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/0"),
	}, 0.1, true)),

	ATTACK_STATE = new StateManager.State("Attack", 20, new Animation(new Bitmap[] {
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/0"),
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/attack"),
	}, 0.1, false)),

	MOVING_STATE = new StateManager.State("Moving", 20, new Animation(new Bitmap[] {
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/0"),
			ResourceLoader.getInstance().getTexture("entities/enemies/bat/1"),
	}, 0.15, true));

	public static final StateManager.StateManagerType stateManager = new StateManager.StateManagerType(
			Utils.asArray(DEFAULT_STATE, DAMAGE_STATE, ATTACK_STATE, MOVING_STATE), 0);

	public EnemyBat(double x, double y) {
		super(x, y, 0, 0, 0);
	}

	public void attack(double attackDirectionX, double attackDirectionY, World w) {
		double ax = x + attackDirectionX * 0.3;
		double ay = y + attackDirectionY * 0.3;

		Predicate<Structure> condition = s -> !(s instanceof Enemy);

		GameUtils.meleeAttack(this, w, 5, ax, ay, attackDistance, attackDistance, condition);
	}

	@Override
	public StateManager.StateManagerType getStateManagerType() {
		return stateManager;
	}

	@Override
	public void update(World world, double dt) {
		super.update(world, dt);
	}

	@Override
	protected int getDefaultStateId() {
		return 0;
	}

	@Override
	protected int getDamageStateId() {
		return 1;
	}

	@Override
	protected int getAttackStateId() {
		return 2;
	}

	@Override
	protected int getMovingStateId() {
		return 3;
	}

	@Override
	protected int getAttackDeltaTime() {
		return 60;
	}

	@Override
	protected int getAttackStateManagerAttackFrameTopBorder() {
		return 20;
	}

	@Override
	protected int getAttackStateManagerAttackFrameBottomBorder() {
		return 10;
	}

	@Override
	protected double getMinDistanceToTargetForAttack() {
		return 0.5;
	}

	@Override
	protected Animation getDeathAnimation() {
		return deathAnimation;
	}

	@Override
	public boolean isVictim(Point p) {
		return p instanceof Entity;
	}

	@Override
	public double getMinimumVictimDistance() {
		return 0.5;
	}

	@Override
	public double getMaxSpeed() {
		return 0.05;
	}

	@Override
	public double getMass() {
		// Bit bat
		return 10;
	}

	@Override
	public double getKneeHeight() {
		return 3;
	}

	@Override
	public double getHeight() {
		return 1;
	}

	@Override
	public double getElasticity() {
		return 0;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public double getSizeX() {
		return 0.25;
	}

	@Override
	public double getSizeY() {
		return 0.25;
	}
}
