package org.zizitop.game.sprites;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public double x, y, z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + '(' + x +  ';' + y + ';' + z + ')';
	}
	
	public double distance(Point p) {
		return Math.hypot(p.x - x, p.y - y);
	}
}
