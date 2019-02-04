package org.zizitop.dd3d.content.scenes;

import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.BackButton;
import org.zizitop.pshell.menu.Button;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.window.Scene;

public class InputOptionsMenu extends StandardMenuBuilder {
	Button controlsButton = new Button(Lang.getText("menu.inputOptionsMenu.controllsButton"));

	public InputOptionsMenu() {}

	@Override
	protected void createMenu(Scene previousScene) {
		mainContainer.add(controlsButton, new BackButton(previousScene));
		
		controlsButton.setActionListener(this);
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.inputOptionsMenu.nameOfMenu");
	}
	
	@Override
	public void onAction(Event e) {
		if(e.eventSource == controlsButton) {
			switchScene(e.window, new KeyInputOptionsMenu());
		}
	}
}
