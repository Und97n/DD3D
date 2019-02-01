package org.zizitop.pshell.menu;

import org.zizitop.pshell.window.Window;

/**
 * Cool abstract class with needed boring code
 * @author Zizitop
 *
 */
public abstract class StandardComponent implements Component {
	protected double x;
	protected double y;
	protected double sizeX;
	protected double sizeY;
	
	public StandardComponent(double sizeX, double sizeY) {
		this.x = 0;
		this.y = 0;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	@Override
	public final double getX() {
		return x;
	}

	@Override
	public final double getY() {
		return y;
	}

	@Override
	public final double getSizeX() {
		return sizeX;
	}

	@Override
	public final double getSizeY() {
		return sizeY;
	}

	@Override
	public final Component setX(double newX) {
		x = newX;	
		return this;
	}

	@Override
	public final Component setY(double newY) {
		y = newY;
		return this;
	}
	
	@Override
	public Component setPosition(double newX, double newY) {
		setX(newX);
		setY(newY);
		
		return this;
	}
	
	@Override
	public void updateComponent(Window window) {}
}
