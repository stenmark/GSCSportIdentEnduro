package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

public class StageResult implements Serializable {
	
	private static final long serialVersionUID = 201111020011L; 
	private int mCardNumber;
	private int mRank;
	private Long mStageTimes;
	private Long mStageTimesBack;
	
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
	    return mStageTimes;
	}
	
//	public Long getStageTimeForSorting() {
//		if( getDnf() ){
//			return Competition.NO_TIME_FOR_STAGE;
//		}
//	    return mStageTimes;
//	}
	
	public void setStageTimes(Long stageTimes) {
		mStageTimes = stageTimes;
	}
	
	public Long getStageTimesBack() {
	    return mStageTimesBack;
	}
	
	public void setStageTimesBack(Long stageTimesBack) {
	    mStageTimesBack = stageTimesBack;
	}
	
	public Integer getRank() {
		return mRank;
	}

	public void setRank(int rank) {
		mRank = rank;
	}
	
	@Override
	public String toString(){
		return "CardNumber: " +mCardNumber + " Rank: " + mRank + " StageTime: " + mStageTimes + " time back " + mStageTimesBack;
	}
}
