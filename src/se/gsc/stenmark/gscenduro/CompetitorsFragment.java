package se.gsc.stenmark.gscenduro;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

public class CompetitorsFragment extends ListFragment {

	private MainActivity mMainActivity = null;
	private CompetitorsListAdapter mCompetitorAdapter;

	public void onActivityCreated(Bundle savedInstanceState) {
		try{
			setListShownNoAnimation(true);
			setHasOptionsMenu(true);		

			mMainActivity = ((MainActivity) getActivity());

			if(getListAdapter() == null) {
				View listView = (View)getLayoutInflater(savedInstanceState).inflate(R.layout.competitor_fragment, null);
				getListView().addHeaderView(listView);
			}

			mCompetitorAdapter = new CompetitorsListAdapter(mMainActivity, mMainActivity.competition.getCompetitors());
			setListAdapter(mCompetitorAdapter);							

			super.onActivityCreated(savedInstanceState);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
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