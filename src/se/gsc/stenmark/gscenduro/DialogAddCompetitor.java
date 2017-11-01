package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.support.v4.util.LogWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

public class DialogAddCompetitor {

	private MainActivity mMainActivity;	
	private static final Integer[] mCardNumbers = {
			8633671,
			8633672,
			8633673,
			8633674,
			8633675,
			8633676,
			8633677,
			8633678,
			8633679,
			8633680,
			8633681,
			8633682,
			8633683,
			8633684,
			8633685,
			8633686,
			8633687,
			8633688,
			8633689,
			8633690,
			8633691,
			8633692,
			8633693,
			8633694,
			8633695,
			8633696,
			8633697,
			8633698,
			8633699,
			8633700};

	public DialogAddCompetitor(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}

	public void createAddCompetitorDialog() {   
		try{
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
			final CheckBox isDamKlass = (CheckBox) promptsView.findViewById(R.id.damklass_checkbox);
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
			isDamKlass.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				}
			});	

			if (mMainActivity.competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {				
				addCompetitorEssLayout.setVisibility(View.GONE);
				cardNumberCheckBox.setVisibility(View.VISIBLE);
				cardNumberInput.setVisibility(View.GONE);
				cardNumberSpinner.setVisibility(View.VISIBLE);
				isDamKlass.setVisibility(View.VISIBLE);

				List<Integer> cardNumberList = new ArrayList<Integer>();
				for (int i = 0; i < mCardNumbers.length; i++) {
					if (!mMainActivity.competition.getCompetitors().checkIfCardNumberExists(mCardNumbers[i])) {
						cardNumberList.add(mCardNumbers[i]);
					}
				}	      

				ArrayAdapter<Integer> LTRadapter = new ArrayAdapter<Integer>(mMainActivity, android.R.layout.simple_spinner_item, cardNumberList);
				LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
				cardNumberSpinner.setAdapter(LTRadapter);			
				cardNumberSpinner.setSelection(0);					        			
			} else {
				addCompetitorEssLayout.setVisibility(View.VISIBLE);
				isDamKlass.setVisibility(View.GONE);
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
					try{
						String status = "";
						String cardNumber = "";
						String team = "";
						String compClass = "";
						String startNumber = "-1";
						String startGroup = "-1";

						if (mMainActivity.competition.getCompetitionType() == Competition.ESS_TYPE) {	
							team = teamInput.getText().toString();
							compClass = competitorClassInput.getText().toString();
							startNumber = startNumberInput.getText().toString();
							startGroup = startGroupInput.getText().toString();
							cardNumber = cardNumberInput.getText().toString();
						}
						else{
							if (cardNumberCheckBox.isChecked()) {						 
								cardNumber = cardNumberInput.getText().toString();
							} else {
								cardNumber = cardNumberSpinner.getSelectedItem().toString();
							}
							if( isDamKlass.isChecked() ){
								compClass = "dam";
							}
						}


						Map<String,Integer> parsingResults = new HashMap<String, Integer>();
						StringBuffer errorMessage = new StringBuffer("");
						boolean parsingError = CompetitionHelper.parseCompetitor("", 
								nameInput.getText().toString(),
								team, 
								compClass, 
								cardNumber, 
								startNumber, 
								startGroup, 
								mMainActivity.competition.getCompetitionType(),
								true,
								mMainActivity.competition.getCompetitors(),
								errorMessage, 
								parsingResults);	
						if ( parsingError ) {
							Toast.makeText(mMainActivity, errorMessage, Toast.LENGTH_LONG).show();
							PopupMessage dialog = new PopupMessage( errorMessage.toString() );
							dialog.show( mMainActivity.getSupportFragmentManager(), "popUp");
							return;
						} 

						mMainActivity.competition.addCompetitor(nameInput.getText().toString(), 
								parsingResults.get("cardNumber"), 
								teamInput.getText().toString(), 
								compClass, 
								parsingResults.get("startNumber"), 
								parsingResults.get("startGroup"),
								mMainActivity.competition.getCompetitionType());
						cardNumber = cardNumberInput.getText().toString();

						status = nameInput.getText().toString() + ", " + cardNumber + ". Added";
						mMainActivity.competition.calculateResults();
						mMainActivity.updateFragments();
						AndroidHelper.saveSessionData(null,mMainActivity.competition, null);
						AndroidHelper.saveSessionData(mMainActivity.competition.getCompetitionName(),mMainActivity.competition, null);


						Toast.makeText(mMainActivity, status, Toast.LENGTH_LONG).show();    	
						addCompetitorAlertDialog.dismiss();
					}
					catch( Exception e){
						LogFileWriter.writeLog(e);
					}
				}
			});  
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}    	
}
