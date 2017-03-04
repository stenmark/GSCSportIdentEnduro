package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ImportOnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogSelectImport {

	private ImportOnClickListener mImportOnClickListener;
	private MainActivity mMainActivity;
	private CharSequence[] items = {"Competitors", "Punches", "Competition"};
	ImportOnClickListener mRadioButtonListener;

	public DialogSelectImport(ImportOnClickListener importOnClickListener, MainActivity MainActivity, ImportOnClickListener radioButtonListener) {
		mImportOnClickListener = importOnClickListener;
		mMainActivity = MainActivity;
		mRadioButtonListener = radioButtonListener;
	}

	public void createImportDialog() {  
		try{
			// Creating and Building the Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
			builder.setTitle("Select what you want to import");	            
			builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					switch(mRadioButtonListener.which) {				
					case 0:		//Import competitors
						DialogImportCompetitors importCompetitorsDialog = new DialogImportCompetitors(mMainActivity);
						importCompetitorsDialog.createImportCompetitorsDialog();						
						break;

					case 1:		//Import punches
						DialogImportPunches importPunchesDialog = new DialogImportPunches(mMainActivity);
						importPunchesDialog.createImportPunchesDialog();						
						break;

					case 2:		//Import competition
						DialogImportCompetition importCompetitionDialog = new DialogImportCompetition(mMainActivity);
						importCompetitionDialog.createImportCompetitionDialog();						
						break;						
					}		    					
				}});
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
				}});	            
			builder.setSingleChoiceItems(items, 0, mImportOnClickListener);    	
			AlertDialog loadDialog = builder.create();
			loadDialog.show(); 
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}
}
