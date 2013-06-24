package com.aopds.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.aopds.AopdsActivity;
import com.aopds.R;

/**
 * 
 * @author Julien COUDSI
 * August 2011
 * Provides static methods to display dialogs
 */

public class AopdsDialog {

	/**
	 * Displays an alert dialog with an only button named "Ok"
	 * @param message
	 * @param activity
	 */
	
	public static void displayAlertOkDialog(String message, AopdsActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message)
		.setCancelable(false)
		.setNegativeButton(activity.getString(R.string.LABEL_OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void displayPendingSuggestionDialog(AopdsActivity activity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.LABEL_NO_PENDING_SUGGESTION)
		.setCancelable(false)
		.setNegativeButton(activity.getString(R.string.LABEL_OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
