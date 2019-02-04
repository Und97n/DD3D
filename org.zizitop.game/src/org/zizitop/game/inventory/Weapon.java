//package org.zizitop.game.inventory;
//
//import org.zizitop.game.world.World;
//import org.zizitop.pshell.utils.Bitmap;
//import org.zizitop.pshell.utils.animation.StateManager;
//import org.zizitop.pshell.window.DisplayMode;
//
//public abstract class Weapon extends Item implements StateManager.StateManagerHead {
//	private static final long serialVersionUID = 1L;
//
//	protected StateManager animation;
//
//	public Weapon() {
//		animation = new StateManager(this);
//	}
//
//	@Override
//	public void drawInHands(Bitmap canvas, int xOffset, int yOffset) {
//		final Bitmap currentFrame = animation.getImage();
//
//		if(currentFrame != null) {
//			final double scalarX = (double)canvas.width / DisplayMode.BASE_VIEWPORT_WIDTH;
//			final double scalarY = (double)canvas.height / DisplayMode.BASE_VIEWPORT_HEIGHT;
//
//			final double width = currentFrame.width * scalarX;
//			final double height = currentFrame.height * scalarY;
//
//			//Draw currentFrame on the bottom middle of the screen
//			drawInHands_ST(canvas, currentFrame, xOffset, yOffset, width, height);
//		}
//	}
//
//	/**
//	 * Util for smart class implementation
//	 * @param canvas
//	 * @return
//	 */
//	protected void drawInHands_ST(Bitmap canvas, Bitmap frame, double x, double y, double width, double height) {
//		canvas.draw(frame, x + (canvas.width - width) / 2,
//				y + canvas.height - height, width, height);
//	}
//
//	@Override
//	public void update(InventoryOwner hero, World w) {
//		animation.update();
//	}
//
//	@Override
//	public void changeSelectedItem(InventoryOwner hero, World w) {
//		animation.switchState(getDefaultStateId());
//	}
//
//	protected abstract int getDefaultStateId();
//
//	@Override
//	public void proceedStateChange(StateManager as, int oldState, int newState) {}
//}
