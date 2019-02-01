package org.zizitop.desktop;

import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.InputHandler;
import org.zizitop.pshell.window.InputSource;

import java.awt.*;
import java.awt.event.*;

/**
 * System for cool input handling.
 * <br><br>
 * Created 08.09.18 22:59
 *
 * @author Zizitop
 */
public class JDInputHandler extends InputHandler implements KeyListener, MouseListener, MouseWheelListener {
	public static final int MOUSE_BUTTONS_COUNT = 3, KEYS_COUNT = 65536;

	private StringBuilder enteredText = new StringBuilder();
	private String lastEnteredText = "";

	public void clear() {
		lastEnteredText = enteredText.toString();
		enteredText.setLength(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		char ch = e.getKeyChar();

		if(ch != KeyEvent.CHAR_UNDEFINED) {
			enteredText.append(ch);
		}

		int keyCode = e.getKeyCode();

		KeyboardSource s = new KeyboardSource(keyCode);
		onPressAction.accept(s);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		KeyboardSource s = new KeyboardSource(keyCode);
		onReleaseAction.accept(s);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton() - 1;

		MouseButtonSource s = new MouseButtonSource(button);
		onPressAction.accept(s);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton() - 1;

		MouseButtonSource s = new MouseButtonSource(button);
		onReleaseAction.accept(s);
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double delta = e.getWheelRotation();

		ScrollActionSource s = new ScrollActionSource(delta > 0);
		onPressAction.accept(s);

		// Strange
		onReleaseAction.accept(s);
	}

	public void link(Component comp) {
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseWheelListener(this);
	}

	@Override
	public String getLastEnteredText() {
		return lastEnteredText;
	}

	public static class KeyboardSource implements InputSource {
		private final int data;

		public KeyboardSource(int data) {
			if(data < 0 || data >= KEYS_COUNT) {
				throw new IllegalArgumentException("Wrong keyboard key id: " + data);
			}

			this.data = data;
		}

		@Override
		public String toString() {
			return KeyEvent.getKeyText(data);
		}

		@Override
		public int toID() {
			return data;
		}

		@Override
		public int getButton() {
			return data;
		}

		@Override
		public int hashCode() {
			return (Utils.intHash(data) | 0xF0000000) & 0xFFFFFFFF;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof KeyboardSource && ((KeyboardSource) obj).data == data;
		}
	}

	public static class MouseButtonSource implements InputSource {
		private final int data;

		public MouseButtonSource(int data) {
			if(data < 0 || data >= MOUSE_BUTTONS_COUNT) {
				throw new IllegalArgumentException("Wrong mo key id: " + data);
			}

			this.data = data;
		}

		@Override
		public String toString() {
			if(data >= 0 && data < MOUSE_BUTTONS_COUNT) {
				return "Mouse button " + data;
			} else {
				return "UNDEFINDED";
			}
		}

		@Override
		public int toID() {
			return -data - 1;
		}

		@Override
		public int getButton() {
			return data;
		}

		@Override
		public int hashCode() {
			return  (Utils.intHash(data) | 0xE0000000) & 0xEFFFFFFF;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof MouseButtonSource && ((MouseButtonSource) obj).data == data;
		}
	}

	public static class ScrollActionSource implements InputSource {
		public static final int MOUSE_WHEEL_UP = -MOUSE_BUTTONS_COUNT - 1, MOUSE_WHEEL_DOWN = MOUSE_WHEEL_UP - 1;

		private final boolean up;

		public ScrollActionSource(boolean up) {
			this.up = up;
		}

		@Override
		public String toString() {
			if (up) {
				return "Mouse wheel up";
			} else {
				return "Mouse wheel down";
			}
		}

		@Override
		public int toID() {
			return up ? MOUSE_WHEEL_UP : MOUSE_WHEEL_DOWN;
		}

		@Override
		public int getButton() {
			return up ? 0 : 1;
		}

		@Override
		public int hashCode() {
			return  (Utils.intHash(up ? 123 : 908) | 0xD0000000) & 0xDFFFFFFF;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ScrollActionSource && ((ScrollActionSource) obj).up == up;
		}
	}
}
