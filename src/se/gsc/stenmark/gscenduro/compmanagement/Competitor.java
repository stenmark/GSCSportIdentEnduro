package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;

import se.gsc.stenmark.gscenduro.SporIdent.Card;


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
