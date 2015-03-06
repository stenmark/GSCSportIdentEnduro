package se.gsc.stenmark.gscenduro;

import java.io.Serializable;

public class TrackResult implements Serializable {
	private static final long serialVersionUID = 201111020011L; 
	
	private String mNames;
	private int mCardNumber;
	private Long mTrackTimes;
	private Long mTrackTimesBack;
	
	public TrackResult(String names, int cardNumber, Long trackTimes) {
		mNames = names;
		mCardNumber = cardNumber;
		mTrackTimes = trackTimes;
		mTrackTimesBack = (long) 0;
	}
	
	public String getName() {
	    return mNames;
	}

	public int getCardNumber() {
	    return mCardNumber;
	}
	
	public void setName(String name) {
	    this.mNames = name;
	}
	
	public Long getTrackTimes() {
	    return mTrackTimes;
	}
	
	public void setTrackTimes(Long TrackTimes) {
	    this.mTrackTimes = TrackTimes;
	}
	
	public Long getTrackTimesBack() {
	    return mTrackTimesBack;
	}
	
	public void setTrackTimesBack(Long TrackTimesBack) {
	    this.mTrackTimesBack = TrackTimesBack;
	}

}
