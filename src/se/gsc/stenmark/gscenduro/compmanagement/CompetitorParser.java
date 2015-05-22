package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;

public class CompetitorParser implements Serializable {

	private static final long serialVersionUID = 1312L;
	private String mStatus = "";
	private ArrayList<Competitor> mCompetitor = null;

	public CompetitorParser() {
	}

	public String getStatus() {
		return mStatus;
	}

	public ArrayList<Competitor> getCompetitors() {
		return mCompetitor;
	}

	public void parseCompetitors(String multiCompetitors) throws IOException {
		mCompetitor = new ArrayList<Competitor>();
		String name = "";
		String cardNumber = "";
		int pos = 0;

		// multiCompetitors = "a,1\nb,2\nc,3";

		BufferedReader bufReader = new BufferedReader(new StringReader(
				multiCompetitors));
		String line = null;
		int lineNumber = 0;
		while ((line = bufReader.readLine()) != null) {
			// count so only one , each line
			int number = 0;
			for (int i = 0, len = line.length(); i < len; ++i) {
				Character c = line.charAt(i);
				if (c == ',') {
					number++;
				}
			}

			if (number != 1) {
				// Do not count empty lines
				if (line.length() != 0) {
					lineNumber++;
					mStatus += Integer.toString(lineNumber) + ". "
							+ "Error adding, because of , = " + line + "\n";
				}
			} else {
				lineNumber++;
				mStatus += Integer.toString(lineNumber) + ". ";
				pos = line.indexOf(",", 0);
				name = line.substring(0, pos);
				cardNumber = line.substring(pos + 1, line.length());
				cardNumber = cardNumber.replaceAll("[^\\d]", ""); // remove all
																	// non
																	// numerical
																	// digits

				if (!cardNumber.matches("\\d+")) {
					mStatus += "Error, card number not a number\n";
				} else {
					Competitor competitorObject = new Competitor(name);

					competitorObject
							.setCardNumber(Integer.parseInt(cardNumber));

					// todo
					// Ingen koll om namn eller kortnummer redan existerar.
					mCompetitor.add(competitorObject);

					mStatus += name + ", " + cardNumber + " added\n";
				}
			}
		}
	}

	public void parseEssCompetitors(String multiCompetitors) throws IOException {
		mCompetitor = new ArrayList<Competitor>();
		String name = "";
		String cardNumber = "";
		String team = "";
		String competitorClass = "";
		String startNumber = "";
		String startGroup = "";
		int start = 0;
		int end = 0;

		BufferedReader bufReader = new BufferedReader(new StringReader(
				multiCompetitors));
		String line = null;
		int lineNumber = 0;
		while ((line = bufReader.readLine()) != null) {
			// count so 5 , each line
			int number = 0;
			for (int i = 0, len = line.length(); i < len; ++i) {
				Character c = line.charAt(i);
				if (c == ',') {
					number++;
				}
			}

			if (number != 5) {
				// Do not count empty lines
				if (line.length() != 0) {
					lineNumber++;
					mStatus += Integer.toString(lineNumber) + ". "
							+ "Error adding, because of , = " + line + "\n";
				}
			} else {
				lineNumber++;
				mStatus += Integer.toString(lineNumber) + ". ";
				start = 0;
				end = line.indexOf(",", start);
				name = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				cardNumber = line.substring(start, end);
				cardNumber = cardNumber.replaceAll("[^\\d]", ""); // remove all
																  // non
																  // numerical
																  // digits

				start = end + 1;
				end = line.indexOf(",", start);
				team = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				competitorClass = line.substring(start, end);

				start = end + 1;
				end = line.indexOf(",", start);
				startNumber = line.substring(start, end);
				startNumber = startNumber.replaceAll("[^\\d]", ""); // remove
																	// all non
																	// numerical
																	// digits

				start = end + 1;
				end = line.indexOf(",", start);
				startGroup = line.substring(start, line.length());
				startGroup = startGroup.replaceAll("[^\\d]", ""); // remove all
																  // non
																  // numerical
																  // digits

				if (!cardNumber.matches("\\d+") || !startNumber.matches("\\d+")
						|| !startGroup.matches("\\d+")) {
					mStatus += "Error, card number, start number or start group not a number\n";
				} else {
					Competitor competitorObject = new Competitor(name);

					competitorObject
							.setCardNumber(Integer.parseInt(cardNumber));
					competitorObject.setTeam(team);
					competitorObject.setCompetitorClass(competitorClass);
					competitorObject.setStartNumber(Integer
							.parseInt(startNumber));
					competitorObject
							.setStartGroup(Integer.parseInt(startGroup));

					// todo
					// Ingen koll om namn, kortnummer eller startnummer redan
					// existerar.
					mCompetitor.add(competitorObject);

					mStatus += name + ", " + cardNumber + " added\n";
				}
			}
		}
	}
}
