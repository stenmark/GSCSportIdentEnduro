package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PunchRowView extends LinearLayout {
	
	Context mContext;
	TextView mControl;
	TextView mTime;
	Button mDelete;
	Button mModify;
	int mPosition;
	LinearLayout mCompoundView;

	protected void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;

		mCompoundView = (LinearLayout) inflater.inflate(R.layout.punch_row, this);

		mControl = (TextView) mCompoundView.findViewById(R.id.punch_control);
		mTime = (TextView) mCompoundView.findViewById(R.id.punch_time);
		mDelete = (Button) mCompoundView.findViewById(R.id.punch_delete);
		mModify = (Button) mCompoundView.findViewById(R.id.punch_modify);

		mDelete.setOnClickListener(mOnDeleteClickListener);
		mModify.setOnClickListener(mOnModifyClickListener);
	}

	public PunchRowView(Context context) {
		super(context);
		init(context);
	}

	public void setControl(String Control) {
		if (mControl != null) {
			mControl.setText(Control);
		}
	}

	public void setTime(String Time) {
		if (mTime != null) {
			mTime.setText(Time);
		}
	}

	public void setPosition(int Position) {
		mPosition = Position;
	}

	private OnClickListener mOnDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.punch_delete, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

			alertDialogBuilder.setView(promptsView);
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									((PunchListActivity) mContext).removePunch(mPosition);									
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	};

	private OnClickListener mOnModifyClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.punch_modify, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setView(promptsView);

			final EditText controlInput = (EditText) promptsView.findViewById(R.id.control_input);
			controlInput.setText(mControl.getText());

			final EditText timeInput = (EditText) promptsView.findViewById(R.id.time_input);
			timeInput.setText(mTime.getText());

			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Modify",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									mControl.setText(controlInput.getText());
									mTime.setText(timeInput.getText());
									((PunchListActivity) mContext).updatePunch(mPosition, Long.valueOf(mControl.getText().toString()), Long.valueOf(mTime.getText().toString()));
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();						
		}
	};
}
