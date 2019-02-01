package org.zizitop.pshell.utils.exceptions;

/**
 * Exception, that appears when program have problems with file loading
 * @author Zizitop
 *
 */
public class FileLoadingException extends FileException {
	private static final long serialVersionUID = 1L;

	/**
	 * {@link FileException#FileException(String, Exception, String)}
	 */
	public FileLoadingException(String message, Exception cause, String filePath) {
		super(message, cause, filePath);
	}

}
