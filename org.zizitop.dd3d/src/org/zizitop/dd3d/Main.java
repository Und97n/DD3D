package org.zizitop.dd3d;

import org.zizitop.dd3d.content.inventory.InventoryPlayerBag;
import org.zizitop.dd3d.content.mainActors.Camera;
import org.zizitop.dd3d.content.mainActors.Steve;
import org.zizitop.dd3d.content.scenes.GameSceneImpl;
import org.zizitop.dd3d.content.scenes.MainMenu;
import org.zizitop.desktop.JavaDesktop;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.Level;
import org.zizitop.game.world.Sector;
import org.zizitop.game.world.World;
import org.zizitop.pshell.DefaultContext;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Window;

/**
 * <br><br>
 * Created 30.01.19 18:10
 *
 * @author Zizitop
 */
public class Main {
	public static void main(String[] args) {
		ShellApplication.launch(new JavaDesktop(),  "DD3D", "alpha 0.1", Main::getDisplayMode, Main::start, args, Utils.asArray(DefaultContext.class));
	}

	public static void start(Window window) {
		window.changeScene(MainMenu.getGameScene(getDisplayMode()));
	}

	public static DisplayMode getDisplayMode() {
		return DisplayModeImpl.getDisplayMode(ShellApplication.getProgramConfig().getIntOption("displayMode"));
	}

//	/**
//	 * Get 3d graphics engine from option
//	 */
//	public static Engine3D getEngine3D(DisplayMode dm) {
//		int option = ShellApplication.getProgramConfig().getIntOption("graphicsEngine");
//
//		switch(option) {
//			case 1: return new Engine3D_NEWEST(dm.getViewportWidth(), dm.getViewportHeight());
//			case 2: return new Engine3D_MODULAR_I(dm.getViewportWidth(), dm.getViewportHeight());
//			case 3: return new Engine3D_MULTITHREAD(dm.getViewportWidth(), dm.getViewportHeight());
//			default: Log.write("Can't find or create engine.", 4); return null;
//		}
//	}
}
