package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ExportOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class DialogSelectExport {
	
	private ExportOnClickListener mExportOnClickListener;
	private MainActivity mMainActivity;
	private CharSequence[] items = {"Competitors", "Results", "Punches", "Competition"};
	ExportOnClickListener mRadioButtonListener;
	
	public DialogSelectExport(ExportOnClickListener exportOnClickListener,
							  MainActivity MainActivity,
							  ExportOnClickListener radioButtonListener) {
		mExportOnClickListener = exportOnClickListener;
		mMainActivity = MainActivity;
		mRadioButtonListener = radioButtonListener;
	}
	
    public void createExportDialog() {   
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle("Select what you want to export");	            
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item) {
				switch(mRadioButtonListener.which) {				
				case 0:
					try {
						String competitorList = mMainActivity.competition.getCompetitors().exportCsvString(mMainActivity.competition.getCompetitionType() );
						AndroidHelper.exportString(mMainActivity, competitorList, "competitors", mMainActivity.competition.getCompetitionName(), "csv");
					} catch (Exception e) {
						Log.d("action_export_competitors", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 1:
					try {
						String resultList = CompetitionHelper.getResultsAsCsvString( 
								mMainActivity.competition.getStages(), 
								mMainActivity.competition.getTotalResults(), 
								mMainActivity.competition.getCompetitors(), 
								mMainActivity.competition.getCompetitionType() );
						AndroidHelper.exportString(mMainActivity, resultList, "results", mMainActivity.competition.getCompetitionName(), "csv");
					} catch (Exception e) {
						Log.d("action_export_results", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 2:
					try {						
						String punchList = mMainActivity.competition.getCompetitors().exportPunchesCsvString();
						AndroidHelper.exportString(mMainActivity, punchList, "punches", mMainActivity.competition.getCompetitionName(), "csv");
					} catch (Exception e) {
						Log.d("action_export_punches", "Error = " + Log.getStackTraceString(e));
					}
					break;						
			
				case 3:
					try {		
						String competitionList = "";
						// Competition Name
						competitionList += "[Name]\n";
						competitionList += mMainActivity.competition.getCompetitionName() + "\n";
						competitionList += "[/Name]\n";
						
						// Competition Date
						competitionList += "[Date]\n";
						competitionList += mMainActivity.competition.getCompetitionDate() + "\n";
						competitionList += "[/Date]\n";

						// Competition Type
						competitionList += "[Type]\n";
						competitionList += mMainActivity.competition.getCompetitionType() + "\n";
						competitionList += "[/Type]\n";

						// Stages
						competitionList += "[Stages]\n";
						competitionList += mMainActivity.competition.getStages().exportStagesCsvString() + "\n";
						competitionList += "[/Stages]\n";
						
						// Competitors
						competitionList += "[Competitors]\n";
						competitionList += mMainActivity.competition.getCompetitors().exportCsvString(mMainActivity.competition.getCompetitionType());
						competitionList += "[/Competitors]\n";

						// Punches
						competitionList += "[Punches]\n";
						competitionList += mMainActivity.competition.getCompetitors().exportPunchesCsvString();
						competitionList += "[/Punches]\n";		
						
						AndroidHelper.exportString(mMainActivity, competitionList, "competition", mMainActivity.competition.getCompetitionName(), "csv");
						
						
					} catch (Exception e) {
						Log.d("action_export_competition", "Error = " + Log.getStackTraceString(e));
					}					
					break;										
				}		    					
		    }});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item) {
		    	dialog.dismiss();
		    }});	            
    	builder.setSingleChoiceItems(items, 0, mExportOnClickListener);
    	
		AlertDialog loadDialog = builder.create();
        loadDialog.show();        
    }
}
