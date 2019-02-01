package org.zizitop.pshell.menu;

/**
 * Cool class for menu building. Work like {@link javax.swing.Box}.
 * @author Zizitop
 *
 */
public class Box extends Container {
	private double lastPosition, delta;
	
	private final boolean vertical;
	
	public static Box createHorizontalBox(double delta) {
		return new Box(false, delta);
	}
	
	public static Box createVerticalBox(double delta) {
		return new Box(true, delta);
	}
	
	protected Box(boolean vertical, double delta) {
		super();
		this.vertical = vertical;
		this.delta = delta;
	}
	
	/**
	 * Add component to the box.
	 * @param offset - distance between this component and previous
	 * @return this Box
	 */
	public Container addComponent(Component comp, double offset) {		
		lastPosition += offset + delta;
		
		if(vertical) {
			comp.setY(lastPosition);
		} else {
			comp.setX(lastPosition);
		}
		
		components.add(comp);
		
		if(vertical) {
			lastPosition += comp.getSizeY();		
		} else {
			lastPosition += comp.getSizeX();
		}
		
		return this;
	}
	
	public void addComponentNative(Component comp, double x, double y) {
		components.add(comp.setPosition(x, y));
	}
	
	/**
	 * Add empty space to the box.
	 * @param length - size(widh or height) of this empty space
	 * @return this Box
	 */
	public Container addStrut(double length) {
		if(length < 0) {
			throw new IllegalArgumentException("Strut size is negative.");
		}
		
		lastPosition += length;
		
		return this;
	}

	@Override
	protected void addComponent(Component comp) {
		addComponent(comp, delta);
	}
	
	@Override
	public Container addContainer(Container cont) {
		double offsetX = 0, offsetY = 0;
		
		if(vertical) {
			offsetY = lastPosition;
		} else {
			offsetX = lastPosition;
		}
		
		cont.setPosition(offsetX, offsetY);
		
		for(Component comp: cont.getComponents()) {
			comp.setPosition(comp.getX() + cont.getX(), 
					comp.getY() + cont.getY());
						
			components.add(comp);
			
			if(vertical) {
				lastPosition = Math.max(lastPosition, comp.getY() + comp.getSizeY());
			} else {
				lastPosition = Math.max(lastPosition, comp.getX() + comp.getSizeX());
			}			
		}
		
		return this;
	}
}
