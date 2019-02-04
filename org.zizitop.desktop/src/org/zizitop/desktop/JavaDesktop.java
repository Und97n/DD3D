package org.zizitop.desktop;

import org.zizitop.pshell.Platform;
import org.zizitop.pshell.resources.SoundEngine;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.utils.exceptions.FileSavingException;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.InputSource;
import org.zizitop.pshell.window.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

/**
 * Some platform-depended things.
 * <br><br>
 * Created 09.09.18 0:04
 *
 * @author Zizitop
 */
public class JavaDesktop implements Platform {
	public JavaDesktop() {
		//If java have problems in AWT and Swing
		if(GraphicsEnvironment.isHeadless()) {
			throw new RuntimeException("Graphics is not supported!", null);
		}
	}

	@Override
	public InputSource inputSourceFromID(int id) {
		if(id >= 0 && id <= JDInputHandler.KEYS_COUNT) {
			return new JDInputHandler.KeyboardSource(id);
		} else if((-id - 1) < JDInputHandler.MOUSE_BUTTONS_COUNT) {
			return new JDInputHandler.MouseButtonSource(-id - 1);
		} else if(id == JDInputHandler.ScrollActionSource.MOUSE_WHEEL_UP) {
			return new JDInputHandler.ScrollActionSource(true);
		} else if(id == JDInputHandler.ScrollActionSource.MOUSE_WHEEL_DOWN) {
			return new JDInputHandler.ScrollActionSource(false);
		} else {
			throw new IllegalArgumentException("Wrong input source id: " + id);
		}
	}

	@Override
	public Window createWindow(String name, String upperRightText, int windowWidth, int windowHeight, DisplayMode dm, boolean fullscreen) {
		return new JDWindow(name, upperRightText, windowWidth, windowHeight, dm, fullscreen);
	}

	@Override
	public int getUserScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	@Override
	public int getUserScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	@Override
	public SoundEngine getSoundEngine() {
		return new SoundEngine2();
	}

	@Override
	public void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	public static BufferedImage loadBuf(String s) throws FileLoadingException {
		if(s == null){
			throw new NullPointerException("File path is null.");
		}

		Image imageX;

		try {
			imageX = new ImageIcon(s).getImage();
		} catch(Exception e) {
			throw new FileLoadingException("Problems with image loading.", e, s);
		}

		if(imageX.getWidth(null) == -1 || imageX.getHeight(null) == -1) {
			throw new FileLoadingException("Problems with image loading.", null, s);
		}

		BufferedImage bl = new BufferedImage(imageX.getWidth(null),
				imageX.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		//Strange tools
		Graphics2D ig = bl.createGraphics();
		ig.drawImage(imageX, 0, 0, null);
		ig.dispose();

		return bl;
	}

	@Override
	public Bitmap loadImage(String filePath) throws FileLoadingException {
		BufferedImage source = loadBuf(filePath);

		//Don't worry: BufferedImage source is always in ARGB integer format
		int[] imagePixels = ((DataBufferInt)source.getRaster().getDataBuffer()).getData();

		int[] pixels = new int[imagePixels.length];

		System.arraycopy(imagePixels, 0, pixels, 0, imagePixels.length);

		Bitmap ret = new Bitmap(pixels, source.getWidth(), source.getHeight());

		return ret;
	}

	/**
	 * Bitmap, that was created with this factory is linked to the <code>image</code><br>
	 * Any manipulation with this bitmap change <code>image</code>
	 * @param image - BufferedImage to link
	 * @return Linked bitmap
	 */
	public static Bitmap getLinkedBitmap(BufferedImage image) {
		Bitmap ret = new Bitmap(((DataBufferInt)image.getRaster().getDataBuffer()).getData(), image.getWidth(), image.getHeight());
		return ret;
	}

	public static boolean saveBuf(BufferedImage i, String directory, String fileName, String format) throws FileSavingException {
		if(directory == null){
        	throw new NullPointerException("Directory path is null.");
        }

		if(fileName == null) {
        	throw new NullPointerException("File name is null or empty.");
        }

		if(format == null) {
        	throw new NullPointerException("Format is null or empty.");
        }

		File f = new File(directory + fileName + "." + format);

		if(f.isDirectory()) {
			throw new FileSavingException("File path is incorrect!", null, f.getPath());
		}

		try {
			File dir = new File(directory);

			if(!dir.exists()) {
				if(!dir.mkdir()) {
					throw new FileSavingException("Cannot create directory!", null, f.getPath());
				}
			}

			return ImageIO.write(i, format, f);
		} catch (IOException e) {
			throw new FileSavingException("Problems with file saving!", e, f.getPath());
		}
	}

	@Override
	public void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
}
