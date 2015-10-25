package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;

public class DialogAddCompetitor {
	
	private MainActivity mMainActivity;	
	private static final String[] mCardNumbers = {
	"2065302",
	"2065307",
	"2065315",
	"2065317",
	"2065325",
	"2065339",
	"2065349",
	"2065381",
	"2065387",
	"2065396",
	"2077287",
	"2078034",
	"2078036",
	"2078040",
	"2078056",
	"2078064",
	"2078082",
	"2078087",
	"2079711",
	"2079713",
	"2079723",
	"2079732",
	"2079737",
	"2079747",
	"2079749",
	"2079752",
	"2079768",
	"2079774",
	"2079775",
	"2079797"};
	
	
	
	
	
	
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public DialogAddCompetitor(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}
	
    public void createAddCompetitorDialog() {   
    	LayoutInflater li = LayoutInflater.from(mMainActivity);    	
		View promptsView = li.inflate(R.layout.competitor_add, null);
		
		final LinearLayout addCompetitorEssLayout = (LinearLayout) promptsView.findViewById(R.id.add_competitor_ess_layout);
		final EditText nameInput = (EditText) promptsView.findViewById(R.id.name_input);
		final EditText cardNumberInput = (EditText) promptsView.findViewById(R.id.card_number_input);
		final EditText teamInput = (EditText) promptsView.findViewById(R.id.team_input);
		final EditText competitorClassInput = (EditText) promptsView.findViewById(R.id.class_input);
		final EditText startNumberInput = (EditText) promptsView.findViewById(R.id.start_number_input);
		final EditText startGroupInput = (EditText) promptsView.findViewById(R.id.start_group_input);
		final Spinner cardNumberSpinner = (Spinner) promptsView.findViewById(R.id.card_number_spinner);
		final CheckBox cardNumberCheckBox = (CheckBox) promptsView.findViewById(R.id.cardnumber_checkbox);
		cardNumberCheckBox.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View v) {
		    	  if (cardNumberCheckBox.isChecked()) {
					cardNumberInput.setVisibility(View.VISIBLE);
					cardNumberSpinner.setVisibility(View.GONE);
		    	  } else {
					cardNumberInput.setVisibility(View.GONE);
					cardNumberSpinner.setVisibility(View.VISIBLE);		    		  
		    	  }
		      }
		});			
		
		if (mMainActivity.competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {				
			addCompetitorEssLayout.setVisibility(View.GONE);
			cardNumberCheckBox.setVisibility(View.VISIBLE);
			cardNumberInput.setVisibility(View.GONE);
			cardNumberSpinner.setVisibility(View.VISIBLE);
						
	        List<String> cardNumberList = new ArrayList<String>();
	        for (int i = 0; i < mCardNumbers.length; i++) {
	        	if (!mMainActivity.competition.getCompetitors().checkIfCardNumberExists(Integer.parseInt(mCardNumbers[i]))) {
	        		cardNumberList.add(mCardNumbers[i]);
	        	}
	        }	      
				
	        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(mMainActivity, android.R.layout.simple_spinner_item, cardNumberList);
	        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
	        cardNumberSpinner.setAdapter(LTRadapter);			
	        cardNumberSpinner.setSelection(0);					        			
		} else {
			addCompetitorEssLayout.setVisibility(View.VISIBLE);
		}    	    		
				
		AlertDialog.Builder addCompetitorAlertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		addCompetitorAlertDialogBuilder.setTitle("Add competitor");
		addCompetitorAlertDialogBuilder.setView(promptsView);			
		addCompetitorAlertDialogBuilder.setPositiveButton("Add", null);
		addCompetitorAlertDialogBuilder.setNegativeButton("Cancel", null);				

		final AlertDialog addCompetitorAlertDialog = addCompetitorAlertDialogBuilder.create();
		addCompetitorAlertDialog.show();		
		addCompetitorAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                	                    
            	String status = "";
            	String cardNumber = "";
				if (mMainActivity.competition.getCompetitionType() == Competition.ESS_TYPE) {					
					
					String errorText = mMainActivity.competition.getCompetitors().checkData(nameInput.getText().toString(), 
																				  cardNumberInput.getText().toString(), 
																				  teamInput.getText().toString(), 
																				  competitorClassInput.getText().toString(), 
																				  startNumberInput.getText().toString(), 
																				  startGroupInput.getText().toString(),
																				  mMainActivity.competition.getCompetitionType(),
																				  true,
																				  null);					
	            	if (errorText.length() != 0) {
	            		Toast.makeText(mMainActivity, errorText, Toast.LENGTH_LONG).show();
	                    return;
	            	} 
					
					mMainActivity.competition.getCompetitors().add(nameInput.getText().toString(), 
																   cardNumberInput.getText().toString(), 
																   teamInput.getText().toString(), 
																   competitorClassInput.getText().toString(), 
																   startNumberInput.getText().toString(), 
																   startGroupInput.getText().toString(),
			   		   											   mMainActivity.competition.getCompetitionType());
					cardNumber = cardNumberInput.getText().toString();
				} else {					
					if (cardNumberCheckBox.isChecked()) {						 
						cardNumber = cardNumberInput.getText().toString();
					} else {
						cardNumber = cardNumberSpinner.getSelectedItem().toString();
					}		
					
					String errorText = mMainActivity.competition.getCompetitors().checkData(nameInput.getText().toString(), cardNumber, "", "", "-1", "-1", mMainActivity.competition.getCompetitionType(), true, null);					
	            	if (errorText.length() != 0) {
	            		Toast.makeText(mMainActivity, errorText, Toast.LENGTH_LONG).show();
	                    return;
	            	} 
	            	
					mMainActivity.competition.getCompetitors().add(nameInput.getText().toString(), cardNumber, "", "", "-1", "-1", mMainActivity.competition.getCompetitionType());
				}			
				
				status = nameInput.getText().toString() + ", " + cardNumber + ". Added";
				mMainActivity.competition.calculateResults();
				mMainActivity.updateFragments();
				
				Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();    	
				addCompetitorAlertDialog.dismiss();
            }
        });    	
    }    	
}
