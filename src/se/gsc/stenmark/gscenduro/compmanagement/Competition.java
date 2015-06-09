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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainApplication;
import se.gsc.stenmark.gscenduro.Results;
import se.gsc.stenmark.gscenduro.StageResult;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

/**
 * Represents a competition with a stage, List of competitors and the name of
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

	private static final String CURRENT_COMPETITION = "current_competition";
	private Stages mStages = null;
	private Competitors mCompetitors = null;
	private List<Results> mResults = null;
	private List<Results> mResultLandscape = null;
	private String mCompetitionName;
	private String mCompetitionDate = "";
	private int mCompetitionType = ESS_TYPE;
	
	public Competition() {
		mStages = new Stages();
		mCompetitors = new Competitors();
		mResults = new ArrayList<Results>();
		mResultLandscape = new ArrayList<Results>();
		setCompetitionName("New");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		String currentDateandTime = sdf.format(new Date());
		setCompetitionDate(currentDateandTime);
		
		try {
			saveSessionData(null);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}
	
	public Stages getStages() {
		return mStages;		
	}

	public Competitors getCompetitors() {
		return mCompetitors;
	}

	public List<Results> getResults() {
		return mResults;
	}

	public String getCompetitionName() {
		return mCompetitionName;
	}
	
	public String getCompetitionDate() {
		return mCompetitionDate;
	}		
	
	public void setCompetitionName(String competitionName) {
		mCompetitionName = competitionName;
	}	
	
	public List<Results> getResultLandscape() {
		return mResultLandscape;
	}

	public int getCompetitionType() {
		return mCompetitionType;
	}

	public void setCompetitionType(int competitionType) {
		mCompetitionType = competitionType;
	}	

	public void setCompetitionDate(String competitionDate) {
		mCompetitionDate = competitionDate;
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
				fileOutputComp = MainApplication.getAppContext().openFileOutput(CURRENT_COMPETITION, Context.MODE_PRIVATE);
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
	public static Competition loadSessionData(String competionName) throws StreamCorruptedException, IOException, ClassNotFoundException {
		FileInputStream fileInputComp = null;

		if (competionName == null || competionName.isEmpty()) {
			try {
				fileInputComp = MainApplication.getAppContext().openFileInput(CURRENT_COMPETITION);
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
		Competition loadCompetition = (Competition) objStreamInComp.readObject();
		objStreamInComp.close();

		return loadCompetition;
	}

	/**
	 * When a new card is read call this method to add the SI card data to the
	 * competition. Will first search for the card number to find which
	 * competitor it belongs to. It will try to make the card data coherent by
	 * removing double punches It will read all the remaining punched and
	 * calculate the stage time for each stage in the competition After this
	 * method is completed the competitor should have all its competition data
	 * added to the competition.
	 * 
	 * @param newCard
	 *            the SI card read and that should be added to a competitor
	 * @return a message String informing on have the parsing of the SI card
	 *         data went
	 */
	public String processNewCard(Card newCard, Boolean calculateResultsAfterAdd) {
		String returnMsg = "";
		Competitor foundCompetitor = mCompetitors.findByCard(newCard);
		if (foundCompetitor == null) {
			return "Read new card with card number: " + newCard.getCardNumber()
					+ " Could not find any competitor with this number";
		}
		newCard.findDoublePunches();
		foundCompetitor.setCard(newCard);

		returnMsg += "New card read for " + foundCompetitor.getName() + " ";

		// The results is a List of Long Integers where each integer represent
		// the time the competitor took to complete the stage
		// i.e. first entry will be for SS1, second SS2 etc.
		List<Long> results = new ArrayList<Long>();
		results = CompetitionHelper.extractResultFromCard(newCard, mStages);

		foundCompetitor.setStageTimes(new ArrayList<Long>());
		int i = 1;
		for (Long stageTime : results) {
			returnMsg += ", Time for SS " + i + " = " + stageTime + " seconds ";
			foundCompetitor.getStageTimes().add(stageTime);
			i++;
		}

		if (calculateResultsAfterAdd) {				
			calculateResults();
		}

		if (results.size() != mStages.size()) {
			return "Not all station punched";
		}

		returnMsg += ("Total time was: " + foundCompetitor.getTotalTime(true) + " seconds \n");
		Log.d("processNewCard", returnMsg);
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
		String competitorList = mCompetitors.exportCsvString(mCompetitionType);
		CompetitionHelper.exportString(activity, competitorList, "competitors", mCompetitionName);
	}

	public void exportResultsAsCsv(Activity activity) throws IOException {
		String resultList = CompetitionHelper.getResultsAsCsvString(mStages, mResultLandscape, mCompetitors, mCompetitionType);
		CompetitionHelper.exportString(activity, resultList, "results", mCompetitionName);
	}

	public void exportPunchesAsCsv(Activity activity) throws IOException {
		String punchList = mCompetitors.exportPunchesCvsString();
		CompetitionHelper.exportString(activity, punchList, "punches", mCompetitionName);
	}

	public void exportCompetitionAsCsv(Activity activity) throws IOException {
		String competitionList = "";
		
		// Competition Name
		competitionList += "[Name]\n";
		competitionList += mCompetitionName + "\n";
		competitionList += "[/Name]\n";
		
		// Competition Date
		competitionList += "[Date]\n";
		competitionList += mCompetitionDate + "\n";
		competitionList += "[/Date]\n";

		// Competition Type
		competitionList += "[Type]\n";
		competitionList += mCompetitionType + "\n";
		competitionList += "[/Type]\n";

		// Stages
		competitionList += "[Stages]\n";
		competitionList += mStages.exportStagesCvsString() + "\n";
		competitionList += "[/Stages]\n";
		
		// Competitors
		competitionList += "[Competitors]\n";
		competitionList += mCompetitors.exportCsvString(mCompetitionType);
		competitionList += "[/Competitors]\n";

		// Punches
		competitionList += "[Punches]\n";
		competitionList += mCompetitors.exportPunchesCvsString();
		competitionList += "[/Punches]\n";		
		
		CompetitionHelper.exportString(activity, competitionList, "competition", mCompetitionName);
	}
	
	public void calculateResults() {
		int i, j;
		Competitors tempCompetitors = new Competitors();
		tempCompetitors = mCompetitors;

		mResults.clear();			
		mResultLandscape.clear();
		
		//Get Competitor Classes
		List<String> competitorClasses;
		if (mCompetitionType == ESS_TYPE) {		
			competitorClasses = mCompetitors.getCompetitorClasses();
		} else {
			competitorClasses = new ArrayList<String>();
			competitorClasses.add("");
		}
	
		int totalTimePosition = 0;
		
		//One result for each competitor class
		for (String competitorClass : competitorClasses) {
			String classString;
			if (competitorClass.length() > 0) {
				classString = competitorClass + " - ";
			} else {
				classString = "";
			}			
				
			// Add total title
			Results result;
			result = new Results(classString + "Total time");
			mResults.add(result);
	
			// Add stage titles
			for (i = 1; i < mStages.size() + 1; i++) {
				result = new Results(classString + "Stage " + i);
				mResults.add(result);
			}
	
			StageResult stageResult;
			
			// Add total times			
			for (i = 0; i < tempCompetitors.size(); i++) {
				if ((mCompetitionType == SVARTVITT_TYPE) || (tempCompetitors.get(i).getCompetitorClass().equals(competitorClass))) {					
					stageResult = new StageResult(tempCompetitors.get(i).getCardNumber(), tempCompetitors.get(i).getTotalTime(true), false);
					mResults.get(totalTimePosition).getStageResult().add(stageResult);
				}
			}
			
			//Sort total times
			mResults.get(totalTimePosition).sortStageResult();		
						
			// Add stage times
			for (j = 1; j < (mResults.size() - totalTimePosition); j++) {
				for (i = 0; i < tempCompetitors.size(); i++) {
					if ((mCompetitionType == SVARTVITT_TYPE) || (tempCompetitors.get(i).getCompetitorClass().equals(competitorClass))) {			

						Long stageTime;
						if ((tempCompetitors.get(i).hasResult()) && (tempCompetitors.get(i).getStageTimes().size() > (j - 1))) {
							stageTime = tempCompetitors.get(i).getStageTimes().get(j - 1);
						} else {
							stageTime = (long) Integer.MAX_VALUE;
						}
		
						stageResult = new StageResult(tempCompetitors.get(i).getCardNumber(), stageTime, stageTime == (long) Integer.MAX_VALUE);
						mResults.get(j + totalTimePosition).getStageResult().add(stageResult);
		
						if (stageResult.getDnf()) {
							// Update the total and set competitor to DNF if one stage is DNF
							for (int k = 0; k < mResults.get(totalTimePosition).getStageResult().size(); k++) {
								if (mResults.get(totalTimePosition).getStageResult().get(k).getCardNumber() == stageResult.getCardNumber()) {
									mResults.get(totalTimePosition).getStageResult().get(k).setDnf(true);
									break;
								}
							}
						}
					}						
				}
	
				//Sort stages times
				mResults.get(j + totalTimePosition).sortStageResult();
			}

			//Calculate time back
			for (j = totalTimePosition; j < mResults.size(); j++) {
				for (i = 0; i < mResults.get(j).getStageResult().size(); i++) {
					Long stageTimeBack = (long) Integer.MAX_VALUE;
					if (mResults.get(j).getStageResult().get(i).getStageTimes() == (long) Integer.MAX_VALUE) {
						stageTimeBack = (long) Integer.MAX_VALUE;
					} else {
						if (mResults.get(j).getStageResult().size() > 0) {
							stageTimeBack = mResults.get(j).getStageResult().get(i).getStageTimes() - mResults.get(j).getStageResult().get(0).getStageTimes();
						}
					}
					mResults.get(j).getStageResult().get(i).setStageTimesBack(stageTimeBack);
				}
			}
					
			//Calculate results for landscape
			if (mResults != null && !mResults.isEmpty()) {
				for (StageResult totalTimeResult : mResults.get(totalTimePosition).getStageResult()) {							
					int cardNumber = totalTimeResult.getCardNumber();				
					Results resultLandscapeObject = new Results();						
					resultLandscapeObject.getStageResult().add(totalTimeResult);
					
					for (int stage = 1; stage < (mResults.size() - totalTimePosition); stage++) {										
						StageResult newStageResult = new StageResult(cardNumber, (long) Integer.MAX_VALUE, true);					
						for (StageResult stageResultObject : mResults.get(stage + totalTimePosition).getStageResult()) {						
							if (cardNumber == stageResultObject.getCardNumber()) {
								newStageResult = stageResultObject;
							}																			
						}					
						
						if (newStageResult.getStageTimes() == (long) Integer.MAX_VALUE) {
							resultLandscapeObject.getStageResult().get(0).setStageTimes((long) Integer.MAX_VALUE);
						}
						
						resultLandscapeObject.getStageResult().add(newStageResult);
					}
					
					if (competitorClass.length() > 0) {
						resultLandscapeObject.setTitle(competitorClass);
					}
					mResultLandscape.add(resultLandscapeObject);
				}
			}
			
			totalTimePosition = mResults.size();
		}
		try {
			saveSessionData(null);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}
}
