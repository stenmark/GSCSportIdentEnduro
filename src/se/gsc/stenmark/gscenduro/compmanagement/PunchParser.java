package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class PunchParser implements Serializable {

	private static final long serialVersionUID = 1212L;
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

	public void parsePunches(String punches, List<Competitor> competitors) throws IOException {
		mCards = new ArrayList<Card>();
		
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
		//cardNumber,control,time,control,time..
		
		BufferedReader bufReader = new BufferedReader(new StringReader(punches));
		String line = null;
		while ((line = bufReader.readLine()) != null) {

			// count so at least 3, each line
			int number = 0;
			for (int i = 0, len = line.length(); i < len; ++i) {
				Character c = line.charAt(i);
				if (c == ',') {
					number++;
				}
			}
		
			if (number < 3) {
				if (line.length() == 0) {
					mStatus += "Error, empty line\n";
				} else {
					mStatus += "Error = " + line + "\n";
				}
			} else {
				Card cardObject = new Card();

				int start = 0;
				int end = 0;

				end = line.indexOf(",", start);
				String cardNumber = line.substring(start, end);
				start = end + 1;

				if (!cardNumber.matches("\\d+")) {
					mStatus += "Error, cardnumber not a number\n";
				} else {				
					for (int i = 0; i < competitors.size(); i++) {
						if (competitors.get(i).getCardNumber() == Integer.parseInt(cardNumber)) {
							cardObject.setCardNumber(competitors.get(i).getCardNumber());
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
								Punch punchObject = new Punch(Long.valueOf(time), Long.valueOf(control));
								cardObject.getPunches().add(punchObject);
							}
						}
					}

					if (cardObject.getPunches().size() > 0) {
						if (cardObject.getCardNumber() != 0) {
							mCards.add(cardObject);
							mStatus += "card added";
						}
					}				
					mStatus += "\n";
				}
			}
		}
	}
}
