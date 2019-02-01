package org.zizitop.pshell.window;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.XML;
import org.zizitop.pshell.utils.exceptions.FileException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for implementation some user input.
 * @author Zizitop
 *
 */
public class InputOption {
	public static final String OPTIONS_FILE_PATH = "res/input.xml";
	public static final Map<String, InputOption> inputOptions = new HashMap<>();

	private static boolean initialized = false;
	private static int nextID = 0;
	private static XML xmlData;

	private static InputOption nullInputOption = new InputOption(null, "null",
			0,
			0, false, "");

	public static InputOption getInputOption(String identifier) {
		if(!initialized) {
			throw new IllegalStateException("Input options not initialized now.");
		} else {
			InputOption io = inputOptions.get(identifier);

			if(io == null) {
				Log.write("Input option not found: " + identifier, 2);
				return nullInputOption;
			} else {
				return io;
			}
		}
	}

	public static void init() throws FileException {
		xmlData = new XML(OPTIONS_FILE_PATH);

		inputOptions.put("null", nullInputOption);

		load(xmlData.getRoot(), "");

		initialized = true;
	}

	public static void saveChanges() throws Exception {
		xmlData.saveChanges();
		xmlData.setChanged(false);
	}

	private static void load(Element el, String path) {
		String name = el.getTagName();

		if(name.equals("io")) {
			path = path.replace("root.", "");

			InputOption io = null;
			try {
				io = createInputOption(el, path);
			} catch (Exception e) {
				Log.write("Wrong input option: " + el.getAttribute("name") + ".", 2);
				Log.writeException(e);
			}

			if(io != null) {
				inputOptions.put(io.identifier, io);
			}
		} else {
			path += (name);
			path += ('.');
			NodeList nl = el.getChildNodes();

			for(int i = 0; i < nl.getLength(); ++i) {
				Node n = nl.item(i);

				if(n instanceof Element) {
					load((Element) n, path);
				}
			}
		}
	}

	public static InputOption[] getAllInputOptions() {
		InputOption[] ret = new InputOption[inputOptions.size()];

		return (InputOption[]) inputOptions.values().toArray(ret);
	}

	private final int id;
	private final String name, identifier;
	private final InputSource defaultInputSource;
	private final boolean changeable;

	private String niceName;

	private InputSource inputSource;
	private final Element src;

	private InputOption(Element src, String name, int defaultKeyCode, int keyCode, boolean changeable, String path) {
		this.id = nextID++;

		this.name = name;
		this.identifier = path + name;

		this.src = src;

		this.defaultInputSource = ShellApplication.getPlatform().inputSourceFromID(defaultKeyCode);
		this.inputSource = ShellApplication.getPlatform().inputSourceFromID(keyCode);
		this.changeable = changeable;
	}

	private static InputOption createInputOption(Element io, String path) {
		String name = io.getAttribute("name");

		Integer keyCode = Utils.parseInteger(io.getAttribute("code"));
		Integer defKeyCode = Utils.parseInteger(io.getAttribute("defcode"));
		Boolean changeable = Utils.parseBoolean(io.getAttribute("changeable"));

		int _defKeyCode = defKeyCode == null ? 0 : defKeyCode;

		return new InputOption(io,
				name.isEmpty() ? "Unnamed" : name,
				_defKeyCode,
				keyCode == null ? _defKeyCode : keyCode,
				changeable == null ? false : changeable, path);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public boolean isChangeable() {
		return changeable;
	}

	public InputSource getDefaultInputSource() {
		return defaultInputSource;
	}

	public void restoreDefaults() {
		inputSource = defaultInputSource;
		xmlData.setChanged(true);
	}

	public void setInputSource(InputSource inputSource) {
		this.inputSource = inputSource;

		if(src != null) src.setAttribute("code", String.valueOf(inputSource.toID()));

		niceName = null;

		xmlData.setChanged(true);
	}

	public String getIdentifier() {
		return identifier;
	}

	public InputSource getInputSource() {
		return inputSource;
	}

	@Override
	public String toString() {
		return "Input option \"" + name + "\". Input source: " + inputSource.toString();
	}

	public String niceStringImplementation() {
		if(niceName != null) {
			return niceName;
		}

		return niceName = Lang.getText("inputOption." + identifier) + ": " + inputSource.toString();
	}
}
