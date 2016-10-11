package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.apache.commons.collections4.queue.CircularFifoQueue;
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

	private static final long serialVersionUID = 2L;
	
	public static final int SVART_VIT_TYPE = 0;
	public static final int ESS_TYPE = 1;	
	
	public static final long COMPETITION_DNF = 5000000L;
	public static final long NO_TIME_FOR_STAGE = 10000000L;
	public static final long NO_TIME_FOR_COMPETITION = 20000000L;
	public static final int RANK_DNF = 30000000;
	
	//Circular buffer to hold the 6 most recently read cards
	public CircularFifoQueue<String> lastReadCards = new CircularFifoQueue<String>(6);
	
	private static final String CURRENT_COMPETITION = "current_competition";
	private Stages mStages = null;
	private Stage totalResults = null;
	private Competitors mCompetitors = null;
	//OLD VERSION
//	private ResultList<Results> mResults = null;
//	private List<Results> mResultLandscapeList = null;
	private String mCompetitionName;
	private String mCompetitionDate = "";
	private int mCompetitionType = SVART_VIT_TYPE;
	
	public Competition() {
		mStages = new Stages();
		totalResults = new Stage();
		//OLD VERSION
//		mResults = new ResultList<Results>();
//		mResultLandscapeList = new ArrayList<Results>();
		mCompetitors = new Competitors();
		setCompetitionName("New");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd", new Locale("swedish", "sweden"));
		String currentDateandTime = sdf.format(new Date());
		setCompetitionDate(currentDateandTime);
		
		try {
			saveSessionData(null);
			saveSessionData(mCompetitionName);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}

	public Long getFastestOnStage(String competitorClass, int stageNumber) {
		//OLD VERSION
//		if( !mResults.getStageResult(stageNumber, competitorClass).getStageResult().isEmpty() ){
//			return mResults.getStageResult(stageNumber, competitorClass).getStageResult().get(0).getStageTime();
//		}
		
		stageNumber=stageNumber-1;  //Convert to 0 indexed
		if( !mStages.get(stageNumber).getCompetitorResults().isEmpty()){
			return mStages.get(stageNumber).getCompetitorResults().get(0).getStageTime();
		}
		return 0L;
	}
	
	public Stage getTotalResults(){
		return totalResults;
	}
	public Stages getStages() {
		return mStages;		
	}

	public Competitors getCompetitors() {
		return mCompetitors;
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
	public static Competition loadSessionData(String competionName) throws StreamCorruptedException, IOException, ClassNotFoundException, InvalidClassException {
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
		Competition loadCompetition = null;
		ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
		loadCompetition = (Competition) objStreamInComp.readObject();
		objStreamInComp.close();

		return loadCompetition;
	}

	public String processNewCard(Card card, Boolean calculateResultsAfterAdd) {
		Competitor foundCompetitor = mCompetitors.findByCard(card);
		if (foundCompetitor == null) {
			return "WARNING! Could not find any competitor with card number: " + card.getCardNumber() + "\n";
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
		String resultList = AndroidIndependantCompetitionHelper.getResultsAsCsvString(mStages, totalResults, mCompetitors, mCompetitionType);
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
	
	/**
	 * Get slowest stage time with maximum deviation from median time.
	 * Calculates the median time difference between all adjacent competitors and then return the slowest stage time
	 * with maximum two times deviation from  median time difference. The idea is to exclude very slow times from the calculation
	 * @param competitorClass
	 * @param stageNumber
	 * @param resultsList
	 * @return
	 */
	public Long calculateSlowestOnStage(String competitorClass, int stageNumber) {
		if( getCompetitors().size() == 0){
			return 0L;
		}
		List<Long>timeDeltaList = new ArrayList<Long>();
		//OLD VERSION
//		Results stageResultForAllCompetitors = mResults.get(stageNumber);
		List<StageResult> competitorResults = mStages.get(stageNumber-1).getCompetitorResults();
		//OLD VERSION
//		for( int currentCompetitorNumber = 0; currentCompetitorNumber < (stageResultForAllCompetitors.getStageResult().size()-1); currentCompetitorNumber++ ){
		for( int currentCompetitorNumber = 0; currentCompetitorNumber < competitorResults.size()-1; currentCompetitorNumber++){
			int nextCompetitorNumber = currentCompetitorNumber+1;
			//OLD VERSION
//			Long currentCompetitorStageTime = stageResultForAllCompetitors.getStageResult().get(currentCompetitorNumber).getStageTime();
//			Long nextCompetitorStageTime = stageResultForAllCompetitors.getStageResult().get(nextCompetitorNumber).getStageTime();
			Long currentCompetitorStageTime = competitorResults.get(currentCompetitorNumber).getStageTime();
			Long nextCompetitorStageTime = competitorResults.get(nextCompetitorNumber).getStageTime();
			if( currentCompetitorStageTime != Competition.NO_TIME_FOR_STAGE && nextCompetitorStageTime != Competition.NO_TIME_FOR_STAGE ){
				timeDeltaList.add(nextCompetitorStageTime - currentCompetitorStageTime);
			}
		}	
		Collections.sort(timeDeltaList);
		
		Long medianTimeDelta = 0L;
		if( !timeDeltaList.isEmpty() ){
			medianTimeDelta = timeDeltaList.get(timeDeltaList.size()/2);
			//If the competitors are very close on the stage (low median time delta) we set a minimum medianTimeDelta of 5 to prevent early cutoff
			if( medianTimeDelta < 7){
				medianTimeDelta = 7L;
			}
		}

		
		//OLD VERSION
//		for( int currentCompetitorNumber = 0; currentCompetitorNumber < (stageResultForAllCompetitors.getStageResult().size()-1); currentCompetitorNumber++ ){
		for( int currentCompetitorNumber = 0; currentCompetitorNumber < competitorResults.size()-1; currentCompetitorNumber++){
			int nextCompetitorNumber = currentCompetitorNumber+1;
			//OLD VERSION
//			Long currentCompetitorStageTime = stageResultForAllCompetitors.getStageResult().get(currentCompetitorNumber).getStageTime();
//			Long nextCompetitorStageTime = stageResultForAllCompetitors.getStageResult().get(nextCompetitorNumber).getStageTime();
			Long currentCompetitorStageTime = competitorResults.get(currentCompetitorNumber).getStageTime();
			Long nextCompetitorStageTime = competitorResults.get(nextCompetitorNumber).getStageTime();

			Long timeDelta = nextCompetitorStageTime - currentCompetitorStageTime;
			//Filter out competitors that are a lot slower than most competitors. 
			//only filter out competitors that are from the lower half of the competitor list (Use timeDelta list since it already contains only competitors with stage times set
			if( (timeDelta > medianTimeDelta*4) && (currentCompetitorNumber > (timeDeltaList.size()/2)) ){
				return currentCompetitorStageTime;
			}
		}
		//OLD VERSION
//		return stageResultForAllCompetitors.getStageResult().get( stageResultForAllCompetitors.getStageResult().size()-1 ).getStageTime();
		return competitorResults.get( competitorResults.size()-1 ).getStageTime();
	}	
	
	public void calculateResults() {
		//OLD VERSION
//		mResults.clear();		
		mStages.clearResults();
		totalResults.clearResult();
		
		//Get Competitor Classes
		List<String> competitorClasses;
		if (mCompetitionType == ESS_TYPE) {		
			competitorClasses = mCompetitors.getCompetitorClasses();
		} else {
			competitorClasses = new ArrayList<String>();
			competitorClasses.add("");
		}

		//One result for each competitor class
		for (String competitorClass : competitorClasses) {
			String classString = "";
			if ( !competitorClass.isEmpty() ) {
				classString = competitorClass + " - ";
			} 		
				
			// Add total title
			//OLD VERSION
//			Results result = new Results(classString + "Total time");
//			mResults.addTotalResult(result, competitorClass);
			totalResults.title = classString + "Total time";
	
			//OLD VERSION
//			// Add stage titles
//			List<Results> tmpResultList = new ArrayList<Results>();
//			for (int i = 1; i < mStages.size() + 1; i++) {
//				result = new Results(classString + "Stage " + i);
//				tmpResultList.add(result);
//			}
//			mResults.addAllStageResults( tmpResultList, competitorClass );
			for (int i = 0; i < mStages.size(); i++) {
				mStages.get(i).title = classString + "Stage " + (i+1);
			}

			// Add total times		
			for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
				Competitor currentCompetitor = currentCompetitorEntry.getValue();
				StageResult totalTimeResult;
				if ((mCompetitionType == SVART_VIT_TYPE) || (currentCompetitor.getCompetitorClass().equals(competitorClass))) {
					if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() == 0) ) {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(),NO_TIME_FOR_COMPETITION);
					} else if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() < mStages.size() )) {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), COMPETITION_DNF);
					} 
					else {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), currentCompetitor.getTotalTime(mStages.size()));							
					}
					//OLD VERSION
