package org.zizitop.pshell.window;

import java.util.function.Consumer;

/**
 * Nice abstract tool for input management
 * @author Zizitop
 */
public abstract class InputHandler {
	protected Consumer<InputSource> onPressAction, onReleaseAction;

	private static final Consumer<InputSource> emptyConsumer = (is) -> {};

	public InputHandler() {
		onPressAction = onReleaseAction = emptyConsumer;
	}

	/**
	 * Clear all input data from current tick and be ready for next.
	 */
	public abstract void clear();

	public abstract String getLastEnteredText();

	public void setOnPressAction(Consumer<InputSource> onPressAction) {
		if(onPressAction == null) {
			onPressAction = emptyConsumer;
		}

		this.onPressAction = onPressAction;
	}

	public void setOnReleaseAction(Consumer<InputSource> onReleaseAction) {
		if(onReleaseAction == null) {
			onReleaseAction = emptyConsumer;
		}

		this.onReleaseAction = onReleaseAction;
	}
}