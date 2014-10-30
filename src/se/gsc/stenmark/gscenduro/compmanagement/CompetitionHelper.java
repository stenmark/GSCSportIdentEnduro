package se.gsc.stenmark.gscenduro.compmanagement;

import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.PopupMessage;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class CompetitionHelper {

	public static Competitor findCompetitor(Card cardToMatch,
			List<Competitor> competitors) {
		for (Competitor competitor : competitors) {
			if (competitor.cardNumber == cardToMatch.cardNumber) {
				return competitor;
			}
		}
		return null;
	}

	public static Punch findPunchForStationNrInCard(Card card, long stationNumber, int instanceNumber) {
		int i = 0;
		for (Punch punch : card.punches) {
			if (punch.control == stationNumber) {
				i++;
				if (i == instanceNumber) {
					return punch;
				}
			}
		}
		return null;
	}
	
	public static List<Long> extractResultFromCard(Card card, List<TrackMarker> track) {
		List<Long> result = new ArrayList<Long>();

		try {
			for (int i = 0; i < track.size(); i++) {
				TrackMarker trackMarker = track.get(i);
				Punch startPunch = findPunchForStationNrInCard(card, trackMarker.start,
								i + 1);
				Punch finishPunch = findPunchForStationNrInCard(card, trackMarker.finish,
								i + 1);
				long trackTime = finishPunch.time - startPunch.time;
				result.add(trackTime);
			}
		} catch (Exception e) {
			throw new NotAllStationsPunchedException("Not all stations have been checked for card number: " + card.cardNumber);
		}

		return result;
	}
	
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
