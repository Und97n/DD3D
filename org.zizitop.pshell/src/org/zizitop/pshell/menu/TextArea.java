package org.zizitop.pshell.menu;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public class TextArea extends TextLabel implements ActionComponent {	
	protected static final InputOption input_earse = InputOption.getInputOption("earse");
	
	private StringBuilder builder;
	
	protected boolean selected = false;	
	
	public TextArea(double sizeX, double sizeY, CharSequence text) {
		super(sizeX, sizeY, text, DDFont.DEFAULT_FONT);
		
		builder = new StringBuilder(text);
	}

	public TextArea(CharSequence text) {
		super(text);
		
		builder = new StringBuilder(text);
	}

	protected boolean canWrite(char ch) {
		return true;
	}

	@Override
	public void updateComponent(Window window) {
		String s = window.getLastEnteredText();
		
		if(selected) {
			if(window.getActionsCount(input_earse) > 0) {
				if(text.length() > 0) {
					builder.deleteCharAt(text.length() - 1);
				} else {
					Utils.beep();
				}
			} else {
				for(char ch: s.toCharArray()) {
					if(canWrite(ch)) {
						builder.append(ch);
					}
				}
			}	
			
			super.setText(builder);
		}		
	}
	
	@Override
	public void drawComponent(Bitmap canvas) {		
		if(this.selected) {
			canvas.fillRect_SC(0x7f0007, x, y, sizeX, sizeY);
		}
		
		font.draw(canvas, x, y, text, textColor);
		
		canvas.drawRect_SC(0, x, y, sizeX, sizeY, canvas.getPixelScSize());
		
		if(this.selected) {
			canvas.fillRect_SC(0x7f007, font.getWidth(text) + x, y, 
					0.0125, font.getHeight());
		}
	}
	
	@Override
	public void select(Window window) {
		selected = true;
	}

	@Override
	public void unselect(Window window) {
		selected = false;
	}
}