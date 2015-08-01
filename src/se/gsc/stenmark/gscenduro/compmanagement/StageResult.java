package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

public class StageResult implements Serializable {
	
	private static final long serialVersionUID = 201111020011L; 
	private int mCardNumber;
	private int mRank;
	private Long mStageTimes;
	private Long mStageTimesBack;
	private Boolean mDnf;
	
	public StageResult(int cardNumber, Long stageTimes, Boolean dnf) {
		setCardNumber(cardNumber);
		setStageTimes(stageTimes);
		setStageTimesBack((long) 0);
		setDnf(dnf);
	}

	public int getCardNumber() {
	    return mCardNumber;
	}
	
	public void setCardNumber(int cardNumber) {
	    mCardNumber = cardNumber;
	}	
	
	public Long getStageTimes() {
	    return mStageTimes;
	}
	
	public void setStageTimes(Long stageTimes) {
		mStageTimes = stageTimes;
	}
	
	public Long getStageTimesBack() {
	    return mStageTimesBack;
	}
	
	public void setStageTimesBack(Long stageTimesBack) {
	    mStageTimesBack = stageTimesBack;
	}

	public Boolean getDnf() {
		return mDnf;
	}
	
	public void setDnf(Boolean dnf) {
		mDnf = dnf;
	}	
	
	public int getRank() {
		return mRank;
	}

	public void setRank(int rank) {
		mRank = rank;
	}	
}
