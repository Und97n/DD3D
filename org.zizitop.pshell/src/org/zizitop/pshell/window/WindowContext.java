package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.controlling.Context;

/**
 * Just context of window.
 * <br><br>
 * Created 03.05.2018 10:24
 *
 * @author Zizitop
 */
public final class WindowContext implements Context {
	private Window window;

	static {
		
	}

	public WindowContext(Window window) {
		this.window = window;
	}

	@Override
	public Object[] getContextData() {
		return Utils.asArray(window);
	}

	@Override
	public String getNamespace() {
		return "window";
	}
}
