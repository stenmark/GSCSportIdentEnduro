package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ExportOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class DialogSelectExport {

	private ExportOnClickListener mExportOnClickListener;
	private MainActivity mMainActivity;
	private CharSequence[] items = {"Competitors", "Results", "Punches", "Competition","Debug Data"};
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
			final int EXPORT_DEBUGDATA= 4;
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
							LogFileWriter.writeLog(e);
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
							LogFileWriter.writeLog(e);
						}
						break;

					case EXPORT_PUNCHES:
						try {						
							String punchList = mMainActivity.competition.getCompetitors().exportPunchesCsvString();
							AndroidHelper.exportString(mMainActivity, punchList, "punches", mMainActivity.competition.getCompetitionName(), "csv");
						} catch (Exception e) {
							LogFileWriter.writeLog(e);
						}
						break;						

					case EXPORT_COMPETITION:
						try {		
							String competitionList = CompetitionHelper.getCompetitionAsString( mMainActivity.competition );
							AndroidHelper.exportString(mMainActivity, competitionList, "competition", mMainActivity.competition.getCompetitionName(), "csv");


						} catch (Exception e) {
							LogFileWriter.writeLog(e);
						}					
						break;	
					case EXPORT_DEBUGDATA:
						try {		
					    	File sdCard = Environment.getExternalStorageDirectory();
					    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro/");
							if (dir.exists()) {
								dir.mkdirs();
							}
							File zipFile = new File(sdCard.getAbsolutePath() + "/gscEnduro/debugData.zip" );
							if(zipFile.exists()){
								zipFile.delete();
							}
							zipFile = new File(sdCard.getAbsolutePath() + "/gscEnduro/debugData.zip" );
							
							FileOutputStream fos = new FileOutputStream(zipFile);
							ZipOutputStream zos = new ZipOutputStream(fos);
							File[] files = dir.listFiles();
							List<File> allFiles = new ArrayList<File>();
							//Quick hack to list files in dirs with depth 1
							for(File file : files){
								if(file.isDirectory()){
									for(File file1Depth : file.listFiles() ){
										if(!file1Depth.isDirectory()){
											allFiles.add(file1Depth);
										}
									}
								}
								else{
									allFiles.add(file);
								}
							}
							for(File file : allFiles) {
								if(!file.isDirectory() && !file.getName().equals("debugData.zip") && file.length() > 0){
									LogFileWriter.writeLog("debugLog", "Opening " + file.getName() + " with " +(file.length()+10) + " bytes buffer" );
									byte[] buffer = new byte[(int) file.length()+10];
									FileInputStream fis = new FileInputStream(file);
									zos.putNextEntry(new ZipEntry(file.getName()));
									int length = fis.read(buffer);
									LogFileWriter.writeLog("debugLog", "Read " + length + " bytes" );
									zos.write(buffer, 0, length);
									zos.closeEntry();
									fis.close();
								}
							}
							zos.close();
							Intent mailIntent = new Intent(Intent.ACTION_SEND);
							mailIntent.setType("text/plain");
							mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
							mailIntent.putExtra(Intent.EXTRA_SUBJECT, "GSC Enduro debug data");
							mailIntent.putExtra(Intent.EXTRA_TEXT, "GSC Enduro debug data in attached file");
							Uri uri = Uri.fromFile(zipFile);
							mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
							mMainActivity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
							
						} catch (Exception e) {
							LogFileWriter.writeLog(e);
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
