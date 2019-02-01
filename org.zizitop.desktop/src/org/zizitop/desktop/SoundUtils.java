package org.zizitop.desktop;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundUtils {
	public static float SAMPLE_RATE = 8000f;

	public static void beep() {
		beep(7000, 100);
	}

	public static void beep(int hz, int msecs) {
		beep(hz, msecs, 1.0);
	}

	public static void beep(int hz, int msecs, double vol) {
		Thread t = new Thread(() -> {
			byte[] buf = new byte[1];
			AudioFormat af = new AudioFormat(SAMPLE_RATE, // sampleRate
					8, // sampleSizeInBits
					1, // channels
					true, // signed
					false); // bigEndian
			SourceDataLine sdl;

			try {
				sdl = AudioSystem.getSourceDataLine(af);
				sdl.open(af);
			} catch (LineUnavailableException e) {
				e.printStackTrace(System.out);

				return;
			}

			sdl.start();

			for(int i = 0; i < msecs * 8; i++) {
				double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
				buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
				sdl.write(buf, 0, 1);
			}

			sdl.drain();
			sdl.stop();
			sdl.close();
		});

//		t.setDaemon(true);
		t.start();
	}
}