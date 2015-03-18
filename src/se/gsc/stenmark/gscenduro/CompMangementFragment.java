package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CompMangementFragment extends ListFragment {

	static CompMangementFragment mCompMangementFragment;
	MainActivity mMainActivity = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
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
		
        Button addCompetitorButton = (Button) getView().findViewById(R.id.addCompetitorButton);
        addCompetitorButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				addCompetitorAlert();
			}
		});		
		
		Button addMultiCompetitorButton = (Button) getView().findViewById(R.id.addMultiCompetitorButton);
		addMultiCompetitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(mMainActivity);
				View promptsView = li.inflate(R.layout.add_multi_competitors, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

				alertDialogBuilder.setView(promptsView);

				final EditText MultiCompetitorsInput = (EditText) promptsView.findViewById(R.id.editTextMultiCompetitorsInput);

				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Add",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {		
										try {
											String status = mMainActivity.competition.addMultiCompetitors(MultiCompetitorsInput.getText().toString());
											mMainActivity.updateFragments();
											
											AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
									        builder.setIcon(android.R.drawable.ic_dialog_alert);
									        builder.setMessage(status).setTitle("Add Multi Competitor Status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
									        {
									            public void onClick(DialogInterface dialog, int which) {}
									        });
									 
									        AlertDialog alert = builder.create();
									        alert.show();																					
										} catch (Exception e) {
											PopupMessage dialog1 = new PopupMessage(MainActivity.generateErrorMessage(e));
											dialog1.show(getFragmentManager(), "popUp");
										}																					
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();				
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
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CompMangementFragment getInstance(int sectionNumber) {
		mCompMangementFragment = new CompMangementFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		mCompMangementFragment.setArguments(args);
		return mCompMangementFragment;
	}
		
	public void addCompetitorAlert(){
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.add_competitor, null);
	
		final EditText NameInput = (EditText) promptsView.findViewById(R.id.editCompetitorName);
		final EditText CardNumberInput = (EditText) promptsView.findViewById(R.id.editCardNumber);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								addCompetitor(NameInput.getText().toString(), CardNumberInput.getText().toString());
								mMainActivity.updateFragments();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
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
			
		}else if (mMainActivity.competition.checkNameExists(CompetitorName))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setMessage("Name already exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	 
	        AlertDialog alert = builder.create();
	        alert.show();						
			
		}else if (mMainActivity.competition.checkCardNumberExists(Integer.parseInt(CardNumber)))
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