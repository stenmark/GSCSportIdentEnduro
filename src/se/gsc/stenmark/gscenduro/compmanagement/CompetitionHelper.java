package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import se.gsc.stenmark.gscenduro.Result;
import se.gsc.stenmark.gscenduro.TrackResult;
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

	public static int getPosition(int cardNumber,
			ArrayList<TrackResult> trackResults) {
		int i = 1;
		for (TrackResult trackResult : trackResults) {
			if (trackResult.getCardNumber() == cardNumber) {
				return i;
			}
			i++;
		}
		return -1;
	}

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

	public static String getCompetitionAsCsvString(String CompetitionName, List<TrackMarker> Track, ArrayList<Competitor> Competitors) {
		String competitionData = "";

		// Competition Name
		competitionData += "competitionName," + CompetitionName + "\n";

		// Track
		competitionData += "track";
		for (TrackMarker trackMarker : Track) {
			competitionData += "," + trackMarker.getStart() + ","
					+ trackMarker.getFinish();
		}
		competitionData += "\n";

		// Competitor
		for (Competitor competitor : Competitors) {
			competitionData += "competitor," + competitor.getName() + ","
					+ competitor.getCardNumber();

			for (Punch punch : competitor.getCard().getPunches()) {
				competitionData += "," + punch.getTime() + ","
						+ punch.getControl();
			}
			competitionData += "\n";
		}
		competitionData += "\n";

		return competitionData;
	}

	public static String getResultsAsCsvString(List<TrackMarker> Track, List<Result> ResultLandscape) {
		String resultData = "";
		/*		

		resultData = "Rank,Name,Card Number,Total Time,";
		for (int i = 0; i < Track.size(); i++) {
			resultData += "Stage " + (i + 1) + ",RK,";
		}
		resultData += "\n";

		int rank = 1;
		for (ResultLandscape res : ResultLandscape) {
			
			String name = ((MainActivity) mContext).competition.getCompetitor(cardNumber).getName();
			
			resultData += String.valueOf(rank) + "," + res.getName() + "," + res.getCardNumber() + ",";

			if (res.getTotalTime() == Integer.MAX_VALUE) {
				resultData += "--:--" + ",";
			} else {
				if (res.getTotalTime() == 0) {
					resultData += "--:--" + ",";
				} else {

					resultData += CompetitionHelper.secToMinSec(res.getTotalTime()) + ",";
				}
			}

			int i = 0;
			for (Long Time : res.getTime()) {
				if (Time == Integer.MAX_VALUE) {
					resultData += "--:--" + ",";
				} else {
					resultData += CompetitionHelper.secToMinSec(Time) + ",";
				}

				int pos = res.getRank().get(i);
				if (pos == (long) Integer.MAX_VALUE) {
					resultData += "-,";
				} else {
					resultData += String.valueOf(pos) + ",";
				}
				i++;
			}
			rank++;
			resultData += "\n";
		}

		resultData += "\n\n";
*/
		return resultData;
	}

	public static Boolean checkNameExists(List<Competitor> competitors,
			String Name) {

		for (int i = 0; i < competitors.size(); i++) {
			if (Name.equalsIgnoreCase(competitors.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	public static Boolean checkCardNumberExists(List<Competitor> competitors,
			int cardNumber) {

		for (int i = 0; i < competitors.size(); i++) {
			if (cardNumber == competitors.get(i).getCardNumber()) {
				return true;
			}
		}
		return false;
	}

	public static Boolean checkStartNumberExists(List<Competitor> competitors,
			int startNumber) {

		for (int i = 0; i < competitors.size(); i++) {
			if (startNumber == competitors.get(i).getStartNumber()) {
				return true;
			}
		}
		return false;
	}

	public static String getPunchesAsCsvString(List<Competitor> competitors) {
		String PunchList = "";

		if (competitors != null && !competitors.isEmpty()) {
			Collections.sort(competitors);
			for (Competitor competitor : competitors) {
				PunchList += competitor.getCardNumber() + ",";

				Card card = new Card();

				card = competitor.getCard();

				if (card != null) {
					Collections.sort(card.getPunches(),
							new Comparator<Punch>() {
								@Override
								public int compare(Punch s1, Punch s2) {
									return new Long(s1.getTime()).compareTo(s2
											.getTime());
								}
							});

					for (Punch punch : card.getPunches()) {
						PunchList += punch.getControl() + "," + punch.getTime()
								+ ",";
					}

					PunchList += "\n";
				}
			}
		}
		return PunchList;
	}

	public static String getCompetitorsAsCsvString(List<Competitor> competitors) {
		String competitorList = "";

		if (competitors != null && !competitors.isEmpty()) {
			Collections.sort(competitors);
			for (Competitor competitor : competitors) {
				competitorList += competitor.getName() + ","
						+ competitor.getCardNumber() + "\n";
			}
		}

		return competitorList;
	}

	/**
	 * Finds a competitor in list of competitors supplied, it searches for the
	 * competitors card number. If no competitor with the supplied cardNumer is
	 * found, null is returned.
	 * 
	 * @param cardToMatch
	 * @param competitors
	 * @return
	 */
	public static Competitor findCompetitor(Card cardToMatch,
			List<Competitor> competitors) {
		for (Competitor competitor : competitors) {
			if (competitor.getCardNumber() == cardToMatch.getCardNumber()) {
				return competitor;
			}
		}
		return null;
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

	/*
	 * public static List<String> getSavedCompetitionsAsList(){ List<String>
	 * result = new ArrayList<String>(); String[] fileList =
	 * MainApplication.getAppContext().fileList(); for (String file : fileList)
	 * { if (!file.equals(Competition.CURRENT_COMPETITION)) { result.add(file);
	 * }
	 * 
	 * } return result; }
	 */
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

	/*
	 * public static String getSavedCompetitions(){ String result = ""; String[]
	 * fileList = MainApplication.getAppContext().fileList(); for (String file :
	 * fileList) { if (!file.equals(Competition.CURRENT_COMPETITION)) { result
	 * += file + "\n"; }
	 * 
	 * } return result; }
	 */
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
	public static Punch findPunchForStationNrInCard(Card card,
			long stationNumber, int instanceNumber) {
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
	 * Loops through the track and associates each TrackMarker in the track with
	 * a punch from the card. It then calculates a track time, i.e. how long did
	 * it take to reach from each pair of trackmarkers in the track. This method
	 * will only work on a coherent number of punches. I.e. all double punches
	 * and punches on Trackmarkers not in the track must be removed before
	 * calling this method
	 * 
	 * @param card
	 * @param track
	 * @return a list of Long Integers denoting the time it took to get through
	 *         each pair of track markers
	 */
	public static List<Long> extractResultFromCard(Card card,
			List<TrackMarker> track) {
		List<Long> result = new ArrayList<Long>();

		try {
			for (int i = 0; i < track.size(); i++) {
				TrackMarker trackMarker = track.get(i);
				Punch startPunch = findPunchForStationNrInCard(card,
						trackMarker.getStart(), i + 1);
				Punch finishPunch = findPunchForStationNrInCard(card,
						trackMarker.getFinish(), i + 1);
				long trackTime = finishPunch.getTime() - startPunch.getTime();
				result.add(trackTime);
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
					errorMsg += "Could not create directory: "
							+ dir.getAbsolutePath();

					Toast.makeText(activity, "Error = " + errorMsg,
							Toast.LENGTH_LONG).show();
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
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro " + filetype
						+ " for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT, filetype
						+ " in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent,
						"Send mail"));
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
		if (bgDrawable != null)
			// has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			// does not have background drawable, then draw white background on
			// the canvas
			canvas.drawColor(Color.WHITE);
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

			childView.layout(0, 0, childView.getMeasuredWidth(),
					childView.getMeasuredHeight());
			childView.setDrawingCacheEnabled(true);
			childView.buildDrawingCache();
			bmps.add(childView.getDrawingCache());
			allitemsheight += childView.getMeasuredHeight();
		}

		Bitmap bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(),
				allitemsheight, Bitmap.Config.ARGB_8888);
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
