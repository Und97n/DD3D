package org.zizitop.dd3d.content.scenes;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.BackButton;
import org.zizitop.pshell.menu.Button;
import org.zizitop.pshell.menu.ControlsSelectorComponent;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.window.*;

/**
 * 
 * @author Zizitop
 *
 */
public class KeyInputOptionsMenu extends StandardMenuBuilder implements Window.GetKeyResultReceiver {
	public static final InputSource NONE_KEY_CODE = ShellApplication.getPlatform().inputSourceFromID(27);

	private Button changeOption = new Button(Lang.getText("menu.keyInputOptionsMenu.changeOptionButton"));
	private Button saveChanges = new Button(Lang.getText("menu.keyInputOptionsMenu.saveChangesButton"));
	private Button defaults = new Button(Lang.getText("menu.keyInputOptionsMenu.defaultButton"));
	
	private ControlsSelectorComponent selector = ControlsSelectorComponent.getControlsSelectorComponent();		
	
	public KeyInputOptionsMenu() {}

	@Override
	protected void createMenu(Scene previousScene) {	
		changeOption.setActionListener(this);
		saveChanges.setActionListener(this);
		defaults.setActionListener(this);
	
		mainContainer.add(selector, changeOption, defaults, saveChanges, new BackButton(previousScene));
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.keyInputOptionsMenu.nameOfMenu");
	}
	
	@Override
	public void onAction(Event e) {
		if(e.eventSource == changeOption) {
			GetKeyScene.getPressedKey(menuScene, e.window, this);
		} else if(e.eventSource == saveChanges) {
			try {
				InputOption.saveChanges();

				Log.write("Input configuration saved.", 1);
			} catch (Exception e1) {
				Log.write("Problems with input config saving.", 3);
				Log.writeException(e1);
			}
		} else if(e.eventSource == defaults) {
			InputOption io = selector.getSelectedObject();
			io.restoreDefaults();
		}
	}

	@Override
	public void receive(InputSource keyCode) {
		InputOption io = selector.getSelectedObject();

		// 27 means escape
		io.setInputSource(keyCode.equals(NONE_KEY_CODE) ? null : keyCode);
	}
}
