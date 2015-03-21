package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CompMangementFragment extends ListFragment {
	MainActivity mMainActivity = null;
	protected ListCompetitorAdapter mCompetitorAdapter;

	public void onActivityCreated(Bundle savedInstanceState) {
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);		
		
		mMainActivity = ((MainActivity) getActivity());
			
		if(getListAdapter() == null) {
			View listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitors_list, null);
			getListView().addHeaderView(listView);
		}
				
		mCompetitorAdapter = new ListCompetitorAdapter(mMainActivity, mMainActivity.competition.getCompetitors());
		setListAdapter(mCompetitorAdapter);				

		final EditText NameInput = (EditText) getView().findViewById(R.id.editCompetitorName);
		final EditText CardNumberInput = (EditText) getView().findViewById(R.id.editCardNumber);
		
        Button addCompetitorButton = (Button) getView().findViewById(R.id.addCompetitorButton);
        addCompetitorButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {				
				addCompetitor(NameInput.getText().toString(), CardNumberInput.getText().toString());
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
		
	public void addCompetitor(String CompetitorName, String CardNumber) {
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
		}else
		{
			mMainActivity.competition.addCompetitor(CompetitorName, Integer.parseInt(CardNumber));																					
			Toast.makeText(mMainActivity, "Competitor added: " + CompetitorName + ", " + CardNumber, Toast.LENGTH_SHORT).show();
		}	
	}
}