package org.zizitop.dd3d.content.scenes;


import org.zizitop.dd3d.DisplayModeImpl;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.menu.*;
import org.zizitop.pshell.utils.Configuration;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 * 
 */
public class GraphicsOptionsMenu extends StandardMenuBuilder {
	private TextAreaForInteger windowWidth;
	private TextAreaForInteger windowHeight;
	private TextLabel outMessage = new TextLabel("");;
	
	private Button changeOption = new Button(Lang.getText("menu.graphicsOptionsMenu.changeOptionsButton"));
	
	private SelectorComponent<DisplayModeImpl> displayModes;
	private DisplayModeImpl lastDisplayMode;
	
	public GraphicsOptionsMenu(Window w) {
		windowWidth = new TextAreaForInteger(w.getWidth(), ShellApplication.getUserScreenWidth());
		windowHeight = new TextAreaForInteger(w.getHeight(), ShellApplication.getUserScreenHeight());
		
		displayModes = new SelectorComponent<DisplayModeImpl>(Utils.createList
				(DisplayModeImpl.getAvalibleDisplayModes()), 0.015, DDFont.CONSOLE_FONT);
		displayModes.setSelectedObject(ShellApplication.getProgramConfig().getIntOption("displayMode"));
		
		lastDisplayMode = displayModes.getSelectedObject();
	}

	@Override
	protected void createMenu(Scene previousScene) {
		Box windowSizes = Box.createHorizontalBox(getDistanceBetweenComponents());
		windowSizes.add(windowWidth, windowHeight);
		
		mainContainer.add(new TextLabel(Lang.getText("menu.graphicsOptionsMenu.windowSizeL")));
		mainContainer.addContainer(windowSizes);
		mainContainer.add(new TextLabel(Lang.getText("menu.graphicsOptionsMenu.contentDisplayModeL")));
		mainContainer.add(displayModes, changeOption, outMessage, new BackButton(previousScene));
		
		changeOption.setActionListener(this);
	}

	@Override
	public void onAction(ActionComponent.Event e) {
		int width = windowWidth.getValue();
		int height = windowHeight.getValue();

		Configuration config = ShellApplication.getProgramConfig();
		boolean changed = false;
		
		if(width < DisplayModeImpl.BASE_WIDTH) {
			outMessage.setText(Lang.getText("menu.graphicsOptionsMenu.widthIsTooSmallMessage"));
		} else if(height < DisplayModeImpl.BASE_HEIGHT) {
			outMessage.setText(Lang.getText("menu.graphicsOptionsMenu.heightIsTooSmallMessage"));
		} else if(width > ShellApplication.getUserScreenWidth()) {
			outMessage.setText(Lang.getText("menu.graphicsOptionsMenu.widthIsTooBigMessage"));
		} else if(height > ShellApplication.getUserScreenHeight()) {
			outMessage.setText(Lang.getText("menu.graphicsOptionsMenu.heightIsTooBigMessage"));
		} else {			
			config.setOptionInt("windowWidth", width);
			config.setOptionInt("windowHeight", height);
			
			changed = true;			
		}

		DisplayModeImpl dm = displayModes.getSelectedObject();
		
		if(dm != null && dm != lastDisplayMode) {
			config.setOptionInt("displayMode", dm.getID());
			lastDisplayMode = dm;
			
			//changed = true;
		}
		
		if(changed) {
			config.saveProperties();	
			outMessage.setText(Lang.getText("menu.graphicsOptionsMenu.done"));
		}
	}
	
	@Override
	protected String getMenuName() {
		return Lang.getText("menu.graphicsOptionsMenu.nameOfMenu");
	}
}
