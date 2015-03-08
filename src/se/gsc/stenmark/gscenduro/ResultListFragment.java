package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ResultListFragment extends ListFragment {
	
	static ResultListFragment mResultListFragment;
	MainActivity mMainActivity = null;
	protected ListResultAdapter mResultAdapter;
	protected List<Competitor> mAllCompetitor = new ArrayList<Competitor>();
	
		
	protected List<Result> mAllResults = new ArrayList<Result>();
	protected List<Result> mResult = new ArrayList<Result>();
	
	private static final String ARG_SECTION_NUMBER = "section_number";

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		
		mMainActivity = ((MainActivity) getActivity());
		FetchItems();
	}
		
	protected void FetchItems() {	
		mAllCompetitor = mMainActivity.competition.getCompetitors();		
		mAllResults = mMainActivity.competition.getResults();
				
		FillList();	
	}
	
	protected void FillList() {
		PopulateList();
		if (mResultAdapter == null) {
			mResultAdapter = new ListResultAdapter(mMainActivity,
					mResultListFragment, mResult);
			setListAdapter(mResultAdapter);
		} else {
			mResultAdapter.notifyDataSetChanged();
		}
	}

	protected void PopulateList() {
		mResult.clear();
		for (int i = 0; i < mAllResults.size(); i++) {
			mResult.add(mAllResults.get(i));
		}
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
