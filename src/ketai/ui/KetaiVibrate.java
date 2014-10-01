/*
 * 
 */
package ketai.ui;

import processing.core.PApplet;
import android.content.Context;
import android.os.Vibrator;

/**
 * The KetaiVibrate Class allows access to the vibration motor service.
 */
public class KetaiVibrate {
	
	/** The parent. */
	private PApplet parent;
	
	/** The vibe. */
	private Vibrator vibe;

	/**
	 * Instantiates a new ketai vibrate.
	 *
	 * @param _parent the PApplet/sketch
	 */
	public KetaiVibrate(PApplet _parent) {
		parent = _parent;
		vibe = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);
	}

	/**
	 * Checks for vibrator.
	 *
	 * @return true, if successful
	 */
	public boolean hasVibrator() {
		return vibe.hasVibrator();
	}

	/**
	 * Vibrate.
	 */
	public void vibrate() {
		// forever! (well...almost)
		long[] pattern = { 0, Long.MAX_VALUE };
		vibe.vibrate(pattern, 0);
	}

	/**
	 * Vibrate.
	 *
	 * @param _duration the _duration in millis
	 */
	public void vibrate(long _duration) {
		vibe.vibrate(_duration);
	}

	/**
	 * Vibrate.
	 *
	 * @param pattern the pattern, off/on values in an array, in millis
	 * @param repeat the repeat
	 */
	public void vibrate(long[] pattern, int repeat) {
		vibe.vibrate(pattern, repeat);
	}

	/**
	 * Stop.
	 */
	public void stop() {
		vibe.cancel();
	}
}
