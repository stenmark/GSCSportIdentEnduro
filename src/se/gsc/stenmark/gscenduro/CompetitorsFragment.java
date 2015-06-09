package se.gsc.stenmark.gscenduro;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CompetitorsFragment extends ListFragment {

	private MainActivity mMainActivity = null;
	private CompetitorsListAdapter mCompetitorAdapter;
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
			if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.SVARTVITT_TYPE) {
				listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitors_list, null);
			} else {
				listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitors_list_ess, null);				
			}
			getListView().addHeaderView(listView);
		}
				
		mCompetitorAdapter = new CompetitorsListAdapter(mMainActivity, mMainActivity.competition.getCompetitors().getCompetitors());
		setListAdapter(mCompetitorAdapter);							
				
        Button addCompetitorButton = (Button) getView().findViewById(R.id.add_competitor_button);
        addCompetitorButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = "";
				
				mNameInput = (EditText) getView().findViewById(R.id.name_input);
				mCardNumberInput = (EditText) getView().findViewById(R.id.card_number_input);
				
				if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.ESS_TYPE) {
					mTeamInput = (EditText) getView().findViewById(R.id.team_input);
					mCompetitorClassInput = (EditText) getView().findViewById(R.id.class_input);
					mStartNumberInput = (EditText) getView().findViewById(R.id.start_number_input);
					mStartGroupInput = (EditText) getView().findViewById(R.id.start_group_input);

					status = mMainActivity.competition.getCompetitors().add(mNameInput.getText().toString(), 
			   		   													    mCardNumberInput.getText().toString(), 
			   		   													    mTeamInput.getText().toString(), 
			   		   													    mCompetitorClassInput.getText().toString(), 
			   		   													    mStartNumberInput.getText().toString(), 
			   		   													    mStartGroupInput.getText().toString(),
			   		   													    mMainActivity.competition.getCompetitionType());										
				} else {
					status = mMainActivity.competition.getCompetitors().add(mNameInput.getText().toString(), 
								    										mCardNumberInput.getText().toString(), 
								    										"", 
								    										"",
								    										"-1", 
								    										"-1",
								    										mMainActivity.competition.getCompetitionType());					
				}						
				mMainActivity.competition.calculateResults();
				mMainActivity.updateFragments();
				
				Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();
			}
		});		
			
		super.onActivityCreated(savedInstanceState);
	}
	
	public CompetitorsListAdapter getListCompetitorAdapter() {
		return mCompetitorAdapter;
	}		
			
	@Override
	public void onDestroyView() {
	    super.onDestroyView();

	    // free adapter
	    setListAdapter(null);
	}	
}