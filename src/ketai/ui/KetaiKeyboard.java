/*
 * 
 */
package ketai.ui;

import processing.core.PApplet;
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
	static public void toggle(PApplet parent) {

		InputMethodManager imm = (InputMethodManager) parent.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.toggleSoftInput(0, 0);
	}

	/**
	 * Show.
	 *
	 * @param parent the PApplet/sketch 
	 */
	static public void show(PApplet parent) {
		InputMethodManager imm = (InputMethodManager) parent.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(parent.getActivity().getCurrentFocus(), 0);
	}

	/**
	 * Hide.
	 *
	 * @param parent the PApplet/sketch 
	 */
	static public void hide(PApplet parent) {
		InputMethodManager imm = (InputMethodManager) parent.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(parent.getActivity().getCurrentFocus().getWindowToken(),
				0);

	}

}
