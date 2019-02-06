package org.zizitop.dd3d.content.scenes;

import org.zizitop.game.GameScene;
import org.zizitop.game.world.World;
import org.zizitop.game.world.WorldRendererSimple;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * <br><br>
 * Created 30.01.19 23:30
 *
 * @author Zizitop
 */
public class GameSceneImpl extends GameScene {
	public static final InputOption input_escape = InputOption.getInputOption("menu.escape");
	public static final InputOption input_saveGame = InputOption.getInputOption("menu.saveGame");

	public GameSceneImpl(World world, DisplayMode dm) {
		super(() -> new WorldRendererSimple(0.73, 0.73, dm.getViewportWidth(), dm.getViewportHeight()), world);
	}

//	public GameSceneImpl(String saveName) throws FileLoadingException {
//		super(saveName);
//	}

	@Override
	public void tick(Window w) {
		super.tick(w);

//		if(w.getActionsCount(input_saveGame) > 0) {
//			//Save game
//			w.changeScene(new SaveMenu(world).createScene(this));
//		} else if(w.getActionsCount(input_escape) > 0) {
//			//Game pause
//			w.changeScene(new PauseMenu().createScene(this));
//		}
	}
}
