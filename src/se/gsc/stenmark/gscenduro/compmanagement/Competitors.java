package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
			mCompetitors.get(i).clearCard();
		}
	}	
	
	public String checkData(String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type, Boolean checkAgainstCurrent, ArrayList<Competitor> competitors) {
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
	
	public void add(String name, String cardNumber, String team, String competitorClass, String startNumber, String startGroup, int type) {				
		name = name.replaceFirst("\\s+$", "");
		Competitor competitor = new Competitor(name, Integer.parseInt(cardNumber), team, competitorClass, Integer.parseInt(startNumber), Integer.parseInt(startGroup));
		mCompetitors.add(competitor);

		sort();
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
			if (competitorClass.equals(competitor.getCompetitorClass())) {
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
			if (competitorClasses.equals(competitorClass)) {
				//Already in list
			} else {
				competitorClasses.add(competitor.getCompetitorClass());
			}
		}
		return competitorClasses;
	}	
	
	public Long getFastestOnStage(String competitorClass, int stageNumber) {
		Long fastestTimeOnStage = Long.MAX_VALUE;
		for(Competitor competitor : mCompetitors) {
			
			if (competitorClass.equals(competitor.getCompetitorClass())) {						
				try{
					fastestTimeOnStage = Math.min(fastestTimeOnStage, competitor.getStageTimes().getTimesOfStage(stageNumber - 1));
				}
				catch(IndexOutOfBoundsException e){	
				}
			}
		}
		return fastestTimeOnStage;
	}	
	
	public Long getSlowestOnStage(String competitorClass, int stageNumber) {
		Long slowestTimeOnStage = (long) 0;
		for(Competitor competitor : mCompetitors){
			if (competitorClass.equals(competitor.getCompetitorClass())) {
				try{
					if (competitor.getStageTimes().getTimesOfStage(stageNumber - 1) != (long) Integer.MAX_VALUE) {
						slowestTimeOnStage = Math.max(slowestTimeOnStage, competitor.getStageTimes().getTimesOfStage(stageNumber - 1));
					}
				}
				catch(IndexOutOfBoundsException e){
					return (long) Integer.MAX_VALUE;
				}
			}
		}
		
		if (slowestTimeOnStage == 0) {
			return (long) Integer.MAX_VALUE;
		}
		return slowestTimeOnStage;
	}	
	
	
	public String exportCsvString(int type) {
		String competitorsAsCsv = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
			for (Competitor competitor : mCompetitors) {			
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
		for (Competitor competitor : mCompetitors) {
			if (competitor.getCardNumber() == cardToMatch.getCardNumber()) {
				return competitor;
			}
		}
		return null;
	}
		
	public String exportPunchesCsvString() {
		String punchesAsCsv = "";

		if (mCompetitors != null && !mCompetitors.isEmpty()) {
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
	
	public Boolean checkIfNameExists(String name, ArrayList<Competitor> competitors) {
		name = name.replaceFirst("\\s+$", "");
		
		for (int i = 0; i < competitors.size(); i++) {
			if (name.equalsIgnoreCase(competitors.get(i).getName())) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean checkIfNameExists(String name) {
		return checkIfNameExists(name, mCompetitors);
	}		
	
	public Boolean checkIfCardNumberExists(int cardNumber, ArrayList<Competitor> competitors) {

		for (int i = 0; i < competitors.size(); i++) {
			if (cardNumber == competitors.get(i).getCardNumber()) {
				return true;
			}
		}
		return false;
	}	
	
	public Boolean checkIfCardNumberExists(int cardNumber) {
		return checkIfCardNumberExists(cardNumber, mCompetitors);
	}
	
	public Boolean checkIfStartNumberExists(int startNumber, ArrayList<Competitor> competitors) {

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
		ArrayList<Competitor> newCompetitors = new ArrayList<Competitor>();
		
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
							newCompetitors.add(competitor);
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
							newCompetitors.add(competitor);
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
				
				add(name, cardNumber.replaceAll("[^\\d]", ""), team, competitorClass, startNumber.replaceAll("[^\\d]", ""), startGroup.replaceAll("[^\\d]", ""), 1);				
			} else {				
				/*
				name,cardNumber
				name,cardNumber
				name,cardNumber
				*/
	
				int pos = line.indexOf(",", 0);
				name = line.substring(0, pos);
				cardNumber = line.substring(pos + 1, line.length());

				add(name, cardNumber.replaceAll("[^\\d]", ""), "", "", "-1", "-1", 0);					
			}					
		}
	}	
	
	public void importPunches(String punches, Stages stages, int type) throws IOException {
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
		
		BufferedReader bufReader = new BufferedReader(new StringReader(punches));
		String line = null;
		while ((line = bufReader.readLine()) != null) {	
			Card cardObject = new Card();
			
			int start = 0;
			int end = 0;
	
			end = line.indexOf(",", start);
			String cardNumber = line.substring(start, end);
			start = end + 1;
	
			for (int i = 0; i < mCompetitors.size(); i++) {
				if (mCompetitors.get(i).getCardNumber() == Integer.parseInt(cardNumber)) {
					cardObject.setCardNumber(mCompetitors.get(i).getCardNumber());
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
					
					mCompetitors.get(i).processCard(cardObject, stages, type);	
					break;						
				}
			}					
		}
	}	
}
