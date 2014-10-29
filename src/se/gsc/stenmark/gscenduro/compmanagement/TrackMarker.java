package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;

public class TrackMarker implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int start;
	public int finish;
	public String compName;
	
	public TrackMarker(int start, int finish, String compName){
		this.start = start;
		this.finish = finish;
		this.compName = compName;
	}
}
