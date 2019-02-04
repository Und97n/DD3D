package org.zizitop.game.world;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

import static java.lang.Math.min;

/**
 * Simple realization of a 3d engine.
 * <br><br>
 * Created 31.01.19 0:09
 *
 * @author Zizitop
 */
public class WorldRendererSimple extends WorldRenderer {
	public static final int MAX_QUEUE = 128;
	public static final Bitmap texture = ResourceLoader.getInstance().getTexture("brick1"), textureFloor = ResourceLoader.getInstance().getTexture("floor1");
	final double hfov, vfov;

	double brightness = 0.08;

	// snake_style because this code is copypasted from C program
	int set_brightness(double distance, int color) {
		double fog = 1.0 - distance * brightness;

		int r = ((color & 0xff0000) >> 16);
		int g = ((color & 0xff00) >> 8);
		int b = ((color & 0xff));

		int d = ((int) (fog * 400));

		d = 256 - d;

		d = d < 0 ? 0 : d;
		// if(r != b || r != g)System.out.println("HHH");

		r -= d;
		g -= d;
		b -= d;

		r = r < 0 ? 0 : r;

		g = g < 0 ? 0 : g;

		b = b < 0 ? 0 : b;

		return ((r << 16) + (g << 8) + b);
	}

	double[] zBuffer, zBufferWall;
	int[] yTop, yBottom, wallStartY, wallEndY;

	public WorldRendererSimple(double hfov, double vfov, int screenWidth, int screenHeight) {
		super(screenWidth, screenHeight);
		this.hfov = hfov;
		this.vfov = vfov;
	}

	private void drawPlanes(int beginX, int endX, double px, double py, double psin, double pcos, double yaw, double view_cos, double yceil, double yfloor) {
		final int W = canvas.width;
		final int H = canvas.height;

		final int centerX = W / 2;
		final int centerY = H / 2;

		final double verticalLook = -yaw*H*vfov;

		int[] pix = canvas.pixels;

		for(int x = beginX; x <= endX; ++x) {
			double cameraX = 2 * x / (double) (W) - 1;
			cameraX *= view_cos;

			final double rayDirX_ = -cameraX*0.73;
			final double rayDirY_ = 0.73;

			final double rayDirX = rayDirX_*psin + rayDirY_*pcos;
			final double rayDirY = rayDirX_*pcos - rayDirY_*psin;

			final double ray_length = Math.sqrt(rayDirX * rayDirX + rayDirY * rayDirY);

			final double distMultiplierF = (yfloor) * H;
			final double distMultiplierC = (yceil) * H;

			int yEndF = (int) (-distMultiplierF*brightness*ray_length + centerY + verticalLook);
			int yStartC = (int) (-distMultiplierC*brightness*ray_length + centerY + verticalLook);

			// Floor
			for (int y = Math.max(yEndF, wallEndY[x]); y < yBottom[x]; ++y) {
				final double dist = distMultiplierF / (y - centerY - verticalLook);

				final double offsetX = (rayDirX * dist) - px;
				final double offsetY = (rayDirY * dist) + py;

				final int texX = (int) (offsetX * TX_SIZE);
				final int texY = (int) (offsetY * TX_SIZE);

				int color;
				color = textureFloor.pixels[(texX & (TX_SIZE - 1)) +
						((texY & (TX_SIZE - 1)) << TX_SIZE_2_LOG)];

				double fog = 1.0 + dist*ray_length*brightness;

				int r = ((color & 0xff0000) >> 16);
				int g = ((color & 0xff00) >> 8);
				int b = ((color & 0xff));

				int d = ((int) (fog * 400));

				d = 256 - d;

				d = d < 0 ? 0 : d;
				// if(r != b || r != g)System.out.println("HHH");

				r -= d;
				g -= d;
				b -= d;

				r = r < 0 ? 0 : r;
				g = g < 0 ? 0 : g;
				b = b < 0 ? 0 : b;

				pix[y * W + x] = (r << 16) + (g << 8) + b;
			}

			// Ceilling
			for (int y = yTop[x]; y < Math.min(yStartC, wallStartY[x]); ++y) {
				final double dist = distMultiplierC / (y - centerY - verticalLook);

				final double offsetX = (rayDirX * dist) - px;
				final double offsetY = (rayDirY * dist) + py;

				final int texX = (int) (offsetX * TX_SIZE);
				final int texY = (int) (offsetY * TX_SIZE);

				int color;
				color = textureFloor.pixels[(texX & (TX_SIZE - 1)) +
						((texY & (TX_SIZE - 1)) << TX_SIZE_2_LOG)];

				double fog = 1.0 + dist*ray_length*brightness;

				int r = ((color & 0xff0000) >> 16);
				int g = ((color & 0xff00) >> 8);
				int b = ((color & 0xff));

				int d = ((int) (fog * 400));

				d = 256 - d;

				d = d < 0 ? 0 : d;
				// if(r != b || r != g)System.out.println("HHH");

				r -= d;
				g -= d;
				b -= d;

				r = r < 0 ? 0 : r;
				g = g < 0 ? 0 : g;
				b = b < 0 ? 0 : b;

				pix[y * W + x] = (r << 16) + (g << 8) + b;
			}
		}
	}

