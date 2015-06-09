package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PunchRowView extends LinearLayout {
	
	Context mContext;
	TextView mControl;
	TextView mTime;
	Button mDelete;
	Button mModify;
	int mPosition;
	LinearLayout mCompoundView;

	protected void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
			alertDialogBuilder.setTitle("Delete punch");
			alertDialogBuilder.setView(promptsView);
			alertDialogBuilder.setPositiveButton("Delete", null);
			alertDialogBuilder.setNegativeButton("Cancel", null);		
			
			final AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {	
	            	((PunchActivity) mContext).removePunch(mPosition);														
					alertDialog.dismiss();
	            }
			});			
		}	
	};

	private OnClickListener mOnModifyClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.punch_modify, null);			

			final Spinner spinner = (Spinner) promptsView.findViewById(R.id.add_punch_controls_spinner);	
	        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ((PunchActivity)mContext).getControls());
	        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
	        spinner.setAdapter(LTRadapter);			
	        spinner.setSelection(((PunchActivity)mContext).getControls().indexOf(mControl.getText()));	
			
			final EditText timeInput = (EditText) promptsView.findViewById(R.id.time_input);
			timeInput.setText(mTime.getText());

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setTitle("Modify punch");
			alertDialogBuilder.setView(promptsView);
			alertDialogBuilder.setPositiveButton("Modify", null);
			alertDialogBuilder.setNegativeButton("Cancel", null);
			
			final AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	if (timeInput.length() == 0) {
	            		Toast.makeText(mContext, "No time was supplied", Toast.LENGTH_LONG).show();
	            		return;            		 
	            	}	            	
	            	
	            	mControl.setText(spinner.getSelectedItem().toString());
	            	mTime.setText(timeInput.getText());
					((PunchActivity) mContext).updatePunch(mPosition, Long.valueOf(mControl.getText().toString()), Long.valueOf(mTime.getText().toString()));
					
					alertDialog.dismiss();
	            }
			});								
		}
	};
}
