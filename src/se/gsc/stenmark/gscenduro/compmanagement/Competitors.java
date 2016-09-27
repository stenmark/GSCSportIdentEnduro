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
	private String mErrorText = "";
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
		mErrorText = "";
		
		if(name.length() == 0) {
			mErrorText = "Incorrect name\n";
		}else if(cardNumber.length() == 0) {
			mErrorText = "Incorrect card number\n";			
		} else if ((type == 1) && (team.length() == 0)) { //ESS
			mErrorText = "Incorrect team\n";		
		} else if ((type == 1) && (competitorClass.length() == 0)) { //ESS
			mErrorText = "Incorrect competitor class\n";	
		} else if ((type == 1) && (startNumber.length() == 0) || (startGroup.length() == 0)) { //ESS
			mErrorText = "Incorrect startnumber\n";	
		} else if ((type == 1) && (startGroup.length() == 0)) { //ESS
			mErrorText = "Incorrect start group\n";				
		} else if (checkAgainstCurrent && checkIfNameExists(name)) {
			mErrorText = "Name already exists\n";		
		} else if (!cardNumber.matches("\\d+")) {
			mErrorText = "Card number not a number\n";
		} else if (checkAgainstCurrent && checkIfCardNumberExists(Integer.parseInt(cardNumber))) {
			mErrorText = "Card number already exists\n";       
		} else if ((type == 1) && (!startNumber.matches("\\d+"))) {
			mErrorText = "Start number not a number\n";			
		} else if (checkAgainstCurrent && (type == 1) && (checkIfStartNumberExists(Integer.parseInt(startNumber)))) {
			mErrorText = "Start number already exists\n";	              
		} else if ((type == 1) && (!startGroup.matches("\\d+"))) {
			mErrorText = "Start group not a number\n";
		} else if(competitors != null) {
			if (checkIfNameExists(name, competitors)) {
				mErrorText = "Name already exists\n";	
			} else if (checkIfCardNumberExists(Integer.parseInt(cardNumber), competitors)) {
				mErrorText = "Card number already exists\n";  
			} else if ((type == 1) && (checkIfStartNumberExists(Integer.parseInt(startNumber), competitors))) {
				mErrorText = "Start number already exists\n";	
			}
		}
		
		return mErrorText;
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

		for (int i = 0; i < competitors.size(); i++) {
			if (startNumber == competitors.get(i).getStartNumber()) {
				return true;
			}
		}
		return false;
	}	
	
	public Boolean checkIfStartNumberExists(int startNumber) {
		return checkIfStartNumberExists(startNumber, mCompetitors);
	}	
	
	public String checkImportCompetitors(String competitorsInput, Boolean checkAgainstCurrent, int type) {	
		LinkedHashMap<Integer,Competitor> newCompetitors = new LinkedHashMap<Integer,Competitor>();
		
		String status = "";
		String name = "";
		String cardNumber = "";
		String team = "";
		String competitorClass = "";
		String startNumber = "";
		String startGroup = "";
		int start = 0;
		int end = 0;
		String line = null;
		BufferedReader bufReader = new BufferedReader(new StringReader(competitorsInput));
				
		//todo kolla så inte kortnumret mm redan finns bland de som håller på att läggas till, just nu kollas bara mot de som redan finns.
		//keep
		
		try {
			while ((line = bufReader.readLine()) != null) {					
				int number = 0;			
				
				for (int i = 0, len = line.length(); i < len; ++i) {
					Character c = line.charAt(i);
					if (c == ',') {
						number++;
					}
				}
				
				if (type == 1) { //ESS_TYPE
					/*
					name,cardNumber,team,competitorClass,startNumber,startGroup
					name,cardNumber,team,competitorClass,startNumber,startGroup
					name,cardNumber,team,competitorClass,startNumber,startGroup
					*/
					
					if (number != 5) {
						//import data error
						if (line.length() != 0) {
							status += "Can not parse line = " + line + "\n";
						}
					} else {
						start = 0;
						end = line.indexOf(",", start);
						name = line.substring(start, end);

						start = end + 1;
						end = line.indexOf(",", start);
						cardNumber = line.substring(start, end);

						start = end + 1;
						end = line.indexOf(",", start);
						team = line.substring(start, end);

						start = end + 1;
						end = line.indexOf(",", start);
						competitorClass = line.substring(start, end);

						start = end + 1;
						end = line.indexOf(",", start);
						startNumber = line.substring(start, end);				

						start = end + 1;
						end = line.indexOf(",", start);
						startGroup = line.substring(start, line.length());
						
						String errorText = checkData(name, cardNumber.replaceAll("[^\\d]", ""), team, competitorClass, startNumber.replaceAll("[^\\d]", ""), startGroup.replaceAll("[^\\d]", ""), 1, checkAgainstCurrent, newCompetitors);
						
						if (errorText.length() != 0) {
							status += name + ", " + cardNumber + ", " + team + ", " + competitorClass + ", " + startNumber + ", " + startGroup + ". " + errorText;
						} else {
							Competitor competitor = new Competitor(name, Integer.parseInt(cardNumber.replaceAll("[^\\d]", "")), team, competitorClass, Integer.parseInt(startNumber.replaceAll("[^\\d]", "")), Integer.parseInt(startGroup.replaceAll("[^\\d]", "")));
							newCompetitors.put(Integer.parseInt(cardNumber),competitor);
						}
					}	
				} else {				
					/*
					name,cardNumber
					name,cardNumber
					name,cardNumber
					*/

					if (number != 1) {
						//import data error
						if (line.length() != 0) {
							status += "Can not parse line = " + line + "\n";
						}
					} else {
						int pos = line.indexOf(",", 0);
						name = line.substring(0, pos);
						cardNumber = line.substring(pos + 1, line.length());

						String errorText = checkData(name, cardNumber.replaceAll("[^\\d]", ""), "", "", "-1", "-1", 0, checkAgainstCurrent, newCompetitors);

						if (errorText.length() != 0) {
							status += name + ", " + cardNumber + ". " + errorText;
						} else {
							Competitor competitor = new Competitor(name, Integer.parseInt(cardNumber.replaceAll("[^\\d]", "")));
							newCompetitors.put(Integer.parseInt(cardNumber),competitor);
						}
					}			
				}					
			}
		} catch (IOException e) {
			status += "Can not parse competitors";
		}
		return status;
	}		
	
	public void importCompetitors(String newCompetitors, Boolean keep, int type) throws NumberFormatException, IOException {	
		String name = "";
		String cardNumber = "";
		String team = "";
		String competitorClass = "";
		String startNumber = "";
		String startGroup = "";
		int start = 0;
		int end = 0;
		String line = null;
		BufferedReader bufReader = new BufferedReader(new StringReader(newCompetitors));
		
		if (!keep) {
			mCompetitors.clear();
		}
		
		while ((line = bufReader.readLine()) != null) {					
			if (type == 1) { //ESS_TYPE
				/*
				name,cardNumber,team,competitorClass,startNumber,startGroup
				name,cardNumber,team,competitorClass,startNumber,startGroup
				name,cardNumber,team,competitorClass,startNumber,startGroup
				*/
				
				start = 0;
				end = line.indexOf(",", start);
				name = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				cardNumber = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				team = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				competitorClass = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				startNumber = line.substring(start, end);				

				start = end + 1;
				end = line.indexOf(",", start);
				startGroup = line.substring(start, line.length());
				cardNumber = cardNumber.replaceAll("[^\\d]", "");
				
				add(name, Integer.parseInt(cardNumber), team, competitorClass, startNumber.replaceAll("[^\\d]", ""), startGroup.replaceAll("[^\\d]", ""), 1);				
			} else {				
				/*
				name,cardNumber
				name,cardNumber
				name,cardNumber
				*/
	
				int pos = line.indexOf(",", 0);
				name = line.substring(0, pos);
				cardNumber = line.substring(pos + 1, line.length());
				
				cardNumber = cardNumber.replaceAll("[^\\d]", "");
				add(name, Integer.parseInt(cardNumber), "", "", "-1", "-1", 0);					
			}					
		}
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
