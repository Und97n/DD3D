package org.zizitop.pshell.menu;

import java.util.List;

import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.window.InputOption;
import org.zizitop.pshell.window.Window;

/**
 * 
 * @author Zizitop
 *
 */
public class SelectorComponent<T> extends StandardComponent implements ActionComponent {
	private static final InputOption input_scrollUp = InputOption.getInputOption("menu.scrollUp");
	private static final InputOption input_scrollDown = InputOption.getInputOption("menu.scrollDown");
	private static final InputOption input_listNext = InputOption.getInputOption("menu.listNext");
	private static final InputOption input_listPrevious = InputOption.getInputOption("menu.listPrevious");
	
	public static final int SELECTED_ELEMENT_COLOR = (DEFAULT_SELECTED_COLOR & 0x00ffff) + 
			((DEFAULT_SELECTED_COLOR * 2) & 0xff0000);
	protected static final int SLIDER_COLOR = DEFAULT_SELECTED_COLOR / 2;
	
	protected static final double SLIDER_WIDTH = 0.025;
	protected static final double WIDTH_RESERVE = 0.0125;

	protected int elementsYCount, maxPosition;
	protected int currentPosition;
	
	protected DDFont font = DDFont.DEFAULT_FONT;
	
	protected List<T> objects;
	protected int selectedObject = -1;	
	
	protected double deltaY;
	
	protected boolean selected;
	
	public SelectorComponent(List<T> objects, double deltaY, double sizeY) {
		this(objects, deltaY, sizeY, DDFont.DEFAULT_FONT);
	}

	public SelectorComponent(List<T> objects, double deltaY) {
		this(objects, deltaY, DDFont.DEFAULT_FONT);
	}
	
	public SelectorComponent(List<T> objects, double deltaY, DDFont font) {
		this(objects, deltaY, objects.size() * (deltaY + font.getHeight()) + deltaY, font);
	}
	
	public SelectorComponent(List<T> objects, double x, double y, double deltaY) {
		this(objects, deltaY, DDFont.DEFAULT_FONT);
	}
	
	public SelectorComponent(List<T> objects, double deltaY, double sizeY, DDFont font) {
		super(0, sizeY);
		
		this.sizeY = sizeY;
		this.objects = objects;
		this.deltaY = deltaY;
		
		if(font != null) {
			this.font = font;
		}
		
		objects.forEach((obj) -> {			
			sizeX = Math.max(sizeX, font.getWidth(obj.toString()));
		}); 
		
		this.
		
		sizeX += SLIDER_WIDTH + WIDTH_RESERVE * 2;
		
		elementsYCount = (int)(sizeY / (font.getHeight() + deltaY));
		
		if(elementsYCount == 0) {
			if(objects.size() > 0) {
				throw new IllegalArgumentException("sizeY of ScrolableSelectorComponent is too small.");
			} else {
				this.sizeY = 0;
			}
		}
		
		maxPosition = objects.size() - elementsYCount;
	}
	
	@Override
	public void drawComponent(Bitmap canvas) {
		double elementHeight = font.getHeight();
		double dist = elementHeight + deltaY;
		
		final int startI = (int)((y > 0 ? 0 : -y) / dist) + currentPosition;
		final int endI = Math.min(startI + elementsYCount, objects.size());
		
		canvas.fillRect_SC(DEFAULT_BACKGROUND_COLOR, x, y, sizeX, sizeY);
		
		for(int i = startI; i < endI; ++i) {
			CharSequence text = (CharSequence) objToString(objects.get(i));
			
			final double elementY = y + (i - currentPosition) * dist + deltaY;
			
			if(selectedObject == i) {
				canvas.fillRect_SC(SELECTED_ELEMENT_COLOR, x, elementY - deltaY / 2.0,
						sizeX, elementHeight + deltaY);
			}
			
			font.draw(canvas, x + WIDTH_RESERVE, elementY, font.getWidth(text), elementHeight, text, -1);
		}
		
		canvas.fillRect_SC(SLIDER_COLOR * 2, x + sizeX - SLIDER_WIDTH, y, SLIDER_WIDTH, sizeY);
		
		final double sliderHeight = sizeY * ((double)elementsYCount / objects.size());
		final double sliderY = y + (sizeY - sliderHeight) * ((double)(currentPosition) / (objects.size() - elementsYCount + 1));
		
		canvas.fillRect_SC(SLIDER_COLOR, x + sizeX - SLIDER_WIDTH, sliderY, SLIDER_WIDTH, sliderHeight);
		
		canvas.drawRect_SC(selected ? 0x8f0000 : 0, x, y, sizeX, sizeY, canvas.getPixelScSize());
	}
	
	@Override
	public void onAction(Action a) {
		if(a instanceof MouseAction) {
			
			double my = ((MouseAction)a).mouseY - y;
			
			double dist = font.getHeight() + deltaY;
			
			int sl = (int)((my - deltaY / 2.0) / dist) + currentPosition;
			
			if(sl >= 0 && sl < objects.size()) { 
				if(sl != selectedObject) {
					menuSound.play();
				}
				
				selectedObject = sl;
			}
		}
	}
	
	@Override
	public void updateComponent(Window window) {
		if(objects.isEmpty()) {
			return;
		}
		
		boolean nextAction = window.getActionsCount(input_listNext) > 0 && selected;
		boolean previousAction = window.getActionsCount(input_listPrevious) > 0 && selected;
		
		if(window.getActionsCount(input_scrollUp) > 0) {
			--currentPosition;
			
			if(currentPosition < 0) {
				currentPosition = 0;
			}
		} else if(window.getActionsCount(input_scrollDown) > 0) {
			++currentPosition;
			
			if(currentPosition > maxPosition) {				
				currentPosition = maxPosition;
			}
		}
		
		if(previousAction ^ nextAction) {
			if(nextAction) {
				++selectedObject;
			} else if(previousAction) {
				--selectedObject;
			}
			
			selectedObject += selectedObject < 0 ? objects.size() : 0;
			selectedObject %= objects.size();
		}
		
		final int firstObject = currentPosition;
		final int lastObject = elementsYCount + currentPosition - 1;
		
		if(selectedObject >= 0) {
			if(selectedObject < firstObject) {
				currentPosition -= firstObject - selectedObject;
			} else if(selectedObject > lastObject) {
				currentPosition += selectedObject - lastObject;
			}
		}
	}
	
	@Override
	public boolean playSoundOnEnter() {
		return false;
	}
	
	public T getSelectedObject() {
		if(selectedObject >= 0 && selectedObject < objects.size()) {
			return objects.get(selectedObject);
		}
		
		return null;
	}
	
	public void setSelectedObject(int newSelected) {
		selectedObject = newSelected;
	}
	
	public void remove(int index) {
		if(index >= 0 && index < objects.size()) {
			if(elementsYCount == objects.size()) {
				elementsYCount--;
			}
			
			objects.remove(index);
			
			selectedObject = -1;
		}
	}
	
	public int getNumberOfSelected() {
		return selectedObject;
	}
	
	public DDFont changeFont(DDFont newFont) {
		DDFont old = font;
		font = newFont;
		
		return old;
	}

	@Override
	public void select(Window window) {
		selected = true;
	}
	
	@Override
	public void unselect(Window window) {
		selected = false;
	}

	protected String objToString(T t) {
		return t.toString();
	}
}
