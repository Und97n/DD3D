package org.zizitop.pshell.menu;

/**
 * Simple container. Just puts components with their coordinates.
 * @author Zizitop
 *
 */
public class FreeContainer extends Container {

	@Override
	protected void addComponent(Component comp) {
		components.add(comp);
	}

	@Override
	public Container addContainer(Container cont) {
		for(Component comp: cont.getComponents()) {
			comp.setPosition(comp.getX() + cont.getX(), comp.getY() + cont.getY());
			
			add(comp);
		}
		
		return this;
	}

}
