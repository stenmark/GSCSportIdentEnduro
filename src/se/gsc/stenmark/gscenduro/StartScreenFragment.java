package se.gsc.stenmark.gscenduro;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
	private boolean isInView = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMainActivity = ((MainActivity) getActivity());   
           
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
	public void onPause() {
		super.onPause();
	      SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("connectionStatus", connectionStatus);
	      editor.commit();
	      isInView = false;
	}	
	
	public void addPointsTable(String points) throws IOException
	{
		ArrayList<String> pointsTable = new ArrayList<String>();											
		BufferedReader bufReader = new BufferedReader(new StringReader(points));
		String line = null;
		while((line = bufReader.readLine()) != null)
		{
			pointsTable.add(line);
		}
		mMainActivity.competition.setStringArrayPref(mMainActivity, "POINTSTABLE", pointsTable);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

		SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
		int maxNumberOfStages = Integer.parseInt(settings.getString("MAX_NUMBER_OF_STAGES", "15"));
		
		// Spinner for add stages 
        List<String> numerOfStages = new ArrayList<String>();
        for (int i = 1; i < (maxNumberOfStages + 1); i++){
        	numerOfStages.add(Integer.toString(i));
        }
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerTrackDefinition);
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, numerOfStages);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(LTRadapter);
		
        spinner.setSelection(mMainActivity.competition.getNumberOfTracks() - 1);
        
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

		Button addTrackButton = (Button) rootView.findViewById(R.id.addTrackButton);
		addTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
					int startStationNumner = Integer.parseInt(settings.getString("START_STATION_NUMBER", "71"));
					int finishStationNumner = Integer.parseInt(settings.getString("FINISH_STATION_NUMBER", "72"));
					
					Spinner mySpinner = (Spinner) getView().findViewById(R.id.spinnerTrackDefinition);					
					String newTrack = mySpinner.getSelectedItem().toString();					
					
					if(newTrack.length() == 0)
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
						int numberOfSs = 1;
						try{
							numberOfSs = Integer.parseInt(newTrack);
						}
						catch( NumberFormatException e){
							PopupMessage dialog = new PopupMessage("Invalid number entered");
							dialog.show(getFragmentManager(), "popUp");
							return;
						}
						
						String trackString = "";
						for(int i = 0; i < numberOfSs;  i++){
							trackString += startStationNumner + "," + finishStationNumner + ",";
						}
						trackString = trackString.substring(0, trackString.length()-1);   //remove last ","
						
						mMainActivity.competition.addNewTrack( trackString );
						updateTrackText();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button addMultiCompetitorButton = (Button) rootView.findViewById(R.id.addMultiCompetitorButton);
		addMultiCompetitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(mMainActivity);
				View promptsView = li.inflate(R.layout.add_multi_competitors, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

				alertDialogBuilder.setView(promptsView);

				final EditText MultiCompetitorsInput = (EditText) promptsView.findViewById(R.id.editTextMultiCompetitorsInput);

				// set dialog message
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Add",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {		
										try {
											String status = mMainActivity.competition.addMultiCompetitors(MultiCompetitorsInput.getText().toString());
											
											AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
									        builder.setIcon(android.R.drawable.ic_dialog_alert);
									        builder.setMessage(status).setTitle("Add Multi Competitor Status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
									        {
									            public void onClick(DialogInterface dialog, int which) {}
									        });
									 
									        AlertDialog alert = builder.create();
									        alert.show();																					
										} catch (Exception e) {
											PopupMessage dialog1 = new PopupMessage(MainActivity
													.generateErrorMessage(e));
											dialog1.show(getFragmentManager(), "popUp");
										}																					
										mMainActivity.updateFragments();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();				
			}		
		});
			
		Button addCompetitorButton = (Button) rootView.findViewById(R.id.addCompetitorButton);
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
						mMainActivity.competition.addCompetitor(competitorName.getText().toString(), Integer.parseInt(cardNumber.getText().toString()) );																					
						Toast.makeText(mMainActivity, "Competitor added: " + competitorName.getText().toString() + ", " + cardNumber.getText().toString(), Toast.LENGTH_SHORT).show();
						mMainActivity.updateFragments();
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
					mMainActivity.competition.competitionName = compName;
					mMainActivity.competition.saveSessionData(compName);
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
					SelectCompetitionDialog dialog = new SelectCompetitionDialog( savedCompetitions, listener, mMainActivity, mStartScreenFragment, listener );
					dialog.show(getFragmentManager(), "comp_select");
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
					mMainActivity.competition = new Competition();
					updateTrackText();
					mMainActivity.updateFragments();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}

		});

		Button addPointsTableButton = (Button) rootView.findViewById(R.id.addPointsTableButton);
		addPointsTableButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					//Set up Points Table
					LayoutInflater li = LayoutInflater.from(mMainActivity);
					View promptsView = li.inflate(R.layout.add_points_table, null);
					
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

					alertDialogBuilder.setView(promptsView);

					final EditText PointsTableInput = (EditText) promptsView.findViewById(R.id.editTextPointsTableInput);
					alertDialogBuilder
							.setCancelable(false)
							.setPositiveButton("Add",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,	int id) {			
											try {
												addPointsTable(PointsTableInput.getText().toString());
												mMainActivity.updateFragments();
											} catch (Exception e) {
												PopupMessage dialog1 = new PopupMessage(MainActivity.generateErrorMessage(e));
												dialog1.show(getFragmentManager(), "popUp");
											}												
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.cancel();
										}
									});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();										
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}

		});
		
		return rootView;
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
				trackInfoTextView.setText("Current loaded Stages: " + mMainActivity.competition.getTrackAsString() );
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
    	public int which = 0;
    	
    	public CompetitionOnClickListener( List<String> savedCompetitions ) {
		}
    	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.which = which;
		}
    }
}
