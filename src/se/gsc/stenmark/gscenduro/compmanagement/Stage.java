package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Small data Class to store a representation of StageControls. I.e. a pair of SI
 * control units. i.e. SS1 can be represented by start = 71 and finish = 72
 * 
 * @author Andreas
 * 
 */
public class Stage implements Serializable {

	private static final long serialVersionUID = 4L;
	public String title;
	public int start;
	public int finish;
	private List<StageResult> competitorResults = new ArrayList<StageResult>();

	public Stage() {
		this.start = -1;
		this.finish = -1;
	}
	
	public Stage(int start, int finish) {
		this.start = start;
		this.finish = finish;
	}
	
	public void addCompetitorResult( StageResult newCompetitorResult ){
		competitorResults.add(newCompetitorResult);
	}
	
	public long getFastestTime(){
		if( !competitorResults.isEmpty() ){
			return competitorResults.get(0).getStageTime();
		}
		return 0;
	}
	
	public int numberOfCompetitors(){
		return competitorResults.size();
	}
	
	public StageResult getStageResultByCardnumber( int cardNumber ){
		for(StageResult competitorResult : competitorResults){
			if(competitorResult.getCardNumber() == cardNumber){
				return competitorResult;
			}
		}
		return null;
	}
	
	public List<StageResult> getCompetitorResults(){
		return competitorResults;
	}
	
	public void clearResult(){
		competitorResults.clear();
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
	public Long calculateSlowestOnStage() {
		if( competitorResults.isEmpty() ){
			return 0L;
		}
		List<Long>timeDeltaList = new ArrayList<Long>();

		for( int currentCompetitorNumber = 0; currentCompetitorNumber < competitorResults.size()-1; currentCompetitorNumber++){
			int nextCompetitorNumber = currentCompetitorNumber+1;
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
		for( int currentCompetitorNumber = 0; currentCompetitorNumber < competitorResults.size()-1; currentCompetitorNumber++){
			int nextCompetitorNumber = currentCompetitorNumber+1;
			Long currentCompetitorStageTime = competitorResults.get(currentCompetitorNumber).getStageTime();
			Long nextCompetitorStageTime = competitorResults.get(nextCompetitorNumber).getStageTime();

			Long timeDelta = nextCompetitorStageTime - currentCompetitorStageTime;
			//Filter out competitors that are a lot slower than most competitors. 
			//only filter out competitors that are from the lower half of the competitor list (Use timeDelta list since it already contains only competitors with stage times set
			if( (timeDelta > medianTimeDelta*4) && (currentCompetitorNumber > (timeDeltaList.size()/2)) ){
				return currentCompetitorStageTime;
			}
		}
		return competitorResults.get( competitorResults.size()-1 ).getStageTime();
	}	
	
	public void calculateTimeBack(){
		for (StageResult currentStageResult : competitorResults ) {
			Long stageTimeBack = Competition.NO_TIME_FOR_STAGE;
			Long currentStageTime = currentStageResult.getStageTime();
			if (currentStageTime != Competition.NO_TIME_FOR_STAGE && currentStageTime != Competition.COMPETITION_DNF && currentStageTime != Competition.NO_TIME_FOR_COMPETITION ) {
				if ( !competitorResults.isEmpty() ) {
					stageTimeBack = currentStageTime - getFastestTime();
				}
			}
			currentStageResult.setStageTimesBack(stageTimeBack);
		}
	}
	
	public void sortStageResult( final long NO_TIME_MAGIC_NUMBER) {
		if ( !competitorResults.isEmpty() ) {		
			Collections.sort(competitorResults);
			int rank = 1;	

			//If the first competitor has no time, then no other competitors will have time either (after sort)
			if (competitorResults.get(0).getStageTime() == NO_TIME_MAGIC_NUMBER)
			{
				//Set all competitors to DNF
				for (int i = 0; i < competitorResults.size(); i++) {	
					competitorResults.get(i).setRank(Competition.RANK_DNF);
				}
			}
			else
			{
				//Init first competitor to Rank 1
				competitorResults.get(0).setRank(rank);			
				for (int i = 1; i < competitorResults.size(); i++) {
					StageResult competitorResult = competitorResults.get(i);
					Long competitorStageTime = competitorResult.getStageTime();
					//Only increase the rank counter if two competitors dont have the same time. (Same time == same rank, by lowest possible rank)
					if( competitorStageTime > competitorResults.get(i - 1).getStageTime()) {
						rank = i + 1;
					}
					
					//Only set the correct rank for the competitor if the competitor has a valid time
					if ( competitorStageTime == NO_TIME_MAGIC_NUMBER || competitorStageTime == Competition.COMPETITION_DNF ) {
						competitorResult.setRank(Competition.RANK_DNF);
					} else {			
						competitorResult.setRank(rank);
					}
				}
			}
		}
	}

}
