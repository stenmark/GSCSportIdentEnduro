package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

public class Card {
	public int numberOfPunches;
	public long cardNumber;
	public Punch startPunch;
	public Punch finishPunch;
	public Punch checkPunch;
	public List<Punch> punches;
	
	Card(){
		punches = new ArrayList<Punch>();
	}
	
	@Override
	public String toString(){
		String result  = "CardNumber " + cardNumber +
		" Number of punches " + numberOfPunches + 
		" start punch " + startPunch.toString() +
		" finsh punch " + finishPunch.toString() + 
		" check punch " + checkPunch.toString();
		
		for( Punch punch : punches ){
			result += " " + punch.toString();
		}
		
		return result;
	}
}
