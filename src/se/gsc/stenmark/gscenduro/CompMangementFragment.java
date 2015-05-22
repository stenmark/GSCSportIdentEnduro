package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CompMangementFragment extends ListFragment {

	private MainActivity mMainActivity = null;
	private ListCompetitorAdapter mCompetitorAdapter;
	private EditText mNameInput;
	private EditText mCardNumberInput;
	private EditText mTeamInput;
	private EditText mCompetitorClassInput;
	private EditText mStartNumberInput;
	private EditText mStartGroupInput;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);		
		
		mMainActivity = ((MainActivity) getActivity());
			
		if(getListAdapter() == null) {
			View listView;
			if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.SVARTVITT_TYPE)
			{
				listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitors_list, null);
			}
			else
			{
				listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitors_list_ess, null);				
			}
			getListView().addHeaderView(listView);
		}
				
		mCompetitorAdapter = new ListCompetitorAdapter(mMainActivity, mMainActivity.competition.getCompetitors());
		setListAdapter(mCompetitorAdapter);							
				
        Button addCompetitorButton = (Button) getView().findViewById(R.id.add_competitor_button);
        addCompetitorButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {		
				mNameInput = (EditText) getView().findViewById(R.id.name_input);
				mCardNumberInput = (EditText) getView().findViewById(R.id.card_number_input);
				
				if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.ESS_TYPE)
				{
					mTeamInput = (EditText) getView().findViewById(R.id.team_input);
					mCompetitorClassInput = (EditText) getView().findViewById(R.id.class_input);
					mStartNumberInput = (EditText) getView().findViewById(R.id.start_number_input);
					mStartGroupInput = (EditText) getView().findViewById(R.id.start_group_input);
					
					addCompetitor(mNameInput.getText().toString(), mCardNumberInput.getText().toString(), 
								  mTeamInput.getText().toString(), mCompetitorClassInput.getText().toString(), mStartNumberInput.getText().toString(), mStartGroupInput.getText().toString());
				}
				else
				{
					addCompetitor(mNameInput.getText().toString(), mCardNumberInput.getText().toString(), "", "", "", "");	
				}
				
				
				mMainActivity.updateFragments();
			}
		});		
			
		super.onActivityCreated(savedInstanceState);
	}
	
	public ListCompetitorAdapter getListCompetitorAdapter() {
		return mCompetitorAdapter;
	}		
			
	@Override
	public void onDestroyView()
	{
	    super.onDestroyView();

	    // free adapter
	    setListAdapter(null);
	}	
		
	public void addCompetitor(String CompetitorName, String CardNumber, String CompetitorTeam, String CompetitorClass, String StartNumber, String StartGroup) {
				
		if((CompetitorName.length() == 0) && (CardNumber.length() == 0))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("Name and Card Number are empty").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();	
		
		}else if ((mMainActivity.competition.getCompetitionType() == mMainActivity.competition.ESS_TYPE) &&
	     		 ((CompetitorTeam.length() == 0) && (CompetitorClass.length() == 0) && (CompetitorTeam.length() == 0) && (StartGroup.length() == 0)))
	    {	        
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("All data ust be entered").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();		    
			
		}else if (CompetitionHelper.checkNameExists(mMainActivity.competition.getCompetitors(), CompetitorName))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("Name already exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();						
			
		}else if (CompetitionHelper.checkCardNumberExists(mMainActivity.competition.getCompetitors(), Integer.parseInt(CardNumber)))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("Card number exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();		
	        
		}else if (CompetitionHelper.checkStartNumberExists(mMainActivity.competition.getCompetitors(), Integer.parseInt(StartNumber)))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("Start number exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();	
	        
		}else
		{
			mMainActivity.competition.addCompetitor(CompetitorName, Integer.parseInt(CardNumber), CompetitorTeam, CompetitorClass, Integer.parseInt(StartNumber), Integer.parseInt(StartGroup));
			Toast.makeText(mMainActivity, "Competitor added: " + CompetitorName + ", " + CardNumber + ", " + CompetitorTeam + ", " + CompetitorClass, Toast.LENGTH_SHORT).show();
		}	
	}
}