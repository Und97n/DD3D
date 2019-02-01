package org.zizitop.pshell.utils;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;

/**
 * 
 * @author Zizitop
 *
 */
public class Lang {
	private static final String DEFAULT_LANGUAGE = "en";
	public static final String OPTION_NOT_FOUND = "?????";
	
	private static Configuration currentLanguage;
	
	static {
		String lng = ShellApplication.getProgramConfig().getOption("language").toLowerCase();
		
		try {
			currentLanguage = new Configuration("res/lang/" + lng + ".l", false);
//			currentLanguage.forEach((k, v) -> System.out.println(k + "   " + v));
		} catch(FileLoadingException e) {
			Log.write("Language not found: " + lng + ". Default language selected.", 3);
			Log.writeException(e);
			ShellApplication.getProgramConfig().setOption("language", DEFAULT_LANGUAGE);
			lng = DEFAULT_LANGUAGE;
			
			try {
				currentLanguage = new Configuration("res/lang/" + lng + ".l", false);
			} catch (FileLoadingException e1) {
				Log.write("No default language lang file.", 4);
				Log.writeException(e1);
				
				currentLanguage = null;
			}
		}
	}
	
	public static String getText(String option) {
		if(currentLanguage == null) {
			return OPTION_NOT_FOUND;
		}
		
		return currentLanguage.getOptionSafe(option, OPTION_NOT_FOUND);
	}
}
