package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;

/**
 * Represents an SI card punch. I.e. when a competitor has either started a stage of finished a stage.
 * The punches are read from the SI card
 * @author Andreas
 *
 */
public class Punch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Punch(long time, long control){
		this.time = time;
		this.control = control;
		markAsDoublePunch = false;
	}
	public long time;
	public long control;
	public boolean markAsDoublePunch;
	
	public Long getTime() {
	    return time;
	}
	
	@Override
	public String toString(){
		return "Control=" + control + "  Time="+time;
	}
	
}
