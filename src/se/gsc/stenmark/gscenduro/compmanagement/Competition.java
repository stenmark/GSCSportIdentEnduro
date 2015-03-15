package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.Result;
import se.gsc.stenmark.gscenduro.ResultLandscape;
import se.gsc.stenmark.gscenduro.TrackResult;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * Represents a competition with a track, List of competitors and the name of the competition.
 * The competition can be saved to disc and loaded back from disc. 
 * @author Andreas
 *
 */
public class Competition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String CURRENT_COMPETITION = "current_competition";
	
	private List<TrackMarker> track = null;
	private ArrayList<Competitor> competitors = null;
	private List<Result> mResults = null;
	private List<ResultLandscape> mResultLandscape = null;
	
	public String competitionName;		
	
	public Competition(){
		track = new ArrayList<TrackMarker>();
		competitors = new ArrayList<Competitor>();
		mResults = new ArrayList<Result>();
		mResultLandscape = new ArrayList<ResultLandscape>();
		competitionName = "New";
		
		try {
			saveSessionData( null );
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}	
	}
	
	public List<TrackMarker> getTrack() {
		return track;
	}
	
	public ArrayList<Competitor> getCompetitors() {
		return competitors;
	}

	public List<Result> getResults() {
		return mResults;
	}	
	
	public List<ResultLandscape> getResultLandscape() {
		return mResultLandscape;
	}	
		
	public Boolean checkNameExists(String Name){
		
		for (int i = 0; i < competitors.size(); i++)
		{
			if (Name.equalsIgnoreCase(competitors.get(i).name))
			{
				return true;
			}
		}
		return false;
	}
	
	public Boolean checkCardNumberExists(int cardNumber){
		
		for (int i = 0; i < competitors.size(); i++)
		{
			if (cardNumber == competitors.get(i).cardNumber)
			{
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Add a new Competitor to the competition.
	 * @param name 
	 * @param cardNumber the number of the SI card for this user
	 */
	public void addCompetitor(String name, int cardNumber){
		Competitor competitor = new Competitor( name );	
		competitor.cardNumber = cardNumber;
		competitors.add(competitor);		
		calculateResults();
	}
		
	public String addMultiCompetitors(String multiCompetitors) throws IOException{
		String name = "";
		String cardNumber = "";
		String status = "";
		int pos = 0;
		
		//multiCompetitors = "a;1\nb;2\nc;3";
												
		BufferedReader bufReader = new BufferedReader(new StringReader(multiCompetitors));
		String line = null;
		int lineNumber = 0;
		while((line = bufReader.readLine()) != null)
		{
			lineNumber++;
			
			status += Integer.toString(lineNumber) + ". "; 
					
			//count so only one ; each line
			int number = 0;
			for (int i = 0, len = line.length(); i < len; ++i) {
                Character c = line.charAt(i);
                if (c == ';')
                {
                	number++;
                }
            }
			
			if (number != 1) 
			{
				if (line.length() == 0)
				{
					status += "Error adding, because of empty line\n";
				}
				else
				{
					status += "Error adding, because of ; = " + line +"\n";
				}
			}
			else
			{
				pos = line.indexOf(";", 0);							
				name = line.substring(0, pos);	
				cardNumber = multiCompetitors.substring(pos + 1, line.length());

			
				if (!cardNumber.matches("\\d+"))
				{
					status += "Error, cardnumber not a number\n";
				}
				else if (checkNameExists(name))
				{
					status += "Error adding " + name + ", it already exists a competitor with that name\n";
				}
				else if (checkCardNumberExists(Integer.parseInt(cardNumber)))
				{
					status += "Error adding " + name + ", it already exists a competitor with that cardnumber\n";
				} 
				else 
				{	
					addCompetitor(name, Integer.parseInt(cardNumber));
					status += name + ", " + cardNumber + " added\n";
				}									
			}
			
		}	
		calculateResults();
		return status;
	}	
	
	/**
	 * Will find and update the SI card number for the given user
	 * If user is found nothing happens.
	 * @param nameToModify
	 * @param newCardNumber 
	 */
	public void updateCompetitorCardNumber(int index, String newName, String newCardNumber){
		Competitor newCompetitor = null;		
		newCardNumber = newCardNumber.replace(" ", "");
		
		newCompetitor = competitors.get(index);
		newCompetitor.name = newName;
		newCompetitor.cardNumber = Integer.parseInt(newCardNumber);

		competitors.set(index, newCompetitor);
			
	    Collections.sort(competitors, new Comparator<Competitor>() {
	        @Override
	        public int compare(Competitor s1, Competitor s2) {
	            return s1.name.compareToIgnoreCase(s2.name);
	        }
	    });
	    
	    calculateResults();
	}
	
	/**
	 * Remove a competitor from the competition. Will find and delete competitor by searching for the specified name.
	 * If the name is not found nothing happens
	 * @param nameToDelete
	 */
	public void removeCompetitor( String nameToDelete ){
		for (Competitor competitor : competitors ) {
			if (competitor.name.equals(nameToDelete)) {
				competitors.remove(competitor);
				break;
			}
		}
		
		calculateResults();
	}
	
	public int getNumberOfTracks(){
		if (track != null)
		{
			return track.size();
		}
		return 0;		
	}
	
	/**
	 * Will remove the old track and replace it with the new one.
	 * The input is a comma separated list of track markers (SI control unit)
	 * The user need to specify the name of the track markers as String with integers, 
	 * where each Integer represents the number programmed in to theSI control unit 
	 * @param newTrack comma separated list of Integers i.e. "71,72,71,72"
	 */
	public void addNewTrack( String newTrack){
		String[] trackMarkers = newTrack.split(",");
		track.clear();
		for (int i = 0; i < trackMarkers.length; i += 2) {
			
			int startMarker = 0;
			int finishMarker = 0;
			startMarker = Integer.parseInt(trackMarkers[i]);
			finishMarker = Integer.parseInt(trackMarkers[i + 1]);
			track.add(new TrackMarker(startMarker, finishMarker));
		}
		
		calculateResults();
	}
	
	/**
	 * Reads the current track and returns at as a comma separated String of Integers.
	 * @return
	 */
	public String getTrackAsString(){
		String trackAsString = " ";
		if ( !track.isEmpty() && track != null ) {
			int i = 0;
			for (TrackMarker trackMarker : track ) {
				i++;
				trackAsString += ", SS" + i + ": "+ trackMarker.start + "->" + trackMarker.finish;
			}
		} else {
			trackAsString += " No track loaded";
		}
		return trackAsString;
	}
	
	/**
	 * Takes the whole current competition object and serializes it to the Android file system. 
	 * It is written to the applications private storage, so it wont be accessible from outside this program.
	 * If the competition name is empty it will be treated as "current competition" 
	 * this is run time data that need to be saved to disc when the application or GUI is being deallocated from memory by the android system.
	 * @param competionName
	 * @throws IOException
	 */
	public void saveSessionData( String competionName ) throws IOException {
		if( CompetitionHelper.isExternalStorageWritable() ) {
			FileOutputStream fileOutputComp;
			if (competionName == null || competionName.isEmpty()) {
				fileOutputComp = MainApplication.getAppContext().openFileOutput( CURRENT_COMPETITION, Context.MODE_PRIVATE);
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

			objStreamOutComp.writeObject(this);
			objStreamOutComp.close();
		}
	}
/*	
	public void saveSessionData( String competionName ) throws IOException {
		FileOutputStream fileOutputComp;
		if (competionName == null || competionName.isEmpty()) {
			fileOutputComp = MainApplication.getAppContext().openFileOutput( CURRENT_COMPETITION, Context.MODE_PRIVATE);
		} 
		else {
			competionName = competionName.replace(" ", "_");
			fileOutputComp = MainApplication.getAppContext().openFileOutput(competionName , Context.MODE_PRIVATE);
		}

		ObjectOutputStream objStreamOutComp = new ObjectOutputStream(fileOutputComp);
		objStreamOutComp.writeObject(this);
		objStreamOutComp.close();
	}
*/
	
	/**
	 * Loads a serialized competition object from Android file system and returns the competition loaded from disc.
	 * If the competition name is empty it will be treated as "current competition" 
	 * this is run time data that is read back as the active competition when the app is brought back by the android system.
	 * @param competionName
	 * @return
	 * @throws StreamCorruptedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Competition loadSessionData(String competionName ) throws StreamCorruptedException, IOException, ClassNotFoundException {
		FileInputStream fileInputComp = null;

		if (competionName == null || competionName.isEmpty()) {
			fileInputComp = MainApplication.getAppContext().openFileInput(CURRENT_COMPETITION);
		} 
		else {
			File sdCard = Environment.getExternalStorageDirectory();
			competionName = competionName.replace(" ", "_");
			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, competionName + ".dat");
			fileInputComp = new FileInputStream(file);
			
			//fileInputComp = MainApplication.getAppContext().openFileInput(competionName);
		}
		ObjectInputStream objStreamInComp = new ObjectInputStream( fileInputComp);
		Competition loadCompetition = (Competition) objStreamInComp.readObject();
		objStreamInComp.close();
				
		return loadCompetition;
	}	
/*	
	public static Competition loadSessionData(String competionName ) throws StreamCorruptedException, IOException, ClassNotFoundException {
		FileInputStream fileInputComp = null;

		if (competionName == null || competionName.isEmpty()) {
			fileInputComp = MainApplication.getAppContext().openFileInput(CURRENT_COMPETITION);
		} 
		else {
			fileInputComp = MainApplication.getAppContext().openFileInput(competionName);
		}
		ObjectInputStream objStreamInComp = new ObjectInputStream( fileInputComp);
		Competition loadCompetition = (Competition) objStreamInComp.readObject();
		objStreamInComp.close();
		
		return loadCompetition;
	}
*/	
	/**
	 * When a new card is read call this method to add the SI card data to the competition.
	 * Will first search for the card number to find which competitor it belongs to.
	 * It will try to make the card data coherent by removing double punches
	 * It will read all the remaining punched and calculate the track time for each track in the competition
	 * After this method is completed the competitor should have all its competition data added to the competition.
	 * @param newCard the SI card read and that should be added to a competitor 
	 * @return a message String informing on have the parsing of the SI card data went
	 */
	public String processNewCard(Card newCard){	
		String returnMsg = "";
		Competitor foundCompetitor = CompetitionHelper.findCompetitor(newCard, competitors);
		if (foundCompetitor == null) {
			return "Read new card with card number: " + newCard.cardNumber	+ " Could not find any competitor with this number";			
		}		
		newCard.findDoublePunches();
		foundCompetitor.card = newCard;

		returnMsg += "New card read for "+ foundCompetitor.name + " ";

		//The results is a List of Long Integers where each integer represent the time the competitor took to complete the track
		//i.e. first entry will be for SS1, second SS2 etc.
		List<Long> results = new ArrayList<Long>();
		results = CompetitionHelper.extractResultFromCard(newCard, track );
		
	
		foundCompetitor.trackTimes = new ArrayList<Long>();
		int i = 1;
		for (Long trackTime : results) {
			returnMsg +=", Time for SS " + i + " = " + trackTime + " seconds ";
			foundCompetitor.trackTimes.add(trackTime);
			i++;
		}

		calculateResults();
		
		if( results.size() != track.size() ){
			return "Not all station punched";
		}
		
		returnMsg += ("Total time was: "+ foundCompetitor.getTotalTime(true) + " seconds \n");			
		
		return returnMsg;
	}
	
	public void messageAlert(Activity activity, String title, String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setIcon(android.R.drawable.ic_dialog_alert);
	    builder.setMessage(message).setTitle(title).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
	    {
	        public void onClick(DialogInterface dialog, int which) {}
	    });
	
	    AlertDialog alert = builder.create();
	    alert.show();	
	}	
	
	public int getPosition(int cardNumber, ArrayList<TrackResult> trackResults)
	{
		int i = 1;
		for (TrackResult trackResult : trackResults) {					
			if (trackResult.getCardNumber() == cardNumber)
			{
				return i; 
			}
			i++;
		}		
		return -1;
	}
	
	public void exportPunchesAsCsv(Activity activity) throws IOException{
		String errorMsg = "";
		
		String PunchList = "Name,Card Number\n";

		if(CompetitionHelper.isExternalStorageWritable()) {
			File sdCard = Environment.getExternalStorageDirectory();
			String compName = competitionName;
			compName = compName.replace(" ", "_");

			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				errorMsg += "Dir does not exist " + dir.getAbsolutePath();
				if (!dir.mkdirs()) {
					errorMsg += "Could not create directory: " + dir.getAbsolutePath();
					
					messageAlert(activity, "Error", errorMsg);
					return;
				}
			}

			File file = new File(dir, compName + "_competitors.csv");

			if(competitors!= null && !competitors.isEmpty()) {
				Collections.sort(competitors);
				for (Competitor competitor : competitors) {
					PunchList += competitor.name + ","	+ competitor.cardNumber + ",";
					
					Card card = new Card();
					
					card = competitor.card;
					
					if (card != null)
					{
					    Collections.sort(card.punches, new Comparator<Punch>() {
					        @Override
					        public int compare(Punch s1, Punch s2) {
					            return s1.getTime().compareTo(s2.getTime());
					        }
					    });	
					    
					    for(Punch punch : card.punches){
					    	PunchList += punch.control + "," + punch.time + ",";					    	
					    }
					    
					    PunchList += "\n";
					}											
				}
			}
			
			FileWriter fw = new FileWriter(file);
			fw.write(PunchList);

			if(activity != null){
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro punches for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT   , "Punches in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			errorMsg = "External file storage not available, could not export punches";
			messageAlert(activity, "Error", errorMsg);
			return;
		}		
	}
	
	/**
	 * Will take the whole competition and create CSV file, Comma Separated Value file.
	 * This is intended to analyze the competition results in an external program, like excel.
	 * The result will not be exported to a file but directly to a mail client of choice for the Android user.
	 * The format of the CSV is for each column "Name,card number, total time, Time for SS1, Time for SS2,...,Time for SSn"
	 * @param fragment need to send the result to a mail client
	 * @return a results string with information intended for the GUI on how the export progress went
	 * @throws IOException
	 */
	public void exportResultAsCsv(Activity activity) throws IOException{
		String errorMsg = "";
		String outputData = "";	

		if( CompetitionHelper.isExternalStorageWritable() ) {
			File sdCard = Environment.getExternalStorageDirectory();
			String compName = competitionName;
			compName = compName.replace(" ", "_");

			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				errorMsg += "Dir does not exist " + dir.getAbsolutePath();
				if (!dir.mkdirs()) {
					errorMsg += "Could not create directory: " + dir.getAbsolutePath();
					messageAlert(activity, "Error", errorMsg);
					return;
				}
			}

			File file = new File(dir, compName + ".csv");

			outputData = "Rank,Name,Card Number,Total Time,";
			for (int i = 0; i < track.size(); i++) {
				outputData += "Stage " + (i + 1) + ",RK,";
			}
			outputData += "\n";
			
			int rank = 1;
			for (ResultLandscape res : mResultLandscape)
			{			
				outputData += String.valueOf(rank) + "," 
						+ res.getName() + ","
						+ res.getCardNumber() + ",";
				
				if (res.getTotalTime() == Integer.MAX_VALUE)
				{
					outputData += "--:--" + ",";
				}
				else
				{					
					if (res.getTotalTime() == 0)
					{
						outputData += "--:--" + ",";
					}
					else
					{
						
						outputData += secToMinSec(res.getTotalTime()) + ",";						
					}	
				}				
				
				int i = 0;
				for (Long Time : res.getTime()) {												
					if (Time == Integer.MAX_VALUE)
					{
						outputData += "--:--" + ",";
					}
					else
					{
						outputData += secToMinSec(Time) + ",";
					}											
					
					int pos = res.getRank().get(i);	
					if (pos == (long) Integer.MAX_VALUE)
					{
						outputData += "-,";
					}
					else
					{
						outputData += String.valueOf(pos) + ",";
					}
					i++;
				}
				rank++;
				outputData += "\n";
			}
			
			outputData += "\n\n";					
		
			FileWriter fw = new FileWriter(file);
			fw.write(outputData);

			if(activity != null){
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro results for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT   , "Results in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			errorMsg = "External file storage not available, could not export results";			
			messageAlert(activity, "Error", errorMsg);
			return;			
		}
	}	
	
	public void exportCompetitorsAsCsv(Activity activity) throws IOException{
		String errorMsg = "";
		
		String competitorList = "Name,Card Number\n";

		if(CompetitionHelper.isExternalStorageWritable()) {
			File sdCard = Environment.getExternalStorageDirectory();
			String compName = competitionName;
			compName = compName.replace(" ", "_");

			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				errorMsg += "Dir does not exist " + dir.getAbsolutePath();
				if (!dir.mkdirs()) {
					errorMsg += "Could not create directory: " + dir.getAbsolutePath();
					
					messageAlert(activity, "Error", errorMsg);
					return;
				}
			}

			File file = new File(dir, compName + "_competitors.csv");

			if(competitors!= null && !competitors.isEmpty() ) {
				Collections.sort(competitors);
				for (Competitor competitor : competitors) {
					competitorList += competitor.name + ","	+ competitor.cardNumber + "\n";
				}
			}
			
			FileWriter fw = new FileWriter(file);
			fw.write(competitorList);

			if(activity != null){
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro competitors for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT   , "Competitors in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				activity.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			errorMsg = "External file storage not available, could not export competitors";
			messageAlert(activity, "Error", errorMsg);
			return;
		}
	}
	
	public String secToMinSec(Long sec)
	{
		if (sec == Integer.MAX_VALUE)
		{
			return "no result";
		}
		else
		{		
			Long totalTime_sec = sec;
			Long toltalTime_min = sec / 60;
			totalTime_sec -= toltalTime_min * 60;
	
			return String.format("%02d:%02d", toltalTime_min, totalTime_sec);
		}
	}	
	
	public void calculateResults()
	{
		int i, j;	
		mResults.clear();
		
		Result result;
		result = new Result("Total time");		
		mResults.add(result);
				
		// Add track titles
		for (i = 1; i < getTrack().size() + 1; i++) {
			result = new Result("Stage " + i);		
			mResults.add(result);
		}		
		
		// Add total times
		String name;	
		int cardNumber;
		Long trackTime = (long) 0;
		Long trackTimeBack = (long) 0;
		TrackResult trackResult;
		
		for (i = 0; i < competitors.size(); i++) {
			trackResult = new TrackResult(competitors.get(i).getName(), competitors.get(i).getCardNumber(), competitors.get(i).getTotalTime(true)); 
			mResults.get(0).mTrackResult.add(trackResult);
		}
		
		Collections.sort(mResults.get(0).mTrackResult, new Comparator<TrackResult>() {
			@Override
			public int compare(TrackResult lhs, TrackResult rhs) {
				return lhs.getTrackTimes().compareTo(rhs.getTrackTimes());
			}
		});		
		
		// Add track times
		for (j = 1; j < mResults.size(); j++) {
			for (i = 0; i < competitors.size(); i++) {
				name = competitors.get(i).getName();		
				cardNumber = competitors.get(i).getCardNumber();
				
				if ( (competitors.get(i).hasResult()) && (competitors.get(i).trackTimes.size() > j - 1) ) {
					trackTime = competitors.get(i).trackTimes.get(j - 1);
				}
				else
				{
					trackTime = (long) Integer.MAX_VALUE;
				}	
					
				trackResult = new TrackResult(name, cardNumber, trackTime); 
				mResults.get(j).mTrackResult.add(trackResult);
			}
			
			Collections.sort(mResults.get(j).mTrackResult, new Comparator<TrackResult>() {
				@Override
				public int compare(TrackResult lhs, TrackResult rhs) {
					return lhs.getTrackTimes().compareTo(rhs.getTrackTimes());
				}
			});											
		}
		for (j = 0; j < mResults.size(); j++) {
			for (i = 0; i < mResults.get(j).mTrackResult.size(); i++) {			
				if (mResults.get(j).mTrackResult.get(i).getTrackTimes() == (long) Integer.MAX_VALUE)				
				{
					trackTimeBack = (long) Integer.MAX_VALUE;					
				}
				else
				{
					if (mResults.get(j).mTrackResult.size() > 0) {			
						trackTimeBack = mResults.get(j).mTrackResult.get(i).getTrackTimes() - mResults.get(j).mTrackResult.get(0).getTrackTimes();
					}
				}	
				mResults.get(j).mTrackResult.get(i).setTrackTimesBack(trackTimeBack);
			}
		}
				
		mResultLandscape.clear();
		if(mResults != null && !mResults.isEmpty()) {		
			ResultLandscape resultLandscapeObject = null;

			if(competitors != null && !competitors.isEmpty()) {
				Collections.sort(competitors);

				for (Competitor competitor : competitors) {
					resultLandscapeObject = new ResultLandscape();
					resultLandscapeObject.setName(competitor.name);
					resultLandscapeObject.setCardNumber(competitor.cardNumber);
					resultLandscapeObject.setTotalTime(competitor.getTotalTime(false));			
															
					if (competitor.trackTimes != null) {						
						int stage = 1;
						for (long time : competitor.trackTimes) {					
							resultLandscapeObject.getTime().add(time);
													
							if (stage < mResults.size())
							{
								int pos = getPosition(competitor.cardNumber, mResults.get(stage).getTrackResult());
								if (pos == -1)
								{
									resultLandscapeObject.getRank().add(Integer.MAX_VALUE);
								}
								else
								{
									resultLandscapeObject.getRank().add(pos);
								}
							}
							stage++;
						}													
					} else {
						for (i = 0; i < track.size(); i++) {
							resultLandscapeObject.getTime().add((long) Integer.MAX_VALUE);
							resultLandscapeObject.getRank().add(Integer.MAX_VALUE);
						}					
					}							

					mResultLandscape.add(resultLandscapeObject);
				}				
			}					
		}
		
		try {
			saveSessionData( null );
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}			
	}
}
