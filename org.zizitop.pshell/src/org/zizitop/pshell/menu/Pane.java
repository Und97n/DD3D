package org.zizitop.pshell.menu;

import java.util.ArrayList;
import java.util.List;

import org.zizitop.pshell.menu.ActionComponent.Action;
import org.zizitop.pshell.menu.ActionComponent.KeyAction;
import org.zizitop.pshell.menu.ActionComponent.MouseAction;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Scene;
import org.zizitop.pshell.window.Window;

/**
 * Pane implementation in DD3D.
 * @author Zizitop
 *
 */
public class Pane implements Scene {
	protected static final InputOption input_next = InputOption.getInputOption("menu.next");
	protected static final InputOption input_previous = InputOption.getInputOption("menu.previous");
	protected static final InputOption input_actionOnMenuItemKeyboard = 
			InputOption.getInputOption("menu.actionOnMenuItemKeyboard");
	protected static final InputOption input_actionOnMenuItem = 
			InputOption.getInputOption("menu.actionOnMenuItem");
	
	protected ActionComponent selectedAC;
	
	protected List<Component> components;
	protected List<ActionComponent> actionComponents;
	
	private int bcgColor;
	
	public Pane(int bcgColor, Component... comps) {
		this(bcgColor, Utils.createList(comps));
	}
	
	public Pane(int bcgColor, List<Component> components) {		
		this.bcgColor = bcgColor;
		this.components = components;
		
		this.actionComponents = new ArrayList<>();
		
		for(Component comp: components) {
			if(comp instanceof ActionComponent) {
				actionComponents.add((ActionComponent) comp);
			}
		}
	}
	
	/**
	 * Add components to the pane.
	 * @return this Menu
	 */
	public Pane add(Component ... comps) {		
		for(Component comp: comps) {
			components.add(comp);
			
			if(comp instanceof ActionComponent) {
				actionComponents.add((ActionComponent) comp);
			}
		}
		
		return this;
	}
	
	/**
	 * Remove component from the menu.
	 * @return this Menu
	 */
	public Pane removeComponent(Component comp) {
		return removeComponent(components.indexOf(comp));
	}
	
	/**
	 * Remove component from the menu.
	 * @return this Menu
	 */
	public Pane removeComponent(int index) {
		actionComponents.remove(components.get(index));
		components.remove(index);
		
		return this;
	}
	
	public Pane addContainer(Container cont) {
		for(Component comp: cont.getComponents()) {
			comp.setPosition(comp.getX() + cont.getX(), comp.getY() + cont.getY());
			
			add(comp);
		}
		
		return this;
	}
	
	public int getBcgColor() {
		return bcgColor;
	}

	public void setBcgColor(int bcgColor) {
		this.bcgColor = bcgColor;
	}
	
	@Override
	public void tick(Window window) {
		for(Component comp : components) {
			comp.updateComponent(window);
		}
		
		final ActionComponent previousSelectedAC = selectedAC;
		
		final boolean keyUp = window.getActionsCount(input_next) > 0;
		final boolean keyDown = window.getActionsCount(input_previous) > 0;
		
		final int acCount = actionComponents.size();
				
		if(window.mouseMoved()) {
			for(ActionComponent ac: actionComponents) {
				if(ac.checkSelection(window)) {
					selectedAC = ac;
				}
			}
		} else  if(keyUp || keyDown) {
			int selected = actionComponents.indexOf(selectedAC);
			
			if(keyUp) {
				--selected;
			} else if(keyDown) {
				++selected;
			}
			
			selected += selected < 0 ? acCount : 0;
			selected %= acCount;
			
			selectedAC = actionComponents.get(selected);
		}
		
		if(previousSelectedAC != selectedAC) {
			Component.menuSound.play();
			
			if(previousSelectedAC != null) {
				previousSelectedAC.unselect(window);
			}
			
			if(selectedAC != null) {
				selectedAC.select(window);
			}
		}
		
		final boolean actionK = window.getActionsCount(input_actionOnMenuItemKeyboard) > 0;
		final boolean actionM = window.getActionsCount(input_actionOnMenuItem) > 0;
		
		if(actionK || actionM) {
			Action action = null;
			
			if(actionM) {
				action = new MouseAction(window);
			} else if(actionK) {
				action = new KeyAction(window, input_actionOnMenuItemKeyboard);
			}
			
			if(selectedAC != null) {
				selectedAC.onAction(action);
			}
			
			if(previousSelectedAC != null && selectedAC != previousSelectedAC) {
				previousSelectedAC.onLeave(action);
			}
		}
	}

	@Override
	public void draw(Bitmap canvas) {		
		canvas.fillRect_SC(getBcgColor(), 0, 0, 1, 1);
				
		for(int i = 0; i < components.size(); ++i) {			
			components.get(i).drawComponent(canvas);
		}
	}
}
