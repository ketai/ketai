/*
 * 
 */
package ketai.ui;

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
	public static void popup(Activity _parent, String _title, String _message) {
		final Activity parent = _parent;
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
