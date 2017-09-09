package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompetitorsRowView extends LinearLayout {

	Context mContext;
	TextView mName;
	TextView mCardNumber;
	TextView mTeam;
	TextView mCompetitorClass;
	TextView mStartNumber;
	TextView mStartGroup;
	Button mDeleteButton;
	Button mModifyButton;
	Button mCardButton;
	int mPosition;	
	CompetitorsListAdapter mAdapter;

	EditText mNameInput = null;
	EditText mCardNumberInput = null;
	EditText mTeamInput = null;
	EditText mCompetitorClassInput = null;
	EditText mStartNumberInput = null;
	EditText mStartGroupInput = null;
	private String competitorClassString;

	protected void init(Context context) {
		try{
			mContext = context;

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout mCompoundView = (LinearLayout) inflater.inflate(R.layout.competitor_row, this);			
			final LinearLayout competitorRowEssLayout = (LinearLayout) mCompoundView.findViewById(R.id.competitor_row_ess_layout);

			if (((MainActivity) mContext).competition.getCompetitionType() == Competition.SVART_VIT_TYPE)	{
				competitorRowEssLayout.setVisibility(View.GONE);
			} else {
				competitorRowEssLayout.setVisibility(View.VISIBLE);		
				mTeam = (TextView) mCompoundView.findViewById(R.id.competitor_team);
				mCompetitorClass = (TextView) mCompoundView.findViewById(R.id.competitor_class);
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
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public CompetitorsRowView(Context context) {
		super(context);
		init(context);
	}	

	public CompetitorsRowView(Context context, CompetitorsListAdapter adapter) {
		super(context);
		init(context);
		mAdapter = adapter;
	}

	public void setName(String name) {
		try{
			if (mName != null) {
				mName.setText(name);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void setCardNumber(String CardNumber) {
		try{
			if (mCardNumber != null) {
				mCardNumber.setText(CardNumber);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void setTeam(String Team) {
		try{
			if (mTeam != null) {
				mTeam.setText(Team);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}	

	public void setCompetitorClass(String competitorClass) {
		try{
			if (mCompetitorClass != null) {
				mCompetitorClass.setText(competitorClass);
			}
			competitorClassString = competitorClass;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}		

	public void setStartNumber(String startNumber) {
		try{
			if (mStartNumber != null) {
				mStartNumber.setText(startNumber);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}	

	public void setStartGroup(String startGroup) {
		try{
			if (mStartGroup != null) {
				mStartGroup.setText(startGroup);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}		

	public void setPosition(int Position) {
		try{
			mPosition = Position;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	private OnClickListener mOnDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				LayoutInflater li = LayoutInflater.from(mContext);
				View promptsView = li.inflate(R.layout.competitor_delete, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);	
				alertDialogBuilder.setTitle("Delete competitor");	   
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setPositiveButton("Delete", null);
				alertDialogBuilder.setNegativeButton("Cancel", null);				

				final AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {								
						((MainActivity) mContext).competition.getCompetitors().removeByCardNumber( Integer.parseInt( (String) mCardNumber.getText()) );		
						((MainActivity) mContext).competition.calculateResults();
						((MainActivity) mContext).updateFragments();
						AndroidHelper.saveSessionData(null,((MainActivity) mContext).competition);
						AndroidHelper.saveSessionData(((MainActivity) mContext).competition.getCompetitionName(),((MainActivity) mContext).competition);

						alertDialog.dismiss();
					}
				});		
			}
			catch( Exception e1){
				MainActivity.generateErrorMessage(e1);
			}
		}
	};

	private OnClickListener mOnModifyClickListener = new OnClickListener() {
		private CheckBox damClassCheckBox;

		@Override
		public void onClick(View v) {
			try{
				LayoutInflater li = LayoutInflater.from(mContext);			
				View promptsView = li.inflate(R.layout.competitor_modify, null);

				final LinearLayout modifyCompetitorEssLayout = (LinearLayout) promptsView.findViewById(R.id.modify_competitor_ess_layout);

				if (((MainActivity) mContext).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {				
					modifyCompetitorEssLayout.setVisibility(View.GONE);	
					
				} else {
					modifyCompetitorEssLayout.setVisibility(View.VISIBLE);	
				}

				if (((MainActivity) mContext).competition.getCompetitionType() == Competition.ESS_TYPE) {
					mTeamInput = (EditText) promptsView.findViewById(R.id.team_input);	
					mTeamInput.setText(mTeam.getText());

					mCompetitorClassInput = (EditText) promptsView.findViewById(R.id.class_input);	
					mCompetitorClassInput.setText(mCompetitorClass.getText());

					mStartNumberInput = (EditText) promptsView.findViewById(R.id.start_number_input);	
					mStartNumberInput.setText(mStartNumber.getText());	

					mStartGroupInput = (EditText) promptsView.findViewById(R.id.start_group_input);	
					mStartGroupInput.setText(mStartGroup.getText());						
				}


				mNameInput = (EditText) promptsView.findViewById(R.id.name_input);	
				mNameInput.setText(mName.getText());

				mCardNumberInput = (EditText) promptsView.findViewById(R.id.card_number_input);	
				mCardNumberInput.setText(mCardNumber.getText());
				
				if (((MainActivity) mContext).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
					damClassCheckBox = (CheckBox) promptsView.findViewById(R.id.damklass_checkbox_modify);	
					damClassCheckBox.setChecked(competitorClassString.equalsIgnoreCase("dam"));
					damClassCheckBox.setVisibility(View.VISIBLE);
					damClassCheckBox.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
						}
					});	
				}

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);	
				alertDialogBuilder.setTitle("Modify competitor");
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setPositiveButton("Modify", null);
				alertDialogBuilder.setNegativeButton("Cancel", null);	

				final AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if ((mNameInput.length() == 0) || (mCardNumberInput.length() == 0)) {
							Toast.makeText((MainActivity) mContext, "All data must be entered", Toast.LENGTH_LONG).show();
							return;
						} else if ((((MainActivity) mContext).competition.getCompetitionType() == Competition.ESS_TYPE) &&
								((mTeamInput.length() == 0) || (mCompetitorClassInput.length() == 0) || (mStartNumberInput.length() == 0) || (mStartGroupInput.length() == 0))) {
							Toast.makeText((MainActivity) mContext, "All data must be entered", Toast.LENGTH_LONG).show();
							return;
						} else if (!mName.getText().toString().equalsIgnoreCase(mNameInput.getText().toString()) && 
								(((MainActivity) mContext).competition.getCompetitors().checkIfNameExists(mNameInput.getText().toString()))) {
							Toast.makeText((MainActivity) mContext, "Name already exists", Toast.LENGTH_LONG).show();
							return;
						} else if (!mCardNumber.getText().toString().equalsIgnoreCase(mCardNumberInput.getText().toString()) &&
								(((MainActivity) mContext).competition.getCompetitors().checkIfCardNumberExists(Integer.parseInt(mCardNumberInput.getText().toString())))) {							
							Toast.makeText((MainActivity) mContext, "Card number already exists", Toast.LENGTH_LONG).show();
							return;
						} else if ((((MainActivity) mContext).competition.getCompetitionType() == Competition.ESS_TYPE) && 
								(!mStartNumber.getText().toString().equalsIgnoreCase(mStartNumberInput.getText().toString()) &&
										((MainActivity) mContext).competition.getCompetitors().checkIfStartNumberExists(Integer.parseInt(mStartNumberInput.getText().toString())))) {
							Toast.makeText((MainActivity) mContext, "Start number already exists", Toast.LENGTH_LONG).show();
							return;
						}		            	 

						String status = "";

						if (((MainActivity) mContext).competition.getCompetitionType() == Competition.ESS_TYPE) {
							mTeam.setText(mTeamInput.getText());	
							mCompetitorClass.setText(mCompetitorClassInput.getText());
							mStartNumber.setText(mStartNumberInput.getText());
							mStartGroup.setText(mStartGroupInput.getText());

							status = ((MainActivity) mContext).competition.getCompetitors().update( 
									mNameInput.getText().toString(), 
									Integer.parseInt(mCardNumber.getText().toString()),
									Integer.parseInt(mCardNumberInput.getText().toString()), 
									mTeam.getText().toString(), 
									mCompetitorClass.getText().toString(), 
									mStartNumber.getText().toString(), 
									mStartGroup.getText().toString());
						} else {
							String compClass = "";
							if( damClassCheckBox.isChecked()){
								compClass = "dam";
							}
							status = ((MainActivity) mContext).competition.getCompetitors().update( 
									mNameInput.getText().toString(),
									Integer.parseInt(mCardNumber.getText().toString()),
									Integer.parseInt(mCardNumberInput.getText().toString()),
									"",
									compClass,
									"-1",
									"-1");	
						}
						mName.setText(mNameInput.getText());
						mCardNumber.setText(mCardNumberInput.getText());	
						((MainActivity) mContext).competition.calculateResults();
						((MainActivity) mContext).updateFragments();
						AndroidHelper.saveSessionData(null,((MainActivity) mContext).competition);
						AndroidHelper.saveSessionData(((MainActivity) mContext).competition.getCompetitionName(),((MainActivity) mContext).competition);

						Toast.makeText((MainActivity) mContext, status, Toast.LENGTH_LONG).show();

						alertDialog.dismiss();
					}

				});		
			}
			catch( Exception e1){
				MainActivity.generateErrorMessage(e1);
			}
		}
	};

	private OnClickListener mOnPunchClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((MainActivity)mContext).listPunches(Integer.parseInt(mCardNumber.getText().toString()));
		}
	};
}
