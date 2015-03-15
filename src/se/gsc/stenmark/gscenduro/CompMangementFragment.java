package se.gsc.stenmark.gscenduro;

import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class CompMangementFragment extends ListFragment {

	static CompMangementFragment mCompMangementFragment;
	MainActivity mMainActivity = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
	protected ListCompetitorAdapter mCompetitorAdapter;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		mMainActivity = ((MainActivity) getActivity());
				
		List<Competitor> competitors = mMainActivity.competition.getCompetitors();
		mCompetitorAdapter = new ListCompetitorAdapter(mMainActivity, competitors);
		setListAdapter(mCompetitorAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
						
		ReloadData();
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
	
	public void ReloadData() {
		mCompetitorAdapter.notifyDataSetChanged();
	}
}