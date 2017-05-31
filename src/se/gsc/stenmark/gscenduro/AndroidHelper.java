package se.gsc.stenmark.gscenduro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
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
import se.gsc.stenmark.gscenduro.compmanagement.Competition;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided
 * for each method call (no internal variables allowed)
 * 
 * @author Andreas
 * 
 */
public abstract class AndroidHelper {
	private static final String DEBUG_FILENAME = "debugData.txt";

	/**
	 * Generate an RGB value for a transition from Red to Green.
	 * @return RGB coded color
	 */
	public static int generateRedToGreenColorTransition(Long fastestTimeOnStage, Long slowestTimeOnStage, Long competitorStageTime, long rank){		
		try{
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
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return 0;
		}
	}	

	/**
	 * Takes the whole competition object and serializes it to the
	 * Android file system. It is written to the applications private storage,
	 * so it wont be accessible from outside this program. If the competition
	 * name is empty it will be treated as "current competition" this is run
	 * time data that need to be saved to disc when the application or GUI is
	 * being deallocated from memory by the android system.
	 * 
	 * @param competionName
	 * @throws IOException
	 */
	public static void saveSessionData(String competionName, Competition competition) {
		try{
			if (AndroidHelper.isExternalStorageWritable()) {
				FileOutputStream fileOutputComp;
				if (competionName == null || competionName.isEmpty()) {
					fileOutputComp = MainApplication.getAppContext().openFileOutput(Competition.CURRENT_COMPETITION, Context.MODE_PRIVATE);
				} else {

					File sdCard = Environment.getExternalStorageDirectory();
					competionName = competionName.replace(" ", "_");
					File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File file = new File(dir, competionName + ".dat");
					fileOutputComp = new FileOutputStream(file);
				}

				ObjectOutputStream objStreamOutComp = new ObjectOutputStream(fileOutputComp);

				objStreamOutComp.writeObject(competition);
				objStreamOutComp.close();
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}

	}

	/**
	 * Loads a serialized competition object from Android file system and
	 * returns the competition loaded from disc. If the competition name is
	 * empty it will be treated as "current competition" this is run time data
	 * that is read back as the active competition when the app is brought back
	 * by the android system.
	 * 
	 * @param competionName
	 * @return
	 * @throws StreamCorruptedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Competition loadSessionData(String competionName) throws StreamCorruptedException, IOException, ClassNotFoundException, InvalidClassException {
		FileInputStream fileInputComp = null;
		Competition loadCompetition = null;
		try{
			if (competionName == null || competionName.isEmpty()) {
				try {
					fileInputComp = MainApplication.getAppContext().openFileInput(Competition.CURRENT_COMPETITION);
				} catch (FileNotFoundException e) {
					// If this is the first time the app is started the file does
					// not exist, handle it by returning an empty competition
					Competition competition = new Competition();
					AndroidHelper.saveSessionData(null,competition);
					AndroidHelper.saveSessionData(competition.getCompetitionName(),competition);
					return competition;
				}
			} else {
				File sdCard = Environment.getExternalStorageDirectory();
				competionName = competionName.replace(" ", "_");
				File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, competionName + ".dat");
				fileInputComp = new FileInputStream(file);
			}
			loadCompetition = null;
			ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
			loadCompetition = (Competition) objStreamInComp.readObject();
			objStreamInComp.close();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return loadCompetition;
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
		List<String> result = new ArrayList<String>();
		try{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				return new ArrayList<String>();
			}

			File[] fileList = dir.listFiles();
			for (File file : fileList) {
				if (file.getName().contains(".dat")) {
					result.add(file.getName().replace(".dat", ""));
				}
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
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
		String result = "";
		try{

			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				return "";
			}

			File[] fileList = dir.listFiles();
			for (File file : fileList) {
				if (file.getName().contains(".dat")) {
					result += file.getName().replace(".dat", "") + "\n";
				}
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
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
		try{
			if (AndroidHelper.isExternalStorageWritable()) {
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
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public static Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = null;
		try{	
			// Define a bitmap with the same size as the view
			returnedBitmap = Bitmap.createBitmap(view.getWidth(),
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
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return returnedBitmap;
	}

	public static File writeImageToFile(String fileName, Bitmap image) {
		FileOutputStream out = null;
		File file = null;
		try{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			file = new File(dir, fileName);
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
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return file;
	}

	public static Bitmap getWholeListViewItemsToBitmap(ListView listview) {
		Bitmap bigbitmap  = null;
		try{
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

			bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
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
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
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
