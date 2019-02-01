package org.zizitop.pshell.utils.controlling;

import org.zizitop.pshell.utils.Utils;

/**
 * Possible types of arguments for {@link Command}.
 */
public enum ArgumentType {
	String(String.class, s -> s.isEmpty() ? null : s),

	Class(java.lang.Class.class, s -> {
		try {
			if(s.startsWith("def.")) {
				return java.lang.Class.forName("org.zizitop.dd3d.content." + s.split("def.")[1]);
			}

			return java.lang.Class.forName(s);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}),

	Number(Double.class, Utils::parseDouble);

	private final Class<?> type;
	private final ArgumentParser parser;

	ArgumentType(Class<?> type, ArgumentParser parser) {
		this.type = type;
		this.parser = parser;
	}

	/**
	 * Parse some String to needed Object.
	 * If problems - return null
	 */
	public Object parse(String data) {
		return parser.parse(data);
	}

	@FunctionalInterface
	private interface ArgumentParser {
		/**
		 * Parse some String to needed Object.
		 * If problems - return null
		 */
		Object parse(String data);
	}
}
