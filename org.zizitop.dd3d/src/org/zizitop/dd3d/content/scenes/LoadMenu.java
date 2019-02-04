package org.zizitop.dd3d.content.scenes;

import org.zizitop.game.GameScene;
import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.BackButton;
import org.zizitop.pshell.menu.Button;
import org.zizitop.pshell.menu.SelectorComponent;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.window.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Zizitop
 *
 */
public final class LoadMenu extends StandardMenuBuilder {
	private Button load = new Button(Lang.getText("menu.loadMenu.loadButton"));		
	private Button delete = new Button(Lang.getText("menu.loadMenu.deleteButton"));
	private SelectorComponent<MyFile> selector;
	
	public LoadMenu() {
		File dir = new File(GameScene.SAVES_DIRECTORY);
		
		File[] fileList = dir.listFiles();
		
		if(fileList == null) {
			//:(
			fileList = new File[0];
		}
		
		List<MyFile> files= new ArrayList<>();
		
		for(int i = 0; i < fileList.length; ++i) {
			if(fileList[i].getPath().endsWith(GameScene.SAVES_EXTENSION)) {
				files.add(new MyFile(fileList[i]));
			}
		}
		
		selector = new SelectorComponent<MyFile>(files, 0.025);
	}

	@Override
	protected void createMenu(Scene previousScene) {
		mainContainer.add(selector, load, delete, new BackButton(previousScene));
		
		load.setActionListener(this);
		delete.setActionListener(this);
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.loadMenu.nameOfMenu");
	}
	
	@Override
	public void onAction(Event e) {
		if(e.eventSource == load) {
//			String s = String.valueOf(selector.getSelectedObject());
//
//			try {
//				if(selector.getNumberOfSelected() >= 0) {
//					e.window.changeScene(new GameSceneImpl(s));
//				}
//
//				Log.write("World loading complete(" + s + ")." , 1);
//			} catch (FileLoadingException ee) {
//				Log.write("Problems with world loading(" + s + ")!", 3);
//				Log.writeException(ee);
//			}
		} else if(e.eventSource == delete) {
			if(selector.getNumberOfSelected() >= 0) {
				GameScene.deleteSave(String.valueOf(selector.getSelectedObject().toString()));
				selector.remove(selector.getNumberOfSelected());
			}
		}
	}
	
	private static class MyFile {
		public File file;
		private final String fileName;
		
		public MyFile(File file) {
			this.file = file;
			this.fileName = Utils.getFileName(file);
		}
		
		@Override
		public String toString() {
			return fileName;
		}
	}
}
