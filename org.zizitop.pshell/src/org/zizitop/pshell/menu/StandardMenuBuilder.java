package org.zizitop.pshell.menu;

import org.zizitop.pshell.menu.ActionComponent.ActionListener;
import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class StandardMenuBuilder implements ActionListener {
	/**
	 * Here this class keep Scene. In first
	 */
	protected Scene menuScene;
	protected Box mainContainer;
	
	protected StandardMenuBuilder() {}
	
	public Scene createScene(Scene previousScene) {
		if(menuScene == null) {
			mainContainer = Box.createVerticalBox(getDistanceBetweenComponents());
			
			TextLabel nameLabel = getMenuNameLabel();
			
			if(nameLabel != null) {
				mainContainer.add(nameLabel);
			}
			
			createMenu(previousScene);
			
			menuScene = new Pane(getBcgColor()).addContainer(mainContainer);
		}
		
		return menuScene;
	}
	
	/**
	 * Create new Menu. For this you need add components to the {@link #mainContainer}.
	 * @param previousScene - previous scene(for back button)
	 */
	protected abstract void createMenu(Scene previousScene);
	protected abstract String getMenuName();
	
	protected TextLabel getMenuNameLabel() {
		String menuName = getMenuName();
		
		if(menuName != null) { 
			return new TextLabel(1.4, menuName);
		} else {
			return null;
		}
	}
	
	protected int getBcgColor() {
		return Component.DEFAULT_BACKGROUND_COLOR;
	}
	
	protected double getDistanceBetweenComponents() {
		return 0.015;
	}
	
	/**
	 * Just tool for fast and easy scene changing
	 */
	public void switchScene(Window window, StandardMenuBuilder newSceneBuilder) {
		window.changeScene(newSceneBuilder.createScene(menuScene));
	}
	
	//By default
	@Override
	public void onAction(Event e) {}
}
