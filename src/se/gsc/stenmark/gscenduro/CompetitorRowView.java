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

public class CompetitorRowView extends LinearLayout {
	Context mContext;
	TextView mName;
	TextView mCardNumber;
	Button mDeleteButton;
	Button mModifyButton;
	Button mCardButton;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;

		mCompoundView = (LinearLayout) inflater.inflate(R.layout.competitor_row, this);

		mName = (TextView) mCompoundView.findViewById(R.id.competitor_name);
		mCardNumber = (TextView) mCompoundView.findViewById(R.id.competitor_cardnumber);
		mDeleteButton = (Button) mCompoundView.findViewById(R.id.competitor_delete);
		mModifyButton = (Button) mCompoundView.findViewById(R.id.competitor_modify);
		mCardButton = (Button) mCompoundView.findViewById(R.id.competitor_punch);

		mDeleteButton.setOnClickListener(mOnDeleteClickListener);
		mModifyButton.setOnClickListener(mOnModifyClickListener);
		mCardButton.setOnClickListener(mOnPunchClickListener);
	}

	public CompetitorRowView(Context context) {
		super(context);
		try{		
			Init(context);
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}

	public void setName(String Name) {
		try{
			if (mName != null) {
				mName.setText(Name);
			}
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}

	public void setCardNumber(String CardNumber) {
		try{
			if (mCardNumber != null) {
				mCardNumber.setText(CardNumber);
			}
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}

	public void setPosition(int Position) {
		try{
			mPosition = Position;
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}		
	}

	private OnClickListener mOnDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				LayoutInflater li = LayoutInflater.from(mContext);
				View promptsView = li.inflate(R.layout.delete_competitor, null);
	
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);	
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										((MainActivity) mContext).competition.removeCompetitor((String) mName.getText());	
										((MainActivity) mContext).updateFragments();
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
			catch( Exception e){
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			}
		}
	};

	private OnClickListener mOnModifyClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				LayoutInflater li = LayoutInflater.from(mContext);
				View promptsView = li.inflate(R.layout.modify_competitor, null);
	
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						mContext);
	
				alertDialogBuilder.setView(promptsView);
	
				final EditText NameInput = (EditText) promptsView.findViewById(R.id.editTextNameInput);
	
				NameInput.setText(mName.getText());
	
				final EditText CardNumberInput = (EditText) promptsView.findViewById(R.id.editTextCardNumberInput);
	
				CardNumberInput.setText(mCardNumber.getText());
	
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Modify",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										mName.setText(NameInput.getText());
										mCardNumber.setText(CardNumberInput.getText());	
										((MainActivity) mContext).competition.updateCompetitorCardNumber(mPosition, mName.getText().toString(),mCardNumber.getText().toString());
										((MainActivity) mContext).updateFragments();
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
			catch( Exception e){
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			}
		}
	};
	
	private OnClickListener mOnPunchClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				((MainActivity)mContext).listPunches(mPosition);
			}
			catch( Exception e){
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			}
		}
	};
}
