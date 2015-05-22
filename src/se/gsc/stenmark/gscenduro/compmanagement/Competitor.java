package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;

import se.gsc.stenmark.gscenduro.SporIdent.Card;

/**
 * Represents a Competitor with a competitor name, an SI-card connected to the
 * competitor and the SI-card number for the competitor. Also List of Long
 * Integers denote the results for this user in the form of tracktimes.
 * 
 * @author Andreas
 * 
 */
public class Competitor implements Comparable<Competitor>, Serializable {

	private static final long serialVersionUID = 1L;
	private String mName;
	private int mCardNumber;
	private Card mCard;
	private ArrayList<Long> mTrackTimes;
	private String mTeam;
	private String mCompetitorClass;
	private int mStartNumber;
	private int mStartGroup;

	public Competitor(String name) {
		setName(name);
		setCardNumber(-1);
		setCard(null);
		setTrackTimes(null);
	}

	Competitor(String name, int cardNumber) {
		setName(name);
		setCardNumber(cardNumber);
		setCard(null);
		setTrackTimes(null);
	}

	Competitor(String name, int cardNumber, String team,
			String competitorClass, int startNumber, int startGroup) {
		setName(name);
		setCardNumber(cardNumber);
		setCard(null);
		setTrackTimes(null);
		setTeam(team);
		setCompetitorClass(competitorClass);
		setStartNumber(startNumber);
		setStartGroup(startGroup);
	}

	/**
	 * Calculate the time it took for the competitor complete all tracks in the
	 * tracTimes list.
	 * 
	 * @param useMaxValueOnEmptyResult
	 * @return
	 */
	public long getTotalTime(boolean useMaxValueOnEmptyResult) {
		long totalTime = 0;
		if (mTrackTimes == null) {
			if (useMaxValueOnEmptyResult) {
				return Integer.MAX_VALUE;
			} else {
				return 0;
			}
		}

		for (long trackTime : mTrackTimes) {
			totalTime += trackTime;
		}
		return totalTime;
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
	}

	public boolean hasResult() {
		return mCard != null;
	}

	public ArrayList<Long> getTrackTimes() {
		return mTrackTimes;
	}

	public void setTrackTimes(ArrayList<Long> trackTimes) {
		mTrackTimes = trackTimes;
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

	@Override
	public int compareTo(Competitor another) {
		return (int) (this.getTotalTime(true) - another.getTotalTime(true));
	}
}
