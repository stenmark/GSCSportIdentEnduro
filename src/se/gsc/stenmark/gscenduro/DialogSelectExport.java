package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ExportOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import org.apache.commons.collections4.map.CompositeMap;

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
		try{
			final int EXPORT_COMPETITORS = 0;
			final int EXPORT_RESULTS = 1;
			final int EXPORT_PUNCHES= 2;
			final int EXPORT_COMPETITION= 3;
			// Creating and Building the Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			builder.setTitle("Select what you want to export");	            
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					switch(mRadioButtonListener.which) {				
					case EXPORT_COMPETITORS:
						try {
							String competitorList = mMainActivity.competition.getCompetitors().exportCsvString(mMainActivity.competition.getCompetitionType() );
							AndroidHelper.exportString(mMainActivity, competitorList, "competitors", mMainActivity.competition.getCompetitionName(), "csv");
						} catch (Exception e) {
							Log.d("action_export_competitors", "Error = " + Log.getStackTraceString(e));
						}
						break;

					case EXPORT_RESULTS:
						try {
							String resultList = CompetitionHelper.getResultsAsCsvString( 
									mMainActivity.competition.getStagesForAllClasses(), 
									mMainActivity.competition.getTotalResultsForAllClasses(), 
									mMainActivity.competition.getCompetitors(), 
									mMainActivity.competition.getCompetitionType() );
							AndroidHelper.exportString(mMainActivity, resultList, "results", mMainActivity.competition.getCompetitionName(), "csv");
						} catch (Exception e) {
							Log.d("action_export_results", "Error = " + Log.getStackTraceString(e));
						}
						break;

					case EXPORT_PUNCHES:
						try {						
							String punchList = mMainActivity.competition.getCompetitors().exportPunchesCsvString();
							AndroidHelper.exportString(mMainActivity, punchList, "punches", mMainActivity.competition.getCompetitionName(), "csv");
						} catch (Exception e) {
							Log.d("action_export_punches", "Error = " + Log.getStackTraceString(e));
						}
						break;						

					case EXPORT_COMPETITION:
						try {		
							String competitionList = CompetitionHelper.getCompetitionAsString( mMainActivity.competition );
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
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
}
