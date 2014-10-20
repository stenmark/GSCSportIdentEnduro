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
	public String errorMsg;
	
	Card(){
		cardNumber = 0;
		numberOfPunches = 0;
		startPunch = new Punch(-1, -1);
		finishPunch = new Punch(-1, -1);
		checkPunch = new Punch(-1, -1);
		punches = new ArrayList<Punch>();
		errorMsg = "";
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
