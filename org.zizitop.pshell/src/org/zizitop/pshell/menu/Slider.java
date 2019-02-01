package org.zizitop.pshell.menu;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * Cool slider for solving any problems
 * @author Zizitop
 *
 */
public class Slider extends Button {
	private static final InputOption input_scrollUp = InputOption.getInputOption("menu.scrollUp");
	private static final InputOption input_scrollDown = InputOption.getInputOption("menu.scrollDown");
	private static final InputOption input_listNext = InputOption.getInputOption("menu.listNext");
	private static final InputOption input_listPrevious = InputOption.getInputOption("menu.listPrevious");
	private static final InputOption input_actionOnMenuItem = InputOption.getInputOption("menu.actionOnMenuItem");
	
	protected final double maxValue, minValue;
	
	//Util for slider changing if mouse is out of slider borders
	protected boolean wasChangedByMouse = false;
	
	//[0; 1]
	protected double value;
	protected double incrementDelta = 0.01;
	
	public Slider(double sizeX, double sizeY, CharSequence text, DDFont font, double minValue, double maxValue) {
		super(sizeX, sizeY, text);
		
		super.font = font;
		
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
	
	public Slider(CharSequence text, DDFont font, double minValue, double maxValue) {
		this(font.getWidth(text), font.getHeight(), text, font, minValue, maxValue);
	}
	
	public Slider(CharSequence text, double width, double height, double minValue, double maxValue) {
		this(width, height, text, DDFont.DEFAULT_FONT, minValue, maxValue);
	}
	
	public Slider(CharSequence text, double minValue, double maxValue) {
		this(text, DDFont.DEFAULT_FONT, minValue, maxValue);
	}
	
	@Override
	public void drawComponent(Bitmap canvas) {
		canvas.fillRect_SC(DEFAULT_SELECTED_COLOR, x, y, sizeX * value, sizeY);
		canvas.drawRect_SC(selected ? 0x4f0000 : 0, x, y, sizeX, sizeY, canvas.getPixelScSize());
		font.draw(canvas, x, y, text, textColor);
	}
	
	@Override
	public void updateComponent(Window window) {
		if(!selected) {
			return;
		}
		
		final double oldValue = value;		
		
		final Action actionK = checkKeyboardAction(window);
		final Action actionM = checkMouseAction(window);
		
		final Action action = actionM == null ? actionK : actionM;
		
		if(action != null && value != oldValue) {
			onAction(action);
		}
	}
	
	private Action checkKeyboardAction(Window window) {
		//Get source of value decreasing action
		InputOption incSource = null;
		
		if(window.getActionsCount(input_scrollUp) > 0) {
			incSource = input_scrollUp;
		} else if(window.getActionsCount(input_listNext) > 0) {
			incSource = input_listNext;
		}
		
		//Get source of value increasing action
		InputOption decSource = null;
		
		if(window.getActionsCount(input_scrollDown) > 0) {
			decSource = input_scrollDown;
		} else if(window.getActionsCount(input_listPrevious) > 0) {
			decSource = input_listPrevious;
		}
		
		final boolean inc = incSource == null ? false : window.getActionsCount(incSource) > 0;
		final boolean dec = decSource == null ? false : window.getActionsCount(decSource) > 0;
		
		InputOption source = null;
		
		//Don't change value if value incremented and decremented
		if(inc ^ dec) {
			if(inc) { 
				value += incrementDelta;
				
				source = incSource;
			} else if(dec) {
				value -= incrementDelta;
				
				source = decSource;
			}
			
			checkValue();	
		}
		
		if(source == null) {
			return null;
		} else {
			return new KeyAction(window, source);			
		}		
	}
	
	private Action checkMouseAction(Window window) {
		wasChangedByMouse = false;
		
		if(!(window.getActionsCount(input_actionOnMenuItem) > 0)) {
			//If no needed button pressed
			return null;
		}
		
		wasChangedByMouse = true;
		
		final double mx = window.getMouseX() / window.getContentWidth() - x;
		
		value = mx / getSizeX();
		
		checkValue();
		
		return new MouseAction(window);
	}
	
	@Override
	public void updateSize() {}
	
	public double getValue() {
		return minValue + value * maxValue;
	}
	
	public Slider setValue(double newValue) {
		this.value = (newValue - minValue) / maxValue;
		
		return this;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public double getMinValue() {
		return minValue;
	}
	
	public void checkValue() { 
		if(value < 0) {
			value = 0.0;
		} else if(value > 1) {
			value = 1.0;
		}
	}
	
	public double getIncrementDelta() {
		return incrementDelta;
	}

	public void setIncrementDelta(double incrementDelta) {
		this.incrementDelta = incrementDelta;
	}
	
	@Override
	public void unselect(Window window) {
		super.unselect(window);
		
		//If mouse is out of slider borders when it change value, whe set value to minimum or maximum
		if(wasChangedByMouse) {
			final double mx = window.getMouseX() / window.getContentWidth() - x;
			
			if(mx > getSizeX()) {
				value = 1.0;
			} else if(mx < 0) {
				value = 0.0;
			} else {
				return;
			}
			
			onAction(new MouseAction(window));
		}
	}
}
