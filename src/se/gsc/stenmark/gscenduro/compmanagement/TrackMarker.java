package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

/**
 * Small data Class to store a representation of TrackMarker. I.e. a pair of SI
 * control units. i.e. SS1 can be represented by start=71 and finish=72
 * 
 * @author Andreas
 * 
 */
public class TrackMarker implements Serializable {

	private static final long serialVersionUID = 1L;
	private int mStart;
	private int mFinish;

	public TrackMarker(int start, int finish) {
		setStart(start);
		setFinish(finish);
	}

	public int getFinish() {
		return mFinish;
	}

	public void setFinish(int finish) {
		mFinish = finish;
	}

	public int getStart() {
		return mStart;
	}

	public void setStart(int start) {
		mStart = start;
	}
}
