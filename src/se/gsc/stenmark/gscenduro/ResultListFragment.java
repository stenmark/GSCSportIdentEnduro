package se.gsc.stenmark.gscenduro;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListView;

public class ResultListFragment extends ListFragment {
	MainActivity mMainActivity = null;
	protected ListResultAdapter mResultsAdapter;
	protected ListResultLandscapeAdapter mResultLandscapeAdapter;
	ListView mListView = null;
	
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
		
		mListView = getListView();
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
