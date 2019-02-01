package org.zizitop.pshell.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Properties;
import java.util.function.BiConsumer;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;

/**
 * Cool class, that help to manage many paramethers in your program.
 * <br><br>
 * Created 05.03.2018 0:50:53 
 * @author Zizitop
 */
public class Configuration implements ResourceManager {
	public static final String CODEPAGE = "UTF-8";
	
	private final Properties props;
	private final String filePath;
	private final boolean needSaveOnExit;
	
	private boolean changed = false;
	
	public Configuration(String filePath, boolean needSaveOnExit) throws FileLoadingException {
		this.filePath = filePath;
		this.needSaveOnExit = needSaveOnExit;
		
		try(FileInputStream fis = new FileInputStream(filePath)) {
			props = new Properties();
			
			InputStreamReader isr = new InputStreamReader(fis, CODEPAGE);
			Reader r = new BufferedReader(isr);
			
			props.load(r);
		} catch(Exception e) {
			throw new FileLoadingException("Can't load configuration file.", e, filePath);
		}
		
		ShellApplication.addResourceManager(this);
		
	}
	
	/**
	 * Set program property
	 */
	public void setOptionBoolean(String option, boolean value) {
		setOption(option, String.valueOf(value));
	}
	
	/**
	 * Set program property
	 */
	public void setOptionInt(String option, int value) {
		setOption(option, String.valueOf(value));
	}
	
	/**
	 * Set program property
	 */
	public void setOptionDouble(String option, double value) {
		setOption(option, String.valueOf(value));
	}
	
	/**
	 * Set program property
	 */
	public void setOption(String option, String value) {
		if(!value.equals(props.getProperty(option))) {			
			props.setProperty(option, value);
			changed = true;
		}
	}
	
	/**
	 * Get property value from configuration file.
	 * @param option - name of property
	 * @return value of property
	 * @throws IllegalArgumentException if needed property not found or corrupted.
	 */
	public boolean getBooleanOption(String option) {
		String value = props.getProperty(option);
		Boolean v = Utils.parseBoolean(value);
		
		if(v == null) {
			throw new IllegalArgumentException("Boolean option \"" + option + "\" not fount or corrupted.");			
		} else {
			return v;
		}
	}
	
	/**
	 * Get property value from configuration file.
	 * @param option - name of property
	 * @return value of property
	 * @throws IllegalArgumentException if needed property not found or corrupted.
	 */
	public int getIntOption(String option) {
		String value = props.getProperty(option);
		Integer v = Utils.parseInteger(value);
		
		if(v == null) {
			throw new IllegalArgumentException("Integer option \"" + option + "\" not fount or corrupted.");			
		} else {
			return v;
		}
	}
	
	/**
	 * Get property value from configuration file.
	 * @param option - name of property
	 * @return value of property
	 * @throws IllegalArgumentException if needed property not found or corrupted.
	 */
	public double getDoubleOption(String option) {
		String value = props.getProperty(option);
		Double v = Utils.parseDouble(value);
		
		if(v == null) {
			throw new IllegalArgumentException("Double option \"" + option + "\" not fount or corrupted.");			
		} else {
			return v;
		}
	}
	
	/**
	 * Get property value from configuration file.
	 * @param option - name of property
	 * @return value of property
	 * @throws IllegalArgumentException if needed property not found or corrupted.
	 */
	public String getOption(String option) {
		String value = props.getProperty(option);
		
		if(value == null) {
			throw new IllegalArgumentException("String option \"" + option + "\" not fount or corrupted.");			
		} else {
			return value;
		}
	}

	/**
	 * Iterate all entries in configuration
	 * @param consumer - action for all entries
	 */
	public void forEach(BiConsumer<? super Object, ? super Object> consumer) {
		props.forEach(consumer);
	}
	
	/**
	 * Save current properties state.
	 * This method launches at program exit if properties changet and needed option enabled
	 */
	public void saveProperties() {
		try(PrintWriter writer = new PrintWriter(filePath, CODEPAGE)) {
			props.store(writer, "Last modified: " + Utils.getTime());
			
			Log.write("Configuration saved successfully at path: " + filePath, 1);
		} catch(IOException e) {
			Log.write("Can't save properties.", 3);
			Log.writeException(e);
		}
	}

	/**
	 * Get property value from configuration file.
	 * @param option - name of property
	 * @return value of property, defVal if not found
	 */
	public String getOptionSafe(String option, String defVal) {
		String value = props.getProperty(option);

		if(value == null) {
			return defVal;
		} else {
			return value;
		}
	}

	public boolean containsOption(String key) {
		return props.containsKey(key);
	}

	@Override
	public void freeResources() {
		if(needSaveOnExit && changed) {
			saveProperties();
		}
	}
}
