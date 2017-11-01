package se.gsc.stenmark.gscenduro.webtime;


import java.io.Serializable;

import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class WebTimeHandler implements Serializable{
	private static final long serialVersionUID = 1L;
	private Competitor competitorsOnStage[] = new Competitor[3];

	public WebTimeHandler() {
		for( int i = 0; i < competitorsOnStage.length; i++){
			competitorsOnStage[i] = null;
		}
	}

	public void addCompetitorOnStage( Competitor competitor){
		try{
			LogFileWriter.writeLog("debugLog", "ENTER addCompetitorOnStage");
			for( int i = 0; i < competitorsOnStage.length; i++){
				LogFileWriter.writeLog("debugLog", "addCompetitorOnStage loop " + i);
				if(competitorsOnStage[i] == null ){
					competitorsOnStage[i] = competitor;
					LogFileWriter.writeLog("debugLog", "addCompetitorOnStage found and adding " + competitor.toString());
					return;
				}
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	public void removeCompetitorOnStage( Competitor competitor){
		for( int i = 0; i < competitorsOnStage.length; i++){
			if(competitorsOnStage[i] == competitor ){
				competitorsOnStage[i] = null;
				return;
			}
		}
	}

	public Competitor[] getCompetitorsOnstage(){
		return competitorsOnStage;
	}

}
