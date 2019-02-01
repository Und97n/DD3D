package org.zizitop.pshell.menu;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;

/**
 * 
 * @author Zizitop
 *
 */
public class TextLabel extends StandardComponent {
	protected int textColor = -1;
	protected CharSequence text;
	protected DDFont font = DDFont.DEFAULT_FONT;
	
	double scalar = 1;
	
	public TextLabel(double sizeX, double sizeY, CharSequence text, DDFont font) {
		super(sizeX, sizeY);
		
		this.font = font;
		this.text = text;
	}

	public TextLabel(double sizeX, double sizeY, CharSequence text) {
		this(sizeX, sizeY, text, DDFont.DEFAULT_FONT);
	}
	
	public TextLabel(double size, CharSequence text, DDFont font) {
		this(font.getWidth(text) * size, font.getHeight() * size, text, font);
		
		this.scalar = size;
	}
	
	public TextLabel(CharSequence text, DDFont font) {
		this(1, text, font);
	}
	
	public TextLabel(CharSequence text) {
		this(text, DDFont.DEFAULT_FONT);
	}
	
	public TextLabel(double size, String text) {
		this(size, text, DDFont.DEFAULT_FONT);
	}

	@Override
	public void drawComponent(Bitmap canvas) {

		font.draw(canvas, x, y, sizeX, sizeY, text, textColor);
	}
	
	public DDFont getFont() {
		return font;
	}
	
	public void setFont(DDFont f) {
		font = f;
		
		updateSize();
	}
	
	public void updateSize() {
		sizeX = font.getWidth(text) * scalar;
		sizeY = font.getHeight() * scalar;
	}
	
	public CharSequence getText() {
		return text;
	}
	
	public void setText(CharSequence newText) {
		text = new StringBuilder(newText);
		
		updateSize();
	}
	
	public int getColor() {
		return textColor;
	}
	
	public TextLabel setColor(int newColor) {
		this.textColor = newColor;
		
		return this;
	}
}
