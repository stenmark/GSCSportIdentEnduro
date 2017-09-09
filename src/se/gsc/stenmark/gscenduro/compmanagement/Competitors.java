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

	private static final long serialVersionUID = 7L;
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
		

	
	public void add( int cardNumber, Competitor competitor) {				
		mCompetitors.put(cardNumber,competitor);
		sort();
	}	
		
	public void removeByCardNumber(Integer cardNumberToDelete) {
		mCompetitors.remove(cardNumberToDelete);
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
				
	public String exportCsvString(int type) {
		String competitorsAsCsv = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.entrySet() ) {	
				Competitor competitor = currentCompetitorEntry.getValue();
				if (type == Competition.ESS_TYPE) { 
					competitorsAsCsv += competitor.getName() + "," + 
									  	competitor.getCardNumber() + "," + 
									  	competitor.getTeam() + "," + 
									  	competitor.getCompetitorClass() + "," + 
									  	competitor.getStartNumber() + "," + 
									  	competitor.getStartGroup() + "\n";					
				} else {
					if( competitor.getCompetitorClass().equalsIgnoreCase("dam")){
						competitorsAsCsv += competitor.getName() + "," + competitor.getCardNumber() + ",dam\n";
					}
					else{
						competitorsAsCsv += competitor.getName() + "," + competitor.getCardNumber() + "\n";
					}
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
					punchesAsCsv += card.getPunchesAsString();
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

	
	public void importPunches(String punches, List<Stage> stages, int type) throws IOException {		
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
				
				cardObject.setCardAsRead();
				competiorMatchingNewCardnumber.processCard(cardObject, stages, type);					
			}			
		}
	}	
}
