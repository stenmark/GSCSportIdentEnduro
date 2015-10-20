package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import se.gsc.stenmark.gscenduro.SporIdent.Card;

/**
 * Represents a Competitor with a competitor name, an SI-card connected to the
 * competitor and the SI-card number for the competitor. Also List of Long
 * Integers denote the results for this user in the form of stagetimes.
 * 
 * @author Andreas
 * 
 */
public class Competitor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String mName;
	private int mCardNumber;
	private Card mCard;	
	private StageTimes mStageTimes;
	private String mTeam;
	private String mCompetitorClass;
	private int mStartNumber;
	private int mStartGroup;

	public Competitor(String name) {
		setName(name);
		setCardNumber(-1);
		mCard = null;
		setStageTimes(new StageTimes());
	}

	Competitor(String name, int cardNumber) {
		setName(name);
		setCardNumber(cardNumber);
		mCard = null;
		setStageTimes(new StageTimes());
	}

	Competitor(String name, int cardNumber, String team, String competitorClass, int startNumber, int startGroup) {
		setName(name);
		setCardNumber(cardNumber);
		mCard = null;
		setStageTimes(new StageTimes());
		setTeam(team);
		setCompetitorClass(competitorClass);
		setStartNumber(startNumber);
		setStartGroup(startGroup);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getCardNumber() {
		return mCardNumber;
	}

	public void setCardNumber(int cardNumber) {
		mCardNumber = cardNumber;
	}

	public Card getCard() {
		return mCard;
	}

	public void clearCard() {
		mCard = null;
		setStageTimes(null);
	}
	
	public String processCard(Card card, Stages stages, int type) {
		String status = "";
		mCard = card;
		mCard.setCardNumber(mCardNumber);
		setStageTimes(new StageTimes());
		
		status += mName + ", " + mCardNumber + ", ";
		
		for (int i = 0; i < stages.size(); i++) {
			long stageTime;
						
			if (type == 1) {
				stageTime = mCard.getStageTimeEss(stages.getStages().get(i).getStart(), stages.getStages().get(i).getFinish());
			} else {
				stageTime = mCard.getStageTimeSvartVitt(stages.getStages().get(i).getStart(), stages.getStages().get(i).getFinish(), i + 1);
			}
			
			mStageTimes.setTimesOfStage(i, stageTime);
			
			if (i != 0) {
				status += ", ";
			}				
			status += "SS" + i + "=" + AndroidIndependantCompetitionHelper.secToMinSec(stageTime);
		}		
		status += "\n";
		return status;
	}
	
	public boolean hasResult() {
		return mCard != null;
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
