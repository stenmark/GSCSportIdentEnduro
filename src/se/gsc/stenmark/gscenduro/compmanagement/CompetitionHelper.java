package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided
 * for each method call (no internal variables allowed)
 * 
 * @author Andreas
 * 
 */
public abstract class CompetitionHelper {

	public static String milliSecToMinSecMilliSec(Long milliSec) {
		if (milliSec == Competition.NO_TIME_FOR_STAGE) {
			return "no result";
		}
		
		if (milliSec == Competition.NO_TIME_FOR_COMPETITION) {
			return "no time";
		}
		
		if (milliSec == Competition.COMPETITION_DNF) {
			return "DNF";
		}
		
		double milliSecAsDouble = milliSec;
		double tentSecAsDouble = milliSecAsDouble/100D;
		long totalTimeTenth = Math.round(tentSecAsDouble);
		long totalTimeSec = totalTimeTenth/10;
		long totalTimeMin = totalTimeSec/60;
		totalTimeTenth -= totalTimeSec*10;
		totalTimeSec -= (totalTimeMin*60);
		
		return String.format("%02d:%02d.%01d", totalTimeMin, totalTimeSec, totalTimeTenth);

	}

	//OLD VERSION
//	public static String getResultsAsCsvString(Stages stage, Stage totalResults, Competitors competitors, int type) {
//		String resultData = "";
//		
//		if (type == 1)  {
//			resultData = "Rank,Name,Card Number,Team,Start Number,Total Time,";
//		} else {
//			resultData = "Rank,Name,Card Number,Total Time,";
//		}
//			
//		for (int i = 0; i < stage.size(); i++) {
//			resultData += "Stage " + (i + 1) + ",Rank,Time Back,";
//		}
//		resultData += "\n";
//
//		for (int index = 0; index < results.size(); index++) {
//			if (type == Competition.ESS_TYPE)  {
//				if (index == 0) {
//					resultData += results.get(index).getTitle() + "\n";
//				} else if (results.get(index).getTitle() != results.get(index - 1).getTitle()) {
//					resultData += results.get(index).getTitle() + "\n";
//				}
//			}				
//			
//			int cardNumber = results.get(index).getStageResult().get(0).getCardNumber();					
//			int rank = results.get(index).getStageResult().get(0).getRank();					
//			if (rank == Competition.RANK_DNF) {			
//				resultData += "-,";
//			} else {
//				resultData += rank + ",";
//			}
//			
//			resultData += competitors.getByCardNumber(cardNumber).getName() + ",";
//			resultData += cardNumber + ",";
//			if (type == 1)  {
//				resultData += competitors.getByCardNumber(cardNumber).getTeam() + ",";					
//				resultData += String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + ",";
//			}
//			resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(0).getStageTime()) + ",";	
//									
//			for(int stageNumber = 1; stageNumber < results.get(index).getStageResult().size(); stageNumber++) {										
//				resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTime()) + ",";
//				resultData += results.get(index).getStageResult().get(stageNumber).getRank() + ",";
//				resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTimesBack()) + ",";
//			}			
//			resultData += "\n";
//		}
//		
//		return resultData;
//	}

