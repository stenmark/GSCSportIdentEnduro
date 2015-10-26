package se.gsc.stenmark.gscenduro;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class StatusFragment extends Fragment {
	
	private MainActivity mMainActivity;
	private boolean inView = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMainActivity = ((MainActivity) getActivity());                     
    }

	@Override
	public void onResume() {
		super.onResume();
		inView = true;
					
		updateConnectText();
		updateCompetitionStatus();
	}
	
	public void onPause(){
		super.onPause();
		inView = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.status_fragment, container,	false);
        
		TextView connectButton = (TextView) rootView.findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mMainActivity.connectToSiMaster();
					updateConnectText();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});
		
        Button editCompetitionButton = (Button) rootView.findViewById(R.id.modify_competition_button);
        editCompetitionButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {			
				LayoutInflater li = LayoutInflater.from(mMainActivity);				
				View promptsView = li.inflate(R.layout.competition_modify, null);						
		       
				final LinearLayout layoutModifyStageSpinner = (LinearLayout) promptsView.findViewById(R.id.modify_stage_spinner_layout);
				final LinearLayout layoutModifyStageManually = (LinearLayout) promptsView.findViewById(R.id.modify_stage_manually_layout);
				
				final EditText nameInput = (EditText) promptsView.findViewById(R.id.name_input);	
				nameInput.setText(((MainActivity) mMainActivity).competition.getCompetitionName());
				
				final EditText dateInput = (EditText) promptsView.findViewById(R.id.date_input);	
				dateInput.setText(((MainActivity) mMainActivity).competition.getCompetitionDate());																		
				
				final EditText stagesInput = (EditText) promptsView.findViewById(R.id.modify_stages_manually_input);	
				stagesInput.setText(((MainActivity) mMainActivity).competition.getStages().exportStagesCsvString());
				
				if (((MainActivity) mMainActivity).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
					layoutModifyStageSpinner.setVisibility(View.VISIBLE);
					layoutModifyStageManually.setVisibility(View.GONE);
				} else {
					layoutModifyStageSpinner.setVisibility(View.GONE);
					layoutModifyStageManually.setVisibility(View.VISIBLE);
				}
				
				SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
				int maxNumberOfStages = Integer.parseInt(settings.getString("MAX_NUMBER_OF_STAGES", "15"));

		        List<String> numerOfStages = new ArrayList<String>();
		        for (int i = 1; i < (maxNumberOfStages + 1); i++) {
		        	numerOfStages.add(Integer.toString(i));
		        }
				
				final Spinner spinner = (Spinner) promptsView.findViewById(R.id.modify_stage_spinner);	
		        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(mMainActivity, android.R.layout.simple_spinner_item, numerOfStages);
		        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
		        spinner.setAdapter(LTRadapter);			
		        spinner.setSelection(mMainActivity.competition.getStages().size() - 1);						
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);	
				alertDialogBuilder.setTitle("Modify competitor");
				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setPositiveButton("Modify", null);
				alertDialogBuilder.setNegativeButton("Cancel", null);	

				final AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {		            	
		            	if (nameInput.length() == 0) {
		                	Toast.makeText(mMainActivity, "No competition name was supplied", Toast.LENGTH_LONG).show();
		                    return;
		                } else if (dateInput.length() == 0) {
		                	Toast.makeText(mMainActivity, "No competition date was supplied", Toast.LENGTH_LONG).show();
		                    return;                	
		                }		            	
		            			
		            	if (((MainActivity) mMainActivity).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
							SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
							int startStationNumner = Integer.parseInt(settings.getString("START_STATION_NUMBER", "71"));
							int finishStationNumner = Integer.parseInt(settings.getString("FINISH_STATION_NUMBER", "72"));
							
							String newStage = spinner.getSelectedItem().toString();	
							
							if (newStage.length() == 0) {
						        Toast.makeText(mMainActivity, "No stages was supplied", Toast.LENGTH_LONG).show();
						        return;
							} else {
								int numberOfSs = 1;
								try {
									numberOfSs = Integer.parseInt(newStage);
								}
								catch( NumberFormatException e){											
									Toast.makeText(mMainActivity, "Invalid stages has been entered", Toast.LENGTH_LONG).show();
									return;
								}
								
								String stageString = "";
								for (int i = 0; i < numberOfSs;  i++) {
									stageString += startStationNumner + "," + finishStationNumner + ",";
								}
								stageString = stageString.substring(0, stageString.length() - 1);   //remove last ","
								
								Boolean processCards = false;
								//Process all cards again if number of stages has changed								
								if (numberOfSs != mMainActivity.competition.getStages().size()) {
									processCards = true;
								} 	
								
								mMainActivity.competition.getStages().importStages(stageString);
								if (processCards) {
									String status = "Number of stages has changed so all cards have been processed again.\n\n";
									for( Entry<Integer,Competitor> currentCompetitor: mMainActivity.competition.getCompetitors().getCompetitors().entrySet() ) {		
										if ((currentCompetitor.getValue().getCard() != null) &&(currentCompetitor.getValue().getCard().getPunches().size() > 0)) {
											if (currentCompetitor.getValue().getCard().getCardNumber() != 0) {
												status += mMainActivity.competition.processNewCard(currentCompetitor.getValue().getCard(), false);
											}
										}
									}	
									Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();  
									
									mMainActivity.competition.calculateResults();									
									mMainActivity.updateFragments();									
								}
							}											
		            	} else {
		                   	String status = mMainActivity.competition.getStages().checkStagesData(stagesInput.getText().toString(), 1);
		                	if (status.length() > 0) {
		                		Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();                		
		                		return;
		                	}
		                	mMainActivity.competition.getStages().importStages(stagesInput.getText().toString());		                	
		            	}
		            	mMainActivity.competition.setCompetitionName(nameInput.getText().toString());	
						mMainActivity.competition.setCompetitionDate(dateInput.getText().toString());
						
		            	((MainActivity) mMainActivity).competition.calculateResults();
		            	((MainActivity) mMainActivity).updateFragments();
		            	
		            	alertDialog.dismiss();
		            }
				});		            		           
			}		            
        });		
		
		return rootView;
	}
	
	public void updateConnectText() {
		if (inView) {
			TextView statusTextView = (TextView) getView().findViewById(R.id.status_text);
			if (mMainActivity.getConnectionStatus() != null) {
				statusTextView.setText(mMainActivity.getConnectionStatus());
			}
		}
	}
	
	public void updateCompetitionStatus() {
		if (inView) {
			TextView statusTextView;				
			
			statusTextView = (TextView) getView().findViewById(R.id.competition_competition_date);
			if (mMainActivity.competition.getCompetitionDate() != null) {
				statusTextView.setText(mMainActivity.competition.getCompetitionDate());
			}
			
			statusTextView = (TextView) getView().findViewById(R.id.competition_type);				
			if (mMainActivity.competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
				statusTextView.setText("SvartVitt");	
			} else {
				statusTextView.setText("Enduro Sweden Series");
			}
			
			statusTextView = (TextView) getView().findViewById(R.id.competition_name);
			if (mMainActivity.competition.getCompetitionName() != null) {
				statusTextView.setText(mMainActivity.competition.getCompetitionName());
			}
			
			statusTextView = (TextView) getView().findViewById(R.id.stages_status);
			if (mMainActivity.competition.getStages().toString() != null) {
				statusTextView.setText(mMainActivity.competition.getStages().toString());
			}
			
			statusTextView = (TextView) getView().findViewById(R.id.competitor_status);	
			if (mMainActivity.competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
				if (mMainActivity.competition.getCompetitors() != null) {
					statusTextView.setText("Total: " + mMainActivity.competition.getCompetitors().size());
				}
			} else {					
				String numberOfCompetitors = "";
				for (String competitorClass : mMainActivity.competition.getCompetitors().getCompetitorClasses()) {
					
					if (numberOfCompetitors.length() != 0) {
						numberOfCompetitors += "\n";
					}
					
					numberOfCompetitors += competitorClass + ": " + mMainActivity.competition.getCompetitors().sizeByClass(competitorClass);
				}
				
				if (mMainActivity.competition.getCompetitors() != null) {
					statusTextView.setText("Total: " + mMainActivity.competition.getCompetitors().size() + "\n" + numberOfCompetitors);
				}
			}
			
			statusTextView = (TextView) getView().findViewById(R.id.card_status);	
			if( mMainActivity.competition.lastReadCards != null ){
				String cardStatus = "";
				if( mMainActivity.competition.lastReadCards.isEmpty() ){
					cardStatus = "No cards read yet";
				}
				else{
					//Itterate the last backwards to print the most recently added card first
					for( int cardStatusPos = mMainActivity.competition.lastReadCards.size()-1; cardStatusPos >= 0; cardStatusPos--){
						cardStatus += mMainActivity.competition.lastReadCards.get(cardStatusPos);
					}
				}
				statusTextView.setText( cardStatus );
				
			}
		}
	}
}
