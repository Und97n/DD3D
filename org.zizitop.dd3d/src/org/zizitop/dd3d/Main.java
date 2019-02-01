package org.zizitop.dd3d;

import org.zizitop.dd3d.content.mainActors.Camera;
import org.zizitop.dd3d.content.scenes.GameSceneImpl;
import org.zizitop.desktop.JavaDesktop;
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
		double[] verts = {
				-8, 8, 8, 8, -8, -8, 8, -8,

				-4, 4, 4, 4, -4, -4, 4, -4,
				-3, 4, 3, 4, -3, -4, 3, -4,
				-3, 3, 3, 3, -3, -3, 3, -3,
				-4, 3, 4, 3, -4, -3, 4, -3,

//				-8, 2, 8, 2,
		};

		int[][] walls = {
				{-5, 0, 0, -1, 0, 0, -3, 0},
				{-2, 0, 0, -1, 0, 0, -4, 0},
				{-3, 0, 0, -1, 0, 0, -5, 0},
				{-4, 0, 0, -1, 0, 0, -2, 0},
		};

		int[][] verticies = {
				{0, 4, 8, 12, 13, 9, 5, 1, 0},
				{1, 5, 17, 13, 15, 19, 7, 3, 1},
				{3, 7, 11, 15, 14, 10, 6, 2, 3},
				{2, 6, 18, 14, 12, 16, 4, 0, 2},
		};

		Sector[] sectors = {
				new Sector(new int[]{12, 14, 15, 13, 12}, new int[]{-5, -4, -3, -2}, 0.5, 2),

				new Sector(verticies[0], walls[0], 0, 3),
				new Sector(verticies[1], walls[1], 0, 3),
				new Sector(verticies[2], walls[2], 0, 3),
				new Sector(verticies[3], walls[3], 0, 3),


//				new Sector(verticies[5], walls[5], 0, 3),
//				new Sector(verticies[6], walls[6], 0, 3),
		};


		Level l = new Level(verts, sectors);
		World world = new World(l, new Camera());
		window.changeScene(new GameSceneImpl(world));
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
