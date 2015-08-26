/*
 * 
 */
package ketai.ui;

import processing.core.PApplet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * The  KetaiAlertDialog.
 */
public class KetaiAlertDialog {

	/**
	 * Popup.
	 *
	 * @param _parent calling sketch/PApplet/Activity
	 * @param _title the title for the dialog
	 * @param _message the message inside of the dialog box
	 */
	public static void popup(PApplet _parent, String _title, String _message) {
		final Activity parent = _parent.getActivity();
		final String message = _message;
		final String title = _title;

		parent.runOnUiThread(new Runnable() {
			public void run() {
				new AlertDialog.Builder(parent)
						.setTitle(title)
						.setMessage(message)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		});

	}

}
