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
	TextView mTeam;
	TextView mClass;
	TextView mStartNumber;
	TextView mStartGroup;
	Button mDeleteButton;
	Button mModifyButton;
	Button mCardButton;
	int mPosition;
	LinearLayout mCompoundView;
	ListCompetitorAdapter mAdapter;
	
	EditText mTeamInput = null;
	EditText mClassInput = null;
	EditText mStartNumberInput = null;
	EditText mStartGroupInput = null;

	protected void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;

		if (((MainActivity) mContext).competition.getCompetitionType() == ((MainActivity) mContext).competition.SVARTVITT_TYPE)
		{
			mCompoundView = (LinearLayout) inflater.inflate(R.layout.competitor_row, this);
		}
		else
		{
			mCompoundView = (LinearLayout) inflater.inflate(R.layout.competitor_ess_row, this);
			mTeam = (TextView) mCompoundView.findViewById(R.id.competitor_team);
			mClass = (TextView) mCompoundView.findViewById(R.id.competitor_class);
			mStartNumber = (TextView) mCompoundView.findViewById(R.id.competitor_startnumber);
			mStartGroup = (TextView) mCompoundView.findViewById(R.id.competitor_startgroup);
		}				

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
		init(context);
	}	
	
	public CompetitorRowView(Context context, ListCompetitorAdapter Adapter) {
		super(context);
		try{		
			init(context);
			mAdapter = Adapter;
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

	public void setTeam(String Team) {
		try{
			if (mTeam != null) {
				mTeam.setText(Team);
			}
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}	
	
	public void setClass(String Class) {
		try{
			if (mClass != null) {
				mClass.setText(Class);
			}
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}		
	
	public void setStartNumber(String StartNumber) {
		try{
			if (mStartNumber != null) {
				mStartNumber.setText(StartNumber);
			}
		}
		catch( Exception e){
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");	
		}
	}	
	
	public void setStartGroup(String StartGroup) {
		try{
			if (mStartGroup != null) {
				mStartGroup.setText(StartGroup);
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
				View promptsView = li.inflate(R.layout.competitor_delete, null);
	
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
				
				View promptsView;
				
				if (((MainActivity) mContext).competition.competitionType == ((MainActivity) mContext).competition.SVARTVITT_TYPE)
				{				
					promptsView = li.inflate(R.layout.competitor_modify, null);
				}
				else
				{
					promptsView = li.inflate(R.layout.competitor_modify_ess, null);
				}
	
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);	
				alertDialogBuilder.setView(promptsView);
	
				final EditText NameInput = (EditText) promptsView.findViewById(R.id.name_input);	
				NameInput.setText(mName.getText());
	
				final EditText CardNumberInput = (EditText) promptsView.findViewById(R.id.card_number_input);	
				CardNumberInput.setText(mCardNumber.getText());
					
				if (((MainActivity) mContext).competition.competitionType == ((MainActivity) mContext).competition.ESS_TYPE)
				{
					mTeamInput = (EditText) promptsView.findViewById(R.id.team_input);	
					mTeamInput.setText(mTeam.getText());
					
					mClassInput = (EditText) promptsView.findViewById(R.id.class_input);	
					mClassInput.setText(mClass.getText());
					
					mStartNumberInput = (EditText) promptsView.findViewById(R.id.start_number_input);	
					mStartNumberInput.setText(mStartNumber.getText());	
					
					mStartGroupInput = (EditText) promptsView.findViewById(R.id.start_group_input);	
					mStartGroupInput.setText(mStartGroup.getText());						
				}
				
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Modify",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										mName.setText(NameInput.getText());
										mCardNumber.setText(CardNumberInput.getText());	
										
										if (((MainActivity) mContext).competition.competitionType == ((MainActivity) mContext).competition.ESS_TYPE)
										{
											mTeam.setText(mTeamInput.getText());	
											mClass.setText(mClassInput.getText());
											mStartNumber.setText(mStartNumberInput.getText());
											mStartGroup.setText(mStartGroupInput.getText());
											
											//todo
											//om man ändrar namn, kortnumret eller startnumret så kollas inte det om det redan existerar
											((MainActivity) mContext).competition.updateCompetitorEss(mPosition, mName.getText().toString(), mCardNumber.getText().toString(), 
														mTeam.getText().toString(), mClass.getText().toString(), mStartNumber.getText().toString(), mStartGroup.getText().toString());
										}
										else
										{
											//todo											
											//om man ändrar kortnumret så kollas inte det om det redan existerar
											((MainActivity) mContext).competition.updateCompetitor(mPosition, mName.getText().toString(),mCardNumber.getText().toString());	
										}																		
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