//					mResults.getTotalResult(competitorClass).addTotalTimeResult(totalTimeResult);	
					totalResults.addCompetitorResult(totalTimeResult);
				}
			}		
			//Sort total times
			//OLD VERSION
//			mResults.getTotalResult(competitorClass).sortStageResult( NO_TIME_FOR_COMPETITION );	
			totalResults.sortStageResult(NO_TIME_FOR_COMPETITION);

			StageResult stageResult;
			// Add stage times
			for (int stageNumber = 0; stageNumber < mStages.size(); stageNumber++) {
				for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
					Competitor currentCompetitor = currentCompetitorEntry.getValue();
					if ((mCompetitionType == SVART_VIT_TYPE) || (currentCompetitor.getCompetitorClass().equals(competitorClass))) {			

						Long stageTime = NO_TIME_FOR_STAGE;
						if ((currentCompetitor.hasResult()) && (currentCompetitor.getStageTimes().size() > stageNumber)) {  //OLD VERSION -1
							stageTime = currentCompetitor.getStageTimes().getTimesOfStage(stageNumber);  //OLD VERSION -1
						} 
		
						stageResult = new StageResult(currentCompetitor.getCardNumber(), stageTime);
						//OLD VERSION
//						mResults.getStageResult(stageNumber,competitorClass).addStageResult(stageResult);
						mStages.get(stageNumber).addCompetitorResult(stageResult);
					}						
				}
	
				//Sort stages times
				//OLD VERSION
