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
