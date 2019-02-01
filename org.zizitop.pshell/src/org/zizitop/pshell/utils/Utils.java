package org.zizitop.pshell.utils;

import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.utils.exceptions.FileSavingException;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * This is a class with static utility methods.
 * @author Zizitop and some people from internet
 */
public final class Utils {
	/**
	 * Utility for timework
	 */
	private static Date date = new Date();
	
	/**
	 * Date format for timeworking.<br>
	 * In European Standard and US lokale
	 */
	private static SimpleDateFormat time = new SimpleDateFormat("[H:mm ss]", Locale.US);
	
	/**
	 * Date format for date writing.<br>
	 * Not Standard. US lokale
	 */
	private static SimpleDateFormat timeAndDate = new SimpleDateFormat("d-M-y H-mm-ss-S", Locale.US);
	
	/**
	 * Get current time in pattern: "[H:mm ss]"
	 * @return
	 */
	public static String getTime() {
		date.setTime(System.currentTimeMillis());
		return time.format(date);
	}
	

	/**
	 * Get current time.
	 */
	public static double getTimeInSeconds() {

		return System.currentTimeMillis() / 1000.0;
	}
	
	/**
	 * Get current date and time in pattern: "G-y-M-d-H:mm ss S"
	 * @return
	 */
	public static String getDateAndTime() {
		date.setTime(System.currentTimeMillis());
		return timeAndDate.format(date);
	}
	

//

//
//	public static BufferedImage loadBuf(InputStream is) throws FileLoadingException {
//		if(is == null){
//        	throw new NullPointerException("InputStream is null.");
//        }
//
//        Image imageX;
//        try {
//			imageX = ImageIO.read(is);
//		} catch (IOException e) {
//			throw new FileLoadingException("Problems with file loading.", e, "unknow");
//		}
//
//        BufferedImage bl = new BufferedImage(imageX.getWidth(null),
//                imageX.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//
//        //Strange tools
//        Graphics2D ig = bl.createGraphics();
//        ig.drawImage(imageX, 0, 0, null);
//        ig.dispose();
//
//        return bl;
//	}
//
//	public static javafx.scene.image.Image loadImage(String path) throws FileLoadingException {
//		try {
//			return new javafx.scene.image.Image("file:" + path);
//		} catch(Exception e) {
//			throw  new FileLoadingException("Can't load image.", e, path);
//		}
//	}
//
//	public static Media loadMedia(String path) throws FileLoadingException {
//		try {
//			return new Media(new File(path).toURI().toString());
//		} catch(Exception e) {
//			throw  new FileLoadingException("Can't load sound.", e, path);
//		}
//	}

	/**
	 * Count strings in source code
	 * @param path - path to source code
	 * @return number of source code strings, -1 if problems
	 */
	public static int countCodeStrings(String path){
		try {
			int lines = 0;
			File file = new File(path);
			
			if(file.isDirectory()) {
				String[] fln = file.list();
		    	
				for(int i = 0; i < fln.length; ++i){
		            lines += countCodeStrings(path + "/"+fln[i]);
		    	}
			
			} else {
	            
				try(FileReader fileReader = new FileReader(new File(path))) {
					try(BufferedReader bufferedReader = new BufferedReader(fileReader)) {
						 
						while(bufferedReader.readLine() != null) {
				            	++lines;
						 }
					}
				}
			}
			
			return lines;
		} catch(Exception e) {}
		return -1;
	}
	
	/**
	 * This function read object from file
	 * 
	 * @param path - path to file
	 * @return <code>Object</code>
	 * @throws FileLoadingException if problems
	 */
	
	@SuppressWarnings("resource")
	public static Object deSerializeData(String path) throws FileLoadingException {
		FileInputStream fis;
		Object obj;
		ObjectInputStream oin;
		
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new FileLoadingException("File not found.", e, path);
		}
		try {
			oin = new ObjectInputStream(fis);
		} catch (IOException e) {
			throw new FileLoadingException("Program can`t read file.", e, path);
		}
		try {
			obj = oin.readObject();
		} catch (ClassNotFoundException e) {
			throw new FileLoadingException("Program can`t read file(Class not found).", e, path);
		} catch (IOException e) {
			throw new FileLoadingException("Program can`t read file.", e, path);
		}
		
		try {
			oin.close();
		} catch (IOException e) {}
		
