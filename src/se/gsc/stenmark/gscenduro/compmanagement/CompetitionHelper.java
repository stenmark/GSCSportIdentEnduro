package se.gsc.stenmark.gscenduro.compmanagement;

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
import se.gsc.stenmark.gscenduro.Results;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided
 * for each method call (no internal variables allowed)
 * 
 * @author Andreas
 * 
 */
public class CompetitionHelper {

	public static String secToMinSec(Long sec) {
		if (sec == Integer.MAX_VALUE) {
			return "no result";
		} else {
			Long totalTime_sec = sec;
			Long toltalTime_min = sec / 60;
			totalTime_sec -= toltalTime_min * 60;

			return String.format("%02d:%02d", toltalTime_min, totalTime_sec);
		}
	}

	public static Long getFastestOnStage(List<Results> results, String competitorClass, int stageNumber) {
		Long fastestTimeOnStage = Long.MAX_VALUE;
		for(Results resultFastest : results){
			
			if (competitorClass == resultFastest.getTitle()) {						
				try{
					fastestTimeOnStage = Math.min(fastestTimeOnStage, resultFastest.getStageResult().get(stageNumber).getStageTimes());
				}
				catch(IndexOutOfBoundsException e){	
				}
			}
		}
		return fastestTimeOnStage;
	}
	
	public static Long getSlowestOnStage(List<Results> results, String competitorClass, int stageNumber) {
		Long slowestTimeOnStage = Long.MIN_VALUE;
		for(Results resultSlowest : results){
			if (competitorClass == resultSlowest.getTitle()) {
				try{
					slowestTimeOnStage = Math.max(slowestTimeOnStage, resultSlowest.getStageResult().get(stageNumber).getStageTimes());
				}
				catch(IndexOutOfBoundsException e){	
				}
			}
		}
		
		return slowestTimeOnStage;
	}	
	
	public static String getResultsAsCsvString(Stages stage, List<Results> results, Competitors competitors, int type) {
		String resultData = "";
		
		if (type == 1)  {
			resultData = "Rank,Name,Card Number,Team,Start Number,Total Time,";
		} else {
			resultData = "Rank,Name,Card Number,Total Time,";
		}
			
		for (int i = 0; i < stage.size(); i++) {
			resultData += "Stage " + (i + 1) + ",Rank,Time Back,";
		}
		resultData += "\n";

		for (int index = 0; index < results.size(); index++) {
			if (type == 1)  {
				if (index == 0) {
					resultData += results.get(index).getTitle() + "\n";
				} else if (results.get(index).getTitle() != results.get(index - 1).getTitle()) {
					resultData += results.get(index).getTitle() + "\n";
				}
			}				
			
			int cardNumber = results.get(index).getStageResult().get(0).getCardNumber();					
			int rank = results.get(index).getStageResult().get(0).getRank();					
			if (rank == Integer.MAX_VALUE) {			
				resultData += "-,";
			} else {
				resultData += rank + ",";
			}
			
			resultData += competitors.getByCardNumber(cardNumber).getName() + ",";
			resultData += cardNumber + ",";
			if (type == 1)  {
				resultData += competitors.getByCardNumber(cardNumber).getTeam() + ",";					
				resultData += String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + ",";
			}
			resultData += CompetitionHelper.secToMinSec(results.get(index).getStageResult().get(0).getStageTimes()) + ",";	
									
			for(int stageNumber = 1; stageNumber < results.get(index).getStageResult().size(); stageNumber++) {										
				resultData += CompetitionHelper.secToMinSec(results.get(index).getStageResult().get(stageNumber).getStageTimes()) + ",";
				resultData += results.get(index).getStageResult().get(stageNumber).getRank() + ",";
				resultData += CompetitionHelper.secToMinSec(results.get(index).getStageResult().get(stageNumber).getStageTimesBack()) + ",";
			}			
			resultData += "\n";
		}
		
		return resultData;
	}

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
	 * Will map a specific punch for the supplied card for a specific SI card
	 * station. It will find the n:th punch of the specified station number on
	 * the card, denoted by instanceNumber. I.e. if the card has punches from
	 * the following station number: 71,72,71,72,71,72 Then if: StationNumber =
	 * 71 and instanceNumber = 3 it will find the punch for 71,72,71,72, ->71<-
	 * ,72 StationNumber = 72 and instanceNumber = 1 it will find the punch for
	 * 71, ->72<- ,71,72,71,72 If the punch is not found null is returned. i.e.
	 * StationNumber = 72 and instanceNumber = 4 -> null is returned
	 * 
	 * @param card
	 * @param stationNumber
	 * @param instanceNumber
	 * @return
	 */
	public static Punch findPunchForStationNrInCard(Card card, long stationNumber, int instanceNumber) {
		int i = 0;
		for (Punch punch : card.getPunches()) {
			if (punch.getControl() == stationNumber) {
				if (!punch.getMarkAsDoublePunch()) {
					i++;
					if (i == instanceNumber) {
						return punch;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Loops through the stages and associates each StageControl in the stages with
	 * a punch from the card. It then calculates a stage time, i.e. how long did
	 * it take to reach from each pair of stageControls in the stage. This method
	 * will only work on a coherent number of punches. I.e. all double punches
	 * and punches on stageControls not in the stage must be removed before
	 * calling this method
	 * 
	 * @param card
	 * @param stage
	 * @return a list of Long Integers denoting the time it took to get through
	 *         each pair of stage controls
	 */
	public static List<Long> extractResultFromCard(Card card, Stages stages) {
		List<Long> result = new ArrayList<Long>();

		try {
			for (int i = 0; i < stages.size(); i++) {
				StageControls stageControls = stages.get(i);
				Punch startPunch = findPunchForStationNrInCard(card, stageControls.getStart(), i + 1);
				Punch finishPunch = findPunchForStationNrInCard(card, stageControls.getFinish(), i + 1);
				long stageTime = finishPunch.getTime() - startPunch.getTime();
				result.add(stageTime);
			}
		} catch (Exception e) {

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

	public static void exportString(Activity activity, String StringToSend,
			String filetype, String compName) throws IOException {
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

			File file = new File(dir, compName + "_" + filetype + ".csv");

			FileWriter fw = new FileWriter(file);
			fw.write(StringToSend);

			if (activity != null) {
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro " + filetype + " for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT, filetype + " in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			errorMsg = "External file storage not available, could not export competitors";
			Toast.makeText(activity, "Error = " + errorMsg, Toast.LENGTH_LONG)
					.show();
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
}
