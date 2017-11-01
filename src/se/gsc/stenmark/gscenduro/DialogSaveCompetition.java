package se.gsc.stenmark.gscenduro;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogSaveCompetition {

	private MainActivity mMainActivity;

	public DialogSaveCompetition(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}

	public void createSaveCompetitionDialog() {   
		try{
			LayoutInflater li = LayoutInflater.from(mMainActivity);
			View promptsView = li.inflate(R.layout.competition_save, null);					

			final EditText saveCompetitionInput = (EditText) promptsView.findViewById(R.id.save_competition_input);
			saveCompetitionInput.setText(mMainActivity.competition.getCompetitionName());

			AlertDialog.Builder saveAlertDialogBuilder = new AlertDialog.Builder(mMainActivity);
			saveAlertDialogBuilder.setTitle("Save competition");
			saveAlertDialogBuilder.setView(promptsView);			
			saveAlertDialogBuilder.setPositiveButton("Save", null);
			saveAlertDialogBuilder.setNegativeButton("Cancel", null);				

			final AlertDialog saveAlertDialog = saveAlertDialogBuilder.create();
			saveAlertDialog.show();		
			saveAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {                	                
					String compName = saveCompetitionInput.getText().toString();
					if (compName.isEmpty()) {
						Toast.makeText(mMainActivity, "Competition not saved! No competition name was supplied", Toast.LENGTH_LONG).show();
						return;
					} else {
						try {
							mMainActivity.competition.setCompetitionName(compName);
							AndroidHelper.saveSessionData(compName,mMainActivity.competition, null);
						} catch (Exception e) {
							Log.d("action_save", "Error = " + Log.getStackTraceString(e));
						}											
					}

					saveAlertDialog.dismiss();
				}
			});   
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
}
