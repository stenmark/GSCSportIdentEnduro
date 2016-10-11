package se.gsc.stenmark.gscenduro;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ResultsFragment extends ListFragment {
	
	private MainActivity mMainActivity = null;
	private ResultsListAdapter mResultsAdapter;
	private ResultsListLandscapeAdapter mResultLandscapeAdapter;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		
		mMainActivity = ((MainActivity) getActivity());		 
		
		mResultsAdapter = new ResultsListAdapter(mMainActivity, mMainActivity.competition.getStages());	
		setListAdapter(mResultsAdapter);
		
		Configuration configuration = getResources().getConfiguration(); 						
		if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			mResultsAdapter = new ResultsListAdapter(mMainActivity, mMainActivity.competition.getStages());	
			setListAdapter(mResultsAdapter);
		} 
		else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			mResultLandscapeAdapter = new ResultsListLandscapeAdapter(mMainActivity, mMainActivity.competition);			
			setListAdapter(mResultLandscapeAdapter);
		}		
	}	

	public ResultsListAdapter getResultAdapter() {
		return mResultsAdapter;
	}	
	
	public ResultsListLandscapeAdapter getResultLandscapeAdapter() {
		return mResultLandscapeAdapter;
	}		
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();

	    // free adapter
	    setListAdapter(null);
	}	
}
