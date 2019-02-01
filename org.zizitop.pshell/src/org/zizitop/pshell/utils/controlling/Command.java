package org.zizitop.pshell.utils.controlling;

import org.zizitop.pshell.utils.Utils;

import java.util.Arrays;

/**
 * Abstract instruction for program in text form.
 * <br><br>
 * Created 01.05.2018 16:50
 *
 * @author Zizitop
 */
public final class Command {
	private final String name, help;
	private final CommandAction action;
	private final Class<? extends Context>[] context;
	private final ArgumentType[] arguments;

	/**
	 * Create new console command.
	 * @param context context, that we wan`t here.
	 * @param arguments needed arguments for a command.
	 * @param name name of command(and identifier of it)
	 * @param action action, that runs on command execution.
	 */
	public Command(Class<? extends Context>[] context, ArgumentType[] arguments, String name, String help, CommandAction action) {
		if(context == null || name == null || action == null) {
			throw new IllegalArgumentException("Action and name of command must be not null.");
		}

		this.arguments = arguments;
		this.context = context;
		this.name = name;
		this.help = help;
		this.action = action;

		CommandManager.instance.registerCommand(this, name);
	}

	/**
	 * Check, if context array have all needed context instances for this command.
	 */
	public boolean isCorrectContext(Class<? extends Context>[] c) {
		return Utils.contains(context, c);
	}

	/**
	 * Get name of command.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get action of this command.
	 */
	public CommandAction getAction() {
		return action;
	}

	/**
	 * Run a command.
	 * @param cc all context, that we have here.
	 * @param arguments all arguments, that user print.
	 * @return true if command run successfully
	 * @throws IllegalContextException if we don`t have all needed context.
	 */
	public boolean runCommand(Context[] cc, Object[] arguments) throws IllegalContextException {
		Context[] c = Arrays.copyOf(cc, cc.length);

		Object[][] contextData = new Object[this.context.length][];

		contextFinding: for(int i = 0; i < context.length; ++i) {
			Class<? extends Context> needed = this.context[i];

			for(int j = 0; j < c.length; ++j) {
				Context t = c[j];

				if(t != null && t.getClass().equals(needed)) {
					contextData[i] = t.getContextData();

					break contextFinding;
				}
			}

			throw new IllegalContextException("No such context: " + needed.getSimpleName());
		}

		action.action(arguments, contextData);

		return true;
	}

	public static class IllegalContextException extends Exception {
		IllegalContextException(String message) {
			super(message);
		}
	}

	/**
	 * Get all needed arguments for a command.
	 */
	public ArgumentType[] getArguments() {
		return arguments;
	}

	/**
	 * Get all needed arguments for this command in String.
	 */
	public String getArgumentsList() {
		StringBuilder sb = new StringBuilder();

		sb.append('[');

		for(int i = 0; i < arguments.length; ++i) {
			sb.append(arguments[i].name());

			if(i != arguments.length -1) {
				sb.append(", ");
			}
		}

		sb.append(']');

		return sb.toString();
	}

	@FunctionalInterface
	public interface CommandAction {
		void action(Object[] arguments, Object[][] contextData);
	}

	public String getHelp() {
		return help;
	}
}
