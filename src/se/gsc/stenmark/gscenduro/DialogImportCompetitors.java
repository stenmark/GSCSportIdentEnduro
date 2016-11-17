package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

public class DialogImportCompetitors {
	
	private MainActivity mMainActivity;
	
	public DialogImportCompetitors(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}
	
    public void createImportCompetitorsDialog() {   
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.competitor_import, null);

		final EditText importCompetitorsInput = (EditText) promptsView.findViewById(R.id.import_competitors_input);
		TextView importCompetitorsInfo = (TextView) promptsView.findViewById(R.id.import_competitors_info);
		
		if (mMainActivity.competition.getCompetitionType() == Competition.ESS_TYPE) {			
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

				String errorText = "";
				try {
					errorText = CompetitionHelper.importCompetitors(importCompetitorsInput.getText().toString(), keepCheckBox.isChecked(), mMainActivity.competition.getCompetitionType(), false, mMainActivity.competition);
					if (!errorText.isEmpty()) {
						Toast.makeText(mMainActivity, errorText, Toast.LENGTH_LONG).show();
					}
					
					mMainActivity.competition.calculateResults();
					mMainActivity.updateFragments();
					AndroidHelper.saveSessionData(null,mMainActivity.competition);
					AndroidHelper.saveSessionData(mMainActivity.competition.getCompetitionName(),mMainActivity.competition);
				} catch (Exception e) {
					errorText = MainActivity.generateErrorMessage(e);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				String statusMessage = "All competitors added succesfully";
				if( !errorText.isEmpty() ){
					statusMessage = "Failed to import some competitors\n"+errorText;
				}
				builder.setMessage(statusMessage).setTitle("Import competitor status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

				AlertDialog alert = builder.create();
				alert.show();

				alertDialog.dismiss();
			}
		});
	}
}
