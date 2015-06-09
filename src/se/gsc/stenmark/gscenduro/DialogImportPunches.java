package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.compmanagement.PunchParser;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogImportPunches {
	
	private MainActivity mMainActivity;
	
	public DialogImportPunches(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}
	
    public void createImportPunchesDialog() {   
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.import_punches, null);

		final EditText importPunchesInput = (EditText) promptsView.findViewById(R.id.import_punches_input);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		alertDialogBuilder.setTitle("Import punches");
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setPositiveButton("Import", null);
		alertDialogBuilder.setNegativeButton("Cancel", null);			
		
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (importPunchesInput.length() == 0) {
            		Toast.makeText(mMainActivity, "No punches was supplied", Toast.LENGTH_LONG).show();
            		return;
            	}            	
            	
				try {					
					PunchParser punchParser = new PunchParser();
					punchParser.parsePunches(importPunchesInput.getText().toString(), mMainActivity.competition.getCompetitors().getCompetitors());
																		
					for (Card cardObject : punchParser.getCards()) {		
						if (cardObject.getPunches().size() > 0) {
							if (cardObject.getCardNumber() != 0) {
								mMainActivity.competition.processNewCard(cardObject, false);
							}
						}
					}												
					
					mMainActivity.competition.calculateResults();
					
					String statusMsg = punchParser.getStatus();
					mMainActivity.updateFragments();
					
					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Import punches status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {}
			        });
			 
			        AlertDialog alert = builder.create();
			        alert.show();													        											      
				} catch (Exception e) {
					String statusMsg = MainActivity.generateErrorMessage(e);

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Error importing punches").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {}
			        });			
			        AlertDialog alert = builder.create();
			        alert.show();												        
				}
			
				alertDialog.dismiss();
            }
        });											    	    
    }
}