//				mResults.getStageResult(stageNumber,competitorClass).sortStageResult( NO_TIME_FOR_STAGE );
				mStages.get(stageNumber).sortStageResult(NO_TIME_FOR_STAGE);
			}

			//Calculate time back
			//OLD VERSION  need to also calculate for totaltime
//			for (Results currentResult : mResults.getAllResults(competitorClass)) {
			for ( int stageNumber = 0; stageNumber < mStages.size(); stageNumber++) {
				Stage currentStage = mStages.get(stageNumber);
				//OLD VERSION
//				for (StageResult currentStageResult : currentResult.getStageResult() ) {
				for (StageResult currentStageResult : currentStage.getCompetitorResults() ) {
					Long stageTimeBack = NO_TIME_FOR_STAGE;
					Long currentStageTime = currentStageResult.getStageTime();
					if (currentStageTime != NO_TIME_FOR_STAGE && currentStageTime != COMPETITION_DNF && currentStageTime != NO_TIME_FOR_COMPETITION ) {
						//OLD VERSION
//						if (currentResult.getStageResult().size() > 0) {
//							stageTimeBack = currentStageTime - currentResult.getStageResult().get(0).getStageTime();
//						}
						if (currentStage.getCompetitorResults().size() > 0) {
							stageTimeBack = currentStageTime - currentStage.getFastestTime();
						}
					}
					currentStageResult.setStageTimesBack(stageTimeBack);
				}
			}
					
			//OLD VERSION
//			//Calculate results for landscape
//			if (mStages != null ) {
//				for (StageResult totalTimeResultForCompetitor : mResults.getTotalResult(competitorClass).getTotalTimeResult()) {							
//					int cardNumber = totalTimeResultForCompetitor.getCardNumber();				
//					Results resultLandscapeObject = new Results("");	
//					//We need to create a true copy of the totalTimeResult for each competitor, otherwise we will overwrite in the original List
//					resultLandscapeObject.addTotalTimeResult((StageResult) AndroidIndependantCompetitionHelper.deepClone(totalTimeResultForCompetitor));					
//					for (int stage = 1; stage <= mResults.getAllStageResults(competitorClass).size(); stage++) {										
//						StageResult newStageResult = new StageResult(cardNumber, NO_TIME_FOR_STAGE );					
//						for (StageResult stageResultObject : mResults.getStageResult(stage,competitorClass).getStageResult()) {						
//							if (cardNumber == stageResultObject.getCardNumber()) {
//								newStageResult = stageResultObject;
//							}
//							if (newStageResult.getStageTime() == NO_TIME_FOR_STAGE) {
//								newStageResult.setStageTimes(Competition.NO_TIME_FOR_STAGE);
//							}
//						}
//												
//						resultLandscapeObject.addStageResult(newStageResult);
//					}
//					
//					if (competitorClass.length() > 0) {
//						resultLandscapeObject.setTitle(competitorClass);
//					}
//					mResultLandscapeList.add(resultLandscapeObject);
//				}
//			}
			

		}
		try {
			saveSessionData(null);
			saveSessionData(mCompetitionName);
		} catch (Exception e1) {
			Log.d("Competition", "Error = " + e1);
		}
	}
}
