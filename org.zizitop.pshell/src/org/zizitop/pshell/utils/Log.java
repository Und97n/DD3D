package org.zizitop.pshell.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.controlling.Console;

/**
 * Nice and shine <strong>MY</strong> class for logging<br>
 * All members is static<br>
 * If you need write in log message, run {@link #write(String, int)}
 * If you need save log and stop logging, run {@link #saveLog(String)}
 * 
 * <p>Good bye
 * @author Zizitop
 *
 */
public class Log {
	
	private Log() {
		throw new Error("Try to run private constructor!");
	}
	
	/**
	 * Directory for all logs
	 */
	public static final String LOG_DIR = "";
	
	/**
	 * Warnign levels for messages<br>
	 * Writes in message begin
	 */
	public static final String[] WARNING_LEVELS = new String[]{"[Info]", "[Important]", "[Warning]", "[Alert]", "[Error]"};
	
	/** 
	 * Set to true if need print messages in console. 
	 */
	private static boolean logInConsole = true;
	
	/**
	 * Set to true if need save logs in files.
	 */
	private static boolean logInFile = ShellApplication.getProgramConfig().getBooleanOption("logging");
	
	/**
	 * Minimal logging level<br>
	 * You can change him in properies
	 */
	private static int loggingLevel = ShellApplication.getProgramConfig().getIntOption("loggingLevel");
	
	/** 
	 * Storage of all messages and buffer for log file
	 */
	private static PrintWriter writer;
	
	//No problems with init() method
	static {
		if(logInFile) {
			try {
				Writer fileStrream =  new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(LOG_DIR + "last.log"), "utf-8"));

				writer = new PrintWriter(new LogWriter(Utils.asArray(Console.instance.getWriter(), fileStrream)));
				
				//Launch time
				writer.println(new SimpleDateFormat("dd MMMM y", Locale.US).format(new Date()));
			} catch (FileNotFoundException e) {
				System.out.println("Fatal logging exception!");
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				System.out.println("Fatal logging exception!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Save log to the file(in {@link #LOG_DIR})
	 * @param lastMessage - last message in log
	 */
	public static void saveLog(String lastMessage) {
		if(writer != null) {
			writer.println(lastMessage);
			writer.close();
		}
	}
	
	/**
	 * Write to log one message
	 * @param s - message to write
	 * @param warningLevel - warning level({@link #WARNING_LEVELS})
	 */
	public static void write(String s,int warningLevel){
		if(warningLevel < loggingLevel) {
			return;
		}
		
		String message = getBegin(warningLevel) + s;
		
		if(writer != null) {
			writer.println(message);
		}
		
		if(logInConsole) {
			System.out.println(message);
		}
//		
//		if(warningLevel >= 3) {
//			Utils.beep();
//		}
	}

	/**
	 * Write to log one message
	 * @param s - message to write
	 */
	public static void write(String s) {
		String message = getBegin(-1) + s;

		if(writer != null) {
			writer.println(message);
		}

		if(logInConsole) {
			System.out.println(message);
		}
	}

	/**
	 * Write to log one message without timestamp
	 * @param s - message to write
	 */
	public static void writeDirect(String s) {
		String message = s;

		if(writer != null) {
			writer.println(message);
		}

		if(logInConsole) {
			System.out.println(message);
		}
	}

	/**
	 * Get name of warning level.
	 * @param level - int value(warning level)
	 * @return name of warning level
	 * @see #WARNING_LEVELS
	 */
	private static String getWarningLevel(int level){
		if(level < 0 || level >= WARNING_LEVELS.length) {
			return "";
		}
		
		return WARNING_LEVELS[level];
	}
	
	/**
	 * Write Exception(or Error) to the log
	 * @param e - data to write
	 */
	public static void writeException(Throwable e) {
		if(e == null) {
			e = new Exception("null Exception");
		}
		
		if(writer != null) {
			writer.println();
			e.printStackTrace(writer);
			writer.println();
		}
		
		if(logInConsole) {
			System.out.println();
			e.printStackTrace(System.out);
			System.out.println();
		}
	}
	
	/**
	 * Get beginn of the log message(time, warning level)
	 * @param level - warning level
	 * @return string value of message start
	 * @see #WARNING_LEVELS
	 */
	private static String getBegin(int level){
		return Utils.getTime() + getWarningLevel(level);
	}

	private static class LogWriter extends Writer {
		private final Writer[] writers;

		private LogWriter(Writer[] writers) {
			this.writers = writers;
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			for(int i = 0; i < writers.length; ++i) {
				writers[i].write(cbuf, off, len);
			}
		}

		@Override
		public void flush() throws IOException {
			for(int i = 0; i < writers.length; ++i) {
				writers[i].flush();
			}
		}

		@Override
		public void close() throws IOException {
			for(int i = 0; i < writers.length; ++i) {
				writers[i].close();
			}
		}
	}
}
