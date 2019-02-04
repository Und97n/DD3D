package org.zizitop.pshell.window;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.controlling.Console;
import org.zizitop.pshell.utils.controlling.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Great tool for game controlling.+
 * <br><br>
 * Created 26.08.18 16:49
 *
 * @author Zizitop
 */
public final class ConsoleScene implements Scene {
	private static final InputOption input_erase = InputOption.getInputOption("erase");
	private static final InputOption input_switchConsole = InputOption.getInputOption("menu.console.switch");
	private static final InputOption input_up = InputOption.getInputOption("menu.console.up");
	private static final InputOption input_down = InputOption.getInputOption("menu.console.down");
	private static final InputOption input_scrollUp = InputOption.getInputOption("menu.console.scrollUp");
	private static final InputOption input_scrollDown = InputOption.getInputOption("menu.console.scrollDown");
	private static final InputOption input_enter = InputOption.getInputOption("enter");

	private static final int LINES_COUNT = 200;
	private static final int LINE_LIMITER = 44;
	private static final int LINES_ON_SCREEN = 20;
	private static final int LINES_OFFSET = 10;

	private static final char COMMAND_PREFIX = '/';

	private static final double ENTER_FIELD_HEIGHT = 0.034;

	private static List<String> userLines = new ArrayList<>();

	private static char[][] lines = new char[LINES_COUNT][LINE_LIMITER];
	private static int lastLineId;

	private StringBuilder input = new StringBuilder();

	private final Scene previousScene;
	private Bitmap background;
	private int lastSelectedCommand = -1;
	private int offset = 0;

	static {
		Console.ConsoleListener cl = new ConsoleSceneListener();

		Console.instance.addListener(cl);
		Console.instance.proceedAllAvailableText(cl);
	}

	private ConsoleScene(Scene previousScene, Bitmap background) {
		this.previousScene = previousScene;
		this.background = background.copy();
	}

	public static Scene getConsoleScene(Scene previousScene, Bitmap background) {
		return new ConsoleScene(previousScene, background);
	}



	public static synchronized void writeString(final String s) {
		writeString(s.toCharArray(), 0, s.length());
	}

	public static synchronized void writeString(final char[] data, final int start, final int end) {
		int endd = 0;

		int nextStart = -1;

		if((end - start) > LINE_LIMITER) {
			endd = LINE_LIMITER + start;
			nextStart = endd;
		} else {
			endd = end;
		}

		++lastLineId;

		checkFreeLines();

		for(int i = start; i < endd; ++i) {
			char ch = data[i];

			if(ch == '\n') {
				nextStart = i + 1;
				break;
			} else {
				lines[lastLineId][i - start] = data[i];
			}
		}

		if(nextStart >= 0) {
			writeString(data, nextStart, end);
		}
	}

	static void checkFreeLines() {
		if(lastLineId >= LINES_COUNT) {

			for(int i = LINES_OFFSET; i < lines.length; ++i) {
				char[] tmp = lines[i - LINES_OFFSET];
				lines[i - LINES_OFFSET] = lines[i];
				lines[i] = tmp;
			}

			lastLineId -= LINES_OFFSET;

			for(int i = lastLineId; i < lines.length;++i) {
				final char[] line = lines[i];

				for(int j = line.length - 1; j >= 0; --j) {
					line[j] = 0;
				}
			}
		}
	}

	@Override
	public void tick(Window window) {
		String text = window.getLastEnteredText();

		if (lastSelectedCommand >= 0) {
			input = new StringBuilder(userLines.get(lastSelectedCommand));
		}

		if (window.getActionsCount(input_erase) > 0) {
			if (input.length() > 0) {
				input.deleteCharAt(input.length() - 1);
				lastSelectedCommand = -1;
			} else {
				Utils.beep();
			}
		} else if (!text.isEmpty()) {
			for(int i = 0; i < text.length(); ++i) {
				if(text.charAt(i) >= 32) {
					input.append(text.charAt(i));
				}
			}

			lastSelectedCommand = -1;
		}

		if (window.getActionsCount(input_up) > 0) {
			if (lastSelectedCommand == -1) {
				lastSelectedCommand = userLines.size() - 1;
			} else {
				--lastSelectedCommand;

				if (lastSelectedCommand < 0) {
					lastSelectedCommand = 0;
					Utils.beep();
				}
			}
		}

		if (window.getActionsCount(input_down) > 0 && lastSelectedCommand >= 0) {
			++lastSelectedCommand;

			if (lastSelectedCommand >= userLines.size()) {
				lastSelectedCommand = userLines.size() - 1;
			}
		}

		if (window.getActionsCount(input_enter) > 0) {
			String s = input.toString().replace("\n", "");

			userLines.add(s);

			if (s.length() > 0 && s.charAt(0) == COMMAND_PREFIX) {
				Console.instance.command(s.substring(1),
						Utils.concatArrays(Context.class, Utils.asArray(window.getContext()),
								getContext(), previousScene.getContext()));
			} else {
				Log.write(s);
			}

			lastSelectedCommand = -1;

			input.setLength(0);
		}

		offset += window.getActionsCount(input_scrollUp) - window.getActionsCount(input_scrollDown);

		if(offset > 0) {
			offset = 0;
		}

		if(offset < -(lastLineId + 1 - LINES_ON_SCREEN)) {
			offset = -(lastLineId + 1 - LINES_ON_SCREEN);
		}

		if(window.getActionsCount(input_switchConsole) > 0) {
			window.changeScene(previousScene);
		}
	}

	@Override
	public void draw(Bitmap canvas, DisplayMode displayMode) {
		canvas.drawBackground(background);

		final int bufferlength = lastLineId + 1;

		int startLine = bufferlength - LINES_ON_SCREEN + offset;

		int linesCount;

		if(startLine >= 0) {
			linesCount = Math.min(bufferlength + offset, LINES_ON_SCREEN);
		} else {
			startLine = 0;
			linesCount = Math.min(bufferlength, LINES_ON_SCREEN);
		}

		for(int i = 0; i < linesCount; ++i) {
			char[] line = lines[i + startLine];

			final int color = line[0] == '>' ? 0xff0000 : -1;

			DDFont.CONSOLE_FONT.draw(canvas, 0, (double)i / LINES_ON_SCREEN - ENTER_FIELD_HEIGHT, line, color);
		}

		canvas.fillRect_SC(0, 0, 1.0 - ENTER_FIELD_HEIGHT, 1, ENTER_FIELD_HEIGHT);
		DDFont.CONSOLE_FONT.draw(canvas, 0, 1 - ENTER_FIELD_HEIGHT, input, 0xff0000);

		//Draw slider
		{
			final double width = DDFont.CONSOLE_FONT.getWidth(" ");

			double sliderHeight = (double)(LINES_ON_SCREEN) / bufferlength;
			sliderHeight = sliderHeight > 1.0 ? 1.0 : sliderHeight;

			double y = (double)startLine / bufferlength;

			canvas.fillRect_SC(0, 1.0 - width, 0, width, 1.0);
			canvas.fillRect_SC(0x8f000f, 1.0 - width, y, width, sliderHeight);
			canvas.drawRect_SC(0x808080, 1.0 - width, y, width, sliderHeight, canvas.getPixelScSize());
		}
	}

	private static class ConsoleSceneListener implements Console.ConsoleListener {

		@Override
		public void addLine(String line) {
			writeString(line);
		}

		@Override
		public void clear() {
			lastLineId = 0;

			for(char[] line: lines) {
				Arrays.fill(line, ' ');
			}
		}
	}
}