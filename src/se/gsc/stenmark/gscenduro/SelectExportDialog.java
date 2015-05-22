package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ExportOnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class SelectExportDialog {
	
	private ExportOnClickListener mExportOnClickListener;
	private MainActivity mMainActivity;
	private CharSequence[] items = {"Competitors", "Results", "Punches", "Competition", "All"};
	ExportOnClickListener mRadioButtonListener;
	
	public SelectExportDialog(ExportOnClickListener exportOnClickListener,
							  MainActivity MainActivity,
							  ExportOnClickListener radioButtonListener) {
		mExportOnClickListener = exportOnClickListener;
		mMainActivity = MainActivity;
		mRadioButtonListener = radioButtonListener;
	}
	
    public void createExportDialog() {   
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle("What do you want to export?");	            
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
				switch(mRadioButtonListener.which)
				{				
				case 0:
					try {
						mMainActivity.competition.exportCompetitorsAsCsv(mMainActivity);
					} catch (Exception e) {
						Log.d("action_export_competitors", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 1:
					try {
						mMainActivity.competition.exportResultsAsCsv(mMainActivity);
					} catch (Exception e) {
						Log.d("action_export_results", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 2:
					try {
						mMainActivity.competition.exportPunchesAsCsv(mMainActivity);
					} catch (Exception e) {
						Log.d("action_export_punches", "Error = " + Log.getStackTraceString(e));
					}
					break;						
			
				case 3:
					try {
						mMainActivity.competition.exportCompetitionAsCsv(mMainActivity);
					} catch (Exception e) {
						Log.d("action_export_competition", "Error = " + Log.getStackTraceString(e));
					}					
					break;					
					
				case 4:
					try {
						mMainActivity.competition.exportAllAsCsv(mMainActivity);
					} catch (Exception e) {
						Log.d("action_export_all", "Error = " + Log.getStackTraceString(e));
					}					
					break;
				}		    					
		    }});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
		    	dialog.dismiss();
		    }});	            
    	builder.setSingleChoiceItems(items,0, mExportOnClickListener);
    	
		AlertDialog loadDialog = builder.create();
        loadDialog.show();        
    }
}
