package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;

import se.gsc.stenmark.gscenduro.SporIdent.Card;

/**
 * Represents a Competitor with a competitor name, an SI-card connected to the competitor and the SI-card number for the competitor.
 * Also List of Long Integers denote the results for this user in the form of tracktimes.
 * @author Andreas
 *
 */
public class Competitor implements Comparable<Competitor>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public int cardNumber;
	public Card card;
	public ArrayList<Long> trackTimes;
	
	public Competitor(String name){
		this.name = name;
		this.cardNumber = -1;
		this.card = null;
		trackTimes = null;
	}
	
	Competitor(String name, int cardNumber){
		this.name = name;
		this.cardNumber = cardNumber;
		this.card = null;
		trackTimes = null;
	}
	
	/**
	 * Calculate the time it took for the competitor complete all tracks in the tracTimes list.
	 * @param useMaxValueOnEmptyResult
	 * @return
	 */
	public long getTotalTime( boolean useMaxValueOnEmptyResult ){
		long totalTime = 0;
		if( trackTimes == null ){
			if(useMaxValueOnEmptyResult){
				return Integer.MAX_VALUE;
			}
			else{
				return 0;
			}
			
		}
		
		for( long trackTime : trackTimes){
			totalTime += trackTime;
		}
		return totalTime;
				
	}
	
	public boolean hasResult(){
		return card != null;
	}

	@Override
	public int compareTo(Competitor another) {
		return (int) (this.getTotalTime(true) - another.getTotalTime(true));
	}
	
}
