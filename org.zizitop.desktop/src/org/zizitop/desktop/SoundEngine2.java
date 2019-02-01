package org.zizitop.desktop;

import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.resources.SoundEngine;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.ResourceManager;
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
class SoundEngine2 implements SoundEngine {
	private static final int INSTANCES_COUNT = 8;

	protected final SoundV2 nullSound = new SoundV2(null, null, null, Sound.SoundType.Effects);
	protected float mainVolume = 0.8f;
	protected List<SoundV2> sounds = new ArrayList<>();

	protected class SoundV2 extends Sound {
		public AudioFormat af;
		public byte[] audio;
		public DataLine.Info info;
		public SoundInstance[] instances;
		public SoundType type;

		public SoundV2(AudioFormat af, DataLine.Info info, byte[] audio, SoundType type) {
			this.af = af;
			this.info = info;
			this.audio = audio;
			this.type = type;
		}

		@Override
		public void play() {
			if(instances == null) {
				instances = new SoundInstance[INSTANCES_COUNT];

				for(int i = 0; i < INSTANCES_COUNT; ++i) {
					try {
						instances[i] = new SoundInstance(this);
					} catch (LineUnavailableException e) {
						Log.writeException(e);
					}
				}
			}

			int maxFramePos = 0;
			int firstPlayed = 0;

			for(int i = 0; i < INSTANCES_COUNT - 1; ++i) {
				if(!instances[i].clip.isRunning()) {
					instances[i].play();

					return;
				} else if(instances[i].clip.getFramePosition() >= maxFramePos) {
					firstPlayed = i;
					maxFramePos = instances[i].clip.getFramePosition();
				}
			}

			instances[firstPlayed].play();
		}

		@Override
		public void stop() {
			try {
				for(int i = 0; i < INSTANCES_COUNT; ++i) {
					instances[i].stop();
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void updateVolume() {
			try {
				for(int i = 0; i < INSTANCES_COUNT; ++i) {
					instances[i].updateVolume();
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void freeResources() {
			for(int i = 0; i < INSTANCES_COUNT; ++i) {
				instances[i].freeResources();
			}
		}
	}

	@Override
	public void updateVolume(SoundType updated) {
		for(SoundV2 s: sounds) {
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

			AudioFormat af = audioIn.getFormat();
			int size = (int) (af.getFrameSize() * audioIn.getFrameLength());
			byte[] audio = new byte[size];
			DataLine.Info info = new DataLine.Info(Clip.class, af, size);
			audioIn.read(audio, 0, size);

			return new SoundV2(af, info, audio, type);
		} catch (Exception e) {
			throw new FileLoadingException("Problems with sound loading.", e, path);
		}
	}

	protected class SoundInstance implements ResourceManager {
		public Clip clip;
		public FloatControl gainControl;
		public SoundV2 snd;

		public SoundInstance(SoundV2 snd) throws LineUnavailableException {
			this.snd = snd;

			clip = (Clip) AudioSystem.getLine(snd.info);
			clip.open(snd.af, snd.audio, 0, snd.audio.length);
			clip.addLineListener(new EndListener());

			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		}

		public void play() {
			try {
				clip.stop();
				updateVolume();
				clip.setFramePosition(0);
				clip.start();
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		public void stop() {
			try {
				clip.stop();
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		public void updateVolume() {
			try {
				float volume = (float) Math.abs(snd.type.getTypeVolume() * mainVolume);

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
				if(e.getType() == LineEvent.Type.STOP) {}
			}

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
