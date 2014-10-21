package se.gsc.stenmark.gscenduro;

import java.io.Serializable;

public class Punch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Punch(long time, long control){
		this.time = time;
		this.control = control;
	}
	public long time;
	public long control;
	
	@Override
	public String toString(){
		return "Time " + time + " control " + control; 
	}
	
}
