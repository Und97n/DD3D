package org.zizitop.pshell.menu;

public class TextAreaForInteger extends TextArea {
	final int maxlength;
	
	public TextAreaForInteger(int defaultData, int maxData) {
		super(String.valueOf(defaultData));
		
		this.maxlength = (int) Math.ceil(Math.log10(maxData));
		
		updateSize();
	}

	@Override
	public boolean canWrite(char ch) {
		return (text.toString().length() < maxlength) && super.canWrite(ch) && Character.isDigit(ch);
	}
	
	public int getValue() {
		return Integer.parseInt(text.toString());
	}
	
	@Override
	public void updateSize() {
		sizeX = font.getWidth(maxlength) * scalar;
		sizeY = font.getHeight() * scalar;
	}
}
