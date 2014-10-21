package se.gsc.stenmark.gscenduro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int numberOfPunches;
	public long cardNumber;
	public Punch startPunch;
	public Punch finishPunch;
	public Punch checkPunch;
	public ArrayList<Punch> punches;
	public ArrayList<Punch> doublePunches;
	public String errorMsg;
	
	Card(){
		cardNumber = 0;
		numberOfPunches = 0;
		startPunch = new Punch(-1, -1);
		finishPunch = new Punch(-1, -1);
		checkPunch = new Punch(-1, -1);
		punches = new ArrayList<Punch>();
		doublePunches = new ArrayList<Punch>();
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
	
	public void removeDoublePunches(){
		List<Integer> doublePunchesPos = new ArrayList<Integer>();
		for( int i = 0; i < punches.size()-1; i++){
			if(punches.get(i).control == punches.get(i+1).control ){
				doublePunchesPos.add(i);
			}
		}
		
		for(int doublePunchPos : doublePunchesPos){
			doublePunches.add( punches.get(doublePunchPos));
			punches.remove(doublePunchPos);
		}

		
	}
}
