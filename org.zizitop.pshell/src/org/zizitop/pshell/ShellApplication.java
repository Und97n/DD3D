package org.zizitop.pshell;

import org.zizitop.pshell.utils.*;
import org.zizitop.pshell.utils.controlling.Console;
import org.zizitop.pshell.utils.controlling.Context;
import org.zizitop.pshell.utils.exceptions.FileException;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is cool class for Shell launching. Just run
 * {@link #launch(Platform, String, String, Supplier, Starter, String[], Class[])} with all needed.
 * <br><br>
 * Created 24.08.2017 16:26:41
 * @author Zizitop
 */
public final class ShellApplication {
	private static Platform platform;
	private static Starter starter;

	/**
	 * Path to property file
	 */
	private static final String PROPERTY_FILE_PATH = "config.cfg";

	/**
	 * Instance for using settings
	 */
	private static Configuration config;

	/**
	 * List of all ResourceManagers.<br>
	 * To add resource manager use {@link #addResourceManager(ResourceManager)}
	 */
	private static List<ResourceManager> resources = new ArrayList<ResourceManager>();

	private static String VERSION;

	private static int userScreenWidth, userScreenHeight;

	private ShellApplication() {
		throw new Error("Don't try to run private constructor!");
	}

	/**
	 * Main menthod<br>
	 * Here starts program
	 * @param  starter - function, that run`s when all systems are initialized
	 * @param args - program arguments
	 */
	public static void launch(Platform platform, String windowTitle, String version, Supplier<DisplayMode> dms, Starter starter, String[] args, Class<? extends Context>[] neededContext) {
		//TODO Убрать это нахуй
		System.out.println(
				Utils.countCodeStrings("org.zizitop.pshell/src/") +
						Utils.countCodeStrings("org.zizitop.game/src/") +
								Utils.countCodeStrings("org.zizitop.dd3d/src/") +
									Utils.countCodeStrings("org.zizitop.desktop/src/"));
		VERSION = version;

		ShellApplication.platform = platform;

		try {
			//Main config loading
			try {
				config = new Configuration(PROPERTY_FILE_PATH, true);
			} catch(Exception e1) {
				throw new FileException("Main configuration file not found.", e1, PROPERTY_FILE_PATH);
			}

			Log.write("Logging started", 1);
			Log.write("Properties loaded.", 1);

			Log.write("Input loading.", 1);
			Log.write("Input loaded.", 1);

			InputOption.init();

			ShellApplication.starter = starter;

//			ResourceLoader.load();

			for(Class<? extends Context> c: neededContext) {
				try {
					// For running static initializer`s
					Class.forName(c.getName());
				} catch (Exception e) {}
			}

			int screenWidth, screenHeight;

			userScreenWidth = platform.getUserScreenWidth();
			userScreenHeight = platform.getUserScreenHeight();

			DisplayMode dm = dms.get();

			if(userScreenWidth < dm.getBaseWidth() || userScreenHeight < dm.getBaseHeight()) {
				throw new Exception("Your screen is too small(less that needed for correct working)", null);
			}

			userScreenWidth = Math.max(userScreenWidth, dm.getBaseWidth());
			userScreenHeight = Math.max(userScreenHeight, dm.getContentHeight());

			screenWidth = Math.min(config.getIntOption("windowWidth"), userScreenWidth);
			screenHeight = Math.min(config.getIntOption("windowHeight"), userScreenHeight);

			DDFont.initDefaultFonts(dm);

			Window mainWindow = platform.createWindow(windowTitle + " " + VERSION, version,
					screenWidth, screenHeight, dm, false);

			starter.start(mainWindow);

			String startupFile = ShellApplication.getProgramConfig().getOption("startupScenario");

			if(startupFile != null && !startupFile.isEmpty() && !startupFile.equals("null")) {
				try {
					Console.instance.runScenario(startupFile, Utils.asArray(DefaultContext.defaultContext));
				} catch (Exception e) {
					Log.write("Problems with reading startup scenario.", 3);
					Log.writeException(e);
				}
			}

			//Game main loop
			mainWindow.startMainLoop();
		} catch (Throwable e) {
			crash(e);
		}

		//Normal program exit
		exit(0);
	}

	static volatile boolean crashing = false;

	/**
	 * This method is for unresolved exceptions.<br>
	 * Better don't use. This method is for main class<br>
	 * This method save crash log and launches {@link #exit(int)}
	 *
	 * @param cause - cause of problems and information source<br>
	 * 				  for crash file
	 */
	public static void crash(Throwable cause) {
		// Only one crash at one moment!
		if(crashing) {
			Thread.currentThread().interrupt();
			return;
		}

		crashing = true;

		try {
			try(PrintWriter crashFile = new PrintWriter("crash.txt", "UTF-8")) {
				cause.printStackTrace(crashFile);
			}

			cause.printStackTrace();

			StringBuilder message = new StringBuilder("Unhandled exception: ");

			Throwable t = cause;

			while(t != null) {
				if(t != cause) {
					message.append("Caused by: ");
				}

				message.append(t.getClass().getName());

				String msg = t.getMessage();

				if(msg != null) {
					message.append(": ");
					message.append(msg);
				}

				message.append("\n");

				t = t.getCause();
			}

			//For user
			platform.showMessageDialog(message.toString());

			exit(1);
		} catch (Exception ee) {
			ee.printStackTrace();

			//If unresolved problems when another problems are solved(what?)
			try {
				platform.showMessageDialog(ee.getMessage());
			} catch(Throwable e) {
				Log.writeException(ee);
				ee.printStackTrace();
				Log.writeException(e);
				e.printStackTrace();
			}

			exitOnFatalError("Fatal error: exception while crashing.");
		}
	}

	/**
	 * Run all resource managers, save log file and exit with {@link System#exit(int)}
	 * @param code - exit code
	 */
	public static void exit(int code) {
		for(ResourceManager m : resources) {
			try {
				m.freeResources();
			} catch(Throwable e) {}
		}

		Log.saveLog("Logging end");
		System.out.println("Shutting down");

		System.exit(code);
	}

	/**
	 * Add resource manager<br>
	 * It run when program exit
	 * @see ResourceManager
	 * @param m - resource manager
	 */
	public static void addResourceManager(ResourceManager m) {
		if(m != null) {
			resources.add(m);
		}
	}

	/**
	 * Exit if very big problems<br>
	 * Don't use outside the main class!<br>
	 *
	 * @param message - last message for user in dialog Window
	 */
	public static void exitOnFatalError(String message) {
		try {
			platform.showMessageDialog(message);
		} catch(Throwable e) {
			Log.writeException(e);
			e.printStackTrace();
		}

		//Only Devil launches this method!
		System.exit(666);
	}

	public static Configuration getProgramConfig() {
		return config;
	}

	public static int getUserScreenWidth() {
		return userScreenWidth;
	}

	public static int getUserScreenHeight() {
		return userScreenHeight;
	}

	@FunctionalInterface
	public interface Starter {
		void start(Window window);
	}

	public static Platform getPlatform() {
		return platform;
	}
}