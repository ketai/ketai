/*
 * 
 */
package ketai.ui;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * The KetaiKeyboard class allows the soft keyboard to be managed.
 */
public class KetaiKeyboard {

	/**
	 * Toggle.
	 *
	 * @param parent the PApplet/sketch 
	 */
	static public void toggle(Activity parent) {

		InputMethodManager imm = (InputMethodManager) parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.toggleSoftInput(0, 0);
	}

	/**
	 * Show.
	 *
	 * @param parent the PApplet/sketch 
	 */
	static public void show(Activity parent) {
		InputMethodManager imm = (InputMethodManager) parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(parent.getCurrentFocus(), 0);
	}

	/**
	 * Hide.
	 *
	 * @param parent the PApplet/sketch 
	 */
	static public void hide(Activity parent) {
		InputMethodManager imm = (InputMethodManager) parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(),
				0);

	}

}
