package org.zizitop.pshell;

import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.controlling.ArgumentType;
import org.zizitop.pshell.utils.controlling.Command;
import org.zizitop.pshell.utils.controlling.Console;
import org.zizitop.pshell.utils.controlling.Context;

import java.io.IOException;

/**
 * Default context without any additional data or rules.
 * <br><br>
 * Created 01.05.2018 18:21
 *
 * @author Zizitop
 */
public final class DefaultContext implements Context {
	public static final Context defaultContext = new DefaultContext();

	static {
		new Command(Utils.asArray(defaultContext.getClass()), Utils.asArray(ArgumentType.String, ArgumentType.String),
				"changeOption",
				"changeOption(string optionName, string newValue) - Change some program option.",
				(arg, contextData) -> {

			String opName = (String) arg[0];
			String opNewValue = (String) arg[1];

			ShellApplication.getProgramConfig().setOption(opName, opNewValue);

			Log.write("Option \"" + opName + "\" changed to \"" + opNewValue + "\" successfully.");
		});

		new Command(Utils.asArray(defaultContext.getClass()), Utils.asArray(ArgumentType.String),
				"log",
				"log(string data) - print to log some message.",
				(arg, contextData) -> {
			String s = (String) arg[0];

			Log.write(s);
		});

		new Command(Utils.asArray(defaultContext.getClass()), new ArgumentType[]{},
				"cls",
				"cls(void) - clear console.",
				(arg, contextData) -> {
			Console.instance.clear();
		});

		new Command(Utils.asArray(defaultContext.getClass()), Utils.asArray(ArgumentType.String),
				"help",
				"help(string commandName) - print some information about needed command. " +
						"If argument is 'all', then just print all commands info.",
				(arg, contextData) -> {

			String s = (String)arg[0];

			if(s.equals("all")) {
				Console.instance.getAllCommands().forEach(c -> Console.instance.getPrintWriter().print("\n\n\t" + c.getHelp()));
			} else {
				Command c = Console.instance.getCommand(s);

				if(c == null) {
					Log.write("Command \"" + s + "\" not exist.");
				} else {
					Console.instance.getPrintWriter().println("\t" + c.getHelp());
				}
			}
		});
	}

	protected DefaultContext() {}

	@Override
	public Object[] getContextData() {
		return new Object[0];
	}

	@Override
	public String getNamespace() {
		return "";
	}
}
