package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;

public class Punch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Punch(long time, long control){
		this.time = time;
		this.control = control;
	}
	public long time;
	public long control;
	
	@Override
	public String toString(){
		return "Control=" + control + "  Time="+time;
	}
	
}
