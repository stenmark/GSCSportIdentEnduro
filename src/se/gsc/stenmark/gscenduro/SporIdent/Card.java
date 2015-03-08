package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * This class is created when a new SI card is read by the SI main unit.
 * All essential data is parsed from the SI Card and stored in this class.
 * @author Andreas
 *
 */
public class Card implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int numberOfPunches;
	public long cardNumber;
	public Punch startPunch; //Not used by GSC competitions
	public Punch finishPunch; //Not used by GSC competitions
	public Punch checkPunch; //Not used by GSC competitions
	public ArrayList<Punch> punches;
	public ArrayList<Punch> doublePunches;
	public String errorMsg;
	
	public Card(){
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
		"  Number of punches " + numberOfPunches;
//		"\nstart punch " + startPunch.toString() +
//		"\nfinsh punch " + finishPunch.toString() + 
//		"\ncheck punch " + checkPunch.toString();
	
		
		for( Punch punch : punches ){
			result += "\n" + punch.toString();
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
