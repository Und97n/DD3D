package org.zizitop.pshell.utils;

/**
 * Utility class for replacing java.awt.Rectangle(because cross-platform needed).
 * <br><br>
 * Created 05.11.18 12:39
 *
 * @author Zizitop
 */
public final class Rectangle {
	public int x, y, width, height;

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public Rectangle(int width, int height) {
		this(0, 0, width, height);
	}

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Determines whether or not this {@code Rectangle} and the specified
	 * {@code Rectangle} intersect. Two rectangles intersect if
	 * their intersection is nonempty.
	 *
	 * @param r the specified {@code Rectangle}
	 * @return    {@code true} if the specified {@code Rectangle}
	 *            and this {@code Rectangle} intersect;
	 *            {@code false} otherwise.
	 */
	public boolean intersects(Rectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		//      overflow || intersect
		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry));
	}

	/**
	 * Computes the intersection of this {@code Rectangle} with the
	 * specified {@code Rectangle}. Returns a new {@code Rectangle}
	 * that represents the intersection of the two rectangles.
	 * If the two rectangles do not intersect, the result will be
	 * an empty rectangle.
	 *
	 * @param     r   the specified {@code Rectangle}
	 * @return    the largest {@code Rectangle} contained in both the
	 *            specified {@code Rectangle} and in
	 *            this {@code Rectangle}; or if the rectangles
	 *            do not intersect, an empty rectangle.
	 */
	public Rectangle intersection(Rectangle r) {
		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.x;
		int ry1 = r.y;
		long tx2 = tx1; tx2 += this.width;
		long ty2 = ty1; ty2 += this.height;
		long rx2 = rx1; rx2 += r.width;
		long ry2 = ry1; ry2 += r.height;
		if (tx1 < rx1) tx1 = rx1;
		if (ty1 < ry1) ty1 = ry1;
		if (tx2 > rx2) tx2 = rx2;
		if (ty2 > ry2) ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
		if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;

		return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
	}


	/**
	 * Checks whether or not this {@code Rectangle} contains the
	 * point at the specified location {@code (x,y)}.
	 *
	 * @param  X the specified X coordinate
	 * @param  Y the specified Y coordinate
	 * @return    {@code true} if the point
	 *            {@code (x,y)} is inside this
	 *            {@code Rectangle};
	 *            {@code false} otherwise.
	 * @since     1.1
	 */
	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ((w < x || w > X) &&
				(h < y || h > Y));
	}
}
