package org.zizitop.pshell.menu;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public class Button extends TextLabel implements ActionComponent {
	protected static final double SCALING_SPEED = 0.075;
	
	protected int bcgColor = Bitmap.TRANSPARENT_COLOR;
	
	protected ActionListener listener;
	protected double fontSizeMultiper = 1.0;
	
	protected boolean selected;
	
	public Button(double sizeX, double sizeY, CharSequence text) {
		super(sizeX, sizeY, text, DDFont.DEFAULT_FONT);
	}

	public Button(CharSequence text) {
		super(text);
	}
	
	@Override
	public void updateComponent(Window window) {
		if(selected) {
			if(fontSizeMultiper < 1.25) {
				fontSizeMultiper += SCALING_SPEED;
			}
			
			if(fontSizeMultiper > 1.25) {
				fontSizeMultiper = 1.25;
			}
		} else {
			if(fontSizeMultiper > 1.0) {
				fontSizeMultiper -= SCALING_SPEED;
			}
			
			if(fontSizeMultiper < 1.0) {
				fontSizeMultiper = 1.0;
			}
		}
	}

	@Override
	public void drawComponent(Bitmap canvas) {
		font.draw(canvas, x, y, fontSizeMultiper, text, textColor);
	}

	@Override
	public void onAction(Action a) {
		if(listener != null) {
			listener.onAction(new Event(this, a));
		}
	}

	public Button setActionListener(ActionListener listener) {
		this.listener = listener;
		
		return this;
	}

	public int getBcgColor() {
		return bcgColor;
	}

	public void setBcgColor(int bcgColor) {
		this.bcgColor = bcgColor;
	}
	
	@Override
	public void select(Window window) {
		this.selected = true;
	}
	
	@Override
	public void unselect(Window window) {
		this.selected = false;
	}
}