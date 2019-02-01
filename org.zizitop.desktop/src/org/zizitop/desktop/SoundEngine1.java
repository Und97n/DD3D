package org.zizitop.desktop;

import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.resources.SoundEngine;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.zizitop.pshell.resources.Sound.SoundType;

/**
 * 
 * @author Zizitop
 *
 */
class SoundEngine1 implements SoundEngine {
	protected final SoundV1 nullSound = new SoundV1(null, SoundType.Effects);
	protected float mainVolume = 1.0f;
	protected List<SoundV1> sounds = new ArrayList<>();

	protected class SoundV1 extends Sound {
		public final Clip clip;
		public final SoundType type;

		public FloatControl gainControl;

		public SoundV1(Clip clip, SoundType type) {
			this.clip = clip;
			this.type = type;

			if(clip != null) {
				this.clip.addLineListener(new EndListener());
				gainControl = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
			}
		}

		@Override
		public void play() {
			try {
				if (clip != null) {
					new Thread() {
						public void run() {
							synchronized (clip) {
								clip.stop();
								updateVolume();
								clip.setFramePosition(0);
								clip.start();
							}
						}
					}.start();
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void stop() {
			try {
				if (clip != null) {
					new Thread() {
						public void run() {
							synchronized (clip) {
								clip.stop();
							}
						}
					}.start();
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void updateVolume() {
			try {
				float volume = (float) Math.abs(type.getTypeVolume() * mainVolume);

				if (volume > 1) {
					volume = 1;
				} else if (volume < 0) {
					volume = 0;
				}

				float min = gainControl.getMinimum();
				float max = gainControl.getMaximum();

				gainControl.setValue((max - min) * volume + min);
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void freeResources() {
			clip.close();
		}

		private class EndListener implements LineListener {
			@Override
			public void update(LineEvent e) {
				if (e.getType() == LineEvent.Type.STOP) {

				}
			}

		}
	}

	@Override
	public void updateVolume(SoundType updated) {
		for(SoundV1 s: sounds) {
			if(s.type == updated) {
				s.updateVolume();
			}
		}
	}

	@Override
	public Sound loadSound(String path) throws FileLoadingException {
		try {
			SoundType type;
			
			// Bad method
			if (path.contains("sfx/menu")) {
				type = SoundType.MenuSound;
				
			} else if (path.contains("sfx/ambiend") || path.contains("sfx/musik")) {
				type = SoundType.Music;
			} else {
				type = SoundType.Effects;
			}

			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path));

			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);

			return new SoundV1(clip, type);
		} catch (Exception e) {
			throw new FileLoadingException("Problems with sound loading.", e, path);
		}
	}

	@Override
	public Sound getNullSound() {
		return nullSound;
	}

	@Override
	public double getMainVolume() {
		return mainVolume;
	}

	@Override
	public void setMainVolume(double newVolume) {
		mainVolume = (float) newVolume;
	}
}
