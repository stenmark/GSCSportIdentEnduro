package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided
 * for each method call (no internal variables allowed)
 * 
 * @author Andreas
 * 
 */
public abstract class CompetitionHelper {
	private static final String DEBUG_FILENAME = "debugData.txt";
	
	/**
	 * Generate an RGB value for a transition from Red to Green.
	 * @return RGB coded color
	 */
	public static int generateRedToGreenColorTransition(Long fastestTimeOnStage, Long slowestTimeOnStage, Long competitorStageTime, int rank){		
		if (rank == Competition.RANK_DNF) {
			return Color.WHITE;
		} else {
			if( competitorStageTime > slowestTimeOnStage){
				//If this is the slowest competitor, or slower (slower is possible due to filtering out very slow users from "sloesttime")
				//Then return  a very sharp red
				return android.graphics.Color.HSVToColor(new float[]{0f,1f,1f});
			}
			if( competitorStageTime == fastestTimeOnStage){
				//If this is the fastest competitor on stage, return a very sharp green
				return android.graphics.Color.HSVToColor(new float[]{110f,1f,1f});
			}
			float myTimeDiff = competitorStageTime - fastestTimeOnStage; 
			float stageTimeDiff = slowestTimeOnStage - fastestTimeOnStage; 
			float transition;
			if (stageTimeDiff != 0) {
				transition = 1f - (myTimeDiff / stageTimeDiff);				
			} else {
				transition = 1f;
			}
			
			float hue = 10f + (transition*70f);  //the full red and full green are very close to each other for the eye. So dont use full red and full green
		    return android.graphics.Color.HSVToColor(new float[]{hue,1f,1f});
		}			
	}	
	
	//OLD VERSION
	public static String getResultsAsHtmlString(String name, String date, Stages stage, Competitors competitors, int type, Competition competition) {
		return "Not done yet";
	}
//	public static String getResultsAsHtmlString(String name, String date, Stages stage, List<Results> results, Competitors competitors, int type, Competition competition) {
//		String resultData = "<!DOCTYPE html>\n<html>\n<body>\n";		
//		resultData += "<style>\ntable, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\n}\nth, td {\npadding: 5px;\n}\n</style>\n";		
//		resultData += "<h1>" + name + ", " + date + "</h1>\n";
//		
//		for (int index = 0; index < results.size(); index++) {
//			
//			if (type == 1)  {				
//				if (index == 0) {
//					resultData += "<h1>" + results.get(index).getTitle() + "</h1>\n";
//					resultData += "<table style=\"width:100%\">\n";
//					resultData += "<tr>\n<th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Team</th><th>Start Number</th><th>Total Time</th>";
//					for (int i = 0; i < stage.size(); i++) {
//						resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//					}
//					resultData += "</tr>\n";							
//				} else if (results.get(index).getTitle() != results.get(index - 1).getTitle()) {
//					resultData += "</table>\n";
//					resultData += "<h1>" + results.get(index).getTitle() + "</h1>\n";
//					resultData += "<table style=\"width:100%\">\n";
//					resultData += "<tr>\n<th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Team</th><th>Start Number</th><th>Total Time</th>";
//					for (int i = 0; i < stage.size(); i++) {
//						resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//					}
//					resultData += "</tr>\n";		
//				}				
//			} else if (index == 0) {
//				resultData += "<table style=\"width:100%\">\n";
//				resultData += "<tr><th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Total Time</th>";
//				
//				for (int i = 0; i < stage.size(); i++) {
//					resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//				}
//				resultData += "</tr>\n";
//			}			
//			
//			resultData += "<tr>\n";								
//			int rank = results.get(index).getStageResult().get(0).getRank();
//			resultData += "<td><center>";
//			if (rank == Competition.RANK_DNF) {			
//				resultData += "-";
//			} else {
//				resultData += rank;
//			}
//			resultData += "</center></td>";
//			
//			int cardNumber = results.get(index).getStageResult().get(0).getCardNumber();
//			resultData += "<td>" + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()) + "</td>";			
//			resultData += "<td><center>" + cardNumber + "</center></td>";
//			
//			Log.d("html", "convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()) = " + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()));
//			Log.d("html", "competitors.getByCardNumber(cardNumber).getName() = " + competitors.getByCardNumber(cardNumber).getName());
//			
//			if (type == 1)  {
//				resultData += "<td>" + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getTeam()) + "</td>";					
//				resultData += "<td><center>" + String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + "</center></td>";
//			}
//			resultData += "<td><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(0).getStageTime()) + "</center></td>";	
//						
//			for(int stageNumber = 1; stageNumber < results.get(index).getStageResult().size(); stageNumber++) {			
//				
//				if (results.get(index).getStageResult().get(stageNumber).getRank() != Competition.RANK_DNF) {								
//					Long fastestTimeOnStage = competition.getFastestOnStage(results.get(index).getTitle(), stageNumber); 
//					Long slowestTimeOnStage = competition.calculateSlowestOnStage(results.get(index).getTitle(), stageNumber);
//					Long competitorStageTime = results.get(index).getStageResult().get(stageNumber).getStageTime();
//					
//					int color = CompetitionHelper.generateRedToGreenColorTransition(fastestTimeOnStage, slowestTimeOnStage, competitorStageTime, rank);
//	
//					color -= 0xff000000;
//					resultData += "<td bgcolor=\"#" + Integer.toHexString(color) + "\"><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTime()) + "</center></td>";
//					resultData += "<td><center>" + results.get(index).getStageResult().get(stageNumber).getRank() + "</center></td>";
//					resultData += "<td><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTimesBack()) + "</center></td>";
//				} else {
//					resultData += "<td><center>-</center></td>";
//					resultData += "<td><center>-</center></td>";
//					resultData += "<td><center>-</center></td>";
//				}
//			}			
//			resultData += "</tr>\n";
//		}		
//		
//		resultData += "</table>\n</body>\n</html>\n";
//		
//		return resultData;
//	}
	
