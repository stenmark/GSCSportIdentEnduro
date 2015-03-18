package se.gsc.stenmark.gscenduro;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {
	private static final long serialVersionUID = 201111020001L; 
	
	private String mTitle;
	public ArrayList<TrackResult> mTrackResult;
	
	public Result(String Title) {
		mTitle = Title;
		mTrackResult = new ArrayList<TrackResult>();
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String Title) {
		mTitle = Title;
	}
	
	public ArrayList<TrackResult> getTrackResult() {
		return mTrackResult;
	}			
}
