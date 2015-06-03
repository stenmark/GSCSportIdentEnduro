package se.gsc.stenmark.gscenduro;

import java.io.Serializable;

public class TrackResult implements Serializable {
	
	private static final long serialVersionUID = 201111020011L; 
	private int mCardNumber;
	private int mRank;
	private Long mTrackTimes;
	private Long mTrackTimesBack;
	private Boolean mDnf;
	
	public TrackResult(int cardNumber, Long trackTimes, Boolean dnf) {
		setCardNumber(cardNumber);
		setTrackTimes(trackTimes);
		setTrackTimesBack((long) 0);
		setDnf(dnf);
	}

	public int getCardNumber() {
	    return mCardNumber;
	}
	
	public void setCardNumber(int cardNumber) {
	    mCardNumber = cardNumber;
	}	
	
	public Long getTrackTimes() {
	    return mTrackTimes;
	}
	
	public void setTrackTimes(Long TrackTimes) {
	    mTrackTimes = TrackTimes;
	}
	
	public Long getTrackTimesBack() {
	    return mTrackTimesBack;
	}
	
	public void setTrackTimesBack(Long TrackTimesBack) {
	    mTrackTimesBack = TrackTimesBack;
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
