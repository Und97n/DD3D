package org.zizitop.pshell.utils;

/**
 * A utility class for safe resource management
 * @author Zizitop
 *
 */
@FunctionalInterface
public interface ResourceManager {
	
	/**
	 * Starts when program exit<br>
	 * <strong>ALL EXCEPTIONS, ERRORS AND RUNTIME EXCEPTIONS HERE IS IGNORED!</strong>
	 */
	void freeResources();
}
