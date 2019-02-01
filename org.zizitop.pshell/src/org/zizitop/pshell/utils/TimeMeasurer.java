package org.zizitop.pshell.utils;

/**
 * Cool class for time counting.<br>
 * Count some count of ticks and receive middle delay between ticks.
 * <br><br>
 * Created 20.02.2018 23:31:30 
 * @author Zizitop
 */
public class TimeMeasurer {
	private double lastValue = Double.NaN;
	private double[] data;
	private int pointer = 0;
	
	private final String name;
	
	public TimeMeasurer(int valuesCount, String name) {
		this.data = new double[valuesCount];
		this.name = name;
	}
	
	public void startTick() {
		lastValue = Utils.getTimeInSeconds();
	}
	
	/**
	 * @return NaN if not enough data, middle value if needed count of ticks is already proceeded
	 */
	public double endTick() {

		double time2 = Utils.getTimeInSeconds();

		if(lastValue != lastValue) {
			return Double.NaN;
		}

		data[pointer++] = time2 - lastValue;

		lastValue = Double.NaN;

		if(pointer >= data.length) {
			double middle = 0;

			for(int i = 0; i < data.length; i++) {
				middle += data[i];
			}

			middle /= pointer;
			pointer = 0;
			
			return middle;
		}
		
		return Double.NaN;
	}
	
	public void endTickAndPrint() {
		double ret = endTick();
		
		if(ret == ret) {
			System.out.println(name + " counter value: " + (int)(ret * 1000000.0) + " microseconds.");
		}
	}
}
