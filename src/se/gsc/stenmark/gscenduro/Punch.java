package se.gsc.stenmark.gscenduro;

public class Punch{
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
