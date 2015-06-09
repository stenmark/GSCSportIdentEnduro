package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogImportCompetitors {
	
	private MainActivity mMainActivity;
	
	public DialogImportCompetitors(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}
	
    public void createImportCompetitorsDialog() {   
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.import_competitors, null);

		final EditText importCompetitorsInput = (EditText) promptsView.findViewById(R.id.import_competitors_input);
		TextView importCompetitorsInfo = (TextView) promptsView.findViewById(R.id.import_competitors_info);
		
		if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.ESS_TYPE) {			
			importCompetitorsInfo.setText("Enter competitors in the following way:\n\nName,CardNumber,Team,CompetitorClass,StartNumber,StartGroup\nName,CardNumber,Team,CompetitorClass,StartNumber,StartGroup\nName,CardNumber,Team,CompetitorClass,StartNumber,StartGroup\n");			
		} else {
			importCompetitorsInfo.setText("Enter competitors in the following way:\n\nName,CardNumber\nName,CardNumber\nName,CardNumber");
		}
		
		final CheckBox keepCheckBox = (CheckBox) promptsView.findViewById(R.id.import_competitors_keep_checkbox);
		keepCheckBox.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View v) {
		      }
		});		
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		alertDialogBuilder.setTitle("Import competitors");
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setPositiveButton("Import", null);
		alertDialogBuilder.setNegativeButton("Cancel", null);			

		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	 if (importCompetitorsInput.length() == 0) {
                     Toast.makeText(mMainActivity, "No competitors was supplied", Toast.LENGTH_LONG).show();
                     return;
            	 }
            	
				try {	
					String statusMsg = mMainActivity.competition.getCompetitors().importCompetitors(importCompetitorsInput.getText().toString(), keepCheckBox.isChecked(), mMainActivity.competition.getCompetitionType());						
					mMainActivity.competition.calculateResults();
					mMainActivity.updateFragments();													
					
					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Import competitor status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {}
			        });
			 
			        AlertDialog alert = builder.create();
			        alert.show();																					
				} catch (Exception e) {
					String statusMsg = MainActivity.generateErrorMessage(e);

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			        builder.setIcon(android.R.drawable.ic_dialog_alert);
			        builder.setMessage(statusMsg).setTitle("Error importing competitors").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
			        {
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
