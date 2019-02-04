//package org.zizitop.game.inventory;
//
//import org.zizitop.game.Camera;
//import org.zizitop.game.Wall;
//import org.zizitop.game.world.World;
//
///**
// * <br><br>
// * Created 08.10.18 22:41
// *
// * @author Zizitop
// */
//public abstract class Key extends Item {
//	protected boolean used = false;
//
//	@Override
//	public void action(InventoryOwner hero, World w) {
//		if(!used && hero instanceof Camera.CameraHolder) {
//			Camera cam = ((Camera.CameraHolder) hero).getCamera();
//			//Find eyes target
//			World.RayCastingReturnValues castData = new World.RayCastingReturnValues();
//
//			w.traceRay(cam.x, cam.y, cam.directionX, cam.directionY, 3, castData);
//			Wall wall = castData.wall;
//
//			System.out.println(wall);
//
//			if(wall instanceof Keyholder) {
//				used = ((Keyholder) wall).action(this, w);
//			}
//		}
//	}
//
//	@Override
//	public boolean needDeleteFromInventory() {
//		return used;
//	}
//
//	public interface Keyholder {
//		// If true - remove key item from hero inventory
//		boolean action(Key key, World world);
//	}
//}
