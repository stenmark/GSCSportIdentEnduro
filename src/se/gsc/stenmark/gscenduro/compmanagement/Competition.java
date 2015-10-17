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
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import android.app.Activity;
import android.content.Context;
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
	
	public static final long NO_TIME_FOR_STAGE = 10000000L;
	public static final long NO_TIME_FOR_COMPETITION = 20000000L;
	public static final int RANK_DNF = 30000000;
	
	private static final String CURRENT_COMPETITION = "current_competition";
	private Stages mStages = null;
	private Competitors mCompetitors = null;
	private List<Results> mResults = null;
	private List<Results> mResultLandscape = null;
	private String mCompetitionName;
	private String mCompetitionDate = "";
	private int mCompetitionType = SVARTVITT_TYPE;
	
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
		}
		ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
		Competition loadCompetition = (Competition) objStreamInComp.readObject();
		objStreamInComp.close();

		return loadCompetition;
	}

	public String processNewCard(Card card, Boolean calculateResultsAfterAdd) {
		Competitor foundCompetitor = mCompetitors.findByCard(card);
		if (foundCompetitor == null) {
			return "Card not added, could not find any competitor with that card number: " + card.getCardNumber();
		}
		
		String status = foundCompetitor.processCard(card, mStages, mCompetitionType);
		
		if (calculateResultsAfterAdd) {				
			calculateResults();
		}

		return status;
	}

	public void exportCompetitorsAsCsv(Activity activity) throws IOException {
		String competitorList = mCompetitors.exportCsvString(mCompetitionType);
		CompetitionHelper.exportString(activity, competitorList, "competitors", mCompetitionName, "csv");
	}

	public void exportResultsAsCsv(Activity activity) throws IOException {
		String resultList = CompetitionHelper.getResultsAsCsvString(mStages, mResultLandscape, mCompetitors, mCompetitionType);
		CompetitionHelper.exportString(activity, resultList, "results", mCompetitionName, "csv");
	}

	public void exportPunchesAsCsv(Activity activity) throws IOException {
		String punchList = mCompetitors.exportPunchesCsvString();
		CompetitionHelper.exportString(activity, punchList, "punches", mCompetitionName, "csv");
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
		competitionList += mStages.exportStagesCsvString() + "\n";
		competitionList += "[/Stages]\n";
		
		// Competitors
		competitionList += "[Competitors]\n";
		competitionList += mCompetitors.exportCsvString(mCompetitionType);
		competitionList += "[/Competitors]\n";

		// Punches
		competitionList += "[Punches]\n";
		competitionList += mCompetitors.exportPunchesCsvString();
		competitionList += "[/Punches]\n";		
		
		CompetitionHelper.exportString(activity, competitionList, "competition", mCompetitionName, "csv");
	}
	
	public void calculateResults() {
		int i, j;
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
			Results result = new Results(classString + "Total time");
			mResults.add(result);
	
			// Add stage titles
			for (i = 1; i < mStages.size() + 1; i++) {
				result = new Results(classString + "Stage " + i);
				mResults.add(result);
			}
	
			// Add total times			
			for (Competitor currentCompetitor : mCompetitors.getCompetitors() ) {
				StageResult totalTimeResult;
				if ((mCompetitionType == SVARTVITT_TYPE) || (currentCompetitor.getCompetitorClass().equals(competitorClass))) {
					if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() < mStages.getStages().size())) {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), NO_TIME_FOR_COMPETITION);
					} else {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), currentCompetitor.getTotalTime(mStages.size()));							
					}
					mResults.get(totalTimePosition).addTotalTimeResult(totalTimeResult);					
				}
			}
			
			//Sort total times
			mResults.get(totalTimePosition).sortStageResult( NO_TIME_FOR_COMPETITION );		

			StageResult stageResult;
			// Add stage times
			for (j = 1; j < (mResults.size() - totalTimePosition); j++) {
				for (i = 0; i < mCompetitors.size(); i++) {
					if ((mCompetitionType == SVARTVITT_TYPE) || (mCompetitors.get(i).getCompetitorClass().equals(competitorClass))) {			

						Long stageTime = NO_TIME_FOR_STAGE;
						if ((mCompetitors.get(i).hasResult()) && (mCompetitors.get(i).getStageTimes().size() > (j - 1))) {
							stageTime = mCompetitors.get(i).getStageTimes().getTimesOfStage(j - 1);
						} 
		
						stageResult = new StageResult(mCompetitors.get(i).getCardNumber(), stageTime);
						mResults.get(j + totalTimePosition).addStageResult(stageResult);
		
						if (stageTime == NO_TIME_FOR_STAGE) {
							// Update the total and set competitor to DNF if one stage is DNF
							for (int k = 0; k < mResults.get(totalTimePosition).getStageResult().size(); k++) {
								if (mResults.get(totalTimePosition).getStageResult().get(k).getCardNumber() == stageResult.getCardNumber()) {
									mResults.get(totalTimePosition).getStageResult().get(k).setStageTimes(NO_TIME_FOR_COMPETITION);;
									break;
								}
							}
						}
					}						
				}
	
				//Sort stages times
				mResults.get(j + totalTimePosition).sortStageResult( NO_TIME_FOR_STAGE );
			}

			//Calculate time back
			for (j = totalTimePosition; j < mResults.size(); j++) {
				for (i = 0; i < mResults.get(j).getStageResult().size(); i++) {
					Long stageTimeBack = NO_TIME_FOR_STAGE;
					Long currentStageTime = mResults.get(j).getStageResult().get(i).getStageTime();
					if (currentStageTime != NO_TIME_FOR_STAGE || currentStageTime != NO_TIME_FOR_COMPETITION ) {
						if (mResults.get(j).getStageResult().size() > 0) {
							stageTimeBack = currentStageTime - mResults.get(j).getStageResult().get(0).getStageTime();
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
					resultLandscapeObject.addTotalTimeResult(totalTimeResult);
					
					for (int stage = 1; stage < (mResults.size() - totalTimePosition); stage++) {										
						StageResult newStageResult = new StageResult(cardNumber, NO_TIME_FOR_STAGE );					
						for (StageResult stageResultObject : mResults.get(stage + totalTimePosition).getStageResult()) {						
							if (cardNumber == stageResultObject.getCardNumber()) {
								newStageResult = stageResultObject;
							}																			
						}					
						
						if (newStageResult.getStageTime() == NO_TIME_FOR_STAGE) {
							resultLandscapeObject.getStageResult().get(0).setStageTimes(NO_TIME_FOR_STAGE);
						}
						
						resultLandscapeObject.addStageResult(newStageResult);
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