		return obj;
	}
	
	/**
	 * This function write Object to file
	 * 
	 * @param path - path to save
	 * @param data - data to save
	 * @throws FileSavingException if problems
	 */
	@SuppressWarnings("resource")
	public static void serializeData(String path, Object data) throws FileSavingException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			throw new FileSavingException("Program can`t save file.", e, path);
		}
		try {
			oos = new ObjectOutputStream(fos);
		} catch (IOException e) {
			throw new FileSavingException("Program can`t save file.", e, path);
		}
		
		try {
			oos.writeObject(data);
		} catch (IOException e) {
			throw new FileSavingException("Program can`t save file.", e, path);
		}
		
		try {
			fos.flush();
			fos.close();
			oos.flush();
			oos.close();
		} catch(IOException e) {}
	}
	
	/**
	 * Get name of file without path and extension
	 * @param f - sanned file
	 * @return null if have problems with file or file name else.
	 */
	public static String getFileName(File f) {
		//Java Standard library is cool
		String ret = Paths.get(f.toURI()).getFileName().toString();
		
		int i = ret.lastIndexOf('.');
		
		if(i > 0) {
			ret = ret.substring(0, ret.lastIndexOf('.'));
		}
		
		return ret;
	}

	/**
	 * Get extension of file (in lower case).
	 * @param file - out file for check
	 * @return <code>""<code/> if no extension, else - normal extension, in lower case.
	 */
	public static String getFileExtension(File file) {
		String fileName = file.getName();

		if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	/**
	 * Fast array creating.
	 * @param values - values in our array
	 * @return Array of input values. If some value is null, put NaN in array.
	 */
	public static double[] asArray(Double... values) {
		double[] ret = new double[values.length];
		
		for(int i = 0; i < ret.length; ++i) {
			Double d = values[i];
			
			ret[i] = (d == null ? Double.NaN : d);
		}
		
		return ret;
	}
	
	/**
	 * Fast array creating.
	 * @param values - values in our array
	 * @return Array of input values. If some value is null, put 0 in array.
	 */
	public static int[] asArray(Integer... values) {
		int[] ret = new int[values.length];
		
		for(int i = 0; i < ret.length; ++i) {
			Integer d = values[i];
			
			ret[i] = (d == null ? 0 : d);
		}
		
		return ret;
	}
	
	/**
	 * Fast array creating.
	 * @param values - values in our array
	 * @return Array of input values.
	 */
	@SafeVarargs
	public static <T> T[] asArray(T... values) {
		return values;
	}

	public static <T> boolean contains(T[] data, T[] elements) {
		boolean[] tmp = new boolean[elements.length];

		for(int i = 0; i < data.length; ++i) {
			for(T t: data) {
				if(t == elements[i]) {
					tmp[i] = true;
				}
			}
		}

		for(boolean b: tmp) {
			if(!b) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Method for console input parcing
	 * May contains bugs
	 * @param command
	 * @return tokens
	 */
	public static final String[] parseCommand(String command) {
		if(command == null || command.length() == 0) {
			return null;
		}
		
		char[] input = command.toCharArray();			
		
		String[] tokens = null;
		
		List<String> strings = new ArrayList<>();
		
		int lastToken = 0;
		
		boolean writeToken = false;
		
		xx: for(int i = 0; i < input.length; ++i) {
			char ch = input[i];
			
			switch(ch) {
			case ' ':  {
				if(writeToken) {
					strings.add(command.substring(lastToken, i));						
					writeToken = false;
				}
				
				lastToken = i + 1;
			} break;
			case '"': {
				++i;
				writeToken = false;
				
				lastToken = i;
				
				for(;i < input.length; ++i) {
					if(input[i] == '"') {
						if(writeToken) {
							strings.add(command.substring(lastToken, i));
							writeToken = false;
							lastToken = i + 1;
							
							continue xx;
						}
						
						lastToken = i + 1;
					} else {
						writeToken = true;
					}
				}
			} break;

			default: writeToken = true;
			}
		}
		
		if(writeToken) {
			strings.add(command.substring(lastToken, command.length()));
		}
		
		tokens = new String[strings.size()];
		
		return strings.toArray(tokens);
	}
	
	/**
	 * Good method for boolean parsing(default java method is bad).<br>
	 * Parsing ignore case.<br>
	 * Returns 'true' if input is 'true', 'yes', '+' or 'y'.<br>
	 * Returns 'false' if input is 'false', 'no', '-' or 'n'.<br>
	 * Else returns 'null'.
	 * @param s - input
	 * @return see description
	 */
	public static Boolean parseBoolean(String s) {
		if(s == null || s.isEmpty()) {
			return null;
		}
		
		switch(s.toLowerCase()) {
		case "yes": case "true": case "+": case "y":
			return true;
		case "no": case "false": case "-": case "n":
			return false;
		default:
			return null;
		}
	}
	
	/**
	 * Parse integer. No exceptions!
	 * @param data - data, that holds integer
	 * @return value or null, if can't parse string
	 */
	public static Integer parseInteger(String data) {
		int value;
		
		try {
			value = Integer.parseInt(data);
		} catch (Exception e) {
			return null;
		}
		
		return value;
	}
	
	/**
	 * Parse double. No exceptions!
	 * @param data - String, that holds double value
	 * @return value or null, if can't parse string
	 */
	public static Double parseDouble(String data) {
		double value;
		
		try {
			value = Double.parseDouble(data);
		} catch (Exception e) {
			return null;
		}
		
		return value;
	}
	
	public static int shadeColor(int color, double divider) {
		int r = (color & 0xff0000) >> 16;
		int g = (color & 0xff00) >> 8;
		int b = (color & 0xff);
		
		r /= divider;
		g /= divider;
		b /= divider;
		
		return (r << 16) + (g << 8) + b;
	}
	
	public static void beep() {
		ShellApplication.getPlatform().beep();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> createList(T ... objects) {
		List<T> ret = new ArrayList<>(objects.length);
		
		for(T t: objects) {
			ret.add(t);
		}
		
		return ret;
	}

	public static <T> T[] concatArrays(Class<T> tc, T[]... arrays) {
		int n = 0;

		for(T[] array: arrays) {
			if(array != null) {
				n += array.length;
			}
		}

		T[] ret = (T[]) Array.newInstance(tc, n);

		int i = 0;

		for(T[] array: arrays) {
			if(array != null) {
				System.arraycopy(array, 0, ret, i, array.length);

				i += array.length;
			}
		}

		return ret;
	}

	public static <T> T[] clipArray(Class<T> tc, T[] array, int beginIndex, int endIndex) {
		return copyArray(tc, array, beginIndex, endIndex);
	}

	/**
	 * Copy an array.
	 * @param tc - type of elements of array
	 * @param beginIndex - index of first element in new array
	 * @param endIndex - 1 + index of last element
	 * @return
	 */
	public static <T> T[] copyArray(Class<T> tc, T[] src, int beginIndex, int endIndex) {
		if(beginIndex < 0 || endIndex < 0 || beginIndex >= src.length || beginIndex >= endIndex) {
			return (T[]) Array.newInstance(tc, 0);
		}

		T[] ret = (T[]) Array.newInstance(tc, endIndex - beginIndex);

		System.arraycopy(src, beginIndex, ret, 0, endIndex - beginIndex);

		return ret;
	}
	
	/**
	 * Add border for double value. If value less thah min, value = min, if value larger than max, value = max
	 * @param value - value
	 * @param minBorder - minimum value
	 * @param maxBorder - maximum value
	 * @return bordered value
	 * @throws IllegalArgumentException if min > max
	 */
	public static double border(double value, double minBorder, double maxBorder) {
		if(minBorder > maxBorder) {
			throw new IllegalArgumentException("Minimum value is larger than max value.");
		} else if(value < minBorder) {
			return minBorder;
		} else if(value > maxBorder) {
			return maxBorder;
		} else {
			return value;
		}
	}
	
	/**
	 * Add border for integer value. If value less thah min, value = min, if value larger than max, value = max
	 * @param value - value
	 * @param minBorder - minimum value
	 * @param maxBorder - maximum value
	 * @return bordered value or , if min > max, NaN
	 * @throws IllegalArgumentException if min > max
	 */
	public static int border(int value, int minBorder, int maxBorder) {
		if(minBorder > maxBorder) {
			throw new IllegalArgumentException("Minimum value is larger than max value.");
		} else if(value < minBorder) {
			return minBorder;
		} else if(value > maxBorder) {
			return maxBorder;
		} else {
			return value;
		}
	}
	
	/**
	 * Add border for long value. If value less thah min, value = min, if value larger than max, value = max
	 * @param value - value
	 * @param minBorder - minimum value
	 * @param maxBorder - maximum value
	 * @return bordered value or , if min > max, NaN
	 * @throws IllegalArgumentException if min > max
	 */
	public static long border(long value, long minBorder, long maxBorder) {
		if(minBorder > maxBorder) {
			throw new IllegalArgumentException("Minimum value is larger than max value.");
		} else if(value < minBorder) {
			return minBorder;
		} else if(value > maxBorder) {
			return maxBorder;
		} else {
			return value;
		}
	}

//	/**
//	 * Draw nice text with other color border
//	 * @param text text to draw
//	 * @param x x position of text
//	 * @param y y position of text
//	 */
//	public static void drawBorderedText(GraphicsContext gc, String text, double x, double y) {
//		gc.fillText(text, x, y);
//		gc.strokeText(text, x, y);
//	}

//	public static boolean lineSegmentIntersection(double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22)  {
//		double maxx1 = Math.max(x11, x12), maxy1 = Math.max(y11, y12);
//		double minx1 = Math.min(x11, x12), miny1 = Math.min(y11, y12);
//		double maxx2 = Math.max(x21, x22), maxy2 = Math.max(y21, y22);
//		double minx2 = Math.min(x21, x22), miny2 = Math.min(y21, y22);
//
//		if (minx1 > maxx2 || maxx1 < minx2 || miny1 > maxy2 || maxy1 < miny2)
//			return false;  // Момент, када линии имеют одну общую вершину...
//
//
//		double dx1 = x12-x11, dy1 = y12-y11; // Длина проекций первой линии на ось x и y
//		double dx2 = x22-x21, dy2 = y22-y21; // Длина проекций второй линии на ось x и y
//		double dxx = x11-x21, dyy = y11-y21;
//		double div, mul;
//
//
//		if ((div = (double)((double)dy2*dx1-(double)dx2*dy1)) == 0)
//			return false; // Линии параллельны...
//		if (div > 0) {
//			if ((mul = (double)((double)dx1*dyy-(double)dy1*dxx)) < 0 || mul > div)
//				return false; // Первый отрезок пересекается за своими границами...
//			if ((mul = (double)((double)dx2*dyy-(double)dy2*dxx)) < 0 || mul > div)
//				return false; // Второй отрезок пересекается за своими границами...
//		}
//
//		if ((mul = -(double)((double)dx1*dyy-(double)dy1*dxx)) < 0 || mul > -div)
//			return false; // Первый отрезок пересекается за своими границами...
//		if ((mul = -(double)((double)dx2*dyy-(double)dy2*dxx)) < 0 || mul > -div)
//			return false; // Второй отрезок пересекается за своими границами...
//
//		return true;
//	}

	/**
	 * Check intersection of two line parts and save intersection coordinates.
	 * @param x11 first  line first  point x
	 * @param y11 first  line first  point y
	 * @param x12 first  line second point x
	 * @param y12 first  line second point y
	 * @param x21 second line first point x
	 * @param y21 second line first point y
	 * @param x22 second line second point x
	 * @param y22 second line second point y
	 * @param ret place for return values. May be null. Then no return
	 * @return true if intersection, false else
	 */
	public static boolean lineSegmentIntersection(double x11, double y11, double x12, double y12,
	                                              double x21, double y21, double x22, double y22, DoubleVector ret) {
		final double
			bax = (x12 - x11),
			dcy = (y22 - y21),
			bay = (y12 - y11),
			dcx = (x22 - x21),
			acx = (x11 - x21),
			acy = (y11 - y21);

		final double common = bax*dcy - bay*dcx;

		if (common == 0) {
			return false;
		}

		final double rH = acy*dcx - acx*dcy;
		final double sH = acy*bax - acx*bay;

		final double r = rH / common;
		final double s = sH / common;

		if (r >= 0 && r <= 1 && s >= 0 && s <= 1) {
			if(ret != null) {
				// Just expressions, that are used 2 times
				final double shit1 = x21*y22 - x22*y21;
				final double shit2 = x11*y12 - x12*y11;

				ret.x = (shit1*bax - shit2*dcx) / common;
				ret.y = (shit1*bay - shit2*dcy) / common;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Good hashcode function for integer.
	 */
	public static final int intHash(int val) {
		int a = val;

		a -= (a<<6);
		a ^= (a>>17);
		a -= (a<<9);
		a ^= (a<<4);
		a -= (a<<3);
		a ^= (a<<10);
		a ^= (a>>15);

		return a;
	}

	private static final int BIG_INT = Integer.MAX_VALUE;
	private static final double BIG_INT_ROUND = BIG_INT + 0.5;

	public static int floor(double x) {
		return (int) (x + BIG_INT) - BIG_INT;
	}

	public static int round(double x) {
		return (int)(x + BIG_INT_ROUND) - BIG_INT;
	}

	public static int ceil(double x) {
		return BIG_INT - (int) (BIG_INT - x); // credit: roquen
	}

	/**
	 * Get angle of a vector in radians. if (x > 0 && y = 0) angle = 0.
	 */
	public static double getAngle(double x, double y) {
		double length = Math.hypot(x, y);

		x /= length;
		y /= length;

		double rotation = Math.asin(y);

		if(x < 0) {
			rotation = Math.PI - rotation;
		}

		// Anti-NaN. For safety.
		rotation = (rotation != rotation) ? 0 : rotation;

		return rotation;
	}

	public static class DoubleVector {
		public double x, y;
	}
}