	// vxs: Vector cross product
	public static double vxs(double x0, double y0, double x1, double y1) {
		return x0*y1 - x1*y0;
	}

	// PointSide: Determine which side of a line the point is on. Return value: <0, =0 or >0.
	public static double pointSide(double px, double py, double x0, double y0, double x1, double y1) {
		return vxs(x1-x0, y1-y0, px-x0, py-y0);
	}

	public static void lineSegmentIntersection(double x11, double y11, double x12, double y12,
	                                              double x21, double y21, double x22, double y22, Utils.DoubleVector ret) {
		final double
				bax = (x12 - x11),
				dcy = (y22 - y21),
				bay = (y12 - y11),
				dcx = (x22 - x21),
				acx = (x11 - x21),
				acy = (y11 - y21);

		final double common = bax*dcy - bay*dcx;

		final double rH = acy*dcx - acx*dcy;
		final double sH = acy*bax - acx*bay;

		final double r = rH / common;
		final double s = sH / common;
		// Just expressions, that are used 2 times
		final double shit1 = x21*y22 - x22*y21;
		final double shit2 = x11*y12 - x12*y11;

		ret.x = (shit1*bax - shit2*dcx) / common;
		ret.y = (shit1*bay - shit2*dcy) / common;
	}

	private static class RenderingItem {
		final int sectorId, sx1, sx2;
		final double tx1, tz1, tx2, tz2;

		private RenderingItem(int sectorId, int sx1, int sx2, double tx1, double tz1, double tx2, double tz2) {
			this.sectorId = sectorId;
			this.sx1 = sx1;
			this.sx2 = sx2;
			this.tx1 = tx1;
			this.tz1 = tz1;
			this.tx2 = tx2;
			this.tz2 = tz2;
		}

		public boolean onRightSide(double px, double py) {
			return pointSide(px, py, tx1, tz1, tx2, tz2) >= 0;
		}
	}

	public static boolean clipWall(double cx1, double cz1, double cx2, double cz2, Utils.DoubleVector wall1, Utils.DoubleVector wall2) {
		final boolean onRightSide1 = pointSide(wall1.x, wall1.y, cx1, cz1, cx2, cz2) >= 0;
		final boolean onRightSide2 = pointSide(wall2.x, wall2.y, cx1, cz1, cx2, cz2) >= 0;

		if(!onRightSide1 && !onRightSide2) {
			return false;
		} else if(!onRightSide1 || !onRightSide2) {
			Utils.DoubleVector i = new Utils.DoubleVector();

			lineSegmentIntersection(wall1.x, wall1.y, wall2.x, wall2.y, cx1, cz1, cx2, cz2, i);

			if(!onRightSide1) {
				wall1.x = i.x;
				wall1.y = i.y;
			}

			if(!onRightSide2) {
				wall2.x = i.x;
				wall2.y = i.y;
			}
		}

		return true;
	}

