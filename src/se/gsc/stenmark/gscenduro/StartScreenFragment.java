package se.gsc.stenmark.gscenduro;
import java.util.List;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
	public String connectionStatus = "";
	private OnCompetitionChanged onCompetitionChangedCallback;
	private boolean isInView = false;
	
    public interface OnCompetitionChanged {
        public void onCompetitionChanged();
    }	
	
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
		isInView = true;
		SharedPreferences settings = mainActivity.getSharedPreferences(	MainActivity.PREF_NAME, 0);
		connectionStatus = settings.getString("connectionStatus", "NO STATUS");
		
		updateTrackText();
		updateCompName();
		updateConnectText();
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            onCompetitionChangedCallback = (OnCompetitionChanged) activity;
        } catch (ClassCastException e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
        }
    }
	
	@Override
	public void onPause() {
		super.onPause();
	      SharedPreferences settings = mainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("connectionStatus", connectionStatus);
	      editor.commit();
	      isInView = false;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

		Button connectButton = (Button) rootView.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					connectionStatus = mainActivity.connectToSiMaster();
					updateConnectText();
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
					onCompetitionChangedCallback.onCompetitionChanged();
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
					List<String> savedCompetitions = CompetitionHelper.getSavedCompetitionsAsList();
					CompetitionOnClickListener listener = new CompetitionOnClickListener( savedCompetitions );
					SelectCompetitionDialog dialog = new SelectCompetitionDialog( savedCompetitions, listener );
					dialog.show(getFragmentManager(), "comp_select");
					
//					EditText nameOFCompToLoad = (EditText) getView().findViewById(R.id.editSaveLoadComp);
//					mainActivity.competition = Competition.loadSessionData( nameOFCompToLoad.getText().toString() );
//					updateTrackText();
//					onCompetitionChangedCallback.onCompetitionChanged();
					

			
					
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
					onCompetitionChangedCallback.onCompetitionChanged();
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
	
	public void updateConnectText(){
		try {
			if( isInView ){
				TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);	
				statusTextView.setText(connectionStatus);
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	public void updateCompName(){
		try {
			if( isInView ){
				EditText nameOFCompEdit = (EditText) getView().findViewById(R.id.editSaveLoadComp);
				nameOFCompEdit.setText(mainActivity.competition.competitionName);
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}

	public void updateTrackText() {
		try {
			if( isInView ){
				TextView trackInfoTextView = (TextView) getView().findViewById( R.id.trackInfoTextView);
				trackInfoTextView.setText("Current loaded Track: " + mainActivity.competition.getTrackAsString() );
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}

	}
	
	public void appendCardInfoText(String messageToAppend) {
		try {
			if( isInView ){
				TextView trackInfoTextView = (TextView) getView().findViewById( R.id.cardInfoTextView);
				trackInfoTextView.append( messageToAppend);
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}
	
	public void updateCardInfoText(String messageToWrite) {
		try {
			if( isInView ){
				TextView trackInfoTextView = (TextView) getView().findViewById( R.id.cardInfoTextView);
				trackInfoTextView.setText( messageToWrite);
			}
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}
	
    public class CompetitionOnClickListener implements android.content.DialogInterface.OnClickListener{
    	public String selectedItem;
    	private List<String> savedCompetitions;
    	
    	public CompetitionOnClickListener( List<String> savedCompetitions ) {
    		this.savedCompetitions = savedCompetitions;
		}
    	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				selectedItem = savedCompetitions.get(which);	
				mainActivity.competition = Competition.loadSessionData( selectedItem );
				updateTrackText();
				onCompetitionChangedCallback.onCompetitionChanged();
			} catch (Exception e) {
				PopupMessage dialog2 = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog2.show(getFragmentManager(), "popUp");
			}
		}


    	
    }

}
