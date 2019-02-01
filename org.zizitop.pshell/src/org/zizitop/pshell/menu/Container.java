package org.zizitop.pshell.menu;

import java.util.List;

import org.zizitop.pshell.utils.Utils;

/**
 * Class for smart pane filling.
 * @author Zizitop
 *
 */
public abstract class Container {
	protected List<Component> components;

	protected double x, y;
	
	public Container(List<Component> components){
		this.components = components;
	}
	
	public Container(Component... components) {
		this.components = Utils.createList(components);
	}
	
	/**
	 * Add components to the container
	 * @return this Container
	 */
	public final Container add(Component... components) {
		for(Component comp: components) {
			addComponent(comp);
		}
		
		return this;
	}
	
	protected abstract void addComponent(Component comp);
	
	/**
	 * Add component from another Container.
	 * @return this Container
	 */
	public abstract Container addContainer(Container cont);
	
	public List<Component> getComponents() {
		return components;
	}

	public double getX() {
		return x;
	}

	public Container setX(double x) {
		this.x = x;
		
		return this;
	}

	public double getY() {
		return y;
	}

	public Container setY(double y) {
		this.y = y;
		
		return this;
	}
	
	public Container setPosition(double x, double y) {
		setX(x);
		setY(y);
		
		return this;
	}
}
