package org.zizitop.pshell.utils.controlling;

import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;

import java.util.*;

/**
 * Class, that holds all commands.
 * <br><br>
 * Created 01.05.2018 16:56
 *
 * @author Zizitop
 */
class CommandManager {
	private final Map<String, Command> commands = new HashMap<>();

	public static final CommandManager instance = new CommandManager();

	private CommandManager() {}

	void registerCommand(Command c, String name) {
		commands.put(name, c);
	}

	Command getCommand(String name) {
		return commands.get(name);
	}

	boolean runCommand(String data, Context[] availableContext) throws ParsingException, Command.IllegalContextException {
		String[] tokens = Utils.parseCommand(data);

		if(tokens == null || tokens.length < 1 || tokens[0] == null || tokens[0].isEmpty()) {
			return false;
		}

		Command c = getCommand(tokens[0]);

		if(c == null) {
			throw new ParsingException("Command \"" + tokens[0] + "\" not exist.");
		}

		// Tokens with arguments
		String[] argTokens = Utils.clipArray(String.class, tokens, 1, tokens.length);

		ArgumentType[] argumentTypes = c.getArguments();
		Object[] args = new Object[argTokens.length];

		if(argumentTypes.length != argTokens.length) {
			throw new ParsingException("Wrong arguments count in command \"" +
					c.getName() + "\". Arguments: " + Arrays.toString(argTokens) + ". Expected arguments: " + c.getArgumentsList());
		}

		for(int i = 0; i < args.length; ++i) {
			Object arg = args[i] = argumentTypes[i].parse(argTokens[i]);

			if(arg == null) {
				throw new ParsingException("Wrong argument: \"" + argTokens[i] +
						"\". Expected: " + argumentTypes[i] + ".");
			}
		}

		try {
			return c.runCommand(availableContext, args);
		} catch (RuntimeException e) {
			Console.instance.getPrintWriter().println(e.getMessage() + '\n' + e.getStackTrace()[0].toString());
			return false;
		}
	}

	List<Command> getAllCommands() {
		var ret = new ArrayList<Command>();

		for(Map.Entry<String, Command> e: commands.entrySet()) {
			ret.add(e.getValue());
		}

		return ret;
	}

	static class ParsingException extends Exception {
		public ParsingException(String message) {
			super(message);
		}

		public ParsingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

}
