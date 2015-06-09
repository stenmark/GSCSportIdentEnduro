package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class Competitors implements Serializable {

	private static final long serialVersionUID = 44L;
	private String mErrorText = "";
	private ArrayList<Competitor> mCompetitors = null;
	
	public Competitors() {
		mCompetitors = new ArrayList<Competitor>();
	}

	public Competitor get(int index) {
		return mCompetitors.get(index);
	}			
	
	public ArrayList<Competitor> getCompetitors() {
		return mCompetitors;
	}	
	
	public int size() {
		return mCompetitors.size();
	}		

	public void clear() {
		mCompetitors.clear();
	}		
	
	public void clearPunches() {
		for (int i = 0; i < mCompetitors.size(); i++) {
			mCompetitors.get(i).setCard(null);
			mCompetitors.get(i).setStageTimes(null);
		}
	}	
	
	public Boolean checkData(String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type) {
		Boolean error = false;
		mErrorText = "";
		
		if((name.length() == 0) || (cardNumber.length() == 0)) {
			error = true;
			mErrorText = "All data must be entered\n";		
		} else if ((type == 1) &&	//ESS
	     		 ((team.length() == 0) || (competitorClass.length() == 0) || (startNumber.length() == 0) || (startGroup.length() == 0))) {
			error = true;
			mErrorText = "All data must be entered\n";			
		} else if (checkIfNameExists(name)) {
			error = true;
			mErrorText = "Name already exists\n";		
		} else if (!cardNumber.matches("\\d+")) {
			error = true;
			mErrorText = "Card number not a number\n";
		} else if (checkIfCardNumberExists(Integer.parseInt(cardNumber))) {
			error = true;
			mErrorText = "Card number already exists\n";       
		} else if ((type == 1) && (!startNumber.matches("\\d+"))) {
			error = true;
			mErrorText = "Start number not a number\n";			
		} else if ((type == 1) && (checkIfStartNumberExists(Integer.parseInt(startNumber)))) {
			error = true;
			mErrorText = "Start number already exists\n";	              
		} else if ((type == 1) && (!startGroup.matches("\\d+"))) {
			error = true;
			mErrorText = "Start group not a number\n";
		}
		
		return error;
	}
	
	public String add(String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type) {				
		name = name.replaceFirst("\\s+$", "");
		
		if (checkData(name, cardNumber, team, competitorClass, startNumber, startGroup, type)) {
			return mErrorText;
		} else {							
			Competitor competitor = new Competitor(name, Integer.parseInt(cardNumber), team, competitorClass, Integer.parseInt(startNumber), Integer.parseInt(startGroup));
			mCompetitors.add(competitor);

			sort();
			return name + ", " + cardNumber + " added\n";
		}
	}	
	
	public void removeByName(String nameToDelete) {
		for (Competitor competitor : mCompetitors) {
			if (competitor.getName().equals(nameToDelete)) {
				mCompetitors.remove(competitor);
				break;
			}
		}
	}	
	
	public String update(int index, String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup) {
		if (checkData(name, cardNumber, team, competitorClass, startNumber, startGroup, 1)) {
			return mErrorText;
		} else {			
			Competitor newCompetitor = null;
			cardNumber = cardNumber.replace(" ", "");
	
			newCompetitor = mCompetitors.get(index);
			newCompetitor.setName(name);
			newCompetitor.setCardNumber(Integer.parseInt(cardNumber));
			newCompetitor.setTeam(team);
			newCompetitor.setCompetitorClass(competitorClass);
			newCompetitor.setStartNumber(Integer.parseInt(startNumber));
			newCompetitor.setStartGroup(Integer.parseInt(startGroup));
			mCompetitors.set(index, newCompetitor);		
		}			

		sort();
		
		return name + ", " + cardNumber + " updated\n";
	}	
	
	public void updateEss(int index, String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup) {
		
	}
	
	public void sort() {
		Collections.sort(mCompetitors, new Comparator<Competitor>() {
			@Override
			public int compare(Competitor s1, Competitor s2) {
				return s1.getName().compareToIgnoreCase(s2.getName());
			}
		});
	}		
	
	public int sizeByClass(String competitorClass) {
		int count = 0;
		for (Competitor competitor : mCompetitors) {
			if (competitorClass.contains(competitor.getCompetitorClass())) {
				count++;
			}
		}
		return count;
	}
	
	public Competitor getByCardNumber(int cardNumber) {		
		for (Competitor competitor : mCompetitors) {
			if (competitor.getCardNumber() == cardNumber) {
				return competitor;
			}
		}
		
		return null;
	}	
	
	public List<String> getCompetitorClasses() {		
		List<String> competitorClasses = new ArrayList<String>();		
		for (Competitor competitor : mCompetitors) {
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
		String competitorsAsCvs = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			Collections.sort(mCompetitors);
			for (Competitor competitor : mCompetitors) {			
				if (type == 1) { //ESS_TYPE
					competitorsAsCvs += competitor.getName() + "," + 
									  competitor.getCardNumber() + "," + 
									  competitor.getTeam() + "," + 
									  competitor.getCompetitorClass() + "," + 
									  competitor.getStartNumber() + "," + 
									  competitor.getStartGroup() + "\n";					
				} else {
					competitorsAsCvs += competitor.getName() + "," + competitor.getCardNumber() + "\n";
				}
			}
		}

		return competitorsAsCvs;
	}	
	
	public Competitor findByCard(Card cardToMatch) {
		for (Competitor competitor : mCompetitors) {
			if (competitor.getCardNumber() == cardToMatch.getCardNumber()) {
				return competitor;
			}
		}
		return null;
	}
		
	public String exportPunchesCvsString() {
		String punchesAsCvs = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			Collections.sort(mCompetitors);
			for (Competitor competitor : mCompetitors) {
				Card card = new Card();

				card = competitor.getCard();

				if (card != null) {
					Collections.sort(card.getPunches(),
						new Comparator<Punch>() {
							@Override
							public int compare(Punch s1, Punch s2) {
								return new Long(s1.getTime()).compareTo(s2.getTime());
							}
						});

					punchesAsCvs += competitor.getCardNumber() + ",";
					int i = 0;					
					for (Punch punch : card.getPunches()) {
						if (i != 0) {
							punchesAsCvs += ",";	
						}
						punchesAsCvs += punch.getControl() + "," + punch.getTime();
						i++;
					}
					punchesAsCvs += "\n";
				}
			}
		}
		return punchesAsCvs;
	}	
	
	public Boolean checkIfNameExists(String Name) {

		for (int i = 0; i < mCompetitors.size(); i++) {
			if (Name.equalsIgnoreCase(mCompetitors.get(i).getName())) {
				return true;
			}
		}
		return false;
	}	
	
	public Boolean checkIfCardNumberExists(int cardNumber) {

		for (int i = 0; i < mCompetitors.size(); i++) {
			if (cardNumber == mCompetitors.get(i).getCardNumber()) {
				return true;
			}
		}
		return false;
	}	
	
	public Boolean checkIfStartNumberExists(int startNumber) {

		for (int i = 0; i < mCompetitors.size(); i++) {
			if (startNumber == mCompetitors.get(i).getStartNumber()) {
				return true;
			}
		}
		return false;
	}	
	
	public String importCompetitors(String newCompetitors, Boolean keep, int type) throws NumberFormatException, IOException {	
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
		BufferedReader bufReader = new BufferedReader(new StringReader(newCompetitors));
		
		if (!keep) {
			mCompetitors.clear();
		}
		
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
						status += "Error = " + line + "\n";
					}
				} else {
					start = 0;
					end = line.indexOf(",", start);
					name = line.substring(start, end);
	
					start = end + 1;
					end = line.indexOf(",", start);
					cardNumber = line.substring(start, end);
					cardNumber = cardNumber.replaceAll("[^\\d]", ""); // remove all non numerical digits
	
					start = end + 1;
					end = line.indexOf(",", start);
					team = line.substring(start, end);
	
					start = end + 1;
					end = line.indexOf(",", start);
					competitorClass = line.substring(start, end);
	
					start = end + 1;
					end = line.indexOf(",", start);
					startNumber = line.substring(start, end);
					startNumber = startNumber.replaceAll("[^\\d]", ""); // remove all non numerical digits
	
					start = end + 1;
					end = line.indexOf(",", start);
					startGroup = line.substring(start, line.length());
					startGroup = startGroup.replaceAll("[^\\d]", ""); // remove all non numerical digits
					
					status += add(name, cardNumber, team, competitorClass, startNumber, startGroup, 1);
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
						status += "Error = " + line + "\n";
					}
				} else {
					int pos = line.indexOf(",", 0);
					name = line.substring(0, pos);
					cardNumber = line.substring(pos + 1, line.length());
					cardNumber = cardNumber.replaceAll("[^\\d]", ""); // remove all non numerical digits
						
					status += add(name, cardNumber, "", "", "-1", "-1", 0);
				}			
			}					
		}
		return status;
	}	
}
