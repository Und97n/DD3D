package org.zizitop.game.world;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;

import java.util.ArrayDeque;
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
	public static final Bitmap texture = ResourceLoader.getInstance().getTexture("planes.walls.brick");
	final double hfov, vfov;

	int fogType = -1;

	// snake_style because this code is copypasted from C program
	int set_brightness(double distance, int color) {
		double fog = 1.0 - distance * 0.08;

		int r = ((color & 0xff0000) >> 16);
		int g = ((color & 0xff00) >> 8);
		int b = ((color & 0xff));

		int d = ((int) (fog * 400));

		d = 256 - d;

		d = d < 0 ? 0 : d;
		// if(r != b || r != g)System.out.println("HHH");

		r += fogType * d;
		g += fogType * d;
		b += fogType * d;

		r = r < 0 ? 0 : r;
		r = r > 255 ? 255 : r;

		g = g < 0 ? 0 : g;
		g = g > 255 ? 255 : g;

		b = b < 0 ? 0 : b;
		b = b > 255 ? 255 : b;

		return ((r << 16) + (g << 8) + b);
	}

	double[] zBuffer, zBufferWall;

	public WorldRendererSimple(double hfov, double vfov, int screenWidth, int screenHeight) {
		super(screenWidth, screenHeight);
		this.hfov = hfov;
		this.vfov = vfov;
	}

	private void drawFloorVLINE(int[] pix, double z, double px, double py, double rayDirX, double rayDirY, int x, int startY, int endY) {
		final int W = canvas.width;
		final int H = canvas.height;

		final int centerX = W / 2;
		final int centerY = H / 2;

		final double verticalLook = 0;

		double ray_length = Math.sqrt(rayDirX * rayDirX + rayDirY * rayDirY);

		for (int y = startY; y < endY; ++y) {
			double distMultiplier = (z) * H;
			//		if(y < drawMaxY) {
			//			distMultiplier = -(centerY + H * cameraZ);
			//			tx_shit = 24;
			//		} else if(y > drawMinY) {
			//			tx_shit = 16;
			//		} else {
			//			//No celling or floor visible
			//			continue;
			//		}

			final double dist = distMultiplier / (y - centerY - verticalLook);

			final double offsetX = (rayDirX * dist) - px;
			final double offsetY = (rayDirY * dist) + py;

			final int texX = (int) (offsetX * TX_SIZE);
			final int texY = (int) (offsetY * TX_SIZE);

			int color;
			color = texture.pixels[(texX & (TX_SIZE - 1)) +
					((texY & (TX_SIZE - 1)) * TX_SIZE)];

			pix[y * W + x] = set_brightness(Math.abs(dist) * ray_length, color);
		}
	}

	private static class RenderingItem {
		final int sectorId;
		final long sx1, sx2, x1, x2, yTop1, yBottom1, yTop2, yBottom2;

		private RenderingItem(int sectorId, long sx1, long sx2, long x1, long x2, long yTop1, long yBottom1, long yTop2, long yBottom2) {
			this.sectorId = sectorId;
			this.sx1 = sx1;
			this.sx2 = sx2;
			this.yTop1 = yTop1;
			this.yBottom1 = yBottom1;
			this.yTop2 = yTop2;
			this.yBottom2 = yBottom2;
			this.x1 = x1;
			this.x2 = x2;
		}

		int getYTop(int screenX, int screenHeight) {
			double xPortalRelative = (double)(screenX - x1)/(x2 - x1);
			long yTop = (long) (xPortalRelative * (yTop2 - yTop1) + yTop1);

			return (int)Utils.border(yTop, 0, screenHeight-1);
		}

		int getYBottom(int screenX, int screenHeight) {
			double xPortalRelative = (double)(screenX - x1)/(x2 - x1);
			long yBottom = (long) (xPortalRelative * (yBottom2 - yBottom1) + yBottom1);
			return (int)Utils.border(yBottom, 0, screenHeight-1);
		}
	}

	@Override
	public void render(final double px, final double py, final double pz, final double hAngle, final double vAngle, final int pSectorId, final World world) {
		// Code is very ugly, because it`s history contains may years, many programming languages, many authors.
		final int W = canvas.width;
		final int H = canvas.height;

		final Level l = world.getLevel();

		final long[] renderedSectors = new long[l.sectors.length];


		Arrays.fill(zBufferWall, Double.MAX_VALUE);
//		memset(surface->pixels, 0xff, W*H*sizeof(int));
		canvas.fillRect(0xffffff, 0, 0, W, H);

		Arrays.fill(zBuffer, Double.MAX_VALUE);

		for(int n = 0; n < l.sectors.length; ++n) {
			renderedSectors[n] = 0;
		}

		/* Deque of sectors to draw*/
		final Deque<RenderingItem> rq = new ArrayDeque<>();

		/* Begin whole-screen rendering from where the player is. */
		rq.push(new RenderingItem(pSectorId, 0, W-1, 0, W-1, 0, H-1, 0, H-1));

		final double view_cos = 0.685*W/H;
		final double nearz = 0.0, farz = 50, nearside = view_cos*nearz, farside = view_cos*(farz);
		final double pcos = Math.cos(hAngle), psin = Math.sin(hAngle);

		do {
			/* Pick a sector & slice from the queue to draw */
			final RenderingItem now = rq.pollLast();

			if((renderedSectors[now.sectorId] & 0x21) != 0) {
				continue; // Odd = still rendering, 0x20 = give up
			}

			++renderedSectors[now.sectorId];

			Sector sect = l.sectors[now.sectorId];

			/* Render each wall of this sector that is facing towards player. */
			for(int s = 0; s < sect.walls.length; ++s) {
				/* Check the edge type. neighbor=-1 means wall, other=boundary between two sectors. */
				int wall = sect.walls[s];

				final int vert1 = sect.verticies[s + 0], vert2 = sect.verticies[s + 1];

				/* Acquire the x,y coordinates of the two endpoints (vertices) of this edge of the sector */
				final double vx1 = l.verticies[vert1*2 + 0] - px, vy1 = l.verticies[vert1*2 + 1] - py;
				final double vx2 = l.verticies[vert2*2 + 0] - px, vy2 = l.verticies[vert2*2 + 1] - py;
//				System.out.println(vx1);

				double tx1 = vx1 * psin - vy1 * pcos,  tz1 = vx1 * pcos + vy1 * psin;
				double tx2 = vx2 * psin - vy2 * pcos,  tz2 = vx2 * pcos + vy2 * psin;
//				double t = tx1;
//				tx1 = tx2;
//				tx2 = t;
//				t = tz1;
//				tz1 = tz2;
//				tz2 = t;

				final double wall_x_length = tx1 - tx2;
				final double wall_y_length = tz1 - tz2;

				final double wall_length = Math.sqrt(wall_x_length*wall_x_length + wall_y_length*wall_y_length);

				final double wall_cos = -wall_x_length/wall_length;
				final double wall_sin = -wall_y_length/wall_length;

				// Without clipping
				double wallStartX = tx1, wallStartY = tz1;

				/* Is the wall at least partially in front of the player? */
				if(tz1 <= nearz && tz2 <= nearz) {
					continue;
				}

				/* If it's partially behind the player, clip it against player's view frustrum */
				if((tz1 < nearz || tz2 < nearz)) {
//					Vector t1 = (Vector){tx1, tz1};
//					Vector t2 = (Vector){tx2, tz2};
//
//					Vector c11 = (Vector){-nearside, nearz};
//					Vector c12 = (Vector){-farside, farz};
//					Vector c21 = (Vector){+nearside, nearz};
//					Vector c22 = (Vector){+farside, farz};

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

//			tz1 = max(0.0625, tz1);
//			tz2 = max(0.0625, tz2);

				/* Do perspective transformation */
				double xscale1 = H*hfov / tz1, yscale1 = H*vfov / tz1;
				long x1 = W/2 - (long)(tx1 * xscale1);

				double xscale2 = H*hfov / tz2, yscale2 = H*vfov / tz2;
				long x2 = W/2 - (long)(tx2 * xscale2);


				if(x1 >= x2 || x2 < now.sx1 || x1 > now.sx2) {
					// Only render if it's visible
					continue;
				}

				/* Acquire the floor and ceiling Hs, relative to where the player's view is */
				double yceil  = sect.ceiling  - pz;
				double yfloor = sect.floor - pz;

				double nyceil = 0, nyfloor = 0;

				// Is another sector showing through this portal?
				if(wall < 0) {
					nyceil  = l.sectors[-(wall+1)].ceiling  - pz;
					nyfloor = l.sectors[-(wall+1)].floor - pz;
				} else {

				}

				final double yaw = 0;

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
					int yTop = now.getYTop(x, H);
					int yBottom = now.getYBottom(x, H);

					if(now.sectorId == 0)System.out.println(x + "\t" + yTop + "\t" + yBottom);

					double cameraX = 2 * x / (double) (W) - 1;
					cameraX *= view_cos;

					double rayDirX = -cameraX*0.73;
					double rayDirY = 0.73;

					final double tmp_0 = (tx2*tz1 - tz2*tx1) / (rayDirX * (tz1 - tz2) - (tx1 - tx2) * rayDirY);

					double wall_intersection_x = tmp_0*rayDirX;
					double wall_intersection_y = tmp_0*rayDirY;

					double wallX = (wall_intersection_x - wallStartX)*wall_cos + (wall_intersection_y - wallStartY)*wall_sin;

					long texX = (long) (wallX * TX_SIZE);
					texX %= TX_SIZE;
					texX += TX_SIZE;
					texX %= TX_SIZE;
					double dd = Math.hypot(wall_intersection_x, wall_intersection_y);

					if(zBufferWall[x] < dd) {
						continue;
					} else if(wall >= 0) {
						zBufferWall[x] = dd;
					}

					int[] pix = canvas.pixels;

					/* Acquire the Y coordinates for our ceiling & floor for this X coordinate. Clamp them. */
					long ya = (x - x1) * (y2a-y1a) / (x2-x1) + y1a;
					int cya = (int) Utils.border(ya, yTop,yBottom); // top
					long yb = (x - x1) * (y2b-y1b) / (x2-x1) + y1b;
					int cyb = (int) Utils.border(yb, yTop,yBottom); // bottom

					final double y_multiplier = (yceil - yfloor)/(yb-ya);

					final double rayX = rayDirX*psin + rayDirY*pcos;
					final double rayY = rayDirX*pcos - rayDirY*psin;

					// Floor
//					drawFloorVLINE(pix, yfloor, px, py, rayX, rayY, x, cyb, yBottom);
//					// Ceiling
//					drawFloorVLINE(pix, yceil, px, py, rayX, rayY, x, yTop, cya);

					long nya = (x - x1) * (ny2a-ny1a) / (x2-x1) + ny1a;
					int cnya = (int) Utils.border(nya, yTop,yBottom);
					long nyb = (x - x1) * (ny2b-ny1b) / (x2-x1) + ny1b;
					int cnyb = (int) Utils.border(nyb, yTop,yBottom);

					yTop = Utils.border(Math.max(cya, cnya), yTop, H-1);   // Shrink the remaining window below these ceilings
					yBottom = Utils.border(Math.min(cyb, cnyb), 0, yBottom); // Shrink the remaining window above these floors

					for(int y = cya; y < cnya; ++y) {
						final int offset = x + y * W;

						long texY = (long) (((y - yb + 0.0f)*y_multiplier)*TX_SIZE);
						texY %= TX_SIZE;
						texY += TX_SIZE;
						texY %= TX_SIZE;
						pix[offset] = set_brightness(dd, texture.pixels[(int) (texX + TX_SIZE*texY)]);
					}

					for(int y = cnyb; y < cyb; ++y) {
						final int offset = x + y * W;
						long texY = (long) (((y - yb + 0.0f)*y_multiplier)*TX_SIZE);
						texY %= TX_SIZE;
						texY += TX_SIZE;
						texY %= TX_SIZE;
						pix[offset] = set_brightness(dd, texture.pixels[(int) (texX + TX_SIZE*texY)]);
					}
				}

				/* Schedule the neighboring sector for rendering within the window formed by this wall. */
				if(wall < 0 && x2 >= x1 && rq.size() < MAX_QUEUE) {
					

					rq.push(new RenderingItem(-(wall + 1), beginx, endx,x1, x2,
							ny1a,
							ny1b,
							ny2a,
							ny2b));

				}
			}

			// for s in sector's edges
			++renderedSectors[now.sectorId];
		} while(!rq.isEmpty()); // render any other queued sectors
	}

	@Override
	public void reshape(int newWidth, int newHeight) {
		super.reshape(newWidth, newHeight);

		zBuffer = new double[newWidth*newHeight];
		zBufferWall = new double[newWidth];
	}
}
