package org.zizitop.dd3d.content.inventory;

import org.zizitop.game.inventory.WeaponNear;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.animation.Animation;
import org.zizitop.pshell.utils.animation.StateManager;

/**
 *
 * @author Zizitop
 *
 */
public class WeaponFist extends WeaponNear {
	private static final long serialVersionUID = 1L;

	public static final double attackDistance = 0.5;

	public static final Sound swingSound = ResourceLoader.getInstance().getSound("whoosh");
	public static final Sound punchSuccesSound = ResourceLoader.getInstance().getSound("punch");

	public static final Bitmap icon = new Bitmap(32, 32);

	static {
		//Now icon not used, because this item used as null item in hotbar
		icon.draw(ResourceLoader.getInstance().getTexture("/weapon/hands/hand_0"), 0, 0, 32, 32);
	}

	public static final int DEFAULT_STATE_ID = 0;
	public static final int ATTACK_STATE_ID = 1;

	public static final int ATTACK_DURATION = 30;
	public static final int SWING_DURATION = 21;

	public static final StateManager.StateManagerType stateManager = new StateManager.StateManagerType(new StateManager.State[] {
			new StateManager.State("Default", 10, new Animation("weapon.hands.hand", 1, 0, true)),
			new StateManager.State("Attack", ATTACK_DURATION, new Animation("weapon.hands.hand",3 , 0.2, false), false),
			new StateManager.State("Swing", SWING_DURATION, new Animation("weapon.hands.hand1", 3, 0.2, false), true)}, 0);


	@Override
	protected void drawInHands_ST(Bitmap canvas, Bitmap frame, double x, double y, double width, double height) {
		width *= 2;
		height *= 2;

		canvas.draw(frame, x + (canvas.width - width), y + (canvas.height - height), width, height);
	}

	@Override
	public StateManager.StateManagerType getStateManagerType() {
		return stateManager;
	}

	@Override
	public int getSwingStateId() {
		return 2;
	}

	@Override
	public int getAttackStateId() {
		return 1;
	}

	@Override
	public int getSwingDuration() {
		return SWING_DURATION;
	}

	@Override
	public int getAttackFrameId() {
		return 2;
	}

	@Override
	public double getAttackDistance() {
		return attackDistance;
	}

	@Override
	public Sound getSwingSound() {
		return swingSound;
	}

	@Override
	public Sound getPunchSound() {
		return punchSuccesSound;
	}

	@Override
	protected int getDefaultStateId() {
		return 0;
	}

	@Override
	public double getZPosition() {
		return 0;
	}

	@Override
	public Bitmap getItemTexture() {
		return icon;
	}

	@Override
	public double getMinDamage() {
		return 5;
	}

	@Override
	public double getMaxDamage() {
		return 10;
	}

}
