package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class CompMangementFragment extends ListFragment {

	static CompMangementFragment mCompMangementFragment;
	MainActivity mMainActivity = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
	protected ListCompetitorAdapter mCompetitorAdapter;
	protected List<Competitor> mAllCompetitor = new ArrayList<Competitor>();
	public List<Competitor> mCompetitor = new ArrayList<Competitor>();

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		mMainActivity = ((MainActivity) getActivity());
		
		FetchItems();
	}

	public void FetchItems() {
		mAllCompetitor = mMainActivity.competition.getCompetitors();
		FillList();
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

	protected void FillList() {
		PopulateList();
		if (mCompetitorAdapter == null) {
			mCompetitorAdapter = new ListCompetitorAdapter(mMainActivity, mCompetitor);
			setListAdapter(mCompetitorAdapter);
		} else {
			mCompetitorAdapter.notifyDataSetChanged();
		}
	}

	protected void PopulateList() {
		mCompetitor.clear();
		for (int i = 0; i < mAllCompetitor.size(); i++) {
			mCompetitor.add(mAllCompetitor.get(i));
		}
	}

}