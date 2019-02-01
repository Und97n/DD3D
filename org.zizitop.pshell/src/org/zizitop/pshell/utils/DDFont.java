package org.zizitop.pshell.utils;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.window.DisplayMode;

/**
 * 
 * @author Zizitop
 *
 */
public class DDFont {	
	public static final int CHARACTER_COUNT = 256;	
	
	private static char[] extended = new char[]{
			'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П',
			'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',
			'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
			'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
			'%', '☺', '☻', '♥', '♦', '♣', '♠', '•', '◘', '○', '◙', '♂', '♀', '♪', '♫', '☼',
			'►', '◄', '↕', '‼', '¶', '§', '▬', '↨', '↑', '↓', '→', '←', '∟', '↔', '▲', '▼',
			'÷', 'α', 'β', 'π', 'Ё', 'ё', 'Ї', 'ї', '√', '‼', '∞', '®', '¬', '©', '¥', '—',
			'±', '≡', '≡', '░', '▒', '▓', '«', '»', '¿', '≤', '≥', '≈', '¯', '`', '■', ' '};
	
	public static DDFont CONSOLE_FONT, INVENRORY_FONT, DEFAULT_FONT;

	public static void initDefaultFonts(DisplayMode dm) {
		double baseWidth = dm.getBaseWidth(), baseHeight = dm.getBaseHeight();

		CONSOLE_FONT = new DDFont(8.0 / baseWidth, 8.0 / baseHeight,
				ResourceLoader.getInstance().getTexture("font"), false);

		INVENRORY_FONT = new DDFont(8.0 / baseWidth, 8.0 / baseHeight,
				ResourceLoader.getInstance().getTexture("font"), true);

		DEFAULT_FONT = new DDFont(16.0 / baseWidth, 16.0 / baseHeight,
				ResourceLoader.getInstance().getTexture("font"), true);
	}

	private final double sizeX, sizeY;
	private final boolean doubled;
	private final Bitmap[] font;
	
	public DDFont(double sizeX, double sizeY, Bitmap fontImage, boolean doubled) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.doubled = doubled;
		
		font = new Bitmap[CHARACTER_COUNT];
		
		int charSizeX = fontImage.width / 16;
		int charSizeY = fontImage.height / 16;
		
		for(int y = 0; y < 16; ++y) {
			for(int x = 0; x < 16; ++x) {
				font[x + y * 16] = fontImage.getSubImage(x * charSizeX, y * charSizeY, charSizeX, charSizeY);
			}
		}
	}
	
	public void draw(Bitmap canvas, double x, double y, CharSequence s, int color) {
		draw(canvas, x, y, 1, s, color);
	}
	
	public void draw(Bitmap canvas, double x, double y, char[] s, int color) {
		draw(canvas, x, y, 1, s, color);
	}
	
	public void draw(Bitmap canvas, double x, double y, double sizeMultiplier, CharSequence s, int color) {
		
		double sizeOnScreenX = this.sizeX * sizeMultiplier * canvas.width;
		double sizeOnScreenY = this.sizeY * sizeMultiplier * canvas.height;
		
		mainDraw(canvas, (int)(x * canvas.width), (int)(y * canvas.height), sizeOnScreenX, sizeOnScreenY, s, color);
	}
	
	public void draw(Bitmap canvas, double x, double y, double sizeMultiplier, char[] s, int color) {
		
		double sizeOnScreenX = this.sizeX * sizeMultiplier * canvas.width;
		double sizeOnScreenY = this.sizeY * sizeMultiplier * canvas.height;
		
		mainDraw(canvas, (int)(x * canvas.width), (int)(y * canvas.height), sizeOnScreenX, sizeOnScreenY, s, color);
	}
	
	public void draw(Bitmap canvas, double x, double y, double width, double height, CharSequence s, int color) {
		
		double sizeOnScreenX = this.sizeX * canvas.width;
		double sizeOnScreenY = this.sizeY * canvas.height;
		
		sizeOnScreenX *= canvas.width * width / (s.length() * sizeOnScreenX);
		sizeOnScreenY *= canvas.height* height/ (sizeOnScreenY);
		
		mainDraw(canvas, (int)(x * canvas.width), (int)(y * canvas.height), sizeOnScreenX, sizeOnScreenY, s, color);
	}
	
	public void draw(Bitmap canvas, double x, double y, double width, double height, char[] s, int color) {
		
		double sizeOnScreenX = this.sizeX * canvas.width;
		double sizeOnScreenY = this.sizeY * canvas.height;
		
		sizeOnScreenX *= canvas.width * width / (s.length * sizeOnScreenX);
		sizeOnScreenY *= canvas.height* height/ (sizeOnScreenY);
		
		mainDraw(canvas, (int)(x * canvas.width), (int)(y * canvas.height), sizeOnScreenX, sizeOnScreenY, s, color);
	}
	
	private void mainDraw(Bitmap canvas, int x, int y, double sizeOnScreenX, double sizeOnScreenY, CharSequence s, int color) {		
		for(int i = 0; i < s.length(); ++i) {
			final char ch = s.charAt(i);
			
			//ch = ch > CHARACTER_COUNT ? 63 : ch;
			
			//63 = ? (if an unknown symbol)
			int newID = 63;
			
			if(ch < 128) {
				newID = ch;
			} else if(ch >= 'А' && ch <= 'я') {
				newID = ch - 1040 + 128;
			} else {				
				for(int j = 192; j < 256; ++j) {
					if(extended[j - 128] == ch) {
						newID = j;
					}
				}
			}
			
			if(doubled) {
				canvas.drawStencil_SC(font[newID], x + i * sizeOnScreenX + canvas.width * (sizeX / 8.0), 
						y + canvas.height * (sizeY / 8.0), sizeOnScreenX, sizeOnScreenY, 0);
			}
			
			canvas.drawStencil_SC(font[newID], x + i * sizeOnScreenX, y, sizeOnScreenX, sizeOnScreenY, color);
		}
	}
	
	private void mainDraw(Bitmap canvas, int x, int y, double sizeOnScreenX, double sizeOnScreenY, char[] s, int color) {		
		for(int i = 0; i < s.length; ++i) {
			final char ch = s[i];
			
			//ch = ch > CHARACTER_COUNT ? 63 : ch;
			
			//63 = ? (if unknow symbol)
			int newID = 63;
			
			if(ch < 128) {
				newID = ch;
			} else if(ch >= 'А' && ch <= 'я') {
				newID = ch - 1040 + 128;
			} else {				
				for(int j = 192; j < 256; ++j) {
					if(extended[j - 128] == ch) {
						newID = j;
					}
				}
			}
			
			if(doubled) {
				canvas.drawStencil_SC(font[newID], x + i * sizeOnScreenX + canvas.width * (sizeX / 8.0), 
						y + canvas.height * (sizeY / 8.0), sizeOnScreenX, sizeOnScreenY, 0);
			}
			
			canvas.drawStencil_SC(font[newID], x + i * sizeOnScreenX, y, sizeOnScreenX, sizeOnScreenY, color);
		}
	}
	
	public double getHeight() {
		return sizeY;
	}
	
	public double getWidth(CharSequence s) {
		return getWidth(s.length());
	}

	public double getWidth(int stringLength) {
		return sizeX * stringLength;
	}
}
