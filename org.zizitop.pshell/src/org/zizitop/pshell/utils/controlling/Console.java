package org.zizitop.pshell.utils.controlling;

import org.zizitop.pshell.DefaultContext;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Holder of all text controlling data.
 * <br><br>
 * Created 01.05.2018 16:47
 *
 * @author Zizitop
 */
public final class Console {
	public static final Console instance = new Console();

	private List<String> logs = new ArrayList<>();

	private final Writer writer = new CWriter();
	private PrintWriter printWriter = new PrintWriter(writer);

	private List<ConsoleListener> listeners = new ArrayList<>();

	private Console() {}

	/**
	 * Run some list of commands from a file. If errors, EOF or ".end" received, stop running.
	 * @param fileName - name of scenario file
	 * @param c
	 * @throws IOException - if problems with file reading
	 * @return -1 if ok, else - index of bad command.
	 */
	public int runScenario(String fileName, Context[] c) throws IOException {
		try(Scanner scn = new Scanner(new File(fileName))) {
			return runScenario(() -> {
				while(scn.hasNextLine()) {
					String line = scn.nextLine();

					// Skip empty spaces
					if (line.trim().isEmpty()) {
						continue;
					}

					return line;
				}

				return ".end";
			}, Utils.asArray(DefaultContext.defaultContext));
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Run some list of commands. If errors or ".end" received, stop running.
	 * @param commands
	 * @param c
	 * @return -1 if ok, else - index of bad command.
	 */
	public int runScenario(Supplier<String> commands, Context[] c) {
		String tmp;

		int i = 0;

		while(!(tmp = commands.get()).equals(".end")) {
			if(!command(tmp, c)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Run some text command.
	 * @param s text input.
	 * @param c context of command.
	 * @return true if ok
	 */
	public boolean command(String s, Context[] c) {
		// Do nothing if user need to do nothing
		if(s.trim().isEmpty()) {
			return true;
		}

		if(!s.startsWith(" ")) {
			Log.writeDirect(">" + s);
		}

		try {
			return CommandManager.instance.runCommand(s, c);
		} catch (CommandManager.ParsingException | Command.IllegalContextException e) {
			Log.writeDirect(e.getMessage());
//			Log.writeException(e);
		}

		return false;
	}

	/**
	 * Get PrintWriter, that prints all to the console.
	 */
	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	/**
	 * Get Writer, that prints all to the console.
	 */
	public Writer getWriter() {
		return writer;
	}

	private class CWriter extends Writer {
		private StringBuilder tmpBuffer = new StringBuilder();

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			int lastPos = off;
			int end = off + len;

			for(int i = off; i < end; ++i) {
				char c = cbuf[i];

				if(c == '\n') {
					tmpBuffer.append(cbuf, lastPos, i - lastPos);
					flush();

					lastPos = i + 1;
				}
			}

			tmpBuffer.append(cbuf, lastPos, end - lastPos);
		}

		@Override
		public void flush() throws IOException {
			// Put all needed lines to main console buffer.

			String newLine = tmpBuffer.toString();
			tmpBuffer.setLength(0);

			logs.add(newLine);

			for(ConsoleListener c: listeners) {
				c.addLine(newLine);
			}
		}

		@Override
		public void close() throws IOException {}
	}

	public boolean addListener(ConsoleListener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(ConsoleListener listener) {
		return listeners.remove(listener);
	}

	public void proceedAllAvailableText(ConsoleListener cl) {
		for(String s: logs) {
			cl.addLine(s);
		}
	}

	public List<Command> getAllCommands() {
		return CommandManager.instance.getAllCommands();
	}

	public Command getCommand(String name) {
		return CommandManager.instance.getCommand(name);
	}

	public interface ConsoleListener {
		void addLine(String line);
		void clear();
	}


	public void clear() {
		logs.clear();

		for(ConsoleListener cl : listeners) {
			cl.clear();
		}
	}
}
