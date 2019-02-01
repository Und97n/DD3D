package org.zizitop.pshell.menu;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.window.InputOption;

/**
 * Class for making selector of controls.
 * @author Zizitop
 *
 */
public class ControlsSelectorComponent extends SelectorComponent<InputOption> {

	private ControlsSelectorComponent(List<InputOption> objects) {
		super(objects, 0.0125, 0.5, DDFont.INVENRORY_FONT);
	}

	public static ControlsSelectorComponent getControlsSelectorComponent() {
		List<InputOption> im = new ArrayList<>();

		for(InputOption io: InputOption.getAllInputOptions()) {
			im.add(io);
		}

		im.removeIf(io -> !io.isChangeable());

		im.sort(Comparator.comparing(InputOption::getIdentifier));

//		//Labels with input options names
//		ArrayList<CharSequence> namesList = new ArrayList<>();
//
//		for(InputOption inp: im) {
//			namesList.add(inp.toString());
//		}
		
		return new ControlsSelectorComponent(im);
	}

	@Override
	protected String objToString(InputOption inputOption) {
		return inputOption.niceStringImplementation();
	}
}
