package org.zizitop.pshell.utils.animation;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.Utils;

import java.util.Arrays;

/**
 * Tool for easy animation creating.
 * <br><br>
 * Created 24.03.2018 12:08
 *
 * @author Zizitop
 */
public class Animation {
	public final Bitmap[] frames;

	private final double step;
	private final boolean looped;

	/**
	 * Create new animation.
	 * @param frames - all frames in this animation.
	 * @param step - frame pointer changing per second
	 * @param looped - true, if animation must continue from begin after end
	 */
	public Animation(Bitmap[] frames, double step, boolean looped) {
		this.looped = looped;

		this.frames = Utils.copyArray(Bitmap.class, frames, 0, frames.length);

		ResourceLoader rl = ResourceLoader.getInstance();

		this.step = step;
	}

	/**
	 * Create new animation.
	 * @param identifier - path to animation frames.
	 * @param framesCount - count of frames in animation
	 * @param step - frame pointer changing per second
	 * @param looped - true, if animation must continue from begin after end
	 */
	public Animation(String identifier, int framesCount, double step, boolean looped) {
		this.looped = looped;

		frames = new Bitmap[framesCount];

		ResourceLoader rl = ResourceLoader.getInstance();

		for(int i = 0; i < framesCount; ++i) {
			frames[i] = rl.getTexture(identifier + "_" + i);
		}

		this.step = step;
	}

	public double restartAnimation() {
		if(step > 0) {
			return 0;
		} else {
			return step + frames.length;
		}
	}

	public double update(double pointer, double multiplier) {
		if(looped) {
			pointer += step * multiplier;

			if(pointer < 0) {
				pointer += frames.length;
			} else if((int)pointer >= frames.length) {
				pointer -= frames.length * (int)(pointer / frames.length);
			}
		} else {
			pointer += step * multiplier;

			if(pointer < 0) {
				pointer = 0;
			} else if((int)pointer >= frames.length) {
				pointer = frames.length - 1;
			}
		}

		return pointer;
	}

	public Bitmap getCurrentFrame(double pointer) {
		int p = (int)pointer;

		if(p < 0) {
			p = 0;
		} else if(p >= frames.length) {
			p = frames.length - 1;
		}

		return frames[p];
	}
}
