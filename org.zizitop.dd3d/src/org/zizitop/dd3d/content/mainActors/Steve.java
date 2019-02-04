package org.zizitop.dd3d.content.mainActors;

import org.zizitop.game.sprites.Player;
import org.zizitop.game.sprites.abilities.PlayerSpecialAbility;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * Just a player
 * <br><br>
 * Created 02.02.19 19:29
 *
 * @author Zizitop
 */
public class Steve extends Player {
	private static final InputOption input_moveForward = InputOption.getInputOption("game.player.moveForward");
	private static final InputOption input_moveBackward = InputOption.getInputOption("game.player.moveBackward");
	private static final InputOption input_moveLeft = InputOption.getInputOption("game.player.moveLeft");
	private static final InputOption input_moveRight = InputOption.getInputOption("game.player.moveRight");
	private static final InputOption input_sprint = InputOption.getInputOption("game.player.sprint");
	private static final InputOption input_interract = InputOption.getInputOption("game.player.interact");
	private static final InputOption lookUp = InputOption.getInputOption("game.player.lookUp");
	private static final InputOption lookDown = InputOption.getInputOption("game.player.lookDown");
	private static final InputOption turnLeft = InputOption.getInputOption("game.player.turnLeft");
	private static final InputOption turnRight = InputOption.getInputOption("game.player.turnRight");

	private double viewAngle, yaw;

	private double rotateSpeed = 0.02, mouseRotateSpeed = 1.3, movingSpeed = 100;

	public Steve(double x, double y) {
		super(x, y);
		z = 0.5;
	}

	@Override
	public void drawInterface(Bitmap canvas, DisplayMode dm, World w) {
		var playerSpecialAbilities = abilityHolder.getAbilities(PlayerSpecialAbility.class);

		for(PlayerSpecialAbility psa: playerSpecialAbilities) {
			psa.drawInterface(this, dm, canvas);
		}
	}

	@Override
	public boolean setOption(String name, int value) {
		return false;
	}

	@Override
	public void proceedInput(Window window) {
		var playerSpecialAbilities = abilityHolder.getAbilities(PlayerSpecialAbility.class);

		for(PlayerSpecialAbility psa: playerSpecialAbilities) {
			psa.proceedInput(this, window);
		}

		double ddx = 0, ddy = 0;

		double cameraRotateX = 0, cameraRotateY = 0;

		if(window.isPressed(turnLeft)) {
			cameraRotateX -= rotateSpeed;
		}

		if(window.isPressed(turnRight)) {
			cameraRotateX += rotateSpeed;
		}

		if(window.isPressed(lookUp)) {
			cameraRotateY += rotateSpeed;
		}

		if(window.isPressed(lookDown)) {
			cameraRotateY -= rotateSpeed;
		}

		cameraRotateX += mouseRotateSpeed * window.getMouseDx() / window.getDisplayMode().getBaseWidth();
		cameraRotateY += mouseRotateSpeed * window.getMouseDy() / window.getDisplayMode().getBaseHeight();

		viewAngle += cameraRotateX;
		yaw += cameraRotateY;

		yaw += cameraRotateY;

		if(yaw > (1 - rotateSpeed)) {
			yaw = 1 - rotateSpeed;
		} else if(yaw < (-1 + rotateSpeed)) {
			yaw = -1 + rotateSpeed;
		}

		double directionX = Math.cos(viewAngle), directionY = Math.sin(viewAngle);

		if(window.isPressed(input_moveForward)) {
			ddx += directionX * movingSpeed;
			ddy += directionY * movingSpeed;
		}

		if(window.isPressed(input_moveBackward)) {
			ddx -= directionX * movingSpeed;
			ddy -= directionY * movingSpeed;
		}

		if(window.isPressed(input_moveLeft)) {
			ddx += directionY * movingSpeed;
			ddy -= directionX * movingSpeed;
		}

		if(window.isPressed(input_moveRight)) {
			ddx -= directionY * movingSpeed;
			ddy += directionX * movingSpeed;
		}

		addAcceleration(ddx, ddy);
	}

	@Override
	public double getPosZ() {
		return z;
	}

	@Override
	public double getHorizontalViewAngle() {
		return viewAngle;
	}

	@Override
	public double getVerticalViewAngle() {
		return yaw;
	}

	@Override
	public double getMaxSpeed() {
		return 10;
	}

	@Override
	public double getMass() {
		return 87;
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
		return 0.4;
	}

	@Override
	public double getSizeY() {
		return 0.4;
	}

	@Override
	public double getEyeHeight() {
		return 1.7;
	}

	@Override
	public double getKneeHeight() {
		return 0.5;
	}

	@Override
	public double getHeight() {
		return 1.8;
	}

	@Override
	public boolean lockUserMouse() {
		return true;
	}
}
