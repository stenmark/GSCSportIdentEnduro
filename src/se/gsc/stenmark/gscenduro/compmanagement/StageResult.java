package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

public class StageResult implements Serializable, Comparable<StageResult> {
	
	private static final long serialVersionUID = 7L; 
	private int mCardNumber;
	private Long mRank;
	private Long mStageTime;
	private Long mStageTimeBack;
	
	public StageResult(int cardNumber, Long stageTimes) {
		setCardNumber(cardNumber);
		setStageTimes(stageTimes);
		setStageTimesBack((long) 0);
	}

	public int getCardNumber() {
	    return mCardNumber;
	}
	
	public void setCardNumber(int cardNumber) {
	    mCardNumber = cardNumber;
	}	
	
	public Long getStageTime() {
	    return mStageTime;
	}
	
	public void setStageTimes(Long stageTimes) {
		mStageTime = stageTimes;
	}
	
	public Long getStageTimesBack() {
	    return mStageTimeBack;
	}
	
	public void setStageTimesBack(Long stageTimesBack) {
	    mStageTimeBack = stageTimesBack;
	}
	
	public Long getRank() {
		return mRank;
	}

	public void setRank(long rankDnf) {
		mRank = rankDnf;
	}
	
	@Override
	public String toString(){
		return "CardNumber: " +mCardNumber + " Rank: " + mRank + " StageTime: " + mStageTime + " time back " + mStageTimeBack;
	}

	@Override
	public int compareTo(StageResult another) {
		return getStageTime().compareTo(another.getStageTime());
	}
}
