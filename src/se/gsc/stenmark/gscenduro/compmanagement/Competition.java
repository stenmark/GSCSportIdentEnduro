package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.PopupMessage;
import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.ResultListFragment;
import se.gsc.stenmark.gscenduro.StartScreenFragment;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.TextView;

public class Competition {

	private static List<TrackMarker> track = null;
	private ArrayList<Competitor> competitors = null;
	
	public Competition(){
		track = new ArrayList<TrackMarker>();
		competitors = new ArrayList<Competitor>();
	}
		
	public static List<TrackMarker> getTrack() {
		return track;
	}

	public static void setTrack(List<TrackMarker> track) {
		Competition.track = track;
	}

	public ArrayList<Competitor> getCompetitors() {
		return competitors;
	}

	public void setCompetitors(ArrayList<Competitor> competitors) {
		this.competitors = competitors;
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
		String compName = "New";
		if( track != null ) {
			if (!track.isEmpty()) {
				compName = track.get(0).compName;
			}
		}

		for (int i = 0; i < trackMarkers.length; i += 2) {
			
			int startMarker = 0;
			int finishMarker = 0;
			startMarker = Integer.parseInt(trackMarkers[i]);
			finishMarker = Integer.parseInt(trackMarkers[i + 1]);
			track.add(new TrackMarker(startMarker, finishMarker, compName));
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
	
	public void saveSessionData(String competionName, FragmentManager fragmentManager) {
		try {
			FileOutputStream fileOutputComp;
			FileOutputStream fileOutputTrack;
			try {
				if (competionName == null || competionName.isEmpty()) {
					fileOutputComp = MainApplication
							.getAppContext()
							.openFileOutput(
									StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE,
									Context.MODE_PRIVATE);
				} else {
					fileOutputComp = MainApplication.getAppContext()
							.openFileOutput(competionName + "_list",
									Context.MODE_PRIVATE);
				}

				ObjectOutputStream objStreamOutComp = new ObjectOutputStream(
						fileOutputComp);
				objStreamOutComp.writeObject(competitors);
				objStreamOutComp.close();

				if (track == null) {
					track = new ArrayList<TrackMarker>();
				}
				if (competionName == null || competionName.isEmpty()) {
					fileOutputTrack = MainApplication.getAppContext()
							.openFileOutput(
									StartScreenFragment.CURRENT_TRACK_FILE,
									Context.MODE_PRIVATE);
				} else {
					fileOutputTrack = MainApplication.getAppContext()
							.openFileOutput(competionName + "_track",
									Context.MODE_PRIVATE);
				}
				ObjectOutputStream objStreamOutTrack = new ObjectOutputStream(
						fileOutputTrack);
				objStreamOutTrack.writeObject(track);
				objStreamOutTrack.close();

			} catch (FileNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show( fragmentManager, "popUp");
				return;
			} catch (IOException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show( fragmentManager, "popUp");
				return;
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show( fragmentManager, "popUp");
		}

	}

	public void loadSessionData(String competionName, 
								boolean readFile, 
								FragmentManager fragmentManager, 
								ResultListFragment resultListFragment,
								TextView trackInfoTextView,
								TextView cardText) {
		if (readFile) {
			FileInputStream fileInputTrack = null;
			FileInputStream fileInputComp = null;
			try {
				if (competionName == null || competionName.isEmpty()) {
					fileInputComp = MainApplication
							.getAppContext()
							.openFileInput(
									StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE);
				} else {
					fileInputComp = MainApplication.getAppContext()
							.openFileInput(competionName + "_list");
				}
				ObjectInputStream objStreamInComp = new ObjectInputStream(
						fileInputComp);
				competitors = (ArrayList<Competitor>) objStreamInComp.readObject();
				objStreamInComp.close();

				if (competionName == null || competionName.isEmpty()) {
					fileInputTrack = MainApplication.getAppContext()
							.openFileInput(
									StartScreenFragment.CURRENT_TRACK_FILE);
				} else {
					fileInputTrack = MainApplication.getAppContext()
							.openFileInput(competionName + "_track");
				}
				ObjectInputStream objStreamInTrack = new ObjectInputStream(
						fileInputTrack);
				track = (List<TrackMarker>) objStreamInTrack.readObject();
				objStreamInTrack.close();
			} catch (FileNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(fragmentManager, "popUp");
				return;
			} catch (IOException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(fragmentManager, "popUp");
				return;
			} catch (ClassNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(fragmentManager, "popUp");
				return;
			}
		}

		// TODO: some fuck up with this global instances, need to get rid of
		// them
		// StartScreenFragment.instance.updateTrackText();
//		trackInfoTextView = (TextView) findViewById(R.id.trackInfoTextView);
		trackInfoTextView.setText("Current loaded Track: ");
		int i = 0;
		for (TrackMarker trackMarker : track) {
			i++;
			trackInfoTextView.append(", SS" + i + " Start: "
					+ trackMarker.start + " Finish: " + trackMarker.finish);
		}
		
		if (resultListFragment != null) {
			resultListFragment.updateResultList();
		}
		
		cardText.setText("Loaded: \n");

		for (Competitor comp : competitors) {
			cardText.append("Name: " + comp.name + " cardnum"
					+ comp.cardNumber + comp.card + "\n");
		}
	}
	
	public String exportResultAsCsv() throws IOException{
		String responseMsg = "";
		String result = "Name,card number,total time,";
		for (int i = 0; i < track.size(); i++) {
			result += "SS" + (i + 1) + ",";
		}
		result += "\n";

		if( CompetitionHelper.isExternalStorageWritable() ) {
			File sdCard = Environment.getExternalStorageDirectory();
			String compName = "New";
			if( track != null ) {
				if (!track.isEmpty()) {
					compName = track.get(0).compName;
				}
			}
			compName.replace(" ", "_");

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
			fw.close();
		} else {
			return "External file storage not available, coulr not export results";
		}
	
		responseMsg += "\nResult exported succesfuly";
		return responseMsg;
	}
}
