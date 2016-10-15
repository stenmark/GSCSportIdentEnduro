package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

/**
 * Represents a Competitor with a competitor name, an SI-card connected to the
 * competitor and the SI-card number for the competitor. Also List of Long
 * Integers denote the results for this user in the form of stagetimes.
 * 
 * @author Andreas
 * 
 */
public class Competitor implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private String name;
	private int cardNumber;
	private Card card;	
	private StageTimes mStageTimes;
	private String mTeam;
	private String mCompetitorClass;
	private int mStartNumber;
	private int mStartGroup;

	Competitor(String name, int cardNumber) {
		this.name = name;
		this.cardNumber=cardNumber;
		card = null;
		setStageTimes(new StageTimes());
	}

	Competitor(String name, int cardNumber, String team, String competitorClass, int startNumber, int startGroup) {
		this.name = name;
		this.cardNumber=cardNumber;
		card = null;
		setStageTimes(new StageTimes());
		setTeam(team);
		setCompetitorClass(competitorClass);
		setStartNumber(startNumber);
		setStartGroup(startGroup);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(int cardNumber) {
		this.cardNumber=cardNumber;
	}

	public Card getCard() {
		return card;
	}

	public void clearCard() {
		card = null;
		setStageTimes(null);
	}
	
	public String processCard(Card card, List<Stage> stages, int type) {
		boolean containsAllPunches = true;
		this.card = card;
		card.setCardNumber(cardNumber);
		setStageTimes(new StageTimes());
		
		//For SvartVitt, remove all accidental FinishPunches that are before the first StartPunch
		boolean firstFinishPunchFound = false;
		List<Punch> punchesToRemove = new ArrayList<Punch>();
		int firstStartStationNr = stages.get(0).start;
		int firstFinishStationNr = stages.get(0).finish;
		if (type == Competition.SVART_VIT_TYPE) {
			for( Punch punch : card.getPunches()){
				if(punch.getControl() == firstStartStationNr){
					firstFinishPunchFound = true;
				}
				if( !firstFinishPunchFound ){
					if(punch.getControl() == firstFinishStationNr){
						//punchesToRemove.add(punch);
						punch.setIsFinishPunchBeforeStart(true);
					}
				}
			}
			card.getPunches().removeAll(punchesToRemove);
		}
		
		for (int stageNumber = 0; stageNumber < stages.size(); stageNumber++) {
			long stageTime;
			int stageStartStation = stages.get(stageNumber).start;
			int stageFinishStation = stages.get(stageNumber).finish;
			if (type == Competition.ESS_TYPE) {
				stageTime = card.getStageTimeEss(stageStartStation, stageFinishStation);
			} else {
				stageTime = card.getStageTimeSvartVitt(stageStartStation, stageFinishStation, stageNumber + 1);
				if( stageTime == Competition.NO_TIME_FOR_STAGE ){
					containsAllPunches = false;
				}
			}
			
			mStageTimes.setTimesOfStage(stageNumber, stageTime);
		}		
		
		if( containsAllPunches){
			return "Added card: " + cardNumber + "  Competitor:  " + name + "\n";
		}
		else{
			return "WARNING! " + name + " has not completed all stages. Cardnumber: " + cardNumber + "\n";
		}
		
	}
	
	public boolean hasResult() {
		return card != null;
	}

	public StageTimes getStageTimes() {
		return mStageTimes;
	}

	public void setStageTimes(StageTimes stageTimes) {
		mStageTimes = stageTimes;
	}

	public String getTeam() {
		return mTeam;
	}

	public void setTeam(String team) {
		mTeam = team;
	}

	public String getCompetitorClass() {
		return mCompetitorClass;
	}

	public void setCompetitorClass(String competitorClass) {
		mCompetitorClass = competitorClass;
	}

	public int getStartNumber() {
		return mStartNumber;
	}

	public void setStartNumber(int startNumber) {
		mStartNumber = startNumber;
	}

	public int getStartGroup() {
		return mStartGroup;
	}

	public void setStartGroup(int startGroup) {
		mStartGroup = startGroup;
	}

	public long getTotalTime(int numberOfStages) {
		long totalTime = 0;
		
		if (mStageTimes.size() < numberOfStages) {
			return Competition.NO_TIME_FOR_COMPETITION;
		}
		
		int numberOfCompletedStages = 0;
		for( int i = 0; i < mStageTimes.size(); i++){
			if (mStageTimes.getTimesOfStage(i) != Competition.NO_TIME_FOR_STAGE) {
				numberOfCompletedStages++;
			}
		}
		if( numberOfCompletedStages == 0){
			return Competition.NO_TIME_FOR_COMPETITION;
		}
		if( numberOfCompletedStages < numberOfStages){
			return Competition.COMPETITION_DNF;
		}
		
		for (int i = 0; i < mStageTimes.size(); i++) {
			if (mStageTimes.getTimesOfStage(i) == Competition.NO_TIME_FOR_STAGE) {
				return Competition.NO_TIME_FOR_COMPETITION;
			}			
			totalTime += mStageTimes.getTimesOfStage(i);
		}
		return totalTime;
	}			
}
