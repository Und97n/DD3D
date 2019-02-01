package org.zizitop.pshell.menu;

import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.window.Window;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Scene;

public class BackButton extends Button {
	protected static final InputOption input_escape = InputOption.getInputOption("menu.escape");

	public BackButton(Scene previousScene) {
		this((a) -> {
			a.window.changeScene(previousScene);
		});
	}
	
	public BackButton(ActionListener l) {
		super(Lang.getText("menu.backButton"));
		
		this.setActionListener(l);
	}
	
	@Override
	public void updateComponent(Window window) {
		super.updateComponent(window);
		
		if(window.getActionsCount(input_escape) > 0) {
			onAction(new KeyAction(window, input_escape));
		}
	}

}
