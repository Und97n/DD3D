package org.zizitop.pshell.utils;

import org.zizitop.pshell.window.DisplayMode;

import java.util.Arrays;

/**
 * A utility class to store color(int) array.<br>
 * @author Zizitop
 *
 */
public class Bitmap {
	public static final int PRECISION_2LOG = 10;
	public static final int PRECISION = 1 << PRECISION_2LOG;
	
	/**
	 * Transparent color<br>
	 */
	public static final int TRANSPARENT_COLOR = 0xffff00ff;
	
	/**
	 * Color data of image
	 */
	public final int[] pixels;
	
	/**
	 * Width of image
	 */
	public final int width;
	
	/**
	 * Height of image
	 */
	public final int height;
	
	public Bitmap(int width, int height) {		
		this.pixels = new int[width * height];
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Create bitmap and save <code>pixels</code> array reference as color data of image<br>
	 * 
	 * @param pixels - image data
	 * @param width - width of bitmap
	 * @param height - height of bitmap
	 * 
	 * @throws IllegalArgumentException when color array size is incompatible with width and height
	 */
	public Bitmap(int[] pixels, int width, int height) {
		if(pixels.length != width * height) throw new IllegalArgumentException("Color array is incompatible with width and height of bitmap.");
		
		this.pixels = pixels;
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Get part of source image
	 * @param x - x position of subimage on source image
	 * @param y - y position of subimage on source image
	 * @param width - width of subimage
	 * @param height - height of subimage
	 * @return new Bitmap, that is part of source image
	 * 
	 * @throws IllegalArgumentException if arguments is negative or subimage is outside of source image
	 */
	public Bitmap getSubImage(int x, int y, int width, int height) {
		
		if((x + width > this.width) || (y + height > this.height)) {
			throw new IllegalArgumentException("Subimage is outside source image!");
		}
		
		if(x < 0 || y < 0 || width < 0 || height < 0) {
			throw new IllegalArgumentException("Argument is negative!");
		}
		
		int[] subPixels = new int[width * height];
		
		for(int yy = 0; yy < height; ++yy) {
			for(int xx = 0; xx < width; ++xx) {
				subPixels[xx  + yy * width] = pixels[xx + x  + (yy + y) * this.width];
			}
		}
		
		return new Bitmap(subPixels, width, height);
	}
	
	/**
	 * Draw bitmap on canvas. Draw only the area that falls on canvas
	 * @param data - data to drawing
	 * @param x - x-coordinate of image on the screen
	 * @param y - y-coordinate of image on the screen
	 * @param width - width of the image on screen
	 * @param height - height of the image on screen
	 * 
	 * @throws IllegalArgumentException if width or height is negative
	 */
	public void draw(Bitmap data, double x, double y, double width, double height) {
		if(data == null || data == this) {
			return;
		}
		
		if(width < 0 || height < 0) {
			throw new IllegalArgumentException("Argument is negative(width or height)!");
		}
		
		final double multiperX = data.width / width;
		final double multiperY = data.height / height;
		
		final int[] canvasPixels = this.pixels;
		final int[] dataPixels = data.pixels;
		
		final int drawEndX = (int)((width + x) > (this.width) ? (this.width - x) : (width));
		final int drawEndY = (int)((height + y) > (this.height) ? (this.height - y) : (height));
		
		final int drawStartX = (int)x;
		final int drawStartY = (int)y;
		
		for(int yy = 0; yy < drawEndY; ++yy) {
			
			final int texStripeY = (int) (yy * multiperY) * data.width;
			
			if((drawStartY + yy) >= 0) {
				for(int xx = 0; xx < drawEndX; ++xx) {	
					if((drawStartX + xx) >= 0) {
						int texX = (int) (xx * multiperX);
						int color = dataPixels[texX + texStripeY];
						if(color != TRANSPARENT_COLOR) {
							canvasPixels[drawStartX + xx + (drawStartY + yy) * this.width] = color;
						} else {
						}
					}
				}
			}
		}
	}
	
	public void draw_SC(Bitmap data, double x, double y, double width, double height) {
		draw(data, x * this.width, y * this.height, width * this.width, height * this.height);
	}
	
	public void drawStencil_SC(Bitmap data, double x, double y, double width, double height, int color) {		
		if(data == null || data == this) {
			return;
		}
		
		if(width < 0 || height < 0) {
			throw new IllegalArgumentException("Argument is negative(width or height)!");
		}
		
		final double multiperX = data.width / width;
		final double multiperY = data.height / height;
		
		final int[] canvasPixels = this.pixels;
		final int[] dataPixels = data.pixels;
		
		final int drawEndX = (int)((width + x) > (this.width) ? (this.width - x) : (width));
		final int drawEndY = (int)((height + y) > (this.height) ? (this.height - y) : (height));
		
		final int drawStartX = (int)x;
		final int drawStartY = (int)y;
		
		for(int yy = 0; yy < drawEndY; ++yy) {
			
			final int texStripeY = (int) (yy * multiperY) * data.width;
			
			if((drawStartY + yy) >= 0) {
				for(int xx = 0; xx < drawEndX; ++xx) {	
					if((drawStartX + xx) >= 0) {
						int texX = (int) (xx * multiperX);
						int _color = dataPixels[texX + texStripeY];
						
						if(_color != TRANSPARENT_COLOR) canvasPixels[drawStartX + xx + (drawStartY + yy) * this.width] = color;
					}
				}
			}
		}
	}
	
	public void fillRect(int color, double x, double y, double width, double height) {
		fillRect(color, (int)x, (int)y, (int)width, (int)height);
	}
	
	public void fillRect(int color, int x, int y, int width, int height) {
		if(color == TRANSPARENT_COLOR) return;

		if(x >= this.width || y >= this.height) return;
		
		x = x > 0 ? x : -x;
		y = y > 0 ? y : -y;
		
		if(x + width > this.width) {
			width = this.width - x;
		}
		
		if(y + height > this.height) {
			height = this.height - y;
		}
		
		for(int yy = 0; yy < height; ++yy) {
			int stripe = (yy + y) * this.width;
			Arrays.fill(pixels, stripe + x, stripe + x + width, color);
			/*for(int xx = x; xx < width; ++xx) {
				pixels[xx + stripe] = color;
			}*/
		}
	}
	
	public void fillRect_SC(int color, double x, double y, double width, double height) {
		fillRect(color, (int)(x * this.width), (int)(y * this.height), (int)(width * this.width), (int)(height * this.height));
	}
	
	public void drawRect(int color, int x, int y, int width, int height, int lineThickness) {
		int ix = x;
		int iy = y;
		int iw = width;
		int ih = height;
		int ilt = lineThickness;
		
		fillRect(color, ix, iy, ilt, ih);
		fillRect(color, ix, iy, iw, ilt);
		
		fillRect(color, ix + iw - ilt, iy, ilt, ih);
		fillRect(color, ix, iy + ih - ilt, iw, ilt);
	}	
	
	public void drawRect_SC(int color, double x, double y, double width, double height, double lineThickness) {
		int ix = (int) (x * this.width);
		int iy = (int) (y * this.height);
		int iw = (int) (width* this.width);
		int ih = (int) (height * this.height);
		int ilt = (int) (lineThickness * this.width);
		
		fillRect(color, ix, iy, ilt, ih);
		fillRect(color, ix, iy, iw, ilt);
		
		fillRect(color, ix + iw - ilt, iy, ilt, ih);
		fillRect(color, ix, iy + ih - ilt, iw, ilt);
	}
	
	public void drawBackground(Bitmap data) {
		double x = 0, y = 0, width = this.width, height = this.height;
		
		if(data == null || data == this) {
			return;
		}
		
		if(width < 0 || height < 0) {
			throw new IllegalArgumentException("Argument is negative(width or height)!");
		}
		
		final double multiperX = data.width / width;
		final double multiperY = data.height / height;
		
		final int[] canvasPixels = this.pixels;
		final int[] dataPixels = data.pixels;
		
		final int drawEndX = (int)((width + x) > (this.width) ? (this.width - x) : (width));
		final int drawEndY = (int)((height + y) > (this.height) ? (this.height - y) : (height));
		
		final int drawStartX = (int)x;
		final int drawStartY = (int)y;
		
		for(int yy = 0; yy < drawEndY; ++yy) {
			
			final int texStripeY = (int) (yy * multiperY) * data.width;
			
			if((drawStartY + yy) >= 0) {
				for(int xx = 0; xx < drawEndX; ++xx) {	
					if((drawStartX + xx) >= 0) {
						int texX = (int) (xx * multiperX);
						int color = dataPixels[texX + texStripeY];
						
						if(color != TRANSPARENT_COLOR) {							
							int r = (color & 0xff0000) >> 16;
							int g = (color & 0xff00) >> 8;
							int b = (color & 0xff);
							
							r >>= 2;
							g >>= 2;
							b >>= 2;
							
							canvasPixels[drawStartX + xx + (drawStartY + yy) * this.width] = (r << 16) + (g << 8) + b;
						} else {
						}
					}
				}
			}
		}
	}

	public void drawLine_SC(int color, double x0, double y0, double x1, double y1) {
		drawLine(color, (int)(width * x0), (int)(height * y0),  (int)(width * x1), (int)(height * y1));
	}
	
	public void drawLine(int color, int x1, int y1, int x2, int y2) {
		if(x1 < 0) {
			x1 = 0;
		} else if(x1 >= width) {
			x1 = width - 1;
		}
		
		if(x2 < 0) {
			x2 = 0;
		} else if(x2 >= width) {
			x2 = width - 1;
		}
		
		if(y1 < 0) {
			y1 = 0;
		} else if(y1 >= height) {
			y1 = height - 1;
		}
		
		if(y2 < 0) {
			y2 = 0;
		} else if(y2 >= height) {
			y2 = height - 1;
		}
		
		if ((x1 == x2) && (y1 == y2)) {           
			pixels[x1 + y1 * width] = color;
		} else {              
			int dx = Math.abs(x2 - x1);
		    int dy = Math.abs(y2 - y1);
		    int offset = dx - dy;

		    int ddx, ddy;

		    if(x1 < x2) {
		    	ddx = 1; 
		    } else {
		    	ddx = -1;
		    }
		    
		    if(y1 < y2) {
		    	ddy = 1;
		    } else {
		    	ddy = -1;
		    }

		    while((x1 != x2) || (y1 != y2)) {
		        int p = 2 * offset;

		        if(p > -dy) {
		        	offset = offset - dy;
		            x1 = x1 + ddx;
		        }
		        
		        if(p < dx) {
		        	offset = offset + dx;
		            y1 = y1 + ddy;
		        }
		        
		        pixels[x1 + y1 * width] = color;
		    }
		} 
	}
	
	/**
	 * No comments
	 */
	public void setPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}
	
	/**
	 * No comments
	 */
	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}
	
	public Bitmap copy() {
		int[] pixels = new int[this.pixels.length];
		
		System.arraycopy(this.pixels, 0, pixels, 0, pixels.length);
		
		return new Bitmap(pixels, width, height);
	}

	public double getPixelScSize() {
		return 1.0 / Math.min(width, height);
	}


	@Override
	public String toString() {
		return getClass().getName() + ": " + width + ", " + height;
	}
}
