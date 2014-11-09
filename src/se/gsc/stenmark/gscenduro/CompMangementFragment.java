package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompMangementFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	MainActivity mainActivity = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private List<TextView> textViews = null;
	private List<EditText> editViews = null;
	private List<Button> deleteButtons = null;
	private List<Button> modifyButtons = null;
	private boolean isInView = false;

	public CompMangementFragment() {
		textViews = new ArrayList<TextView>();
		editViews = new ArrayList<EditText>();
		deleteButtons = new ArrayList<Button>();
		modifyButtons = new ArrayList<Button>();
		MainApplication.compMangementFragment = this;
	}
	
	public void setActivity( MainActivity mainActivity){
		this.mainActivity = mainActivity;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		isInView = false;
		MainApplication.compMangementFragment = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		isInView = true;
		listCompetitors();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		isInView = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_comp_management,
				container, false);
		return rootView;
	}

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CompMangementFragment getInstance(int sectionNumber, MainActivity mainActivity) {
		CompMangementFragment fragment = null;
		fragment = new CompMangementFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		fragment.mainActivity = mainActivity;
		return fragment;
	}

	private void addDeleteButton(String nameToDelete, int viewIdToAlign) {
		try {
			RelativeLayout relativeLayout = (RelativeLayout) getView()
					.findViewById(R.id.comp_management_fragment);
			Button deleteButton = new Button(MainApplication.getAppContext());
			deleteButton.setText("Delete");
			deleteButton.setTextColor(Color.WHITE);
			deleteButton.setTextSize(8f);
			deleteButton.setId(View.generateViewId());

			LayoutParams layoutParams = new LayoutParams(140, 55);
			layoutParams.addRule(RelativeLayout.RIGHT_OF, viewIdToAlign);
			layoutParams.addRule(RelativeLayout.ALIGN_TOP, viewIdToAlign);

			deleteButton.setOnClickListener(new OnDeleteClickedListener(
					nameToDelete, this));
			relativeLayout.addView(deleteButton, layoutParams);

			deleteButtons.add(deleteButton);
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	private void addModifyButton(String nameToModify, int editViewIdToReadFrom,
			int viewIdToAlign) {
		try {
			RelativeLayout relativeLayout = (RelativeLayout) getView()
					.findViewById(R.id.comp_management_fragment);
			Button modifyButton = new Button(MainApplication.getAppContext());
			modifyButton.setText("Modify");
			modifyButton.setTextColor(Color.WHITE);
			modifyButton.setTextSize(8f);
			modifyButton.setId(View.generateViewId());

			LayoutParams layoutParams = new LayoutParams(140, 55);
			layoutParams.addRule(RelativeLayout.RIGHT_OF, viewIdToAlign);
			layoutParams.addRule(RelativeLayout.ALIGN_TOP, viewIdToAlign);

			modifyButton.setOnClickListener(new OnModifyClickedListener(
					nameToModify, editViewIdToReadFrom, this));
			relativeLayout.addView(modifyButton, layoutParams);

			modifyButtons.add(modifyButton);
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	private int addCompetitorText(String name, String card, int previousViewId) {
		try {
			RelativeLayout relativeLayout = (RelativeLayout) getView()
					.findViewById(R.id.comp_management_fragment);
			TextView competitorNameView = new TextView(
					MainApplication.getAppContext());
			competitorNameView.setText(name);
			competitorNameView.setTextColor(Color.BLACK);
			competitorNameView.setId(View.generateViewId());

			LayoutParams layoutParamsName = new LayoutParams(220,
					LayoutParams.WRAP_CONTENT);
			if (previousViewId == -1) {
				layoutParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParamsName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			} else {
				layoutParamsName.addRule(RelativeLayout.BELOW, previousViewId);
				layoutParamsName.addRule(RelativeLayout.ALIGN_LEFT,
						previousViewId);
			}
			layoutParamsName.setMargins(0, 12, 0, 0);
			relativeLayout.addView(competitorNameView, layoutParamsName);
			textViews.add(competitorNameView);

			EditText competitorCardEdit = new EditText(
					MainApplication.getAppContext());
			competitorCardEdit.setText(card);
			competitorCardEdit.setTextColor(Color.BLACK);
			competitorCardEdit.setTextSize(10);
			competitorCardEdit.setId(View.generateViewId());
			LayoutParams layoutParamsCard = new LayoutParams(140, 55);
			layoutParamsCard.addRule(RelativeLayout.RIGHT_OF,
					competitorNameView.getId());
			layoutParamsCard.addRule(RelativeLayout.ALIGN_TOP,
					competitorNameView.getId());
			relativeLayout.addView(competitorCardEdit, layoutParamsCard);
			editViews.add(competitorCardEdit);

			return competitorNameView.getId();
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
			return 0;
		}
	}

	public void listCompetitors() {
		
		try {
			if( isInView ){
				clearList();
				int previousViewId = -1;
				for (Competitor competitor : mainActivity.competition.getCompetitors()) {
					String cardNumber = String.valueOf(competitor.cardNumber);
					if (competitor.cardNumber == -1) {
						cardNumber = "No card added yet";
					}
	
					String doublePunches = "";
					if (competitor.card != null) {
						if (competitor.card.doublePunches != null) {
							if (!competitor.card.doublePunches.isEmpty()) {
								doublePunches += "Double punches: ";
								for (Punch doublePunch : competitor.card.doublePunches) {
									doublePunches += " On control: "
											+ doublePunch.control + " at time: "
											+ doublePunch.time + ", ";
								}
							}
						}
					}
	
					previousViewId = addCompetitorText(competitor.name + "  "
							+ doublePunches, cardNumber, previousViewId);
					addDeleteButton(competitor.name,
							editViews.get(editViews.size() - 1).getId());
					addModifyButton(competitor.name,
							editViews.get(editViews.size() - 1).getId(),
							deleteButtons.get(deleteButtons.size() - 1).getId());
	
				}
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
		

	}

	private void clearList() {
		RelativeLayout relativeLayout = (RelativeLayout) getView()
				.findViewById(R.id.comp_management_fragment);
		for (TextView textView : textViews) {
			relativeLayout.removeView(textView);
		}
		for (EditText editText : editViews) {
			relativeLayout.removeView(editText);
		}
		for (Button button : deleteButtons) {
			relativeLayout.removeView(button);
		}
		for (Button button : modifyButtons) {
			relativeLayout.removeView(button);
		}
	}

	private class OnDeleteClickedListener implements OnClickListener {
		private String nameToDelete;
		private CompMangementFragment parent;

		OnDeleteClickedListener(String nameToDelete,
				CompMangementFragment parent) {
			this.nameToDelete = nameToDelete;
			this.parent = parent;
		}

		@Override
		public void onClick(View arg0) {
			try {
				mainActivity.competition.removeCompetitor(nameToDelete);
				parent.listCompetitors();
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getFragmentManager(), "popUp");
			}

		}
	}

	private class OnModifyClickedListener implements OnClickListener {
		private String nameToModify;
		private CompMangementFragment parent;
		private int editViewIdToReadFrom;

		OnModifyClickedListener(String nameToModify, int editViewIdToReadFrom,
				CompMangementFragment parent) {
			this.nameToModify = nameToModify;
			this.parent = parent;
			this.editViewIdToReadFrom = editViewIdToReadFrom;
		}

		@Override
		public void onClick(View arg0) {
			try {
				EditText newCardNumberEdit = (EditText) getView().findViewById(editViewIdToReadFrom);
				mainActivity.competition.updateCompetitorCardNumber(nameToModify, newCardNumberEdit.getText().toString() );
				parent.listCompetitors();
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getFragmentManager(), "popUp");
			}

		}
	}

}
