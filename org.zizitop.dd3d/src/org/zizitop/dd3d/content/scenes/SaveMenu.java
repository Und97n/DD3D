package org.zizitop.dd3d.content.scenes;

import org.zizitop.game.GameScene;
import org.zizitop.game.world.World;
import org.zizitop.pshell.menu.*;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.exceptions.FileSavingException;
import org.zizitop.pshell.window.Scene;

/**
 * 
 * @author Zizitop
 *
 */
public final class SaveMenu extends StandardMenuBuilder {
	private TextArea tx = new TextArea(Lang.getText("menu.saveMenu.defaultSaveName"));
	private Button save = new Button(Lang.getText("menu.saveMenu.saveButton"));
	
	private World world;

	public SaveMenu(World world) {
		this.world = world;
	}
	
	@Override
	protected void createMenu(Scene previousScene) {
		save.setActionListener(this);
		mainContainer.add(tx, save, new BackButton(previousScene));
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.saveMenu.nameOfMenu");
	}

	@Override
	public void onAction(ActionComponent.Event e) {
		if(e.eventSource == save) {
			CharSequence saveName = tx.getText();
			
			try {					
				GameScene.save(String.valueOf(saveName), world);
				
				Log.write("World save complete(" + saveName + ")." , 1);
			} catch (FileSavingException ee) {
				Log.write("Problems with world saving(" + saveName + ")!", 3);
				Log.writeException(ee);
			}
		}
	}
}
