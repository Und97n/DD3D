package org.zizitop.pshell.utils.exceptions;

/**
 * Exception, that appears when program have problems with file saving
 * @author Zizitop
 *
 */
public class FileSavingException extends FileException {
	private static final long serialVersionUID = 1L;

	/**
	 * {@link FileException#FileException(String, Exception, String)}
	 */
	public FileSavingException(String message, Exception cause, String path) {
		super(message, cause, path);
	}

}
