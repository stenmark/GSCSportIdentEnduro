package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import se.gsc.stenmark.gscenduro.SporIdent.Card;

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
	public static final String CURRENT_COMPETITION = "current_competition";
	
	//Circular buffer to hold the 6 most recently read cards
	public CircularFifoQueue<String> lastReadCards = new CircularFifoQueue<String>(6);
	private Stage totalResults = null;
	private Competitors mCompetitors = null;
	private List<Stage> stages = new ArrayList<Stage>();
	private String mCompetitionName;
	private String mCompetitionDate = "";
	private int mCompetitionType = SVART_VIT_TYPE;
	
	public Competition() {
		totalResults = new Stage();
		mCompetitors = new Competitors();
		setCompetitionName("New");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd", new Locale("swedish", "sweden"));
		String currentDateandTime = sdf.format(new Date());
		setCompetitionDate(currentDateandTime);
	}
			
	public Stage getTotalResults(){
		return totalResults;
	}
	public List<Stage> getStages() {
		return stages;	
	}
	
	public int getNumberOfStages(){
		return stages.size();
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
		
	public String processNewCard(Card card, Boolean calculateResultsAfterAdd) {
		Competitor foundCompetitor = mCompetitors.findByCard(card);
		if (foundCompetitor == null) {
			return "WARNING! Could not find any competitor with card number: " + card.getCardNumber() + "\n";
		}
		
		String status = foundCompetitor.processCard(card, stages, mCompetitionType);
		
		if (calculateResultsAfterAdd) {				
			calculateResults();
		}

		return status;
	}
	
	public void calculateResults() {
		clearResults();
		
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
			totalResults.title = classString + "Total time";
	
			for (int i = 0; i < stages.size(); i++) {
				stages.get(i).title = classString + "Stage " + (i+1);
			}

			// Add total times		
			for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
				Competitor currentCompetitor = currentCompetitorEntry.getValue();
				StageResult totalTimeResult;
				if ((mCompetitionType == SVART_VIT_TYPE) || (currentCompetitor.getCompetitorClass().equals(competitorClass))) {
					if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() == 0) ) {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(),NO_TIME_FOR_COMPETITION);
					} else if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() < getNumberOfStages() )) {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), COMPETITION_DNF);
					} 
					else {
						totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), currentCompetitor.getTotalTime( getNumberOfStages() ));							
					}
					totalResults.addCompetitorResult(totalTimeResult);
				}
			}		
			//Sort total times
			totalResults.sortStageResult(NO_TIME_FOR_COMPETITION);

			StageResult stageResult;
			// Add stage times
			for (int stageNumber = 0; stageNumber <  getNumberOfStages(); stageNumber++) {
				for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
					Competitor currentCompetitor = currentCompetitorEntry.getValue();
					if ((mCompetitionType == SVART_VIT_TYPE) || (currentCompetitor.getCompetitorClass().equals(competitorClass))) {			

						Long stageTime = NO_TIME_FOR_STAGE;
						if ((currentCompetitor.hasResult()) && (currentCompetitor.getStageTimes().size() > stageNumber)) {  //OLD VERSION -1
							stageTime = currentCompetitor.getStageTimes().getTimesOfStage(stageNumber);  //OLD VERSION -1
						} 
		
						stageResult = new StageResult(currentCompetitor.getCardNumber(), stageTime);
						stages.get(stageNumber).addCompetitorResult(stageResult);
					}						
				}
	
				//Sort stages times
				stages.get(stageNumber).sortStageResult(NO_TIME_FOR_STAGE);
			}

			//Calculate time back
			totalResults.calculateTimeBack();
			for( Stage currentStage : stages ) {
				currentStage.calculateTimeBack();
			}
		}
	}
	
	public void importStages(String stagesToImport) {
		String[] stageControls = stagesToImport.split(",");				
		stages.clear();
		for (int i = 0; i < stageControls.length; i += 2) {
			int startControl = 0;
			int finishControl = 0;
			startControl = Integer.parseInt(stageControls[i]);
			finishControl = Integer.parseInt(stageControls[i + 1]);
			stages.add(new Stage(startControl, finishControl));
		}
	}

	public void clearResults() {
		for( Stage stage : stages){
			stage.clearResult();
		}
		totalResults.clearResult();
	}
	
	public List<String> getControls() {
		List<String> controls = new ArrayList<String>();
		if (stages != null) {
			for (Stage stage : stages) {
				if (!controls.contains(Integer.toString(stage.start))) {
					controls.add(Integer.toString(stage.start));
				}
				if (!controls.contains(Integer.toString(stage.finish))) {
					controls.add(Integer.toString(stage.finish));
				}
			}
		}
		return controls;
	}
	
}
