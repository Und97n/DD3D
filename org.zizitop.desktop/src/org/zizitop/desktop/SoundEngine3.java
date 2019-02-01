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
class SoundEngine3 implements SoundEngine {
	protected final SoundV2 nullSound = new SoundV2(null, null, null, Sound.SoundType.Effects);
	protected float mainVolume = 1f;
	protected List<SoundV2> sounds = new ArrayList<>();

	protected class SoundV2 extends Sound {
		public AudioFormat af;
		public byte[] audio;
		public DataLine.Info info;
		public SoundInstance lastSoundInstance;
		public SoundType type;

		public SoundV2(AudioFormat af, DataLine.Info info, byte[] audio, SoundType type) {
			this.af = af;
			this.info = info;
			this.audio = audio;
			this.type = type;
		}

		@Override
		public void play() {
			lastSoundInstance = new SoundInstance(lastSoundInstance, this);

			lastSoundInstance.start();
		}

		@Override
		public void stop() {
			try {
				SoundInstance si = lastSoundInstance;

				while (si != null) {
					si.stop();
					si = si.previousSoundInstance;
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void updateVolume() {
			try {
				SoundInstance si = lastSoundInstance;

				while (si != null) {

					si.updateVolume();

					si = si.previousSoundInstance;
				}
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		@Override
		public void freeResources() {
			lastSoundInstance.freeResources();
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
		public SoundInstance previousSoundInstance;
		public Clip localSound;
		public FloatControl gainControl;
		public SoundV2 snd;

		public SoundInstance(SoundInstance previousSoundInstance, SoundV2 snd) {
			this.previousSoundInstance = previousSoundInstance;
			this.snd = snd;
		}

		public void start() {
			try {
				localSound = (Clip) AudioSystem.getLine(snd.info);
				localSound.open(snd.af, snd.audio, 0, snd.audio.length);
				localSound.addLineListener(new EndListener());

				gainControl = (FloatControl) localSound.getControl(FloatControl.Type.MASTER_GAIN);

				updateVolume();

				localSound.start();
			} catch (Exception e) {
				Log.write("Problems with audio system.", 3);
				Log.writeException(e);
			}
		}

		public void stop() {
			localSound.stop();
		}

		public void updateVolume() {
			float volume = Math.abs(snd.type.getTypeVolume() * mainVolume);
			
			if(volume > 1) {
				volume = 1;
			} else if(volume < 0) {
				volume = 0;
			}

			float min = gainControl.getMinimum();
			float max = gainControl.getMaximum();
			
			gainControl.setValue((max - min) * volume + min);
		}

		@Override
		public void freeResources() {
			localSound.close();

			if(previousSoundInstance != null) {
				previousSoundInstance.freeResources();
			}
		}

		private class EndListener implements LineListener {
			@Override
			public void update(LineEvent e) {
				if(e.getType() == LineEvent.Type.STOP) {
					localSound.close();
					// If last sound is done, any previous sounds with this track is also
					// done
					previousSoundInstance = null;
				}
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
