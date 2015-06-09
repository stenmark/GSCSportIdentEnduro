package se.gsc.stenmark.gscenduro;

import java.io.BufferedReader;
import java.io.StringReader;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogImportCompetition {
	
	private MainActivity mMainActivity;
	
	public DialogImportCompetition(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}
	
    public void createImportCompetitionDialog() {
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.import_competition, null);

		final EditText importCompetitionInput = (EditText) promptsView.findViewById(R.id.import_competition_input);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		alertDialogBuilder.setTitle("Import competition");
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setPositiveButton("Import", null);
		alertDialogBuilder.setNegativeButton("Cancel", null);			

		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	 if (importCompetitionInput.length() == 0) {
                     Toast.makeText(mMainActivity, "No competitors was supplied", Toast.LENGTH_LONG).show();
                     return;
            	 }		

            	 try {
            		 String importType = "";
            		 String importData = "";
            		 String statusMsg = "";
            		 mMainActivity.competition = new Competition();

					BufferedReader bufReader = new BufferedReader(new StringReader(importCompetitionInput.getText().toString()));
					String line = null;
					while ((line = bufReader.readLine()) != null) {

						if (line.contains("[/Name]")) {
							importType = "";
							mMainActivity.competition.setCompetitionName(importData);		
						} else if (line.contains("[/Date]")) {
							importType = "";
							mMainActivity.competition.setCompetitionDate(importData);		
						} else if (line.contains("[/Type]")) {
							importType = "";
							mMainActivity.competition.setCompetitionType(Integer.parseInt(importData));		
						} else if (line.contains("[/Stages]")) {
							importType = "";
							String statusStages = mMainActivity.competition.getStages().checkStagesData(importData);							
							if (statusStages.length() != 0) {
								statusMsg += statusStages;
							} else {
								mMainActivity.competition.getStages().importStages(importData);
							}
						} else if (line.contains("[/Competitors]")) {
							importType = "";
							statusMsg += mMainActivity.competition.getCompetitors().importCompetitors(importData, false, mMainActivity.competition.getCompetitionType());		
						} else if (line.contains("[/Punches]")) {
							importType = "";							
							//statusMsg += mMainActivity.competition.getCompetitors().importPunches(importData);
						} else if (importType.length() > 0) {							
							importData += line;							
							if ((importType.contains("[Competitors]")) || (importType.contains("[Punches]"))) {
								importData += "\n";
							}
						} else if ((line.contains("[Name]")) || 
						   (line.contains("[Date]")) ||
						   (line.contains("[Type]")) ||
						   (line.contains("[Stages]")) ||
						   (line.contains("[Competitors]")) ||
						   (line.contains("[Punches]"))) {
							importType = line;
							importData = "";
						}
					}

					mMainActivity.competition.calculateResults();					
					mMainActivity.updateFragments();					
            		 
					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Import competition status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {}
			        });
			 
			        AlertDialog alert = builder.create();
			        alert.show();	            		 																		
				} catch (Exception e) {
					String statusMsg = MainActivity.generateErrorMessage(e);

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Error importing Competition").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
