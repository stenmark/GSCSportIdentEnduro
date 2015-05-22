package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.Result;
import se.gsc.stenmark.gscenduro.ResultLandscape;
import se.gsc.stenmark.gscenduro.TrackResult;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

/**
 * Represents a competition with a track, List of competitors and the name of
 * the competition. The competition can be saved to disc and loaded back from
 * disc.
 * 
 * @author Andreas
 * 
 */
public class Competition implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final int SVARTVITT_TYPE = 0;
	public final int ESS_TYPE = 1;
	public int competitionType = ESS_TYPE;

	private static final String CURRENT_COMPETITION = "current_competition";
	private List<TrackMarker> mTrack = null;
	private ArrayList<Competitor> mCompetitors = null;
	private List<Result> mResults = null;
	private List<ResultLandscape> mResultLandscape = null;
	private String mCompetitionName;

	public Competition() {
		mTrack = new ArrayList<TrackMarker>();
		mCompetitors = new ArrayList<Competitor>();
		mResults = new ArrayList<Result>();
		mResultLandscape = new ArrayList<ResultLandscape>();
		setCompetitionName("New");

		try {
			saveSessionData(null);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}

	public List<TrackMarker> getTrack() {
		return mTrack;
	}

	public ArrayList<Competitor> getCompetitors() {
		return mCompetitors;
	}

	public List<Result> getResults() {
		return mResults;
	}

	public String getCompetitionName() {
		return mCompetitionName;
	}
	
	public void setCompetitionName(String competitionName) {
		mCompetitionName = competitionName;
	}	
	
	public List<ResultLandscape> getResultLandscape() {
		return mResultLandscape;
	}

	public int getCompetitionType() {
		return competitionType;
	}

	public void sortCompetitors() {
		Collections.sort(mCompetitors, new Comparator<Competitor>() {
			@Override
			public int compare(Competitor s1, Competitor s2) {
				return s1.getName().compareToIgnoreCase(s2.getName());
			}
		});
	}

	public void sortTrackResult(ArrayList<TrackResult> trackResult) {
		Collections.sort(trackResult, new Comparator<TrackResult>() {
			@Override
			public int compare(TrackResult lhs, TrackResult rhs) {
				if (lhs.getDNF()) {
					return 1;
				} else {
					return lhs.getTrackTimes().compareTo(rhs.getTrackTimes());
				}
			}
		});
	}

	/**
	 * Add a new Competitor to the competition.
	 * 
	 * @param name
	 * @param cardNumber
	 *            the number of the SI card for this user
	 */
	public void addCompetitor(String name, int cardNumber, String team,
			String competitorClass, int startNumber, int startGroup) {
		Competitor competitor = new Competitor(name);
		competitor.setCardNumber(cardNumber);
		competitor.setTeam(team);
		competitor.setCompetitorClass(competitorClass);
		competitor.setStartNumber(startNumber);
		competitor.setStartGroup(startGroup);

		mCompetitors.add(competitor);

		sortCompetitors();

		calculateResults();
	}

	public void clearCompetitors() {
		for (int i = 0; i < mCompetitors.size(); i++) {
			mCompetitors.get(i).setCard(null);
			mCompetitors.get(i).setTrackTimes(null);
		}
	}

	/**
	 * Will find and update the SI card number for the given user If user is
	 * found nothing happens.
	 * 
	 * @param nameToModify
	 * @param newCardNumber
	 */
	public void updateCompetitorCardNumber(int index, String newName,
			String newCardNumber) {
		Competitor newCompetitor = null;
		newCardNumber = newCardNumber.replace(" ", "");

		newCompetitor = mCompetitors.get(index);
		newCompetitor.setName(newName);
		newCompetitor.setCardNumber(Integer.parseInt(newCardNumber));

		mCompetitors.set(index, newCompetitor);

		sortCompetitors();

		calculateResults();
	}

	public void updateCompetitor(int index, String newName,
			String newCardNumber, String newTeam, String newClass,
			String newStartNumber, String newStartGroup) {
		Competitor newCompetitor = null;
		newCardNumber = newCardNumber.replace(" ", "");

		newCompetitor = mCompetitors.get(index);
		newCompetitor.setName(newName);
		newCompetitor.setCardNumber(Integer.parseInt(newCardNumber));
		newCompetitor.setTeam(newTeam);
		newCompetitor.setCompetitorClass(newClass);
		newCompetitor.setStartNumber(Integer.parseInt(newStartNumber));
		newCompetitor.setStartGroup(Integer.parseInt(newStartGroup));

		mCompetitors.set(index, newCompetitor);

		sortCompetitors();

		calculateResults();
	}

	/**
	 * Remove a competitor from the competition. Will find and delete competitor
	 * by searching for the specified name. If the name is not found nothing
	 * happens
	 * 
	 * @param nameToDelete
	 */
	public void removeCompetitor(String nameToDelete) {
		for (Competitor competitor : mCompetitors) {
			if (competitor.getName().equals(nameToDelete)) {
				mCompetitors.remove(competitor);
				break;
			}
		}

		calculateResults();
	}

	public int getNumberOfTracks() {
		if (mTrack != null) {
			return mTrack.size();
		}
		return 0;
	}

	/**
	 * Will remove the old track and replace it with the new one. The input is a
	 * comma separated list of track markers (SI control unit) The user need to
	 * specify the name of the track markers as String with integers, where each
	 * Integer represents the number programmed in to theSI control unit
	 * 
	 * @param newTrack
	 *            comma separated list of Integers i.e. "71,72,71,72"
	 */
	public void addNewTrack(String newTrack) {
		String[] trackMarkers = newTrack.split(",");
		mTrack.clear();
		for (int i = 0; i < trackMarkers.length; i += 2) {

			int startMarker = 0;
			int finishMarker = 0;
			startMarker = Integer.parseInt(trackMarkers[i]);
			finishMarker = Integer.parseInt(trackMarkers[i + 1]);
			mTrack.add(new TrackMarker(startMarker, finishMarker));
		}

		calculateResults();
	}

	/**
	 * Reads the current track and returns at as a comma separated String of
	 * Integers.
	 * 
	 * @return
	 */
	public String getTrackAsString() {
		String trackAsString = "";
		if (!mTrack.isEmpty() && mTrack != null) {
			int i = 0;
			for (TrackMarker trackMarker : mTrack) {
				i++;
				if (i != 1) {
					trackAsString += " ,";
				}
				trackAsString += "SS" + i + ": " + trackMarker.getStart()
						+ "->" + trackMarker.getFinish();
			}
		} else {
			trackAsString += " No track loaded";
		}
		return trackAsString;
	}

	public Integer getNumbeofCompetitors() {
		return mCompetitors.size();
	}

	/**
	 * Takes the whole current competition object and serializes it to the
	 * Android file system. It is written to the applications private storage,
	 * so it wont be accessible from outside this program. If the competition
	 * name is empty it will be treated as "current competition" this is run
	 * time data that need to be saved to disc when the application or GUI is
	 * being deallocated from memory by the android system.
	 * 
	 * @param competionName
	 * @throws IOException
	 */
	public void saveSessionData(String competionName) throws IOException {
		if (CompetitionHelper.isExternalStorageWritable()) {
			FileOutputStream fileOutputComp;
			if (competionName == null || competionName.isEmpty()) {
				fileOutputComp = MainApplication.getAppContext()
						.openFileOutput(CURRENT_COMPETITION,
								Context.MODE_PRIVATE);
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

			ObjectOutputStream objStreamOutComp = new ObjectOutputStream(
					fileOutputComp);

			objStreamOutComp.writeObject(this);
			objStreamOutComp.close();
		}
	}

	/*
	 * public void saveSessionData( String competionName ) throws IOException {
	 * FileOutputStream fileOutputComp; if (competionName == null ||
	 * competionName.isEmpty()) { fileOutputComp =
	 * MainApplication.getAppContext().openFileOutput( CURRENT_COMPETITION,
	 * Context.MODE_PRIVATE); } else { competionName =
	 * competionName.replace(" ", "_"); fileOutputComp =
	 * MainApplication.getAppContext().openFileOutput(competionName ,
	 * Context.MODE_PRIVATE); }
	 * 
	 * ObjectOutputStream objStreamOutComp = new
	 * ObjectOutputStream(fileOutputComp); objStreamOutComp.writeObject(this);
	 * objStreamOutComp.close(); }
	 */

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
	public static Competition loadSessionData(String competionName)
			throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		FileInputStream fileInputComp = null;

		if (competionName == null || competionName.isEmpty()) {
			try {
				fileInputComp = MainApplication.getAppContext().openFileInput(
						CURRENT_COMPETITION);
			} catch (FileNotFoundException e) {
				// If this is the first time the app is started the file does
				// not exist, handle it by returning an empty competition
				return new Competition();
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

			// fileInputComp =
			// MainApplication.getAppContext().openFileInput(competionName);
		}
		ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
		Competition loadCompetition = (Competition) objStreamInComp
				.readObject();
		objStreamInComp.close();

		return loadCompetition;
	}

	/*
	 * public static Competition loadSessionData(String competionName ) throws
	 * StreamCorruptedException, IOException, ClassNotFoundException {
	 * FileInputStream fileInputComp = null;
	 * 
	 * if (competionName == null || competionName.isEmpty()) { fileInputComp =
	 * MainApplication.getAppContext().openFileInput(CURRENT_COMPETITION); }
	 * else { fileInputComp =
	 * MainApplication.getAppContext().openFileInput(competionName); }
	 * ObjectInputStream objStreamInComp = new ObjectInputStream(
	 * fileInputComp); Competition loadCompetition = (Competition)
	 * objStreamInComp.readObject(); objStreamInComp.close();
	 * 
	 * return loadCompetition; }
	 */
	/**
	 * When a new card is read call this method to add the SI card data to the
	 * competition. Will first search for the card number to find which
	 * competitor it belongs to. It will try to make the card data coherent by
	 * removing double punches It will read all the remaining punched and
	 * calculate the track time for each track in the competition After this
	 * method is completed the competitor should have all its competition data
	 * added to the competition.
	 * 
	 * @param newCard
	 *            the SI card read and that should be added to a competitor
	 * @return a message String informing on have the parsing of the SI card
	 *         data went
	 */
	public String processNewCard(Card newCard) {
		String returnMsg = "";
		Competitor foundCompetitor = CompetitionHelper.findCompetitor(newCard,
				mCompetitors);
		if (foundCompetitor == null) {
			return "Read new card with card number: " + newCard.getCardNumber()
					+ " Could not find any competitor with this number";
		}
		newCard.findDoublePunches();
		foundCompetitor.setCard(newCard);

		returnMsg += "New card read for " + foundCompetitor.getName() + " ";

		// The results is a List of Long Integers where each integer represent
		// the time the competitor took to complete the track
		// i.e. first entry will be for SS1, second SS2 etc.
		List<Long> results = new ArrayList<Long>();
		results = CompetitionHelper.extractResultFromCard(newCard, mTrack);

		foundCompetitor.setTrackTimes(new ArrayList<Long>());
		int i = 1;
		for (Long trackTime : results) {
			returnMsg += ", Time for SS " + i + " = " + trackTime + " seconds ";
			foundCompetitor.getTrackTimes().add(trackTime);
			i++;
		}

		calculateResults();

		if (results.size() != mTrack.size()) {
			return "Not all station punched";
		}

		returnMsg += ("Total time was: " + foundCompetitor.getTotalTime(true) + " seconds \n");

		return returnMsg;
	}

	public void messageAlert(Activity activity, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(message).setTitle(title).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void exportCompetitorsAsCsv(Activity activity) throws IOException {
		String competitorList = CompetitionHelper
				.getCompetitorsAsCsvString(mCompetitors);
		CompetitionHelper.exportString(activity, competitorList, "competitors",
				mCompetitionName);
	}

	public void exportResultsAsCsv(Activity activity) throws IOException {
		String resultList = CompetitionHelper.getResultsAsCsvString(mTrack,
				mResultLandscape);
		CompetitionHelper.exportString(activity, resultList, "results",
				mCompetitionName);
	}

	public void exportPunchesAsCsv(Activity activity) throws IOException {
		String punchList = CompetitionHelper.getPunchesAsCsvString(mCompetitors);
		CompetitionHelper.exportString(activity, punchList, "punches",
				mCompetitionName);
	}

	public void exportCompetitionAsCsv(Activity activity) throws IOException {
		String competitionList = CompetitionHelper.getCompetitionAsCsvString(
				mCompetitionName, mTrack, mCompetitors);
		CompetitionHelper.exportString(activity, competitionList,
				"competition", mCompetitionName);
	}

	public void exportAllAsCsv(Activity activity) throws IOException {
		String AllList = "";
		AllList += "Competitors\n";
		AllList += CompetitionHelper.getCompetitorsAsCsvString(mCompetitors);
		AllList += "\n\n";
		AllList += "Results\n";
		AllList += CompetitionHelper.getResultsAsCsvString(mTrack,
				mResultLandscape);
		AllList += "\n\n";
		AllList += "Punches\n";
		AllList += CompetitionHelper.getPunchesAsCsvString(mCompetitors);

		CompetitionHelper.exportString(activity, AllList, "all",
				mCompetitionName);
	}

	public void calculateResults() {
		int i, j;
		ArrayList<Competitor> tempCompetitors = new ArrayList<Competitor>();
		for (Competitor competitor : mCompetitors) {
			tempCompetitors.add(competitor);
		}

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
		String team;
		int cardNumber;
		Long trackTime = (long) 0;
		Long trackTimeBack = (long) 0;
		TrackResult trackResult;

		for (i = 0; i < tempCompetitors.size(); i++) {
			trackResult = new TrackResult(tempCompetitors.get(i).getName(), tempCompetitors.get(i).getCardNumber(), tempCompetitors.get(i).getTotalTime(true), false);
			mResults.get(0).getTrackResult().add(trackResult);
		}

		// Add track times
		for (j = 1; j < mResults.size(); j++) {
			for (i = 0; i < tempCompetitors.size(); i++) {
				name = tempCompetitors.get(i).getName();
				team = tempCompetitors.get(i).getTeam();
				cardNumber = tempCompetitors.get(i).getCardNumber();

				if ((tempCompetitors.get(i).hasResult()) && (tempCompetitors.get(i).getTrackTimes().size() > j - 1)) {
					trackTime = tempCompetitors.get(i).getTrackTimes().get(j - 1);
				} else {
					trackTime = (long) Integer.MAX_VALUE;
				}

				trackResult = new TrackResult(name, cardNumber, trackTime, trackTime == (long) Integer.MAX_VALUE);
				mResults.get(j).getTrackResult().add(trackResult);

				if (trackResult.getDNF()) {
					// Update the total and set competitor to DNF if one stage
					// is DNF
					for (int k = 0; k < mResults.get(0).getTrackResult().size(); k++) {
						if (mResults.get(0).getTrackResult().get(k).getCardNumber() == trackResult.getCardNumber()) {
							mResults.get(0).getTrackResult().get(k).setDNF(true);
							break;
						}
					}
				}
			}

			sortTrackResult(mResults.get(j).getTrackResult());
		}

		sortTrackResult(mResults.get(0).getTrackResult());

		for (j = 0; j < mResults.size(); j++) {
			for (i = 0; i < mResults.get(j).getTrackResult().size(); i++) {
				if (mResults.get(j).getTrackResult().get(i).getTrackTimes() == (long) Integer.MAX_VALUE) {
					trackTimeBack = (long) Integer.MAX_VALUE;
				} else {
					if (mResults.get(j).getTrackResult().size() > 0) {
						trackTimeBack = mResults.get(j).getTrackResult().get(i).getTrackTimes() - mResults.get(j).getTrackResult().get(0).getTrackTimes();
					}
				}
				mResults.get(j).getTrackResult().get(i).setTrackTimesBack(trackTimeBack);
			}
		}

		mResultLandscape.clear();
		if (mResults != null && !mResults.isEmpty()) {
			ResultLandscape resultLandscapeObject = null;

			for (TrackResult trackResultObject : mResults.get(0).getTrackResult()) {
				if (tempCompetitors != null && !tempCompetitors.isEmpty()) {
					for (Competitor competitor : tempCompetitors) {
						if (competitor.getCardNumber() == trackResultObject.getCardNumber()) {
							resultLandscapeObject = new ResultLandscape();
							resultLandscapeObject.setName(competitor.getName());
							resultLandscapeObject.setTeam(competitor.getTeam());
							resultLandscapeObject.setCardNumber(competitor.getCardNumber());
							resultLandscapeObject.setTotalTime(competitor.getTotalTime(false));

							if (competitor.getTrackTimes() != null) {
								int stage = 1;
								for (long time : competitor.getTrackTimes()) {
									resultLandscapeObject.getTime().add(time);

									if (stage < mResults.size()) {
										int pos = CompetitionHelper.getPosition(competitor.getCardNumber(), mResults.get(stage).getTrackResult());
										if (pos == -1) {
											resultLandscapeObject.getRank().add(Integer.MAX_VALUE);
										} else {
											resultLandscapeObject.getRank().add(pos);
										}
									}
									stage++;
								}
							} else {
								for (i = 0; i < mTrack.size(); i++) {
									resultLandscapeObject.getTime().add((long) Integer.MAX_VALUE);
									resultLandscapeObject.getRank().add(Integer.MAX_VALUE);
								}
							}

							mResultLandscape.add(resultLandscapeObject);
						}
					}
				}
			}
		}

		try {
			saveSessionData(null);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}
}
