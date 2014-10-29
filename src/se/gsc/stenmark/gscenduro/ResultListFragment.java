package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.NotAllStationsPunchedException;
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

	public void processNewCard(Card newCard) {
		try {
			TextView latestCardInfoText = null;
			if (isInView) {
				latestCardInfoText = (TextView) getView().findViewById(
						R.id.latestCardInfo);
				latestCardInfoText.setText("");
			}

			Competitor foundCompetitor = CompetitionHelper.findCompetitor(newCard, mainActivity.competitors);
			if (foundCompetitor == null) {
				if (isInView) {
					latestCardInfoText
							.append("Read new card with card number: "
									+ newCard.cardNumber
									+ " Could not find any competitor with this number");
				}
				return;
			}
			newCard.removeDoublePunches();
			foundCompetitor.card = newCard;

			if (isInView) {
				latestCardInfoText.append("New card read for "
						+ foundCompetitor.name + "   ");
			}

			List<Long> results = new ArrayList<Long>();
			try{
				results = CompetitionHelper.extractResultFromCard(newCard);
			}
			catch(NotAllStationsPunchedException e1){
				PopupMessage dialog = new PopupMessage(	e1.getMessage());
				dialog.show(getFragmentManager(), "popUp");
			}
			

			
			foundCompetitor.trackTimes = new ArrayList<Long>();
			int i = 1;
			for (Long trackTime : results) {
				if (isInView) {
					latestCardInfoText.append(", Time for SS " + i + " = "
							+ trackTime + " seconds ");
				}
				foundCompetitor.trackTimes.add(trackTime);
				i++;
			}

			if (isInView) {
				latestCardInfoText.append("Total time was: "
						+ foundCompetitor.getTotalTime(true) + " seconds \n");
			}

			if (isInView) {
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
				TextView resultsText = (TextView) getView().findViewById(
						R.id.resultsTextView);
				resultsText.setText("");
				if (mainActivity.competitors != null
						&& !mainActivity.competitors.isEmpty()) {
					Collections.sort(mainActivity.competitors);
					for (Competitor competitor : mainActivity.competitors) {
						if (competitor.hasResult()) {
							resultsText.append(competitor.name);
							int i = 0;
							for (long trackTime : competitor.trackTimes) {
								i++;
								resultsText.append(" SS" + i + " " + trackTime
										+ ", ");
							}

							resultsText.append("Total time: "
									+ competitor.getTotalTime(true));

						} else {
							resultsText
									.append(competitor.name + " no reuslt\n");
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
