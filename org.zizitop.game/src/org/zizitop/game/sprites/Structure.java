package org.zizitop.game.sprites;

import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Utils;

/**
 * Sprite, that have physics size.
 * @author Zizitop
 *
 */
public abstract class Structure extends Sprite {
	private static final long serialVersionUID = 1L;
	
	public Structure(double x, double y, double z) {
		super(x, y, z);
	}

	public abstract double getElasticity();

	public boolean intersects(Structure s) {
		return 	((x + getSizeX() >= s.x) && (x <= s.x + s.getSizeX())) &&
				((y + getSizeY() >= s.y) && (y <= s.y + s.getSizeY()));
	}

	public boolean intersects(double xx, double yy, double width, double height) {
		return 	((x + getSizeX() >= xx) && (x <= xx + width)) &&
				((y + getSizeY() >= yy) && (y <= yy + height));
	}

	public boolean intersectsWithLineSegment(double x1, double y1, double x2, double y2) {
		final double sx1 = x, sy1 = y;
		final double sx2 = x + getSizeX(), sy2 = y + getSizeY();

		return  Utils.lineSegmentIntersection(sx1, sy1, sx2, sy1, x1, y1, x2, y2, null) ||
				Utils.lineSegmentIntersection(sx1, sy2, sx2, sy2, x1, y1, x2, y2, null) ||
				Utils.lineSegmentIntersection(sx1, sy1, sx1, sy2, x1, y1, x2, y2, null) ||
				Utils.lineSegmentIntersection(sx2, sy1, sx2, sy2, x1, y1, x2, y2, null);
	}
	
	public double distanceToPoint(double _px, double _py) {
		//I want my own coordinate system with center at this rectangle
		final double baseX = x, baseY = y;
		
		//Cool cordinates of point
		final double px = _px - baseX, py = _py - baseY;
		
		//Now we have 9 options - sectors of space
		//				|		|
		//		1		|	2	|		3
		//______________|_______|_________________
		//				|	5	|
		//		4		|  My	|		6
		//				| Rect  |
		//______________|_______|_________________
		//				|		|
		//		7		|	8	|		9
		//				|		|
		
		//We can easy find distance to the point in each sector separately
		
		//Distance between center and sides of rectangle
		final double offsetX = getSizeX() / 2.0;
		final double offsetY = getSizeY() / 2.0;

		double xDist, yDist;
		
		if(px > offsetX) {
			//Sectors 3, 6, 9
			xDist = px - offsetX;
		} else if(px < -offsetX) {
			//Sectors 1, 4, 7
			xDist = px + offsetX;
		} else {
			//Sectors 2, 5, 8
			xDist = 0;
		}
		
		if(py > offsetY) {
			//Sectors 7, 8, 9
			yDist = py - offsetY;
		} else if(py < -offsetY) {
			//Sectors 1, 2, 3
			yDist = py + offsetY;
		} else {
			//Sectors 4, 5, 6
			yDist = 0;
		}
		
		return Math.hypot(xDist, yDist);
	}
	
	public void onEntityCollision(Entity e, World w) {}
	
	public abstract boolean isSolid();
	public abstract double getSizeX();
	public abstract double getSizeY();
}
