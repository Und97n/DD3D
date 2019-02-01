package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Utils;

import java.util.*;
import java.util.function.Consumer;

/**
 * System for {@link InputOption} management in some window.
 * <br><br>
 * Created 18.03.2018 22:56
 *
 * @author Zizitop
 */
public class InputSystem {
	private InputHandler inputHandler;

	// We can have many managers with same keys.
	private Map<InputSource, ArrayList<InputOptionManager>> managers = new HashMap<>();

	private Map<InputOption, InputOptionManager> managersByIO = new HashMap<>();

	private double newMouseX, newMouseY, mouseX, mouseY, mouseDx, mouseDy;

	/**
	 * If need get pressed key
	 */
	private Window.GetKeyResultReceiver receiver = null;

	public InputSystem(InputHandler inputHandler) {
		this.inputHandler = inputHandler;

		InputOption[] inputOptions = InputOption.getAllInputOptions();

		for(int i = 0; i < inputOptions.length; ++i) {
			InputOptionManager iom = new InputOptionManager(inputOptions[i]);

			putManager(iom.is, iom);
			managersByIO.put(iom.io, iom);
		}

		inputHandler.setOnPressAction(this::onPressAction);
		inputHandler.setOnReleaseAction(this::onReleaseAction);
	}

	/**
	 * Clear all input data from current tick.
	 **/
	public void clear() {
		managers.forEach((k, v) -> {
			if(v != null) {
				v.forEach(iom -> iom.update());
			}
		});

		InputOptionManager.needReplace.forEach(this::replaceManager);
		InputOptionManager.needReplace.clear();

		inputHandler.clear();
	}

	public String getLastEnteredText() {
		return inputHandler.getLastEnteredText();
	}

	private void putManager(InputSource is, InputOptionManager iom) {
		ArrayList<InputOptionManager> isl = managers.get(is);

		if(isl == null) {
			managers.put(is, isl = new ArrayList<>());
		}

		if(!isl.contains(iom)) {
			isl.add(iom);
		}
	}

	private void proceedManagers(InputSource is, Consumer<InputOptionManager> consumer) {
		ArrayList<InputOptionManager> isl = managers.get(is);

		if(isl != null) {
			isl.forEach(consumer);
		}
	}

	private void replaceManager(InputOptionManager iom) {
		ArrayList<InputOptionManager> isl = managers.get(iom.is);

		if(isl != null) {
			isl.remove(this);
		}

		iom.is = iom.io.getInputSource();

		putManager(iom.is, iom);
	}

	public void onPressAction(InputSource is) {
		if(receiver != null) {
			receiver.receive(is);

			return;
		}

//		System.out.println(is.toID());

		proceedManagers(is, iom -> iom.onPress());
	}

	public void onReleaseAction(InputSource is) {
		proceedManagers(is, iom -> iom.onRelease());
	}

	public int getActionsCount(InputOption io) {
		return managersByIO.get(io).actionsCount;
	}

	public boolean isPressed(InputOption io) {
		return managersByIO.get(io).pressed;
	}

	public boolean wasReleased(InputOption io) {
		return managersByIO.get(io).wasReleased;
	}

	public void getFirstPressedKey(Window.GetKeyResultReceiver rec) {
		receiver = rec;
	}

	private static class InputOptionManager {
		private static final List<InputOptionManager> needReplace = new ArrayList<>();

		private final InputOption io;

		private int actionsCount;
		private boolean pressed, wasReleased;

		private InputSource is;

		private InputOptionManager(InputOption io) {
			this.io = io;
			is = io.getInputSource();
		}

		private void onPress() {
			if(!pressed) {
				pressed = true;
				++actionsCount;
			}
		}

		private void onRelease() {
			if(pressed) {
				pressed = false;
				wasReleased = true;
			}
		}

		private void update() {
			actionsCount = 0;
			wasReleased = false;

			if(!is.equals(io.getInputSource())) {
				needReplace.add(this);
			}
		}
	}
}