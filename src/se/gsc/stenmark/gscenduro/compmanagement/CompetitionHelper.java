package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided for each method call (no internal variables allowed)
 * @author Andreas
 *
 */
public class CompetitionHelper {

	/**
	 * Finds a competitor in  list of competitors supplied, it searches for the competitors card number.
	 * If no competitor with the supplied cardNumer is found, null is returned.
	 * @param cardToMatch
	 * @param competitors
	 * @return
	 */
	public static Competitor findCompetitor(Card cardToMatch, List<Competitor> competitors) {
		for (Competitor competitor : competitors) {
			if (competitor.cardNumber == cardToMatch.cardNumber) {
				return competitor;
			}
		}
		return null;
	}

	/**
	 * Searched the Android file system for saved competitions and returns a list with filenames for all files found.
	 * The current competition is excluded from the list, only competitions manually saved by the users is returned.
	 * @return
	 */
	public static List<String> getSavedCompetitionsAsList(){
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
	public static List<String> getSavedCompetitionsAsList(){
		List<String> result = new ArrayList<String>();
		String[] fileList = MainApplication.getAppContext().fileList();
		for (String file : fileList) {
			if (!file.equals(Competition.CURRENT_COMPETITION)) {
				result.add(file);			}

		}
		return result;
	}
*/	
	/**
	 * Does the same as getSavedCompetitionsAsList() but instead of returning a list it returns a new line separated String of competitions.
	 * @return
	 */
	public static String getSavedCompetitions(){
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
	public static String getSavedCompetitions(){
		String result = "";
		String[] fileList = MainApplication.getAppContext().fileList();
		for (String file : fileList) {
			if (!file.equals(Competition.CURRENT_COMPETITION)) {
				result += file + "\n";
			}

		}
		return result;
	}
*/	
	/**
	 * Will map a specific punch for the supplied card for a specific SI card station. 
	 * It will find the n:th punch of the specified station number on the card, denoted by instanceNumber.
	 * I.e. if the card has punches from the following station number: 71,72,71,72,71,72
	 * Then if:
	 * StationNumber = 71 and instanceNumber = 3 it will find the punch for 71,72,71,72, ->71<- ,72
	 * StationNumber = 72 and instanceNumber = 1 it will find the punch for 71, ->72<- ,71,72,71,72
	 * If the punch is not found null is returned.
	 * i.e. StationNumber = 72 and instanceNumber = 4 -> null is returned
	 * @param card
	 * @param stationNumber
	 * @param instanceNumber
	 * @return
	 */
	public static Punch findPunchForStationNrInCard(Card card, long stationNumber, int instanceNumber) {
		int i = 0;
		for (Punch punch : card.punches) {
			if (punch.control == stationNumber) {
				if( !punch.markAsDoublePunch ){
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
	 * Loops through the track and associates each TrackMarker in the track with a punch from the card.
	 * It then calculates a track time, i.e. how long did it take to reach from each pair of trackmarkers in the track.
	 * This method will only work on a coherent number of punches. 
	 * I.e. all double punches and punches on Trackmarkers not in the track must be removed before calling this method
	 * @param card
	 * @param track
	 * @return a list of Long Integers denoting the time it took to get through each pair of track markers
	 */
	public static List<Long> extractResultFromCard(Card card, List<TrackMarker> track) {		
		List<Long> result = new ArrayList<Long>();

		try {
			for (int i = 0; i < track.size(); i++) {
				TrackMarker trackMarker = track.get(i);
				Punch startPunch = findPunchForStationNrInCard(card, trackMarker.start,	i + 1);
				Punch finishPunch = findPunchForStationNrInCard(card, trackMarker.finish, i + 1);
				long trackTime = finishPunch.time - startPunch.time;
				result.add(trackTime);
			}
		} catch (Exception e) {
			
		}

		return result;
	}
	
	/**
	 * Simple helper to ensure that we can write to Android filesystem.
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
}
