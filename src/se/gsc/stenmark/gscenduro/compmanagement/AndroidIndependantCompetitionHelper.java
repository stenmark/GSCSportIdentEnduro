package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Stateless helper class that can perform various operations on a competition.
 * All methods should be static and all required information shall be provided
 * for each method call (no internal variables allowed)
 * 
 * @author Andreas
 * 
 */
public abstract class AndroidIndependantCompetitionHelper {

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
			
		Long totalTimeMilliSec = milliSec;
		Long totalTime_sec = totalTimeMilliSec/1000;
		Long toltalTime_min = totalTime_sec / 60;
		totalTimeMilliSec -=   (totalTime_sec*1000);
		double totalTimeMilliSecAsDouble = totalTimeMilliSec;
		double totalTimeTenthSecond = totalTimeMilliSecAsDouble/100;
		long totalTimeTenthSecondLong = Math.round(totalTimeTenthSecond);
		totalTime_sec -= toltalTime_min * 60;
		if( totalTimeTenthSecondLong == 10){
			return milliSecToMinSecMilliSec(milliSec+49);
		}
		return String.format("%02d:%02d.%01d", toltalTime_min, totalTime_sec, totalTimeTenthSecondLong);

	}

	public static String getResultsAsCsvString(Stages stage, List<Results> results, Competitors competitors, int type) {
		String resultData = "";
		
		if (type == 1)  {
			resultData = "Rank,Name,Card Number,Team,Start Number,Total Time,";
		} else {
			resultData = "Rank,Name,Card Number,Total Time,";
		}
			
		for (int i = 0; i < stage.size(); i++) {
			resultData += "Stage " + (i + 1) + ",Rank,Time Back,";
		}
		resultData += "\n";

		for (int index = 0; index < results.size(); index++) {
			if (type == 1)  {
				if (index == 0) {
					resultData += results.get(index).getTitle() + "\n";
				} else if (results.get(index).getTitle() != results.get(index - 1).getTitle()) {
					resultData += results.get(index).getTitle() + "\n";
				}
			}				
			
			int cardNumber = results.get(index).getStageResult().get(0).getCardNumber();					
			int rank = results.get(index).getStageResult().get(0).getRank();					
			if (rank == Competition.RANK_DNF) {			
				resultData += "-,";
			} else {
				resultData += rank + ",";
			}
			
			resultData += competitors.getByCardNumber(cardNumber).getName() + ",";
			resultData += cardNumber + ",";
			if (type == 1)  {
				resultData += competitors.getByCardNumber(cardNumber).getTeam() + ",";					
				resultData += String.valueOf(competitors.getByCardNumber(cardNumber).getStartNumber()) + ",";
			}
			resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(0).getStageTime()) + ",";	
									
			for(int stageNumber = 1; stageNumber < results.get(index).getStageResult().size(); stageNumber++) {										
				resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTime()) + ",";
				resultData += results.get(index).getStageResult().get(stageNumber).getRank() + ",";
				resultData += AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(results.get(index).getStageResult().get(stageNumber).getStageTimesBack()) + ",";
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
