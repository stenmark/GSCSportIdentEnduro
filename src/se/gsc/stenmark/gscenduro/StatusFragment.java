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
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class StatusFragment extends Fragment {

	private MainActivity mMainActivity;
	private boolean inView = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			mMainActivity = ((MainActivity) getActivity());    
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try{
			inView = true;

			updateConnectText();
			updateCompetitionStatus();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void onPause(){
		super.onPause();
		inView = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = null;
		try{
			rootView = inflater.inflate(R.layout.status_fragment, container,	false);

			((TextView) rootView.findViewById(R.id.comp1_on_stage_text)).setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.comp2_on_stage_text)).setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.comp3_on_stage_text)).setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.stop_time_comp1)).setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.stop_time_comp2)).setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.stop_time_comp3)).setVisibility(View.GONE);
			TextView connectButton = (TextView) rootView.findViewById(R.id.connect_button);
			if(!MainActivity.sportIdentMode){
				/*
				TextView connectionStatusHeader = (TextView) rootView.findViewById(R.id.connection_status);
				connectionStatusHeader.setVisibility(View.GONE);
				TextView connectionStatusText = (TextView) rootView.findViewById(R.id.status_text);
				connectionStatusText.setVisibility(View.GONE);
				*/
				((EditText) rootView.findViewById(R.id.connection_ip)).setVisibility(View.VISIBLE);
				((TextView) rootView.findViewById(R.id.on_stage_competitors)).setVisibility(View.VISIBLE);
				connectButton.setOnClickListener(onConnectIp);
				if(mMainActivity.webTime != null){
					if(mMainActivity.webTime.getPersistentData() != null){
						((EditText) rootView.findViewById(R.id.connection_ip)).setText(mMainActivity.webTime.getPersistentData().serverIp);
					}
				}
			}
			else{
				((EditText) rootView.findViewById(R.id.connection_ip)).setVisibility(View.GONE);
				((TextView) rootView.findViewById(R.id.on_stage_competitors)).setVisibility(View.GONE);
				connectButton.setOnClickListener(onConnectSiMain);
			}



			Button editCompetitionButton = (Button) rootView.findViewById(R.id.modify_competition_button);
			editCompetitionButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {			
					LayoutInflater li = LayoutInflater.from(mMainActivity);				
					View promptsView = li.inflate(R.layout.competition_modify, null);	
					Competition competition = ((MainActivity) mMainActivity).competition;

					final LinearLayout layoutModifyStageSpinner = (LinearLayout) promptsView.findViewById(R.id.modify_stage_spinner_layout);
					final LinearLayout layoutModifyStageManually = (LinearLayout) promptsView.findViewById(R.id.modify_stage_manually_layout);

					final EditText nameInput = (EditText) promptsView.findViewById(R.id.name_input);	
					nameInput.setText(competition.getCompetitionName());

					final EditText dateInput = (EditText) promptsView.findViewById(R.id.date_input);	
					dateInput.setText(competition.getCompetitionDate());																		

					final EditText stagesInput = (EditText) promptsView.findViewById(R.id.modify_stages_manually_input);
					List<String> allClasses = competition.getAllClasses();
					if( !allClasses.isEmpty()){
						stagesInput.setText(CompetitionHelper.exportStagesCsvString(competition.getStages(allClasses.get(0))));
					}
					else{
						stagesInput.setText("No Competitor classes found");
					}

					if (competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
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
					spinner.setSelection(competition.getNumberOfStages());						

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
									if (numberOfSs != mMainActivity.competition.getNumberOfStages() ) {
										processCards = true;
									} 	

									mMainActivity.competition.importStages(stageString);
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
										AndroidHelper.saveSessionData(null,mMainActivity.competition, null, MainActivity.sportIdentMode);
										AndroidHelper.saveSessionData(mMainActivity.competition.getCompetitionName(),mMainActivity.competition, null, MainActivity.sportIdentMode);
									}
								}											
							} else {
								String status = CompetitionHelper.checkStagesData(stagesInput.getText().toString(), 1);
								if (status.length() > 0) {
									Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();                		
									return;
								}
								mMainActivity.competition.importStages(stagesInput.getText().toString());		                	
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
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}

		return rootView;

	}

	public void updateConnectText() {
		try{
			if (inView) {
				TextView statusTextView = (TextView) getView().findViewById(R.id.status_text);
				if (mMainActivity.getConnectionStatus() != null) {
					statusTextView.setText(mMainActivity.getConnectionStatus());
				}
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
	
	private void updateWebCompetitors( ){
		try{
			Competitor[] competitorsOnstage = mMainActivity.webTime.getCompetitorsOnstage();
			if(competitorsOnstage[0] == null){
				((TextView) getView().findViewById(R.id.comp1_on_stage_text)).setText( "" );
				((TextView) getView().findViewById(R.id.comp1_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp1)).setVisibility(View.GONE);
			}
			else{
				((TextView) getView().findViewById(R.id.comp1_on_stage_text)).setText( competitorsOnstage[0].getName());
				((TextView) getView().findViewById(R.id.comp1_on_stage_text)).setVisibility(View.VISIBLE);
				((Button) getView().findViewById(R.id.stop_time_comp1)).setVisibility(View.VISIBLE);
				((Button) getView().findViewById(R.id.stop_time_comp1)).setOnClickListener(onStopTimeComp1ButtonClicked);
			}

			if(competitorsOnstage[1] == null){
				((TextView) getView().findViewById(R.id.comp2_on_stage_text)).setText( "" );
				((TextView) getView().findViewById(R.id.comp2_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp2)).setVisibility(View.GONE);
			}
			else{
				((TextView) getView().findViewById(R.id.comp2_on_stage_text)).setText( competitorsOnstage[1].getName() );
				((TextView) getView().findViewById(R.id.comp2_on_stage_text)).setVisibility(View.VISIBLE);
				((TextView) getView().findViewById(R.id.stop_time_comp2)).setVisibility(View.VISIBLE);
				((Button) getView().findViewById(R.id.stop_time_comp2)).setOnClickListener(onStopTimeComp2ButtonClicked);
			}

			if(competitorsOnstage[2] == null){
				((TextView) getView().findViewById(R.id.comp3_on_stage_text)).setText( "" );
				((TextView) getView().findViewById(R.id.comp3_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp3)).setVisibility(View.GONE);
			}
			else{
				((TextView) getView().findViewById(R.id.comp3_on_stage_text)).setText( competitorsOnstage[2].getName() );
				((TextView) getView().findViewById(R.id.comp3_on_stage_text)).setVisibility(View.VISIBLE);
				((TextView) getView().findViewById(R.id.stop_time_comp3)).setVisibility(View.VISIBLE);
				((Button) getView().findViewById(R.id.stop_time_comp1)).setOnClickListener(onStopTimeComp3ButtonClicked);
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}

	}

	public void updateCompetitionStatus() {
		try{
			if (inView) {
				TextView statusTextView;
				
				((TextView) getView().findViewById(R.id.comp1_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.comp2_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.comp3_on_stage_text)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp1)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp2)).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.stop_time_comp3)).setVisibility(View.GONE);
				TextView connectButton = (TextView) getView().findViewById(R.id.connect_button);
				if(!MainActivity.sportIdentMode){
					/*
					TextView connectionStatusHeader = (TextView) getView().findViewById(R.id.connection_status);
					connectionStatusHeader.setVisibility(View.GONE);
					TextView connectionStatusText = (TextView) getView().findViewById(R.id.status_text);
					connectionStatusText.setVisibility(View.GONE);
					*/
					((EditText) getView().findViewById(R.id.connection_ip)).setVisibility(View.VISIBLE);
					((TextView) getView().findViewById(R.id.on_stage_competitors)).setVisibility(View.VISIBLE);
					((TextView) getView().findViewById(R.id.recently_read_cards_header)).setVisibility(View.GONE);
					connectButton.setOnClickListener(onConnectIp);
					updateWebCompetitors();
					if(mMainActivity.webTime != null){
						if(mMainActivity.webTime.getPersistentData() != null){
							((EditText) getView().findViewById(R.id.connection_ip)).setText(mMainActivity.webTime.getPersistentData().serverIp);
						}
					}
				}
				else{
					((EditText) getView().findViewById(R.id.connection_ip)).setVisibility(View.GONE);
					((TextView) getView().findViewById(R.id.recently_read_cards_header)).setVisibility(View.VISIBLE);
					TextView connectionStatusHeader = (TextView) getView().findViewById(R.id.connection_status);
					connectionStatusHeader.setVisibility(View.VISIBLE);
					((TextView) getView().findViewById(R.id.on_stage_competitors)).setVisibility(View.GONE);
					connectButton.setOnClickListener(onConnectSiMain);
				}
				
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
				statusTextView.setText( CompetitionHelper.stageStatusAsString( mMainActivity.competition.getStageDefinition() ));

				statusTextView = (TextView) getView().findViewById(R.id.competitor_status);	
				if (mMainActivity.competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
					if (mMainActivity.competition.getCompetitors() != null) {
						statusTextView.setText("Total: " + mMainActivity.competition.getCompetitors().size());
					}
				} else {					
					String numberOfCompetitors = "";
					for (String competitorClass : mMainActivity.competition.getAllClasses() ) {

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
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
	
	private void stopCompetitor(int number){
		try{
			Competitor competitor = mMainActivity.webTime.getCompetitorsOnstage()[number];
			competitor.getCard().getPunches().add(new Punch(System.currentTimeMillis(), 72));
			competitor.getCard().setNumberOfPunches(competitor.getCard().getNumberOfPunches()+1);
			LogFileWriter.writeLog("debugLog", "ENTER onStopTimeComp1ButtonClicked " + number);
			mMainActivity.competition.processNewCard(competitor.getCard(), true);
			mMainActivity.webTime.removeCompetitorOnStage(competitor);
			updateWebCompetitors();
			mMainActivity.updateFragments();
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}
	
	private OnClickListener onStopTimeComp1ButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stopCompetitor(0);
		}
	};
	private OnClickListener onStopTimeComp2ButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stopCompetitor(1);
		}
	};
	private OnClickListener onStopTimeComp3ButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stopCompetitor(2);
		}
	};
	
	private OnClickListener onConnectSiMain = new OnClickListener() {
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
	};
	
	private OnClickListener onConnectIp = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				if (inView) {
					EditText ipEditBox = (EditText) getView().findViewById(R.id.connection_ip);
					LogFileWriter.writeLog("debugLog", "Connect IP button pressed. Connect to IP: " + ipEditBox.getText().toString() );
					mMainActivity.webTime.connectToIp(ipEditBox.getText().toString());
				}
			}
			catch(Exception e){
				LogFileWriter.writeLog(e);
			}
		}
	};
}
