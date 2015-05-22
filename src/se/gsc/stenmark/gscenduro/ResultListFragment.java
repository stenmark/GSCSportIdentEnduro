package se.gsc.stenmark.gscenduro;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ResultListFragment extends ListFragment {
	
	private MainActivity mMainActivity = null;
	private ListResultAdapter mResultsAdapter;
	private ListResultLandscapeAdapter mResultLandscapeAdapter;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		
		mMainActivity = ((MainActivity) getActivity());		 
		
		mResultsAdapter = new ListResultAdapter(mMainActivity, mMainActivity.competition.getResults());	
		setListAdapter(mResultsAdapter);
		
		Configuration configuration = getResources().getConfiguration(); 						
		if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			mResultsAdapter = new ListResultAdapter(mMainActivity, mMainActivity.competition.getResults());	
			setListAdapter(mResultsAdapter);
		} 
		else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			mResultLandscapeAdapter = new ListResultLandscapeAdapter(mMainActivity, mMainActivity.competition.getResultLandscape());			
			setListAdapter(mResultLandscapeAdapter);
		}		
	}	

	public ListResultAdapter getResultAdapter() {
		return mResultsAdapter;
	}	
	
	public ListResultLandscapeAdapter getResultLandscapeAdapter() {
		return mResultLandscapeAdapter;
	}		
	
	@Override
	public void onDestroyView()
	{
	    super.onDestroyView();

	    // free adapter
	    setListAdapter(null);
	}	
}
