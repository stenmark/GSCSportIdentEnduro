package se.gsc.stenmark.gscenduro;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {
	
	private static final long serialVersionUID = 201111020001L; 	
	private String mTitle;
	private ArrayList<TrackResult> mTrackResult;
	
	public Result(String title) {
		setTitle(title);
		setTrackResult(new ArrayList<TrackResult>());
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
	
	public ArrayList<TrackResult> getTrackResult() {
		return mTrackResult;
	}		
	
	public void setTrackResult(ArrayList<TrackResult> trackResult) {
		mTrackResult = trackResult;
	}			
}
