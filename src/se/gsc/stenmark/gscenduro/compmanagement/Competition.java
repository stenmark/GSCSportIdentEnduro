package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.PopupMessage;
import se.gsc.stenmark.gscenduro.ResultListFragment;
import se.gsc.stenmark.gscenduro.StartScreenFragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
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
}
