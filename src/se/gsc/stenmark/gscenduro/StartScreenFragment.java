package se.gsc.stenmark.gscenduro;
import java.util.List;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartScreenFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	static StartScreenFragment mStartScreenFragment;
	MainActivity mMainActivity;
	public String connectionStatus = "";
	private OnCompetitionChanged onCompetitionChangedCallback;
	private boolean isInView = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMainActivity = ((MainActivity) getActivity());   
    }

    public interface OnCompetitionChanged {
        public void onCompetitionChanged();
    }	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.startScreenFragment = null;
	}
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static StartScreenFragment getInstance(int sectionNumber) {
		mStartScreenFragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		mStartScreenFragment.setArguments(args);
		return mStartScreenFragment;

	}

	public StartScreenFragment() {
	}

	@Override
	public void onResume() {
		super.onResume();
		isInView = true;
		SharedPreferences settings = mMainActivity.getSharedPreferences(	MainActivity.PREF_NAME, 0);
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
	      SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("connectionStatus", connectionStatus);
	      editor.commit();
	      isInView = false;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

		TextView connectButton = (TextView) rootView.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					connectionStatus = mMainActivity.connectToSiMaster();
					updateConnectText();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		TextView addTrackButton = (TextView) rootView.findViewById(R.id.addTrackButton);
		addTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText newTrack = (EditText) getView().findViewById(R.id.editTrackDefinition);
					
					if(newTrack.getText().length() == 0)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
				        builder.setMessage("Track is empty").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
				        {
				            public void onClick(DialogInterface dialog, int which) {}
				        });
				 
				        AlertDialog alert = builder.create();
				        alert.show();							
					}
					else
					{
						mMainActivity.competition.addNewTrack( (newTrack.getText().toString()) );
						updateTrackText();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		TextView addCompetitorButton = (TextView) rootView.findViewById(R.id.addCompetitorButton);
		addCompetitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText competitorName = (EditText) getView().findViewById(R.id.editCompetitorName);
					EditText cardNumber = (EditText) getView().findViewById( R.id.editCardNumber);
					
					if((competitorName.getText().length() == 0) && (cardNumber.getText().length() == 0))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
				        builder.setMessage("Name and Card Number are empty").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
				        {
				            public void onClick(DialogInterface dialog, int which) {}
				        });
				 
				        AlertDialog alert = builder.create();
				        alert.show();	
						
					}else if (mMainActivity.competition.checkNameExists(competitorName.getText().toString()))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
				        builder.setMessage("Name already exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
				        {
				            public void onClick(DialogInterface dialog, int which) {}
				        });
				 
				        AlertDialog alert = builder.create();
				        alert.show();						
						
					}else if (mMainActivity.competition.checkCardNumberExists(Integer.parseInt(cardNumber.getText().toString())))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
				        builder.setMessage("Card number exists").setTitle("Error").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
				        {
				            public void onClick(DialogInterface dialog, int which) {}
				        });
				 
				        AlertDialog alert = builder.create();
				        alert.show();							
					}else
					{
						mMainActivity.competition.addCompetitor(competitorName.getText().toString(), cardNumber.getText().toString() );
						
						Toast.makeText(mMainActivity, "Competitor added: " + competitorName.getText().toString() + ", " + cardNumber.getText().toString(), Toast.LENGTH_SHORT).show();
						onCompetitionChangedCallback.onCompetitionChanged();
						mMainActivity.updateFragments();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity
							.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}

			}
		});

		TextView saveButton = (TextView) rootView.findViewById(R.id.saveButton);
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
					mMainActivity.competition.competitionName = compName;
					mMainActivity.competition.saveSessionData(compName);
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		TextView loadButton = (TextView) rootView.findViewById(R.id.loadButton);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					List<String> savedCompetitions = CompetitionHelper.getSavedCompetitionsAsList();
					CompetitionOnClickListener listener = new CompetitionOnClickListener( savedCompetitions );
					SelectCompetitionDialog dialog = new SelectCompetitionDialog( savedCompetitions, listener );
					dialog.show(getFragmentManager(), "comp_select");
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		TextView listButton = (TextView) rootView.findViewById(R.id.listLoadedButton);
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

		TextView newButton = (TextView) rootView.findViewById(R.id.newCompButton);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mMainActivity.competition = new Competition();
					updateTrackText();
					onCompetitionChangedCallback.onCompetitionChanged();
					mMainActivity.updateFragments();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}

		});

		TextView exportResultButton = (TextView) rootView.findViewById(R.id.exportResultButton);
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
			String exportResult = mMainActivity.competition.exportResultAsCsv(this);
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
				nameOFCompEdit.setText(mMainActivity.competition.competitionName);
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
				trackInfoTextView.setText("Current loaded Track: " + mMainActivity.competition.getTrackAsString() );
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
				mMainActivity.competition = Competition.loadSessionData( selectedItem );
				updateTrackText();
				updateCompName();
				onCompetitionChangedCallback.onCompetitionChanged();
				mMainActivity.updateFragments();
			} catch (Exception e) {
				PopupMessage dialog2 = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog2.show(getFragmentManager(), "popUp");
			}
		}


    	
    }

}
