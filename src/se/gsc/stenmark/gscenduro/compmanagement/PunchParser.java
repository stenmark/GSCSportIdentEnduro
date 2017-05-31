package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class PunchParser implements Serializable {

	private static final long serialVersionUID = 7L;
	private String mStatus = "";
	private ArrayList<Card> mCards = null;

	public PunchParser() {
	}

	public String getStatus() {
		return mStatus;
	}

	public ArrayList<Card> getCards() {
		return mCards;
	}

	public void parsePunches(String punches, LinkedHashMap<Integer,Competitor> competitors, List<Stage> stages) throws IOException {
		mCards = new ArrayList<Card>();
		
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
	
		BufferedReader bufReader = new BufferedReader(new StringReader(punches));
		String line = null;
		while ((line = bufReader.readLine()) != null) {
			String[] commaSeparatedLine = line.split(",");
		
			if (commaSeparatedLine.length < 5) {
				if (line.isEmpty()) {
					mStatus += "Can not parse line = empty line\n";
				} else {
					mStatus += "Can not parse line, too few entries in " + line + "\n";
				}
			} else if ((commaSeparatedLine.length > 5) && ((commaSeparatedLine.length - 3) % 3 != 0)) { 
				//Not correct number of commas
				mStatus += "Can not parse line, every punch does not have a timestamp in: " + line + "\n";
			}else {
				Card cardObject = new Card();
				String cardNumberAsString = commaSeparatedLine[0];
				

				if (!cardNumberAsString.matches("\\d+")) {
					mStatus += line + ". Card number is not a number: " + cardNumberAsString + "\n";
				} else {	
					int cardNumber = Integer.parseInt(cardNumberAsString);
					boolean error = false; 
					boolean foundCompetitor = false;
					if( competitors.containsKey( cardNumber) ){
						foundCompetitor = true;
						cardObject.setCardNumber( cardNumber );
						for( int i = 1; i < commaSeparatedLine.length; i+=2){
							String controlAsString = commaSeparatedLine[i];
							String timeAsString = commaSeparatedLine[i+1];		
							
							if (!controlAsString.matches("\\d+")) {
								mStatus += line + ". Control is not a number: " + controlAsString + "\n";
								error = true;
								break;
							}
							if (!timeAsString.matches("\\d+")) {
								mStatus += line + ". Time is not a number: " + timeAsString + "\n";
								error = true;
								break;
							} 	
							int time = Integer.valueOf(timeAsString);
							int control = Integer.valueOf(controlAsString);
				
							if (!CompetitionHelper.validStageControl(stages, control )) {
								mStatus += line + ". Control is not in any of the stages\n";
								error = true;
								break;									
							}
				
							Punch punchObject = new Punch(time, control);
							cardObject.getPunches().add(punchObject);
						}				
					}
		
					if (!error && foundCompetitor) {
						if (cardObject.getPunches().size() > 0) {
							if (cardObject.getCardNumber() != 0) {
								mCards.add(cardObject);
								mStatus += line + ". Added\n";
							}
						}
					} else if (!foundCompetitor) {
						mStatus += line + ". No competitor with that card number found\n";
					}
				}
			}
		}
	}
}
