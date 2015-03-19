package se.gsc.stenmark.gscenduro;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ResultListFragment extends ListFragment {
	
	static ResultListFragment mResultListFragment;
	MainActivity mMainActivity = null;
	protected ListResultAdapter mResultsAdapter;
	protected ListResultLandscapeAdapter mResultLandscapeAdapter;
	
	private static final String ARG_SECTION_NUMBER = "section_number";

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
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ResultListFragment getInstance(int sectionNumber) {
		mResultListFragment = new ResultListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		mResultListFragment.setArguments(args);
		return mResultListFragment;
	}		
}
