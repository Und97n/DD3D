package org.zizitop.pshell.utils.controlling;

/**
 * Just a context for commands, that helps to put all needed data to command arguments.
 * Commands, that use this context, need to define in static initializer.
 * <br><br>
 * Created 01.05.2018 18:19
 *
 * @author Zizitop
 */
public interface Context {
	Object[] getContextData();

	String getNamespace();
}
