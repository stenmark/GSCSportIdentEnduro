package se.gsc.stenmark.gscenduro;

import java.util.Collections;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultListFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private boolean isInView; // TODO: hack to make the view not update when its
								// called from external object and this view is
								// not active
	private MainActivity mainActivity;


	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ResultListFragment getInstance(int sectionNumber,
			MainActivity mainActivity) {
		ResultListFragment fragment = null;
		fragment = new ResultListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		fragment.mainActivity = mainActivity;
		return fragment;
	}

	public ResultListFragment() {
		MainApplication.resultListFragment = this;
		isInView = false;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.resultListFragment = null;
	}
		
	public void setActivity( MainActivity mainActivity){
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			View rootView = inflater.inflate(R.layout.fragment_result_list,
					container, false);

			return rootView;
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
		return null;

	}

	@Override
	public void onResume() {
		try {
			super.onResume();
			isInView = true;
			updateResultList();
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	@Override
	public void onPause() {
		try {
			super.onPause();
			isInView = false;
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	public void displayNewCard(String newCardInfo ) {
		try {			
			if (isInView) {
				TextView latestCardInfoText = (TextView) getView().findViewById(R.id.latestCardInfo);
				latestCardInfoText.setText(newCardInfo);
				updateResultList();
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	 MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	public void updateResultList() {
		try {
			if (isInView) {
				TextView resultsText = (TextView) getView().findViewById(R.id.resultsTextView);
				resultsText.setText("");
				if (!mainActivity.competition.getCompetitors().isEmpty()) {
					Collections.sort(mainActivity.competition.getCompetitors());
					for (Competitor competitor : mainActivity.competition.getCompetitors()) {
						if (competitor.hasResult()) {
							resultsText.append(competitor.name);
							int i = 0;
							for (long trackTime : competitor.trackTimes) {
								i++;
								resultsText.append(" SS" + i + " " + trackTime+ ", ");
							}

							resultsText.append("Total time: "+ competitor.getTotalTime(true) + "\n");

						} else {
							resultsText	.append(competitor.name + " no reuslt\n");
						}
					}
				} else {
					resultsText.setText("No results yet\n");
				}
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}
}
