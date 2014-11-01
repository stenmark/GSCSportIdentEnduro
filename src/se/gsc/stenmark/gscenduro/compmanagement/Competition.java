package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;

public class Competition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String CURRENT_COMPETITION = "current_competition";
	
	private List<TrackMarker> track = null;
	private ArrayList<Competitor> competitors = null;
	public String competitionName;
	
	public Competition(){
		track = new ArrayList<TrackMarker>();
		competitors = new ArrayList<Competitor>();
		competitionName = "New";
	}
		
	public List<TrackMarker> getTrack() {
		return track;
	}

	public void setTrack(List<TrackMarker> track) {
		this.track = track;
	}

	public ArrayList<Competitor> getCompetitors() {
		return competitors;
	}

	public void setCompetitors(ArrayList<Competitor> competitors) {
		this.competitors = competitors;
	}
	
	public void addCompetitor( String name, String cardNumber){
		Competitor competitor = new Competitor( name );	
		if (!cardNumber.isEmpty()) {
			int cardNumberAsInt = Integer.parseInt(cardNumber);
			competitor.cardNumber = cardNumberAsInt;
		}

		competitors.add(competitor);		
	}
	
	public void updateCompetitorCardNumber(String nameToModify, String newCardNumber){
		Competitor compToModify = null;
		for (Competitor competitor : competitors ) {
			if (competitor.name.equals(nameToModify)) {
				compToModify = competitor;
				break;
			}
		}
		if (compToModify != null) {
			compToModify.cardNumber = Integer.parseInt(newCardNumber);
		}
	}
	
	public void removeCompetitor( String nameToDelete ){
		Competitor compToDelete = null;
		for (Competitor competitor : competitors ) {
			if (competitor.name.equals(nameToDelete)) {
				compToDelete = competitor;
				break;
			}
		}
		if (compToDelete != null) {
			competitors.remove(compToDelete);
		}
	}
	
	public void addNewTrack( String newTrack){
		String[] trackMarkers = newTrack.split(",");
		track =  new ArrayList<TrackMarker>();
		for (int i = 0; i < trackMarkers.length; i += 2) {
			
			int startMarker = 0;
			int finishMarker = 0;
			startMarker = Integer.parseInt(trackMarkers[i]);
			finishMarker = Integer.parseInt(trackMarkers[i + 1]);
			track.add(new TrackMarker(startMarker, finishMarker));
		}
	}
	
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
	
	public String processNewCard( Card newCard){
		String returnMsg = "";
		Competitor foundCompetitor = CompetitionHelper.findCompetitor(newCard, competitors);
		if (foundCompetitor == null) {
			return "Read new card with card number: " + newCard.cardNumber	+ " Could not find any competitor with this number";
		}
		newCard.removeDoublePunches();
		foundCompetitor.card = newCard;

		returnMsg += "New card read for "+ foundCompetitor.name + " ";

		List<Long> results = new ArrayList<Long>();
		results = CompetitionHelper.extractResultFromCard(newCard, track );
	
		foundCompetitor.trackTimes = new ArrayList<Long>();
		int i = 1;
		for (Long trackTime : results) {
			returnMsg +=", Time for SS " + i + " = " + trackTime + " seconds ";
			foundCompetitor.trackTimes.add(trackTime);
			i++;
		}

		returnMsg += ("Total time was: "+ foundCompetitor.getTotalTime(true) + " seconds \n");
		
		return returnMsg;
	}
	
	public String exportResultAsCsv( Fragment fragment) throws IOException{
		String responseMsg = "";
		String result = "Name,card number,total time,";
		for (int i = 0; i < track.size(); i++) {
			result += "SS" + (i + 1) + ",";
		}
		result += "\n";

		if( CompetitionHelper.isExternalStorageWritable() ) {
			File sdCard = Environment.getExternalStorageDirectory();
			String compName = competitionName;
			compName = compName.replace(" ", "_");

			File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
			if (!dir.exists()) {
				responseMsg += "Dir does not exist "	+ dir.getAbsolutePath();
				if (!dir.mkdirs()) {
					responseMsg += "Could not create directory: " + dir.getAbsolutePath();
					return responseMsg;
				}
			}

			File file = new File(dir, compName + ".csv");
			responseMsg += "Saving result to file:\n" + file.getAbsolutePath() + "\n";

			if( competitors!= null && !competitors.isEmpty() ) {
				Collections.sort(competitors);
				for (Competitor competitor : competitors) {
					responseMsg+= competitor.name + ","
							+ competitor.cardNumber + ","
							+ competitor.getTotalTime(false) + ",";
					result += competitor.name + ","
							+ competitor.cardNumber + ","
							+ competitor.getTotalTime(false) + ",";
					if (competitor.trackTimes != null) {
						for (long time : competitor.trackTimes) {
							responseMsg += time + ",";
							result += time + ",";
						}
					} else {
						for (int i = 0; i < track.size(); i++) {
							responseMsg += "0,";
							result += "0,";
						}
					}
					responseMsg += "\n";
					result += "\n\n";
				}
			}
			FileWriter fw = new FileWriter(file);
			fw.write(result);

			if( fragment != null){
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro results for " + compName);
				mailIntent.putExtra(Intent.EXTRA_TEXT   , "Results in attached file");
				Uri uri = Uri.fromFile(file);
				mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				fragment.startActivity(Intent.createChooser(mailIntent, "Send mail"));
			}

			fw.close();
		} else {
			return "External file storage not available, coulr not export results";
		}
		

	
		responseMsg += "\nResult exported succesfuly";
		return responseMsg;
	}
}
