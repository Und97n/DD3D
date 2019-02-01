package org.zizitop.pshell.utils;

/**
 * Smart class for TPS counting.<br>
 * Make ticks and receive ticks per second, if needed time is passed.
 * <br><br>
 * Created 20.02.2018 23:37:24 
 * @author Zizitop
 */
public class TPSCounter {
	private double lastFPSCheckTime = Double.NaN;
	private int framesCounter = 0;
	
	private final double delay;
	private final String name;
	
	public TPSCounter(String name) {
		this(1, name);
	}
	
	public TPSCounter(double delay, String name) {
		this.delay = delay;
		this.name = name;
	}
	
	public void tickAndPrint() {
		double ret = tick();
		
		if(ret == ret) {
			System.out.println(name + ": " + (int)(ret * 1000.0) / 1000 + " ticks per second.");
		}
	}
	
	/**
	 * @return NaN if not enough ticks, middle value if needed count of ticks is already proceeded
	 */
	public double tick() {
		double time2 = Utils.getTimeInSeconds();
		
		if(lastFPSCheckTime != lastFPSCheckTime) {
			lastFPSCheckTime = time2;
			return Double.NaN;
		}
		
		++framesCounter;
		
		if(time2 - lastFPSCheckTime >= delay) {
			double fps = framesCounter / (time2 - lastFPSCheckTime);
			
			lastFPSCheckTime = time2;
			
			framesCounter = 0;
			
			return fps;
		}
		
		return Double.NaN;
	}
}
