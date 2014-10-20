package se.gsc.stenmark.gscenduro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultListFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ResultListFragment newInstance(int sectionNumber) {
		ResultListFragment fragment = new ResultListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ResultListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_result_list, container,
				false);
	
//		TextView cardText = (TextView) rootView.findViewById(R.id.resultsTextView);
//		cardText.setText("ANDREAS");
		return rootView;
	} 	
	
	public void processNewCard(){
		TextView cardText = (TextView) getView().findViewById(R.id.resultsTextView);
		cardText.setText("ANDREAS");
	}

}
