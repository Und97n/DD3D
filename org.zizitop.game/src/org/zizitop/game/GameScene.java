package org.zizitop.game;

import org.zizitop.game.world.World;
import org.zizitop.game.world.WorldRenderer;
import org.zizitop.pshell.DefaultContext;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.controlling.Context;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.utils.exceptions.FileSavingException;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

import java.io.File;
import java.util.function.Supplier;

/**
 * Here works game
 * @author Zizitop
 *
 */
public abstract class GameScene implements Scene {
	public static final String SAVES_DIRECTORY = ShellApplication.getProgramConfig().getOption("savesDirectory");
	public static final String SAVES_EXTENSION = ".sk3";

	protected World world;
	protected WorldRenderer worldRenderer;
	protected final Supplier<WorldRenderer> sp;

	public GameScene(Supplier<WorldRenderer> sp, String saveName) throws FileLoadingException {
		this(sp, load(saveName));
	}
	
	public GameScene(Supplier<WorldRenderer> sp, World world) {
		if(world == null) {
			throw new IllegalArgumentException("World is null.");
		}

		this.sp = sp;
		this.worldRenderer = sp.get();

		this.world = world;
	}

	@Override
	public void tick(Window w) {
		//All physics and updates here
		world.update(0.016, w);
	}

	@Override
	public void draw(Bitmap canvas, DisplayMode displayMode) {
		if(worldRenderer == null || worldRenderer.isBroken()) {
			worldRenderer = sp.get();
		}

		MainActor ma = world.getMainActor();
		if(worldRenderer != null && !worldRenderer.isBroken()) {
			worldRenderer.render(ma.getPosX(), ma.getPosY(), ma.getPosZ(),
					ma.getHorizontalViewAngle(), ma.getVerticalViewAngle(), ma.getSectorId(), world);
		}

		canvas.draw_SC(worldRenderer.getCanvas(), 0, 0, 1, 1);
		ma.drawInterface(canvas, displayMode, world);
	}
	
	@Override
	public boolean lockMouse() {
		return world.getMainActor().lockUserMouse();
	}

	@Override
	public int getCustomCursor() {
		return -1;
	}
	
	/**
	 * Save world to file
	 */
	public static void save(String name, World w) throws FileSavingException {
		Utils.serializeData(SAVES_DIRECTORY + name + SAVES_EXTENSION, w);
	}
	
	/**
	 * Load world from file
	 */
	public static World load(String name) throws FileLoadingException {
		return (World) Utils.deSerializeData(SAVES_DIRECTORY + name + SAVES_EXTENSION);
	}
	
	/**
	 * Delete save file
	 */
	public static void deleteSave(String name) {
		File f = new File(SAVES_DIRECTORY + name + SAVES_EXTENSION);
		
		f.delete();
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public Context[] getContext() {
		return Utils.asArray(DefaultContext.defaultContext, new GameContext(world));
	}

	/**
	 * Just context of window.
	 * <br><br>
	 * Created 03.05.2018 10:24
	 *
	 * @author Zizitop
	 */
	public static final class GameContext implements Context {
		private World world;

		static {

		}

		public GameContext(World world) {
			this.world = world;
		}

		@Override
		public Object[] getContextData() {
			return Utils.asArray(world);
		}

		@Override
		public String getNamespace() {
			return "world";
		}
	}
}
