package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

public class Competiror {
	public String name;
	public int cardNumber;
	public Card card;
	public List<Integer> trackTimes;
	
	Competiror(String name){
		this.name = name;
		this.cardNumber = -1;
		this.card = null;
		trackTimes = new ArrayList<Integer>();
	}
	
	Competiror(String name, int cardNumber){
		this.name = name;
		this.cardNumber = cardNumber;
		this.card = null;
		trackTimes = new ArrayList<Integer>();
	}
	
}
