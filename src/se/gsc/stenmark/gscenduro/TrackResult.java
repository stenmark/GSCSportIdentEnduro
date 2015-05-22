package se.gsc.stenmark.gscenduro;

import java.io.Serializable;

public class TrackResult implements Serializable {
	
	private static final long serialVersionUID = 201111020011L; 
	
	private String mNames;
	private int mCardNumber;
	private Long mTrackTimes;
	private Long mTrackTimesBack;
	private Boolean mDNF;
	
	public TrackResult(String names, int cardNumber, Long trackTimes, Boolean DNF) {
		setName(names);
		setCardNumber(cardNumber);
		setTrackTimes(trackTimes);
		setTrackTimesBack((long) 0);
		setDNF(DNF);
	}
	
	public String getName() {
	    return mNames;
	}

	public void setName(String name) {
	    mNames = name;
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

	public Boolean getDNF() {
		return mDNF;
	}
	
	public void setDNF(Boolean DNF) {
		mDNF = DNF;
	}	
}
