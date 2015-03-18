package se.gsc.stenmark.gscenduro;

import java.util.List;

import se.gsc.stenmark.gscenduro.MainActivity.CompetitionOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class SelectCompetitionDialog {
	private List<String> competitions;
	private CompetitionOnClickListener competitionOnClickListener;
	private MainActivity mMainActivity;
	CompetitionOnClickListener radioButtonListener;
	
	public SelectCompetitionDialog( List<String> competitions, 
									CompetitionOnClickListener competitionOnClickListener,
									MainActivity mMainActivity,
									CompetitionOnClickListener radioButtonListener) {
		this.competitions = competitions;
		this.competitionOnClickListener = competitionOnClickListener;
		this.mMainActivity = mMainActivity;
		this.radioButtonListener = radioButtonListener;
	}
	
    public void createSelectCompetitionDialog() {
		CharSequence[] items = new String[competitions.size()];
        int i = 0;
        for(String competition : competitions) {
        	items[i] = competition;
        	i++;
        }
    
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle("Select competition to load");	            
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
		    	try {
		    		List<String> savedCompetitions = CompetitionHelper.getSavedCompetitionsAsList();
					String selectedItem = savedCompetitions.get(radioButtonListener.which);
					mMainActivity.competition = Competition.loadSessionData(selectedItem);
					mMainActivity.competition.calculateResults();
					mMainActivity.updateFragments();	
				} catch (Exception e) {
					Log.d("action_load", "Error = " + e);
				}					
		    }});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
		    	dialog.dismiss();
		    }});	            
    	builder.setSingleChoiceItems(items,0, competitionOnClickListener );
    	
		AlertDialog loadDialog = builder.create();
        loadDialog.show();        
    }
}
