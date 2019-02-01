package org.zizitop.pshell.utils.exceptions;

/**
 * Exception, that appears when program have problems with file work
 * @author Zizitop
 *
 */
public class FileException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Create new File Exception
	 * @param message - message to the user
	 * @param cause - possible cause of exception or null
	 * @param filePath - path to problem file
	 */
	public FileException(String message, Exception cause, String filePath) {
		super(message + "\nPath: \"" + filePath + '"', cause);
	}
}
