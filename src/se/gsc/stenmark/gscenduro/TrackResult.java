package se.gsc.stenmark.gscenduro;

import java.io.Serializable;

import android.util.Log;

public class TrackResult implements Serializable {
	private static final long serialVersionUID = 201111020011L; 
	
	private String mNames;
	private int mCardNumber;
	private Long mTrackTimes;
	private Long mTrackTimesBack;
	private Boolean mDNF;
	
	public TrackResult(String names, int cardNumber, Long trackTimes, Boolean DNF) {
		mNames = names;
		mCardNumber = cardNumber;
		mTrackTimes = trackTimes;
		mTrackTimesBack = (long) 0;
		mDNF = DNF;
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
		if( getDNF() ){
			return 1000000L;   //Just add 1 million seconds as penalty for DNF to make sure they are sorted at the end of the result list
		}
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

	public Boolean getDNF() {
		return mDNF;
	}
	
	public void setDNF(Boolean DNF) {
		mDNF = DNF;
	}	
}