	@Override
	public void render(final double px, final double py, final double pz, final double hAngle, final double vAngle, final int pSectorId, final World world) {

		// Code is very ugly, because it`s history contains may years, many programming languages, many authors.
		final int W = canvas.width;
		final int H = canvas.height;

		final Level l = world.getLevel();

		final long[] renderedSectors = new long[l.sectors.length];

		// Write here borders of your portals
		// After sector drawing they will be pushed to the global yTop and yBottom
		int[] yTopTmp = new int[W];
		int[] yBottomTmp = new int[W];

		for (int i = 0; i < W; i++) {
			yTop[i] = yTopTmp[i] = wallStartY[i] = 0;
			yBottom[i] = yBottomTmp[i] = wallEndY[i] = H;
		}


//		memset(surface->pixels, 0xff, W*H*sizeof(int));
		canvas.fillRect(0, 0, 0, W, H);

		Arrays.fill(zBuffer, Double.MAX_VALUE);

		for(int n = 0; n < l.sectors.length; ++n) {
			renderedSectors[n] = 0;
		}

		/* Deque of sectors to draw*/
		final Deque<RenderingItem> rq = new ArrayDeque<>();

		final double view_cos = 0.685*W/H;
		final double pcos = Math.cos(hAngle), psin = Math.sin(hAngle);
		final double nearz = 0.001, farz = 50, nearside = view_cos*nearz, farside = view_cos*(farz);

		/* Begin whole-screen rendering from where the player is. */
		rq.push(new RenderingItem(pSectorId, 0, W-1, -10, -nearz, 10, -nearz));


		int secrorsRendered = 0;

		ArrayList<String> textToDraw = new ArrayList<>();

		do {
			++secrorsRendered;
			Arrays.fill(zBufferWall, Double.MAX_VALUE);
			/* Pick a sector & slice from the queue to draw */
			final RenderingItem now = rq.removeLast();
//			System.out.println(now.sectorId);

			if((renderedSectors[now.sectorId] ) > 4) {
				continue; // Odd = still rendering, 0x20 = give up
			}

			++renderedSectors[now.sectorId];

			Sector sect = l.sectors[now.sectorId];

			/* Acquire the floor and ceiling Hs, relative to where the player's view is */
			final double yceil  = sect.ceiling  - pz;
			final double yfloor = sect.floor - pz;

			/* Render each wall of this sector that is facing towards player. */
			for(int s = 0; s < sect.walls.length; ++s) {
				/* Check the edge type. neighbor=-1 means wall, other=boundary between two sectors. */
				int wall = sect.walls[s];

				final int vert1 = sect.verticies[s + 0], vert2 = sect.verticies[s + 1];

				/* Acquire the x,y coordinates of the two endpoints (vertices) of this edge of the sector */
				final double vx1 = l.verticies[vert1*2 + 0] - px, vy1 = l.verticies[vert1*2 + 1] - py;
				final double vx2 = l.verticies[vert2*2 + 0] - px, vy2 = l.verticies[vert2*2 + 1] - py;

				double tx1 = vx1 * psin - vy1 * pcos,  tz1 = vx1 * pcos + vy1 * psin;
				double tx2 = vx2 * psin - vy2 * pcos,  tz2 = vx2 * pcos + vy2 * psin;

				final double wall_x_length = tx1 - tx2;
				final double wall_y_length = tz1 - tz2;

				final double wall_length = Math.sqrt(wall_x_length*wall_x_length + wall_y_length*wall_y_length);

				final double wall_cos = -wall_x_length/wall_length;
				final double wall_sin = -wall_y_length/wall_length;

				// Without clipping
				double wallX1 = tx1, wallY1 = tz1;

				final Utils.DoubleVector t1 = new Utils.DoubleVector(tx1, tz1), t2 = new Utils.DoubleVector(tx2, tz2);

				if(!clipWall(now.tx1, now.tz1, now.tx2, now.tz2, t1, t2)) {
					continue;
				}

				tx1 = t1.x;
				tz1 = t1.y;
				tx2 = t2.x;
				tz2 = t2.y;

				if((tz1 < nearz || tz2 < nearz)) {
					// Find an intersection between the wall and the approximate edges of player's view
					Utils.DoubleVector i1 = new Utils.DoubleVector(), i2 = new Utils.DoubleVector();

					boolean i1_condition = Utils.lineSegmentIntersection(tx1, tz1, tx2, tz2,
							-nearside, nearz, -farside, farz, i1);
					boolean i2_condition = Utils.lineSegmentIntersection(tx1, tz1, tx2, tz2,
							+nearside, nearz, +farside, farz, i2);

					if(tz1 < nearz) {
						if(i2_condition) {
							tz1 = i2.y;
							tx1 = i2.x;
						} else if(i1_condition) {
							tx1 = i1.x;
							tz1 = i1.y;
						} else {
							continue;
						}
					}

					if(tz2 < nearz) {
						if(i1_condition) {
							tx2 = i1.x;
							tz2 = i1.y;
						} else if(i2_condition) {
							tz2 = i2.y;
							tx2 = i2.x;
						} else {
							continue;
						}
					}
				}

				/* Do perspective transformation */
				double xscale1 = H*hfov / tz1, yscale1 = H*vfov / tz1;
				long x1 = W/2 - (long)(tx1 * xscale1);

				double xscale2 = H*hfov / tz2, yscale2 = H*vfov / tz2;
				long x2 = W/2 - (long)(tx2 * xscale2);


				if(x1 >= x2 || x2 < now.sx1 || x1 > now.sx2) {
					// Only render if it's visible
					continue;
				}

				double nyceil = 0, nyfloor = 0;

				// Is another sector showing through this portal?
				if(wall < 0) {
					nyceil  = l.sectors[-(wall+1)].ceiling  - pz;
					nyfloor = l.sectors[-(wall+1)].floor - pz;
				} else {

				}

				final double yaw = vAngle;

				/* Project our ceiling & floor Hs into screen coordinates (Y coordinate) */
				long y1a  = H/2 - (long)((yceil + yaw*tz1) * yscale1),  y1b = H/2 - (long)((yfloor + yaw*tz1) * yscale1);
				long y2a  = H/2 - (long)((yceil + yaw*tz2) * yscale2),  y2b = H/2 - (long)((yfloor + yaw*tz2) * yscale2);
				/* The same for the neighboring sector */
				long ny1a = H/2 - (long)((nyceil + yaw*tz1) * yscale1), ny1b = H/2 - (long)((nyfloor + yaw*tz1) * yscale1);
				long ny2a = H/2 - (long)((nyceil + yaw*tz2) * yscale2), ny2b = H/2 - (long)((nyfloor + yaw*tz2) * yscale2);

				/* Render the wall. */
				int beginx = (int) Math.max(x1, now.sx1), endx = (int) min(x2, now.sx2);

				beginx = Utils.border(beginx, 0, W-1);
				endx = Utils.border(endx, 0, W-1);

				for(int x = beginx; x <= endx; ++x) {
					double cameraX = 2 * x / (double) (W) - 1;
					cameraX *= view_cos;

					double rayDirX = -cameraX*0.73;
					double rayDirY = 0.73;

					final double tmp_0 = (tx2*tz1 - tz2*tx1) / (rayDirX * (tz1 - tz2) - (tx1 - tx2) * rayDirY);

					double wall_intersection_x = tmp_0*rayDirX;
					double wall_intersection_y = tmp_0*rayDirY;

					double distance = Math.hypot(wall_intersection_x, wall_intersection_y);

					int yTop = this.yTop[x];
					int yBottom = this.yBottom[x];

					if(zBufferWall[x] < distance) {
						continue;
					}

					zBufferWall[x] = distance;

					int[] pix = canvas.pixels;

					/* Acquire the Y coordinates for our ceiling & floor for this X coordinate. Clamp them. */
					long ya = (x - x1) * (y2a-y1a) / (x2-x1) + y1a;
					int cya = (int) Utils.border(ya, yTop,yBottom); // top
					long yb = (x - x1) * (y2b-y1b) / (x2-x1) + y1b;
					int cyb = (int) Utils.border(yb, yTop,yBottom); // bottom

					final double y_multiplier = (yceil - yfloor)/(yb-ya);

					long nya = (x - x1) * (ny2a-ny1a) / (x2-x1) + ny1a;
					int cnya = (int) Utils.border(nya, yTop,yBottom);
					long nyb = (x - x1) * (ny2b-ny1b) / (x2-x1) + ny1b;
					int cnyb = (int) Utils.border(nyb, yTop,yBottom);

					yTopTmp[x] = Utils.border(Math.max(cya, cnya), yTop, H-1);   // Shrink the remaining window below these ceilings
					yBottomTmp[x] = Utils.border(Math.min(cyb, cnyb), 0, yBottom); // Shrink the remaining window above these floors

					wallStartY[x] = cya;
					wallEndY[x] = cyb;

					double wallX = (wall_intersection_x - wallX1)*wall_cos + (wall_intersection_y - wallY1)*wall_sin;

					int texX = (int) (wallX * TX_SIZE);
					texX %= TX_SIZE;
					texX += TX_SIZE;
					texX %= TX_SIZE;

					double fog = 1.0 - distance * brightness;

					int colorComponentDelta = ((int) (fog * 400));

					colorComponentDelta = 256 - colorComponentDelta;
					colorComponentDelta = colorComponentDelta < 0 ? 0 : colorComponentDelta;
					// if(r != b || r != g)System.out.println("HHH");

					if(colorComponentDelta < 255) {
						for(int y = cya; y < cnya; ++y) {
							final int offset = x + y * W;
							int texY = (int) (((y - yb + 0.0f)*y_multiplier)*TX_SIZE);
							texY %= TX_SIZE;
							texY += TX_SIZE;
							texY %= TX_SIZE;
							texY <<= TX_SIZE_2_LOG;

							int color =  texture.pixels[texX + texY];

							int r = ((color & 0xff0000) >> 16);
							int g = ((color & 0xff00) >> 8);
							int b = ((color & 0xff));
							r -= colorComponentDelta;
							g -= colorComponentDelta;
							b -= colorComponentDelta;

							r = r < 0 ? 0 : r;
							g = g < 0 ? 0 : g;
							b = b < 0 ? 0 : b;

							pix[offset] = (r << 16) + (g << 8) + (b << 0);
						}

						for(int y = cnyb; y < cyb; ++y) {
							final int offset = x + y * W;
							int texY = (int) (((y - yb + 0.0f)*y_multiplier)*TX_SIZE);
							texY %= TX_SIZE;
							texY += TX_SIZE;
							texY %= TX_SIZE;
							texY <<= TX_SIZE_2_LOG;

							int color = texture.pixels[texX + texY];

							int r = ((color & 0xff0000) >> 16);
							int g = ((color & 0xff00) >> 8);
							int b = ((color & 0xff));
							r -= colorComponentDelta;
							g -= colorComponentDelta;
							b -= colorComponentDelta;

							r = r < 0 ? 0 : r;
							g = g < 0 ? 0 : g;
							b = b < 0 ? 0 : b;

							pix[offset] = (r << 16) + (g << 8) + (b << 0);
						}
					}
				}

				/* Schedule the neighboring sector for rendering within the window formed by this wall. */
				if(wall < 0 && endx >= beginx && rq.size() < MAX_QUEUE) {
					rq.push(new RenderingItem(-(wall + 1), beginx, endx, tx2, tz2, tx1, tz1));

				}
			}

			drawPlanes(now.sx1, now.sx2, px, py, psin, pcos, vAngle, view_cos, yceil, yfloor);

			for(int xx = now.sx1; xx < now.sx2; ++xx) {
				// Update only changed values
				yTop[xx] = Math.max(yTop[xx], yTopTmp[xx]);
				yBottom[xx] = Math.min(yBottom[xx], yBottomTmp[xx]);
			}


//			System.out.println(rq.size());
		} while(!rq.isEmpty()); // render any other queued sectors

		textToDraw.add("Drawed sectors: " + secrorsRendered + "\n");

		DDFont font = DDFont.DEFAULT_FONT;

		canvas.fillRect_SC(0, 0, 0.1, 0.5, textToDraw.size()*font.getHeight()*2);

		for(int i = 0; i < textToDraw.size(); ++i) {
			DDFont.DEFAULT_FONT.draw(canvas, 0, font.getHeight()*2*i + 0.1, textToDraw.get(i), -1);
		}

		textToDraw.clear();

//		System.out.println(secrorsRendered);
	}

	@Override
	public void reshape(int newWidth, int newHeight) {
		super.reshape(newWidth, newHeight);

		zBuffer = new double[newWidth*newHeight];
		zBufferWall = new double[newWidth];

		yTop = new int[newWidth];
		yBottom = new int[newWidth];
		wallStartY = new int[newWidth];
		wallEndY = new int[newWidth];
	}
}
