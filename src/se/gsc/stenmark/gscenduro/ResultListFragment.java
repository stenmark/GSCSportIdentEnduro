package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ResultListFragment extends ListFragment {
	
	static ResultListFragment mResultListFragment;
	MainActivity mMainActivity = null;
	protected ListResultAdapter mResultsAdapter;
	protected ListResultLandscapeAdapter mResultLandscapeAdapter;
	protected List<Result> mResults = new ArrayList<Result>();
	protected List<ResultLandscape> mResultLandscape = new ArrayList<ResultLandscape>();
	
	private static final String ARG_SECTION_NUMBER = "section_number";

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		
		mMainActivity = ((MainActivity) getActivity());		 
		
		Configuration configuration = getResources().getConfiguration(); 						
		if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			for (Result result : mMainActivity.competition.getResults()) {
				mResults.add(result);
			}
			mResultsAdapter = new ListResultAdapter(mMainActivity, mResults);	
			setListAdapter(mResultsAdapter);
		} 
		else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			for (ResultLandscape resultLandscape : mMainActivity.competition.getResultLandscape()) {
				mResultLandscape.add(resultLandscape);
			}	
			mResultLandscapeAdapter = new ListResultLandscapeAdapter(mMainActivity, mResultLandscape);			
			setListAdapter(mResultLandscapeAdapter);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
						
		ReloadData();
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
	
	public void ReloadData() {
		Configuration configuration = getResources().getConfiguration(); 			
		if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
		{	
			mResults.clear();
			
			for (Result result : mMainActivity.competition.getResults()) {
				mResults.add(result);
			}
						
			mResultsAdapter.notifyDataSetChanged();
		} 
		else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{			
			mResultLandscape.clear();
			
			for (ResultLandscape resultLandscape : mMainActivity.competition.getResultLandscape()) {
				mResultLandscape.add(resultLandscape);
			}			
	
			mResultLandscapeAdapter.notifyDataSetChanged();
		}
	}	
}
