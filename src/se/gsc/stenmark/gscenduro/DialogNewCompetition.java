package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class DialogNewCompetition {
	
	private MainActivity mMainActivity;
	
	public DialogNewCompetition(MainActivity mainActivity) {
		mMainActivity = mainActivity;
	}
	
    public void createNewCompetitionDialog() {   
		SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
		int maxNumberOfStages = Integer.parseInt(settings.getString("MAX_NUMBER_OF_STAGES", "15"));
					
        List<String> numerOfStages = new ArrayList<String>();
        for (int i = 1; i < (maxNumberOfStages + 1); i++) {
        	numerOfStages.add(Integer.toString(i));
        }
        						
        LayoutInflater li = LayoutInflater.from(mMainActivity);
        View promptsView = li.inflate(R.layout.competition_new, null);					

		final EditText newCompetitionInput = (EditText) promptsView.findViewById(R.id.new_competition_input);
		newCompetitionInput.setText("New");					
		
		final LinearLayout layoutAddTrackSpinner = (LinearLayout) promptsView.findViewById(R.id.add_track_spinner_layout);
		final LinearLayout layoutAddTrackManually = (LinearLayout) promptsView.findViewById(R.id.add_track_manually_layout);
		
		final EditText addTrackManuallyInput = (EditText) promptsView.findViewById(R.id.add_track_manually_input);
		addTrackManuallyInput.setText("");
		
		final CheckBox addTrackManuallyCheckbox = (CheckBox) promptsView.findViewById(R.id.add_track_manually_checkbox);
		addTrackManuallyCheckbox.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View v) {
		    	  if (addTrackManuallyCheckbox.isChecked()) {
		    		  layoutAddTrackSpinner.setVisibility(View.GONE);
		    		  layoutAddTrackManually.setVisibility(View.VISIBLE);
		    	  } else {
		    		  layoutAddTrackSpinner.setVisibility(View.VISIBLE);
		    		  layoutAddTrackManually.setVisibility(View.GONE);
		    	  }
		      }
		});			
		
		final CheckBox keepCompetitorsCheckBox = (CheckBox) promptsView.findViewById(R.id.keep_competitors_checkbox);
		keepCompetitorsCheckBox.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View v) {
		      }
		});
		
		final Spinner spinner = (Spinner) promptsView.findViewById(R.id.add_track_spinner);	
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(mMainActivity, android.R.layout.simple_spinner_item, numerOfStages);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
        spinner.setAdapter(LTRadapter);			
        spinner.setSelection(mMainActivity.competition.getNumberOfTracks() - 1);			
	
		if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.SVARTVITT_TYPE){
			keepCompetitorsCheckBox.setVisibility(View.VISIBLE);							
		} else {					
			keepCompetitorsCheckBox.setVisibility(View.GONE);
			keepCompetitorsCheckBox.setChecked(false);						
		}	        
        
		RadioGroup radioTypeOfCompetition = (RadioGroup) promptsView.findViewById(R.id.radio_type_of_competition);	
		final RadioButton radioTypeEss = (RadioButton) promptsView.findViewById(R.id.radio_type_ess);	
		radioTypeOfCompetition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if ((checkedId == R.id.radio_type_ess) && mMainActivity.competition.getCompetitionType() == mMainActivity.competition.ESS_TYPE){
					keepCompetitorsCheckBox.setVisibility(View.VISIBLE);							
				} else {					
					keepCompetitorsCheckBox.setVisibility(View.GONE);
					keepCompetitorsCheckBox.setChecked(false);						
				}
				
				if (checkedId == R.id.radio_type_ess) {
					addTrackManuallyCheckbox.setChecked(true);
					addTrackManuallyCheckbox.setVisibility(View.GONE);
		    		layoutAddTrackSpinner.setVisibility(View.GONE);
		    		layoutAddTrackManually.setVisibility(View.VISIBLE);
				} else {
					addTrackManuallyCheckbox.setChecked(false);
		    		layoutAddTrackSpinner.setVisibility(View.VISIBLE);
		    		layoutAddTrackManually.setVisibility(View.GONE);						
					addTrackManuallyCheckbox.setVisibility(View.VISIBLE);
				}
			}
		});	        
        
		AlertDialog.Builder newAlertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		newAlertDialogBuilder.setTitle("New competition");
		newAlertDialogBuilder.setView(promptsView);
		newAlertDialogBuilder.setPositiveButton("Create", null);
		newAlertDialogBuilder.setNegativeButton("Cancel", null);			

		final AlertDialog newAlertDialog = newAlertDialogBuilder.create();
		newAlertDialog.show();	
		newAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	//Check if all data is entered
                if (addTrackManuallyCheckbox.isChecked() && (addTrackManuallyInput.length() == 0)) {
                    Toast.makeText(mMainActivity, "Competition not created! No track was supplied", Toast.LENGTH_LONG).show();
                    return;
                } else if (newCompetitionInput.length() == 0) {
                	Toast.makeText(mMainActivity, "Competition not created! No competition name was supplied", Toast.LENGTH_LONG).show();
                    return;
                }

				if (keepCompetitorsCheckBox.isChecked()) {
					//Keep competitors, clear all other data
					mMainActivity.competition.getTrack().clear();
					mMainActivity.competition.getResults().clear();
					mMainActivity.competition.getResultLandscape().clear();
					mMainActivity.competition.clearCompetitors();									
				} else {
					//Create a new competition
					mMainActivity.competition.getCompetitors().clear();
					mMainActivity.competition = new Competition();
				}									
				mMainActivity.competition.setCompetitionName(newCompetitionInput.getText().toString());	
				
				if (radioTypeEss.isChecked()) {
					mMainActivity.competition.setCompetitionType(mMainActivity.competition.ESS_TYPE);
				} else {
					mMainActivity.competition.setCompetitionType(mMainActivity.competition.SVARTVITT_TYPE);
				}										
				
				if (addTrackManuallyCheckbox.isChecked()) {										
					mMainActivity.competition.addNewTrack(addTrackManuallyInput.getText().toString());
				} else {
					SharedPreferences settings = mMainActivity.getSharedPreferences(MainActivity.PREF_NAME, 0);
					int startStationNumner = Integer.parseInt(settings.getString("START_STATION_NUMBER", "71"));
					int finishStationNumner = Integer.parseInt(settings.getString("FINISH_STATION_NUMBER", "72"));
					
					String newTrack = spinner.getSelectedItem().toString();	
				
					if (newTrack.length() == 0) {
				        Toast.makeText(mMainActivity, "Competition not created! No track was supplied", Toast.LENGTH_LONG).show();
				        return;
					} else {
						int numberOfSs = 1;
						try {
							numberOfSs = Integer.parseInt(newTrack);
						}
						catch( NumberFormatException e){											
							Toast.makeText(mMainActivity, "Competition not created! Invalid track has been entered", Toast.LENGTH_LONG).show();
							return;
						}
						
						String trackString = "";
						for (int i = 0; i < numberOfSs;  i++) {
							trackString += startStationNumner + "," + finishStationNumner + ",";
						}
						trackString = trackString.substring(0, trackString.length() - 1);   //remove last ","
						
						mMainActivity.competition.addNewTrack(trackString);										
					}					
				}					
				mMainActivity.updateFragments();                    
                
                newAlertDialog.dismiss();
            }
        });   
    }
}
