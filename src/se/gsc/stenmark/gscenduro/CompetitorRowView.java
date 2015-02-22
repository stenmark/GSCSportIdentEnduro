package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompetitorRowView extends LinearLayout {
	Context mContext;
	ListFragment mListFragment;
	TextView mName;
	TextView mCardNumber;
	TextView mDelete;
	TextView mModify;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context, ListFragment listFragment) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;
		mListFragment = listFragment;

		mCompoundView = (LinearLayout) inflater.inflate(
				R.layout.competitor_row, this);

		mName = (TextView) mCompoundView.findViewById(R.id.competitor_name);
		mCardNumber = (TextView) mCompoundView
				.findViewById(R.id.competitor_cardnumber);
		mDelete = (TextView) mCompoundView.findViewById(R.id.competitor_delete);
		mModify = (TextView) mCompoundView.findViewById(R.id.competitor_modify);

		mDelete.setOnClickListener(mOnDeleteClickListener);
		mModify.setOnClickListener(mOnModifyClickListener);
	}

	public CompetitorRowView(Context context, ListFragment listFragment) {
		super(context);
		Init(context, listFragment);
	}

	public void setName(String Name) {
		if (mName != null) {
			mName.setText(Name);
		}
	}

	public void setCardNumber(String CardNumber) {
		if (mCardNumber != null) {
			mCardNumber.setText(CardNumber);
		}
	}

	public void setPosition(int Position) {
		mPosition = Position;
	}

	private OnClickListener mOnDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.delete_competitor, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									((MainActivity) mContext).competition
											.removeCompetitor((String) mName
													.getText());									
									((MainActivity) mContext).updateFragments();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
	};

	private OnClickListener mOnModifyClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LayoutInflater li = LayoutInflater.from(mContext);
			View promptsView = li.inflate(R.layout.modify_competitor, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText NameInput = (EditText) promptsView
					.findViewById(R.id.editTextNameInput);

			NameInput.setText(mName.getText());

			final EditText CardNumberInput = (EditText) promptsView
					.findViewById(R.id.editTextCardNumberInput);

			CardNumberInput.setText(mCardNumber.getText());

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Modify",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mName.setText(NameInput.getText());
									mCardNumber.setText(CardNumberInput
											.getText());

									((MainActivity) mContext).competition
											.updateCompetitorCardNumber(
													mPosition, mName.getText()
															.toString(),
													mCardNumber.getText()
															.toString());
									((MainActivity) mContext).updateFragments();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
	};
}
