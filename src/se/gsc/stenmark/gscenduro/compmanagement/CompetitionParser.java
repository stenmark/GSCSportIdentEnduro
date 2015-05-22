package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;

public class CompetitionParser implements Serializable {

	private static final long serialVersionUID = 11212L;

	private String mStatus = "";
	private ArrayList<Card> mCards = null;
	private ArrayList<Competitor> mCompetitors = null;
	private String mCompetitionName = "";
	private String mTrack = "";

	public CompetitionParser() {
	}

	public String getStatus() {
		return mStatus;
	}

	public String getCompetitionName() {
		return mCompetitionName;
	}

	public String getTrack() {
		return mTrack;
	}

	public ArrayList<Competitor> getCompetitors() {
		return mCompetitors;
	}

	public ArrayList<Card> getCards() {
		return mCards;
	}

	public void parseCompetition(String competitionString) throws IOException {
		mStatus = "";
		mCards = new ArrayList<Card>();
		mCompetitors = new ArrayList<Competitor>();

		BufferedReader bufReader = new BufferedReader(new StringReader(
				competitionString));
		String line = null;
		int lineNumber = 0;
		while ((line = bufReader.readLine()) != null) {
			lineNumber++;

			mStatus += Integer.toString(lineNumber) + ". ";

			if (line.length() > 0) {
				int start = 0;
				int end = 0;

				end = line.indexOf(",", start);
				String type = line.substring(start, end);
				start = end + 1;

				if (type.equals("competitionName")) {
					mCompetitionName = line.substring(start, line.length());
					mStatus += "competitionName = " + mCompetitionName + "\n";
				} else if (type.equals("track")) {
					mTrack = line.substring(start, line.length());
					mStatus += "track = " + mTrack + "\n";
				} else if (type.equals("competitor")) {
					end = line.indexOf(",", start);
					String Name = line.substring(start, end);
					start = end + 1;

					end = line.indexOf(",", start);
					String cardNumber = line.substring(start, end);
					start = end + 1;

					if (!cardNumber.matches("\\d+")) {
						mStatus += "Error, cardnumber not a number\n";
					} else {
						Competitor competitorObject = new Competitor(Name);
						competitorObject.setCardNumber(Integer
								.parseInt(cardNumber));
						mCompetitors.add(competitorObject);

						mStatus += "Name = " + Name + " cardNumber = "
								+ cardNumber + "\n";

						Card cardObject = new Card();
						cardObject.setCardNumber(Integer.parseInt(cardNumber));

						while (start < line.length()) {
							end = line.indexOf(",", start);
							String time = line.substring(start, end);
							start = end + 1;

							String control = "";
							end = line.indexOf(",", start);
							if (end == -1) {
								control = line.substring(start, line.length());
								start = line.length() + 1;
							} else {
								control = line.substring(start, end);
								start = end + 1;
							}

							Punch punchObject = new Punch(Long.valueOf(time),
									Long.valueOf(control));
							cardObject.getPunches().add(punchObject);
						}

						mCards.add(cardObject);
					}
				}
			}
		}
	}
}
