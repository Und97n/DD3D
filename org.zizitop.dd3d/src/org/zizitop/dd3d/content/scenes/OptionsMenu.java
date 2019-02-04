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
public final class OptionsMenu extends StandardMenuBuilder {
	private Button soundOptions = new Button(Lang.getText("menu.optionsMenu.toSoundOptionsButton"));
	private Button graphicsOptions = new Button(Lang.getText("menu.optionsMenu.toGraphicsOptionsButton"));
	private Button inputOptions = new Button(Lang.getText("menu.optionsMenu.toInputOptionsButton"));
	
	public OptionsMenu() {}

	@Override
	protected void createMenu(Scene previousScene) {
		mainContainer.add(soundOptions, graphicsOptions, inputOptions, new BackButton(previousScene));
		
		soundOptions.setActionListener(this);
		graphicsOptions.setActionListener(this);
		inputOptions.setActionListener(this);
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.optionsMenu.nameOfMenu");
	}
	
	@Override
	public void onAction(Event e) {
		if(e.eventSource == soundOptions) {
			switchScene(e.window, new SoundOptionsMenu());
		} else if(e.eventSource == graphicsOptions) {
			switchScene(e.window, new GraphicsOptionsMenu(e.window));
		} else if(e.eventSource == inputOptions) {
			switchScene(e.window, new InputOptionsMenu());			
		}
	}
}
