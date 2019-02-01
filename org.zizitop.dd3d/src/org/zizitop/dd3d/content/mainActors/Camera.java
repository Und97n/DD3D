package org.zizitop.dd3d.content.mainActors;

import org.zizitop.game.MainActor;
import org.zizitop.game.sprites.Point;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * Just a flying thing without HP, speed...
 * <br><br>
 * Created 31.01.19 1:21
 *
 * @author Zizitop
 */
public class Camera extends Point implements MainActor {
	public Camera() {
		super(0, 0, 1.7);
	}

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

	/**
	 * For look
	 */
	private double rotateSpeed = 0.02, mouseRotateSpeed = 1.3, movingSpeed = 0.1, angle, yaw;

	int sectorId = 0;

	@Override
	public void drawInterface(Bitmap canvas, Bitmap viewport, World w) {

	}

	@Override
	public boolean setOption(String name, int value) {
		return false;
	}

	@Override
	public void proceedInput(Window window) {
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
		cameraRotateY -= mouseRotateSpeed * window.getMouseDy() / window.getDisplayMode().getBaseHeight();

		angle += cameraRotateX;

//		renderer.camera.verticalLook += cameraRotateY;

//		if(renderer.camera.verticalLook > (1 - rotateSpeed)) {
//			renderer.camera.verticalLook = 1 - rotateSpeed;
//		} else if(renderer.camera.verticalLook < (-1 + rotateSpeed)) {
//			renderer.camera.verticalLook = -1 + rotateSpeed;
//		}

		double directionX = Math.cos(angle), directionY = Math.sin(angle);

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

		x += ddx;
		y += ddy;
	}

	@Override
	public void mainActorTick(World world) {
		int newSectorId = world.getLevel().getSectorId(x, y, sectorId);
		if(newSectorId >= 0) {
			sectorId = newSectorId;
		}
 	}

	@Override
	public double getPosX() {
		return x;
	}

	@Override
	public double getPosY() {
		return y;
	}

	@Override
	public double getPosZ() {
		return z;
	}

	@Override
	public double getHorizontalViewAngle() {
		return angle;
	}

	@Override
	public double getVerticalViewAngle() {
		return yaw;
	}

	@Override
	public int getSectorId() {
		return sectorId;
	}

	@Override
	public boolean lockUserMouse() {
		return true;
	}
}
