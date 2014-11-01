package se.gsc.stenmark.gscenduro;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartScreenFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private MainActivity mainActivity;
	
	public void setActivity( MainActivity mainActivity){
		this.mainActivity = mainActivity;
		MainApplication.startScreenFragment = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.startScreenFragment = null;
	}
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static StartScreenFragment getInstance(int sectionNumber,
			MainActivity mainActivity) {
		StartScreenFragment fragment = null;
		fragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		fragment.mainActivity = mainActivity;
		return fragment;

	}

	public StartScreenFragment() {
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTrackText();
		EditText nameOFCompEdit = (EditText) getView().findViewById(R.id.editSaveLoadComp);
		nameOFCompEdit.setText(mainActivity.competition.competitionName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

		Button connectButton = (Button) rootView.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);
					String connectMsg = mainActivity.connectToSiMaster();
					statusTextView.setText(connectMsg);
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button addTrackButton = (Button) rootView.findViewById(R.id.addTrackButton);
		addTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText newTrack = (EditText) getView().findViewById(R.id.editTrackDefinition);
					 mainActivity.competition.addNewTrack( (newTrack.getText().toString()) );
					updateTrackText();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button addCompetitorButton = (Button) rootView.findViewById(R.id.addCompetitorButton);
		addCompetitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText competitorName = (EditText) getView().findViewById(R.id.editCompetitorName);
					EditText cardNumber = (EditText) getView().findViewById( R.id.editCardNumber);
					mainActivity.competition.addCompetitor(competitorName.getText().toString(), cardNumber.getText().toString() );
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity
							.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}

			}
		});

		Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText nameOFCompToSave = (EditText) getView().findViewById(R.id.editSaveLoadComp);
					String compName = nameOFCompToSave.getText().toString();
					if (compName.isEmpty()) {
						PopupMessage dialog = new PopupMessage("No competition name was supplied");
						dialog.show(getFragmentManager(), "popUp");
						return;
					}
					mainActivity.competition.competitionName = compName;
					mainActivity.competition.saveSessionData(compName);
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button loadButton = (Button) rootView.findViewById(R.id.loadButton);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText nameOFCompToLoad = (EditText) getView().findViewById(R.id.editSaveLoadComp);
					mainActivity.competition = Competition.loadSessionData( nameOFCompToLoad.getText().toString() );
					updateTrackText();
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button listButton = (Button) rootView.findViewById(R.id.listLoadedButton);
		listButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					TextView statusText = (TextView) getView().findViewById( R.id.cardInfoTextView);
					statusText.setText("Existing competitions \n");
					statusText.append( CompetitionHelper.getSavedCompetitions() );

				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button newButton = (Button) rootView.findViewById(R.id.newCompButton);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mainActivity.competition = new Competition();
					updateTrackText();
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}

		});

		Button exportResultButton = (Button) rootView.findViewById(R.id.exportResultButton);
		exportResultButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					exportResult();
			}
		});

		return rootView;
	}

	private void exportResult() {
		try {
			TextView status = (TextView) getView().findViewById(R.id.cardInfoTextView);
			String exportResult = mainActivity.competition.exportResultAsCsv(this);
			status.setText(exportResult);	
						
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}



	public void updateTrackText() {
		try {
			TextView trackInfoTextView = (TextView) getView().findViewById( R.id.trackInfoTextView);
			trackInfoTextView.setText("Current loaded Track: " + mainActivity.competition.getTrackAsString() );
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}

	}

}
