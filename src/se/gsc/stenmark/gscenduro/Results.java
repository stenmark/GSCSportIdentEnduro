package se.gsc.stenmark.gscenduro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Results implements Serializable {
	
	private static final long serialVersionUID = 201111020001L; 	
	private String mTitle;	
	private ArrayList<TrackResult> mTrackResult = new ArrayList<TrackResult>();
	
	public Results() {
		setTitle("");
	}
	
	public Results(String title) {
		setTitle(title);
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
	
	public void sortTrackResult() {
		
		if (mTrackResult.size() > 0) {		
			Collections.sort(mTrackResult, new Comparator<TrackResult>() {
				@Override
				public int compare(TrackResult lhs, TrackResult rhs) {
					if (lhs.getDnf()) {
						return 1;
					} else {
						return lhs.getTrackTimes().compareTo(rhs.getTrackTimes());
					}
				}
			});
		
			//Calculate rank		
			int rank = 1;		
			if (mTrackResult.get(0).getTrackTimes() == Integer.MAX_VALUE)
			{
				//No results, set all to dnf
				for (int i = 0; i < mTrackResult.size(); i++) {	
					mTrackResult.get(i).setRank(Integer.MAX_VALUE);
				}
			}
			else
			{
				mTrackResult.get(0).setRank(rank);			
				for (int i = 1; i < mTrackResult.size(); i++) {				
					if (mTrackResult.get(i).getTrackTimes() > mTrackResult.get(i - 1).getTrackTimes()) {
						rank = i + 1;
					}
					
					if (mTrackResult.get(i).getTrackTimes() == Integer.MAX_VALUE) {
						mTrackResult.get(i).setRank(Integer.MAX_VALUE);
					} else {			
						mTrackResult.get(i).setRank(rank);
					}
				}
			}
		}
	}	
}
