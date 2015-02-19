package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

/**
 * Small data Class to store a representation of TrackMarker. I.e. a pair of SI control units.
 * i.e. SS1 can be represented by start=71 and finish=72
 * @author Andreas
 *
 */
public class TrackMarker implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int start;
	public int finish;
	
	public TrackMarker(int start, int finish){
		this.start = start;
		this.finish = finish;
	}
}