	//OLD VERSION
	public static String getResultsAsHtmlString(String name, String date, Stages stage, Competitors competitors, int type, Competition competition) {
		return "Not done yet";
	}
//	public static String getResultsAsHtmlString(String name, String date, Stages stage, List<Results> results, Competitors competitors, int type, Competition competition) {
//		String resultData = "<!DOCTYPE html>\n<html>\n<body>\n";		
//		resultData += "<style>\ntable, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\n}\nth, td {\npadding: 5px;\n}\n</style>\n";		
//		resultData += "<h1>" + name + ", " + date + "</h1>\n";
//		
//		for (int index = 0; index < results.size(); index++) {
//			
//			if (type == 1)  {				
//				if (index == 0) {
//					resultData += "<h1>" + results.get(index).getTitle() + "</h1>\n";
//					resultData += "<table style=\"width:100%\">\n";
//					resultData += "<tr>\n<th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Team</th><th>Start Number</th><th>Total Time</th>";
//					for (int i = 0; i < stage.size(); i++) {
//						resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//					}
//					resultData += "</tr>\n";							
//				} else if (results.get(index).getTitle() != results.get(index - 1).getTitle()) {
//					resultData += "</table>\n";
//					resultData += "<h1>" + results.get(index).getTitle() + "</h1>\n";
//					resultData += "<table style=\"width:100%\">\n";
//					resultData += "<tr>\n<th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Team</th><th>Start Number</th><th>Total Time</th>";
//					for (int i = 0; i < stage.size(); i++) {
//						resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//					}
//					resultData += "</tr>\n";		
//				}				
//			} else if (index == 0) {
//				resultData += "<table style=\"width:100%\">\n";
//				resultData += "<tr><th><center>Rank</center></th><th>Name</th><th>Card Number</th><th>Total Time</th>";
//				
//				for (int i = 0; i < stage.size(); i++) {
//					resultData += "<th>Stage " + (i + 1) + "</th><th>Rank</th><th>Time Back</th>";
//				}
//				resultData += "</tr>\n";
//			}			
//			
//			resultData += "<tr>\n";								
//			int rank = results.get(index).getStageResult().get(0).getRank();
//			resultData += "<td><center>";
//			if (rank == Competition.RANK_DNF) {			
//				resultData += "-";
//			} else {
//				resultData += rank;
//			}
//			resultData += "</center></td>";
//			
//			int cardNumber = results.get(index).getStageResult().get(0).getCardNumber();
//			resultData += "<td>" + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()) + "</td>";			
//			resultData += "<td><center>" + cardNumber + "</center></td>";
//			
//			Log.d("html", "convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()) = " + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getName()));
//			Log.d("html", "competitors.getByCardNumber(cardNumber).getName() = " + competitors.getByCardNumber(cardNumber).getName());
//			
//			if (type == 1)  {
//				resultData += "<td>" + AndroidIndependantCompetitionHelper.convertToHtmlChars(competitors.getByCardNumber(cardNumber).getTeam()) + "</td>";					
//				resultData += "<td><center>" + String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + "</center></td>";
//			}
//			resultData += "<td><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(0).getStageTime()) + "</center></td>";	
//						
//			for(int stageNumber = 1; stageNumber < results.get(index).getStageResult().size(); stageNumber++) {			
//				
//				if (results.get(index).getStageResult().get(stageNumber).getRank() != Competition.RANK_DNF) {								
//					Long fastestTimeOnStage = competition.getFastestOnStage(results.get(index).getTitle(), stageNumber); 
//					Long slowestTimeOnStage = competition.calculateSlowestOnStage(results.get(index).getTitle(), stageNumber);
//					Long competitorStageTime = results.get(index).getStageResult().get(stageNumber).getStageTime();
//					
//					int color = CompetitionHelper.generateRedToGreenColorTransition(fastestTimeOnStage, slowestTimeOnStage, competitorStageTime, rank);
//	
//					color -= 0xff000000;
//					resultData += "<td bgcolor=\"#" + Integer.toHexString(color) + "\"><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTime()) + "</center></td>";
//					resultData += "<td><center>" + results.get(index).getStageResult().get(stageNumber).getRank() + "</center></td>";
//					resultData += "<td><center>" + AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTimesBack()) + "</center></td>";
//				} else {
//					resultData += "<td><center>-</center></td>";
//					resultData += "<td><center>-</center></td>";
//					resultData += "<td><center>-</center></td>";
//				}
//			}			
//			resultData += "</tr>\n";
//		}		
//		
//		resultData += "</table>\n</body>\n</html>\n";
//		
//		return resultData;
//	}
	
	public static String getResultsAsCsvString(Stages stages, Stage totalResults, Competitors competitors, int type) {
		String resultData = "";
		
		if (type == Competition.ESS_TYPE)  {
			resultData = "Rank,Name,Card Number,Team,Start Number,Total Time,";
		} else {
			resultData = "Rank,Name,Card Number,Total Time,";
		}
			
		for (int i = 0; i < stages.size(); i++) {
			resultData += "Stage " + (i + 1) + ",Rank,Time Back,";
		}
		resultData += "\n";
		if( type == Competition.ESS_TYPE){
			resultData += "\n";
		}
		
		for( int i  = 0; i < totalResults.numberOfCompetitors(); i++ ){		
			int cardNumber = totalResults.getCompetitorResults().get(i).getCardNumber();				
			int rank = totalResults.getCompetitorResults().get(i).getRank();				
			if (rank == Competition.RANK_DNF) {			
				resultData += "-,";
			} else {
				resultData += rank + ",";
			}
			
			resultData += competitors.getByCardNumber(cardNumber).getName() + ",";
			resultData += cardNumber + ",";
			if (type == Competition.ESS_TYPE)  {
				resultData += competitors.getByCardNumber(cardNumber).getTeam() + ",";					
				resultData += String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + ",";
			}
			resultData += CompetitionHelper.milliSecToMinSecMilliSec( totalResults.getCompetitorResults().get(i).getStageTime() ) + ",";	
						
			for(int stageNumber = 0; stageNumber < stages.size(); stageNumber++) {	
				StageResult stageResult = stages.get(stageNumber).getStageResultByCardnumber(cardNumber);
				if( stageResult != null){
					resultData += CompetitionHelper.milliSecToMinSecMilliSec( stageResult.getStageTime() ) + ",";
					resultData += stageResult.getRank() + ",";
					resultData += CompetitionHelper.milliSecToMinSecMilliSec( stageResult.getStageTimesBack() ) + ",";
				}
				else{
					resultData += "-1,-1,-1";
				}
			}			
			resultData += "\n";
		}
		
		return resultData;
	}

	public static String convertToHtmlChars(String text)
	{
		text = text.replaceAll("å", "&aring;").
		replaceAll("ä", "&auml;").
		replaceAll("ö", "&ouml;").
		replaceAll("Å", "&Aring;").
		replaceAll("Ä", "&Auml;").
		replaceAll("Ö", "&Ouml;");
		return text;
	}  	
	
	 public static Object deepClone(Object object) {
		   try {
		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		     ObjectOutputStream oos = new ObjectOutputStream(baos);
		     oos.writeObject(object);
		     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		     ObjectInputStream ois = new ObjectInputStream(bais);
		     return ois.readObject();
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
		 }
	
}
