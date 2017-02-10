package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.gsc.stenmark.gscenduro.SporIdent.Card;


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
	
	public static Competition importCompetition( String importString, StringBuilder errorText) throws IOException{
		Boolean nameAdded = false;
		Boolean dateAdded = false;
		Boolean typeAdded = false;
		Boolean stageAdded = false;
		Boolean competitorsAdded = false;
		Boolean punchesAdded = false;
		int type = 0;
		String importType = "";
		String importData = "";
		BufferedReader bufReader = new BufferedReader(new StringReader(importString));
		String line = null;
		while ((line = bufReader.readLine()) != null) {

			if (line.equals("[/Name]")) {
				importType = "";
				if (importData.length() > 0) {
					nameAdded = true;
				}
			} else if (line.equals("[/Date]")) {
				importType = "";
				if (importData.length() > 0) {
					dateAdded = true;
				}
			} else if (line.equals("[/Type]")) {
				importType = "";
				if (importData.length() > 0) {
					if (importData.matches("\\d+")) {
						type = Integer.parseInt(importData);
						if ((type == 0) || (type == 1)) {
							typeAdded = true;
						}
					}
				}
			} else if (line.equals("[/Stages]")) {
				importType = "";
				errorText.append( CompetitionHelper.checkStagesData(importData, type));
				stageAdded = true;
			} else if (line.equals("[/Competitors]")) {
				importType = "";
				errorText.append( importCompetitors(importData, false, type, true, new Competition() ));
				competitorsAdded = true;
			} else if (line.equals("[/Punches]")) {
				importType = "";
				punchesAdded = true;
			} else if (importType.length() > 0) {
				importData += line;
				if ((importType.equals("[Competitors]")) || (importType.equals("[Punches]"))) {
					importData += "\n";
				}
			} else if ((line.equals("[Name]"))
					|| (line.equals("[Date]"))
					|| (line.equals("[Type]"))
					|| (line.equals("[Stages]"))
					|| (line.equals("[Competitors]"))
					|| (line.equals("[Punches]"))) {
				importType = line;
				importData = "";
			}
		}

		if (!nameAdded) {
			errorText.append( "No name\n" );
		}

		if (!dateAdded) {
			errorText.append( "No date\n" );
		}

		if (!typeAdded) {
			errorText.append( "No type\n" );
		}

		if (!stageAdded) {
			errorText.append( "No stage\n" );
		}
		
		if (!competitorsAdded && punchesAdded) {
			errorText.append( "Can't add punches without adding competitors\n" );
		}
		
		if ( errorText.length() != 0 ) {
			return new Competition();
		}

		importType = "";
		importData = "";
		Competition competition = new Competition();

		bufReader = new BufferedReader(new StringReader(importString));
		line = null;
		while ((line = bufReader.readLine()) != null) {

			if (line.equals("[/Name]")) {
				importType = "";
				competition.setCompetitionName(importData);
			} else if (line.equals("[/Date]")) {
				importType = "";
				competition.setCompetitionDate(importData);
			} else if (line.equals("[/Type]")) {
				importType = "";
				competition.setCompetitionType(Integer.parseInt(importData));
			} else if (line.equals("[/Stages]")) {
				importType = "";
				competition.importStages(importData);
			} else if (line.equals("[/Competitors]")) {
				importType = "";
				CompetitionHelper.importCompetitors(importData, false, competition.getCompetitionType(),false, competition);
			} else if (line.equals("[/Punches]")) {
				importType = "";
				competition.getCompetitors().importPunches(importData, competition.getStageDefinition(), competition.getCompetitionType());
			} else if (importType.length() > 0) {
				importData += line;
				if ((importType.equals("[Competitors]")) || (importType.equals("[Punches]"))) {
					importData += "\n";
				}
			} else if ((line.equals("[Name]"))
					|| (line.equals("[Date]"))
					|| (line.equals("[Type]"))
					|| (line.equals("[Stages]"))
					|| (line.equals("[Competitors]"))
					|| (line.equals("[Punches]"))) {
				importType = line;
				importData = "";
			}
		}

		competition.calculateResults();	
		return competition;
	}
	
	public static String importPunches(String punchString, Competition competition) throws IOException{
		 LinkedHashMap<Integer, Competitor> competitors = competition.getCompetitors().getCompetitors();
		 List<Stage> stages = competition.getStageDefinition();
		
		PunchParser punchParser = new PunchParser();
		punchParser.parsePunches(punchString, competitors, stages);

		String statusMsg = punchParser.getStatus();
		statusMsg += "Processing cards:\n";
		for (Card cardObject : punchParser.getCards()) {		
			if (cardObject.getPunches().size() > 0) {
				if (cardObject.getCardNumber() != 0) {
					statusMsg += competition.processNewCard(cardObject, false);
				}
			}
		}
		return statusMsg;
	}
	
	public static String importCompetitors(String newCompetitors, Boolean keep, int type, boolean onlyCheckDontAdd, Competition competition) throws NumberFormatException, IOException {	
		StringBuffer errorMessage = new StringBuffer("");
		String name = "";
		String cardNumberAsString = "";
		String team = "";
		String competitorClass = "";
		String startNumberAsString = "-1";
		String startGroupAsString = "-1";
		String line = null;
		BufferedReader bufReader = new BufferedReader(new StringReader(newCompetitors));
		
		if (!keep) {
			competition.getCompetitors().clear();
		}
		
		while ((line = bufReader.readLine()) != null) {	
			boolean parsingError = false;
			String[] parsedLine = line.split(",");
			if (type == Competition.ESS_TYPE) { 
				if( parsedLine.length == 6){
					name = parsedLine[0];
					cardNumberAsString = parsedLine[1];
					team = parsedLine[2];
					competitorClass = parsedLine[3];
					startNumberAsString = parsedLine[4];			
					startGroupAsString = parsedLine[5];
				}
				else{
					//Ignore empty lines (Dont print error message)
					if( !(line.replace(" ", "").isEmpty())){
						errorMessage.append( "Could not import " + line + ". Wrong format. Expected \"name,cardNumber,Team,CompetitorClass,StartNumber,StartGroup\". Found " + (parsedLine.length-1) + " Comma(,) signs. Expected 5\n");
					}
					continue;
				}
			} else {	
				if( parsedLine.length == 2){
					name = parsedLine[0];
					cardNumberAsString = parsedLine[1];
				}
				else{
					//Ignore empty lines (Dont print error message)
					if( !(line.replace(" ", "").isEmpty())){
						errorMessage.append( "Could not import " + line + ". Wrong format. Expected \"name,cardNumber\". Found " + (parsedLine.length-1) + " Comma(,) signs. Expected 1\n" );
					}
					continue;
				}
			}	
			
			Integer cardNumber = -1;
			Integer startNumber = -1;
			Integer startGroup = -1;
			Map<String,Integer> parsingResults = new HashMap<String, Integer>();
			parsingError = parseCompetitor(line,name, team, competitorClass, cardNumberAsString, startNumberAsString, startGroupAsString, type, keep,competition.getCompetitors(),errorMessage, parsingResults);
			cardNumber = parsingResults.get("cardNumber");
			startNumber = parsingResults.get("startNumber");
			startGroup = parsingResults.get("startGroup");
			
			if( !parsingError ){
				if( !onlyCheckDontAdd){
					competition.addCompetitor(name, cardNumber, team, competitorClass, startNumber,startGroup , type);
				}
			}
		}
		return errorMessage.toString();
	}	
	
	public static boolean parseCompetitor( String lineToParse, 
									String name,
									String team,
									String competitorClass,
									String cardNumberAsString, 
									String startNumberAsString, 
									String startGroupAsString, 
									int type,
									boolean checkCurrentCompetitors,
									Competitors competitors,
									StringBuffer errorMessage, 
									Map<String,Integer> results){
		boolean parsingError = false;
		 int cardNumber = -1;
		 int startNumber = -1;
		 int startGroup = -1;
		
		//Remove all none digits
		cardNumberAsString = cardNumberAsString.replaceAll("[^\\d]", ""); 
		startNumberAsString = startNumberAsString.replaceAll("[^\\d]", ""); 
		startGroupAsString = startGroupAsString.replaceAll("[^\\d]", "");

		try{
			cardNumber = Integer.parseInt(cardNumberAsString);
		}
		catch( NumberFormatException e){
			errorMessage.append( "Could not import " + lineToParse + ". Could not interpret cardNumber" + cardNumberAsString + "\n");
			parsingError = true;
		}
		
		try{
			startNumber = Integer.parseInt(startNumberAsString);
		}
		catch( NumberFormatException e){
			errorMessage.append( "Could not import " + lineToParse + ". Could not interpret startNumber" + startNumberAsString + "\n");
			parsingError = true;
		}
		
		try{
			startGroup = Integer.parseInt(startGroupAsString);
		}
		catch( NumberFormatException e){
			errorMessage.append( "Could not import " + lineToParse + ". Could not interpret startGroup" + startGroupAsString + "\n");
			parsingError = true;
		}
		
		results.clear();
		results.put("cardNumber", cardNumber);
		results.put("startNumber", startNumber);
		results.put("startGroup", startGroup);
		
		String checkDataResp = checkData(name, cardNumber, team, competitorClass, startNumber, startGroup, type, checkCurrentCompetitors, competitors);
		if( !checkDataResp.isEmpty()){
			parsingError = true;
			errorMessage.append(checkDataResp);
		}
		
		return parsingError;
	}
	
	private static String checkData(String name, int cardNumber, String team, String competitorClass, int startNumber, int startGroup, int type, Boolean checkAgainstCurrent, Competitors competitors) {
		String errorText = "";

		if(name.isEmpty()) {
			errorText += "Could not import competeitor with card number " + cardNumber + " the name is empty\n";				
		} else if (checkAgainstCurrent && competitors.checkIfCardNumberExists(cardNumber)) {
			errorText += "Could not import competitor " + name + " Card number " + cardNumber + " already exists\n";              
		} else if(competitors != null) {
			if (competitors.checkIfCardNumberExists(cardNumber, competitors.getCompetitors() )) {
				errorText += "Could not import competitor " + name + " Card number " + cardNumber + " already exists\n";   
			} 
		}
		
		if( type == Competition.ESS_TYPE){
			if( competitorClass.isEmpty() ){
				errorText += "Incorrect competitor class\n";		
			}
			if( team.isEmpty()){
				errorText += "Incorrect team\n";
			}
			if(competitors.checkIfStartNumberExists(startNumber)){
				errorText += "Start number already exists\n";	 
			}
			if (competitors.getCompetitors() != null) {
				if( competitors.checkIfStartNumberExists(startNumber, competitors.getCompetitors()) ){
					errorText += "Start number already exists\n";	
				}
			}
		}
		
		return errorText;
	}
		
	public static String exportStagesCsvString( List<Stage> stages) {
		String exportString = "";
		for (Stage stage : stages) {
			exportString += "," + stage.start + "," + stage.finish;
		}
		exportString = exportString.replaceFirst(",", "");
		return exportString;
	}	
	
	public static boolean stringContainsItemFromList(String inputString, ArrayList<String> items) {
	    for(int i = 0; i < items.size(); i++) {
	        if(inputString.equals(items.get(i))) {
	            return true;
	        }
	    }
	    return false;
	}	
	
	public static boolean validStageControl(List<Stage> stages, int control) {
		for (Stage stage : stages) {
			if (control == stage.start) {
				return true;
			}			
			
			if (control == stage.finish) {
				return true;
			}			
		}
			
		return false;
	}
	
	public static String checkStagesData(String stages, int type) {
		String[] controlsList = stages.split(",");				
		if ((controlsList.length % 2) == 0) {	//Is even
			//Check that all controls are digits only
			for (String controlAsString : controlsList) {
				if (!controlAsString.matches("\\d+")) {
					return "Controls are not digits only\n";
				}
			}

			ArrayList<String> checkedControlsList = new ArrayList<String>();		
			checkedControlsList.add(controlsList[0]);
			
			if (type == 1) {
				//Check that all controls are unique 
				for (int i = 1; i < controlsList.length; i++) {
					if (CompetitionHelper.stringContainsItemFromList(controlsList[i], checkedControlsList)) {
						return "Not all controls are unique\n";
					}				
					checkedControlsList.add(controlsList[i]);
				}
			} else {
				int startControl = Integer.parseInt(controlsList[0]);
				int finishControl = Integer.parseInt(controlsList[1]);
				for (int i = 2; i < controlsList.length; i += 2) {					
					if (startControl != Integer.parseInt(controlsList[i])) {
						return "More than two controls\n";		
					}
					
					if (finishControl != Integer.parseInt(controlsList[i + 1])) {
						return "More than two controls\n";		
					}					
				}
				
			}
			return "";			
		} else {	//Is odd
			return "Not an even number of controls\n";
		}		
	}

	public static String getResultsAsHtmlString(String name, String date, Map<String, ArrayList<Stage>> stages, Competitors competitors, int type, Competition competition) {
		return "Not done yet!";
	}
	
	public static String getCompetitionAsString( Competition competition){
		String competitionList = "";
		// Competition Name
		competitionList += "[Name]\n";
		competitionList += competition.getCompetitionName() + "\n";
		competitionList += "[/Name]\n";
		
		// Competition Date
		competitionList += "[Date]\n";
		competitionList += competition.getCompetitionDate() + "\n";
		competitionList += "[/Date]\n";

		// Competition Type
		competitionList += "[Type]\n";
		competitionList += competition.getCompetitionType() + "\n";
		competitionList += "[/Type]\n";

		// Stages
		competitionList += "[Stages]\n";
		competitionList += CompetitionHelper.exportStagesCsvString(competition.getStageDefinition()) + "\n";
		competitionList += "[/Stages]\n";
		
		// Competitors
		competitionList += "[Competitors]\n";
		competitionList += competition.getCompetitors().exportCsvString(competition.getCompetitionType());
		competitionList += "[/Competitors]\n";

		// Punches
		competitionList += "[Punches]\n";
		competitionList += competition.getCompetitors().exportPunchesCsvString();
		competitionList += "[/Punches]\n";	
		
		return competitionList;
	}
	
	public static String getResultsAsCsvString(Map<String, ArrayList<Stage>> stagesForAllClasses,  Map<String, Stage>  totalResultsForAllClasses, Competitors competitors, int type) {
		String resultData = "";
		
		if (type == Competition.ESS_TYPE)  {
			resultData = "Rank,Name,Card Number,Team,Start Number,Total Time,";
		} else {
			resultData = "Rank,Name,Card Number,Total Time,";
		}
		if( stagesForAllClasses.isEmpty() ){	
			resultData += "NO STAGES FOUND!,Rank,Time Back,";
		}
		else{
			if( stagesForAllClasses.keySet().isEmpty() ){	
				resultData += "Program failure, class defintion is null";
			}
			else{
				Entry<String, ArrayList<Stage>> firstEntry = stagesForAllClasses.entrySet().iterator().next();
				for (int stageNumber = 1;stageNumber <= stagesForAllClasses.get( firstEntry.getKey()).size(); stageNumber++) {
					resultData += "Stage " + stageNumber + ",Rank,Time Back,";
				}
			}
		}
		resultData += "\n";
		if( type == Competition.ESS_TYPE){
			resultData += "\n";
		}
		
		for( String compClass : stagesForAllClasses.keySet() ){
			List<Stage> stages = stagesForAllClasses.get(compClass);
			Stage totalResults = totalResultsForAllClasses.get(compClass);
			if( totalResults == null || stages == null){
				resultData += "Inconsistant class definitions for class " +compClass +"\n";
				continue;
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
							
				for(Stage stage : stages) {	
					StageResult stageResult = stage.getStageResultByCardnumber(cardNumber);
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
		}
		
		return resultData;
	}

	public static String stageStatusAsString( List<Stage> stages){
		String stagesAsString = "";
		if (!stages.isEmpty() && stages != null) {
			int i = 0;
			for (Stage stageControls : stages) {
				i++;
				if (i != 1) {
					stagesAsString += "\n";
				}
				stagesAsString += "Stage " + i + ": " + stageControls.start + "->" + stageControls.finish;
			}
		}
		else{
			stagesAsString += " No stages loaded";
		}
		
		return stagesAsString;
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
