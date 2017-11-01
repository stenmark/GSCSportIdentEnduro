package se.gsc.stenmark.gscenduro;

import java.io.InvalidClassException;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainActivity.CompetitionOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class DialogSelectCompetition {

	private List<String> competitions;
	private CompetitionOnClickListener competitionOnClickListener;
	private MainActivity mMainActivity;
	CompetitionOnClickListener radioButtonListener;

	public DialogSelectCompetition( List<String> competitions, 
			CompetitionOnClickListener competitionOnClickListener,
			MainActivity mMainActivity,
			CompetitionOnClickListener radioButtonListener) {
		this.competitions = competitions;
		this.competitionOnClickListener = competitionOnClickListener;
		this.mMainActivity = mMainActivity;
		this.radioButtonListener = radioButtonListener;
	}

	public void createSelectCompetitionDialog() {
		try{
			CharSequence[] items = new String[competitions.size()];
			int i = 0;
			for (String competition : competitions) {
				items[i] = competition;
				i++;
			}

			// Creating and Building the Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			builder.setTitle("Select competition to load");	            
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					try {
						List<String> savedCompetitions = AndroidHelper.getSavedCompetitionsAsList();
						String selectedItem = savedCompetitions.get(radioButtonListener.which);
						mMainActivity.competition = AndroidHelper.loadSessionData(selectedItem, null);
						mMainActivity.competition.calculateResults();
						mMainActivity.updateFragments();	
					}
					catch( InvalidClassException e1){
						PopupMessage errorDialog = new PopupMessage("This version of GSCEnduro is not compatible with the version of the competition that you tried to load");
						errorDialog.show( mMainActivity.getSupportFragmentManager(), "popUp");
						mMainActivity.competition = new Competition();
						mMainActivity.competition.calculateResults();
						mMainActivity.updateFragments();	
					}
					catch (Exception e2) {
						PopupMessage errorDialog = new PopupMessage(MainActivity.generateErrorMessage(e2));
						errorDialog.show( mMainActivity.getSupportFragmentManager(), "popUp");
						Log.d("action_load", "Error = " + e2);
						mMainActivity.competition = new Competition();
						mMainActivity.competition.calculateResults();
						mMainActivity.updateFragments();	
					}	
					AndroidHelper.saveSessionData(null,mMainActivity.competition, null);
					AndroidHelper.saveSessionData(mMainActivity.competition.getCompetitionName(),mMainActivity.competition, null);
				}});
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
				}});	            
			builder.setSingleChoiceItems(items,0, competitionOnClickListener );

			AlertDialog loadDialog = builder.create();
			loadDialog.show();   
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
}