	/**
	 * Searched the Android file system for saved competitions and returns a
	 * list with filenames for all files found. The current competition is
	 * excluded from the list, only competitions manually saved by the users is
	 * returned.
	 * 
	 * @return
	 */
	public static List<String> getSavedCompetitionsAsList() {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
		if (!dir.exists()) {
			return new ArrayList<String>();
		}

		File[] fileList = dir.listFiles();
		List<String> result = new ArrayList<String>();
		for (File file : fileList) {
			if (file.getName().contains(".dat")) {
				result.add(file.getName().replace(".dat", ""));
			}
		}
		return result;
	}

	/**
	 * Does the same as getSavedCompetitionsAsList() but instead of returning a
	 * list it returns a new line separated String of competitions.
	 * 
	 * @return
	 */
	public static String getSavedCompetitions() {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
		if (!dir.exists()) {
			return "";
		}

		String result = "";
		File[] fileList = dir.listFiles();
		for (File file : fileList) {
			if (file.getName().contains(".dat")) {
				result += file.getName().replace(".dat", "") + "\n";
			}
		}
		return result;
	}
	
	/**
	 * Simple helper to ensure that we can write to Android filesystem.
	 * 
	 * @return
	 */
	public static boolean isExternalStorageWritable() {
		try {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static void exportString(Activity activity, String StringToSend, String exporttype, String compName, String filetype) throws IOException {
		String errorMsg = "";

		if (CompetitionHelper.isExternalStorageWritable()) {
			File sdCard = Environment.getExternalStorageDirectory();
			compName = compName.replace(" ", "_");

			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				errorMsg += "Dir does not exist " + dir.getAbsolutePath();
				if (!dir.mkdirs()) {
					errorMsg += "Could not create directory: " + dir.getAbsolutePath();

					Toast.makeText(activity, "Error = " + errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
			}

			File file = new File(dir, compName + "_" + exporttype + "." + filetype);

			FileWriter fw = new FileWriter(file);
			fw.write(StringToSend);

			if (activity != null) {
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro " + exporttype + " for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT, exporttype + " in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			errorMsg = "External file storage not available, could not export competitors";
			Toast.makeText(activity, "Error = " + errorMsg, Toast.LENGTH_LONG).show();
			return;
		}
	}

	public static Bitmap getBitmapFromView(View view) {
		// Define a bitmap with the same size as the view
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		// Bind a canvas to it
		Canvas canvas = new Canvas(returnedBitmap);
		// Get the view's background
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null) {
			// has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		} else {
			// does not have background drawable, then draw white background on
			// the canvas
			canvas.drawColor(Color.WHITE);
		}
		// draw the view on the canvas
		view.draw(canvas);
		// return the bitmap
		return returnedBitmap;
	}

	public static File writeImageToFile(String fileName, Bitmap image) {
		FileOutputStream out = null;
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
		File file = new File(dir, fileName);
		try {
			out = new FileOutputStream(file);
			image.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return file;
	}

	public static Bitmap getWholeListViewItemsToBitmap(ListView listview) {
		ListAdapter adapter = listview.getAdapter();
		int itemscount = adapter.getCount();
		int allitemsheight = 0;
		List<Bitmap> bmps = new ArrayList<Bitmap>();

		for (int i = 0; i < itemscount; i++) {
			View childView = adapter.getView(i, null, listview);
			childView.measure(MeasureSpec.makeMeasureSpec(listview.getWidth(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED));

			childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
			childView.setDrawingCacheEnabled(true);
			childView.buildDrawingCache();
			bmps.add(childView.getDrawingCache());
			allitemsheight += childView.getMeasuredHeight();
		}

		Bitmap bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
		Canvas bigcanvas = new Canvas(bigbitmap);

		Paint paint = new Paint();
		int iHeight = 0;

		for (int i = 0; i < bmps.size(); i++) {
			Bitmap bmp = bmps.get(i);
			bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
			iHeight += bmp.getHeight();

			bmp.recycle();
			bmp = null;
		}

		return bigbitmap;
	}
	
	public static void dumpDebugDatToFile( String debugData ){
		BufferedWriter bw = null;
		try{
			File sdCard = Environment.getExternalStorageDirectory();
	    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, DEBUG_FILENAME );
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(debugData);
    	}
    	catch( Exception e){
    		//Do nothing
    	}
    	finally {
			if( bw != null){
				try {
					bw.close();
				} catch (Exception e) {
					//Do nothing
				}
			}
		}
	}
	
}
