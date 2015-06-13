package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

import android.util.Log;
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
		setCard(null);
		setStageTimes(new StageTimes());
	}

	Competitor(String name, int cardNumber) {
		setName(name);
		setCardNumber(cardNumber);
		setCard(null);
		setStageTimes(new StageTimes());
	}

	Competitor(String name, int cardNumber, String team, String competitorClass, int startNumber, int startGroup) {
		setName(name);
		setCardNumber(cardNumber);
		setCard(null);
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

	public void setCard(Card card) {
		mCard = card;
		setStageTimes(new StageTimes());
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
			return Integer.MAX_VALUE;
		}

		Log.d("getTotalTime", "mStageTimes.size() = " + mStageTimes.size());
		
		for (int i = 0; i < mStageTimes.size(); i++) {
			if (mStageTimes.getTimesOfStage(i) == Integer.MAX_VALUE) {
				return Integer.MAX_VALUE;
			}			
			Log.d("getTotalTime", "stage = " + i + " Time = " + mStageTimes.getTimesOfStage(i));
			totalTime += mStageTimes.getTimesOfStage(i);
		}
		return totalTime;
	}			
}
