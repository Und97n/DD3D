package org.zizitop.pshell.menu;

import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public interface ActionComponent extends Component {
	
	default boolean checkSelection(Window window) {
		double mouseX = window.getMouseX() / (double)window.getContentWidth();
		double mouseY = window.getMouseY() / (double)window.getContentHeight();
		
		return containsPoint(mouseX, mouseY);
	}
	
	default boolean playSoundOnEnter() {
		return true;
	}
	
	default void onAction(Action a) {}
	default void onLeave(Action a) {}
	
	default void select(Window window) {}
	default void unselect(Window window) {}

	interface Action {
		Window getWindow();
	}
	
	class MouseAction implements Action {
		public final double mouseX, mouseY;
		public final Window window;
		
		public MouseAction(Window w) {
			this.window = w;
			this.mouseX = w.getMouseX() / w.getContentWidth();
			this.mouseY = w.getMouseY() / w.getContentHeight();
		}
		
		public Window getWindow() {
			return window;
		}
	}
	
	class KeyAction implements Action {
		public final InputOption source;
		public final Window window;
		
		public KeyAction(Window w, InputOption source) {
			this.window = w;
			this.source = source;
		}
		
		public Window getWindow() {
			return window;
		}
	}
	
	class Event {
		public final ActionComponent eventSource;
		public final Action action;
		public final Window window;
		
		public Event(ActionComponent eventSource, Action action) {
			this.eventSource = eventSource;
			this.action = action;
			this.window = action.getWindow();
		}
	}
	
	@FunctionalInterface
	interface ActionListener {
		void onAction(Event e);
	}
}
