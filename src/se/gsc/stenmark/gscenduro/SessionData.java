package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.webtime.WebTimePeristentData;

public class SessionData {
	public Competition competition;
	public WebTimePeristentData webTimeData;
	
	public SessionData(Competition competition, WebTimePeristentData webTimeData){
		this.competition = competition;
		this.webTimeData = webTimeData;
	}
	public SessionData(Competition competition ){
		this.competition = competition;
		this.webTimeData = new WebTimePeristentData();
		
	}
}
