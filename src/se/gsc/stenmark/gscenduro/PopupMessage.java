package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PopupMessage extends DialogFragment {

	private String mMessage;
	private String mTitle = "";

	public PopupMessage() {
	}	
	
	public PopupMessage(String message) {
		this.mMessage = message;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (mTitle.length() > 0) {
			builder.setTitle(mTitle);
		}
		builder.setMessage(mMessage).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
