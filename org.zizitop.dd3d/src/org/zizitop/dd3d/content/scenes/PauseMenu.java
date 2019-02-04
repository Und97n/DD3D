package org.zizitop.dd3d.content.scenes;

import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.BackButton;
import org.zizitop.pshell.menu.Button;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.window.Scene;

/**
 * 
 * @author Zizitop
 *
 */
public final class PauseMenu extends StandardMenuBuilder {
	Button toMainMenu = new Button(Lang.getText("menu.pauseMenu.toMainMenuButton"));
	
	public PauseMenu() {}

	@Override
	protected void createMenu(Scene previousScene) {
		toMainMenu.setActionListener(this);
		
		mainContainer.add(toMainMenu, new BackButton(previousScene));
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.pauseMenu.nameOfMenu");
	}
	
	@Override
	public void onAction(Event e) {
		if(e.eventSource == toMainMenu) {
			e.window.changeScene(new MainMenu().createScene(menuScene));			
		}		
	}
}
