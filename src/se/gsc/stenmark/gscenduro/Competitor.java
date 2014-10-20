package se.gsc.stenmark.gscenduro;

import java.util.List;

public class Competitor implements Comparable<Competitor>{
	public String name;
	public int cardNumber;
	public Card card;
	public List<Long> trackTimes;
	
	Competitor(String name){
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
	
	public long getTotalTime(){
		long totalTime = 0;
		if( trackTimes == null ){
			return Integer.MAX_VALUE;
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
		return (int) (this.getTotalTime() - another.getTotalTime());
	}
	
}
