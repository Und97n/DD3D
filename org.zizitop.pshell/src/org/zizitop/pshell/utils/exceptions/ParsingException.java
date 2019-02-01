package org.zizitop.pshell.utils.exceptions;

/**
 * If problems with XML file parsing.
 * <br><br>
 * Created 18.03.2018 21:44
 *
 * @author Zizitop
 */
public class ParsingException extends FileException {

	/**
	 * {@link FileException#FileException(String, Exception, String)}
	 */
	public ParsingException(String message, Exception cause, String filePath) {
		super(message, cause, filePath);
	}
}
