package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class Competitors implements Serializable {

	private static final long serialVersionUID = 44L;
	private String errorText = "";
	private LinkedHashMap<Integer,Competitor> mCompetitors = null;
	
	public Competitors() {
		mCompetitors = new LinkedHashMap<Integer,Competitor>();
	}

	/**
	 * DONT USE THIS METHOD, slow indexed access to the internal hashmap
	 * @param index
	 * @return
	 */
	@Deprecated 
	public Competitor get(int index) {
		List<Competitor> listRepresentationOfLinkedHashMap = new ArrayList<Competitor>(mCompetitors.values());
		return listRepresentationOfLinkedHashMap.get(index);
	}			
	
	public LinkedHashMap<Integer,Competitor> getCompetitors() {
		return mCompetitors;
	}	
	
	public int size() {
		return mCompetitors.size();
	}		

	public void clear() {
		mCompetitors.clear();
	}		
	
	public void clearPunches() {
		for (Entry<Integer,Competitor> currentCompetitor : mCompetitors.entrySet()) {
			currentCompetitor.getValue().clearCard();
		}
	}	
	
	public String checkData(String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type, Boolean checkAgainstCurrent, LinkedHashMap<Integer,Competitor> competitors) {
		errorText = "";
		
		if(name.length() == 0) {
			errorText = "Incorrect name\n";
		}else if(cardNumber.length() == 0) {
			errorText = "Incorrect card number\n";			
		} else if ((type == 1) && (team.length() == 0)) { //ESS
			errorText = "Incorrect team\n";		
		} else if ((type == 1) && (competitorClass.length() == 0)) { //ESS
			errorText = "Incorrect competitor class\n";	
		} else if ((type == 1) && (startNumber.length() == 0) || (startGroup.length() == 0)) { //ESS
			errorText = "Incorrect startnumber\n";	
		} else if ((type == 1) && (startGroup.length() == 0)) { //ESS
			errorText = "Incorrect start group\n";				
		} else if (checkAgainstCurrent && checkIfNameExists(name)) {
			errorText = "Name already exists\n";		
		} else if (!cardNumber.matches("\\d+")) {
			errorText = "Card number not a number\n";
		} else if (checkAgainstCurrent && checkIfCardNumberExists(Integer.parseInt(cardNumber))) {
			errorText = "Card number already exists\n";       
		} else if ((type == 1) && (!startNumber.matches("\\d+"))) {
			errorText = "Start number not a number\n";			
		} else if (checkAgainstCurrent && (type == 1) && (checkIfStartNumberExists(Integer.parseInt(startNumber)))) {
			errorText = "Start number already exists\n";	              
		} else if ((type == 1) && (!startGroup.matches("\\d+"))) {
			errorText = "Start group not a number\n";
		} else if(competitors != null) {
			if (checkIfNameExists(name, competitors)) {
				errorText = "Name already exists\n";	
			} else if (checkIfCardNumberExists(Integer.parseInt(cardNumber), competitors)) {
				errorText = "Card number already exists\n";  
			} else if ((type == 1) && (checkIfStartNumberExists(Integer.parseInt(startNumber), competitors))) {
				errorText = "Start number already exists\n";	
			}
		}
		
		return errorText;
	}
	
	public String checkData2(String name, String cardNumber, String team, String competitorClass, int startNumber, int startGroup, int type, Boolean checkAgainstCurrent, LinkedHashMap<Integer,Competitor> competitors) {
		errorText = "";
		
		if(name.length() == 0) {
			errorText = "Incorrect name\n";
		}else if(cardNumber.length() == 0) {
			errorText = "Incorrect card number\n";			
		} else if ((type == 1) && (team.length() == 0)) { //ESS
			errorText = "Incorrect team\n";		
		} else if ((type == 1) && (competitorClass.length() == 0)) { //ESS
			errorText = "Incorrect competitor class\n";				
		} else if (checkAgainstCurrent && checkIfNameExists(name)) {
			errorText = "Name already exists\n";		
		} else if (!cardNumber.matches("\\d+")) {
			errorText = "Card number not a number\n";
		} else if (checkAgainstCurrent && checkIfCardNumberExists(Integer.parseInt(cardNumber))) {
			errorText = "Card number already exists\n";       		
		} else if (checkAgainstCurrent && (type == 1) && (checkIfStartNumberExists(startNumber))) {
			errorText = "Start number already exists\n";	              
		} else if(competitors != null) {
			if (checkIfNameExists(name, competitors)) {
				errorText = "Name already exists\n";	
			} else if (checkIfCardNumberExists(Integer.parseInt(cardNumber), competitors)) {
				errorText = "Card number already exists\n";  
			} 
			else if ((type == 1) && (checkIfStartNumberExists(startNumber, competitors))) {
				errorText = "Start number already exists\n";	
			}
		}
		
		return errorText;
	}
	
	public void add(String name, int cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type) {				
		name = name.replaceFirst("\\s+$", "");
		Competitor competitor = new Competitor(name, cardNumber, team, competitorClass, Integer.parseInt(startNumber), Integer.parseInt(startGroup));
		mCompetitors.put(cardNumber,competitor);

		sort();
	}	
	
	public void removeByName(String nameToDelete) {
		for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {
			Competitor competitor = currentCompetitorEntry.getValue();
			if (competitor.getName().equals(nameToDelete)) {
				mCompetitors.remove(currentCompetitorEntry.getKey());
				break;
			}
		}
	}	
	
	public String update(String name, int oldCardNumber, int newCardNumber, String team, String competitorClass, String startNumber, String startGroup) {
		Competitor newCompetitor = null;

		newCompetitor = mCompetitors.get(oldCardNumber);
		newCompetitor.setName(name);
		newCompetitor.setCardNumber(newCardNumber);
		newCompetitor.setTeam(team);
		newCompetitor.setCompetitorClass(competitorClass);
		newCompetitor.setStartNumber(Integer.parseInt(startNumber));
		newCompetitor.setStartGroup(Integer.parseInt(startGroup));
		mCompetitors.put(newCardNumber, newCompetitor);
		if( oldCardNumber != newCardNumber){
			mCompetitors.remove(oldCardNumber);
		}

		sort();
		
		return name + ", " + newCardNumber + " updated\n";
	}	
	
	public void updateEss(int index, String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup) {
		
	}
	
	/**
	 * Sort the competitor list
	 * A bit inefficient, need to copy the internal LinkedHashMap, sort the list and then put it back in the LinkedHashMap
	 * But the sorting is less frequent than the getByCard, so fast lookup by cardNumber is more important to have fast access.
	 */
	public void sort() {
		//Read all the values from the LinkedHashMap into a temporary list
        List<Map.Entry<Integer, Competitor>> entries = new ArrayList<Entry<Integer, Competitor>>(mCompetitors.entrySet());
        
        //Sort the temporary list
		Collections.sort(entries, new Comparator<Map.Entry<Integer, Competitor>>() {
			@Override
			public int compare(Map.Entry<Integer, Competitor> s1, Map.Entry<Integer, Competitor> s2) {
				return s1.getValue().getName().compareToIgnoreCase(s2.getValue().getName());
			}
		});
		
		//Put the sorted values of the list back in the LinkedHashMap
	    mCompetitors.clear();
	    for(Map.Entry<Integer, Competitor> e : entries) {
	    	mCompetitors.put(e.getKey(), e.getValue());
	    }
	}		
	
	public int sizeByClass(String competitorClass) {
		int count = 0;
		for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {
			Competitor competitor = currentCompetitorEntry.getValue();
			if (competitorClass.equals(competitor.getCompetitorClass())) {
				count++;
			}
		}
		return count;
	}
	
	public Competitor getByCardNumber(int cardNumber) {		
		if( mCompetitors.containsKey( cardNumber) ){
			return mCompetitors.get(cardNumber );
		}
		
		return null;
	}	
	
	public List<String> getCompetitorClasses() {		
		List<String> competitorClasses = new ArrayList<String>();		
		for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {
			Competitor competitor = currentCompetitorEntry.getValue();
			String competitorClass = competitor.getCompetitorClass();
			if (competitorClasses.contains(competitorClass)) {
				//Already in list
			} else {
				competitorClasses.add(competitor.getCompetitorClass());
			}
		}
		return competitorClasses;
	}	
			
	public String exportCsvString(int type) {
		String competitorsAsCsv = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {	
				Competitor competitor = currentCompetitorEntry.getValue();
				if (type == 1) { //ESS_TYPE
					competitorsAsCsv += competitor.getName() + "," + 
									  	competitor.getCardNumber() + "," + 
									  	competitor.getTeam() + "," + 
									  	competitor.getCompetitorClass() + "," + 
									  	competitor.getStartNumber() + "," + 
									  	competitor.getStartGroup() + "\n";					
				} else {
					competitorsAsCsv += competitor.getName() + "," + competitor.getCardNumber() + "\n";
				}
			}
		}

		return competitorsAsCsv;
	}	
	
	public Competitor findByCard(Card cardToMatch) {
		return getByCardNumber(cardToMatch.getCardNumber());
	}
		
	public String exportPunchesCsvString() {
		String punchesAsCsv = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {	
				Competitor competitor = currentCompetitorEntry.getValue();
				
				Card card = new Card();

				card = competitor.getCard();

				if (card != null) {
					Collections.sort(card.getPunches(),
						new Comparator<Punch>() {
							@Override
							public int compare(Punch s1, Punch s2) {
								return Long.valueOf(s1.getTime()).compareTo(s2.getTime());
							}
						});

					punchesAsCsv += competitor.getCardNumber() + ",";
					int i = 0;					
					for (Punch punch : card.getPunches()) {
						if (i != 0) {
							punchesAsCsv += ",";	
						}
						punchesAsCsv += punch.getControl() + "," + punch.getTime();
						i++;
					}
					punchesAsCsv += "\n";
				}
			}
		}
		return punchesAsCsv;
	}	
	
	public Boolean checkIfNameExists(String name, LinkedHashMap<Integer,Competitor> competitors) {
		name = name.replaceFirst("\\s+$", "");
		
		for (Entry<Integer,Competitor> currentCompetitorEntry : competitors.entrySet()) {
			if (name.equalsIgnoreCase(currentCompetitorEntry.getValue().getName())) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean checkIfNameExists(String name) {
		return checkIfNameExists(name, mCompetitors);
	}		
	
	public Boolean checkIfCardNumberExists(int cardNumber, LinkedHashMap<Integer,Competitor> competitors) {
		return competitors.containsKey(cardNumber);
	}	
	
	public Boolean checkIfCardNumberExists(int cardNumber) {
		return mCompetitors.containsKey(cardNumber);
	}
	
	public Boolean checkIfStartNumberExists(int startNumber, LinkedHashMap<Integer,Competitor> competitors) {

		for( int cardNumber : competitors.keySet()) {
			if (startNumber == competitors.get(cardNumber).getStartNumber()) {
				return true;
			}
		}
		return false;
	}	
	
	public Boolean checkIfStartNumberExists(int startNumber) {
		return checkIfStartNumberExists(startNumber, mCompetitors);
	}	

	public String importCompetitors(String newCompetitors, Boolean keep, int type, boolean onlyCheckDontAdd) throws NumberFormatException, IOException {	
		String errorMessage = "";
		String name = "";
		String cardNumberAsString = "";
		String team = "";
		String competitorClass = "";
		String startNumberAsString = "-1";
		String startGroupAsString = "-1";
		String line = null;
		BufferedReader bufReader = new BufferedReader(new StringReader(newCompetitors));
		
		if (!keep) {
			mCompetitors.clear();
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
						errorMessage += "Could not import " + line + ". Wrong format. Expected \"name,cardNumber,Team,CompetitorClass,StartNumber,StartGroup\". Found " + parsedLine.length + " Comma(,) signs. Expected just 5\n";
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
						errorMessage += "Could not import " + line + ". Wrong format. Expected \"name,cardNumber\". Found " + parsedLine.length + " Comma(,) signs. Expected just 1\n";
					}
					continue;
				}
			}	
			
			//Remove all none digits
			cardNumberAsString = cardNumberAsString.replaceAll("[^\\d]", ""); 
			startNumberAsString = startNumberAsString.replaceAll("[^\\d]", ""); 
			startGroupAsString = startGroupAsString.replaceAll("[^\\d]", "");
			int cardNumber = -1;
			int startNumber = -1;
			int startGroup = -1;
			try{
				cardNumber = Integer.parseInt(cardNumberAsString);
			}
			catch( NumberFormatException e){
				errorMessage += "Could not import " + line + ". Could not interpret cardNumber" + cardNumberAsString + "\n ";
				parsingError = true;
			}
			
			try{
				startNumber = Integer.parseInt(startNumberAsString);
			}
			catch( NumberFormatException e){
				errorMessage += "Could not import " + line + ". Could not interpret startNumber" + startNumberAsString + "\n ";
				parsingError = true;
			}
			
			try{
				startGroup = Integer.parseInt(startGroupAsString);
			}
			catch( NumberFormatException e){
				errorMessage += "Could not import " + line + ". Could not interpret startGroup" + startGroupAsString + "\n ";
				parsingError = true;
			}
			
			if( !parsingError ){
				String checkDataResp = checkData2(name, cardNumberAsString, team, competitorClass, startNumber, startGroup, type, keep, getCompetitors());
				errorMessage += checkDataResp;
				if( checkDataResp.isEmpty()){
					if( !onlyCheckDontAdd){
						add(name, cardNumber, team, competitorClass, startNumberAsString,startGroupAsString , type);
					}
				}	
			}
		}
		return errorMessage;
	}	
	
	public void importPunches(String punches, Stages stages, int type) throws IOException {		
		BufferedReader bufReader = new BufferedReader(new StringReader(punches));
		String line = null;
		while ((line = bufReader.readLine()) != null) {	
			Card cardObject = new Card();
			
			int start = 0;
			int end = 0;
	
			end = line.indexOf(",", start);
			String cardNumber = line.substring(start, end);
			start = end + 1;
	
			Competitor competiorMatchingNewCardnumber = getByCardNumber(Integer.parseInt(cardNumber));
			if (competiorMatchingNewCardnumber != null) {
				cardObject.setCardNumber(competiorMatchingNewCardnumber.getCardNumber());
				while (start < line.length()) {
					end = line.indexOf(",", start);
					String control = line.substring(start, end);
					start = end + 1;								
					
					end = line.indexOf(",", start);
					String time;
					if (end == -1) {
						time = line.substring(start, line.length());
						start = line.length() + 1;
					} else {											
						time = line.substring(start, end);
						start = end + 1;
					}				
					
					Punch punchObject = new Punch(Long.valueOf(time), Integer.valueOf(control));
					cardObject.getPunches().add(punchObject);
				}
				
				competiorMatchingNewCardnumber.processCard(cardObject, stages, type);					
			}			
		}
	}	
}
