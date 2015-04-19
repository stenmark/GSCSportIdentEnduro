package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;

public class CompetitorParser implements Serializable{

	private static final long serialVersionUID = 1212L;

	private String mStatus = "";
	private ArrayList<Competitor> mCompetitor = null;
	
	public CompetitorParser(){
	}
		
	public String getStatus() {
		return mStatus;
	}	
	
	public ArrayList<Competitor> getCompetitors() {
		return mCompetitor;
	}			

	public void parseCompetitors(String multiCompetitors) throws IOException{
		mCompetitor = new ArrayList<Competitor>();
		String name = "";
		String cardNumber = "";
		int pos = 0;
		
		//multiCompetitors = "a,1\nb,2\nc,3";
				
		BufferedReader bufReader = new BufferedReader(new StringReader(multiCompetitors));
		String line = null;
		int lineNumber = 0;
		while((line = bufReader.readLine()) != null)
		{					
			//count so only one , each line
			int number = 0;
			for (int i = 0, len = line.length(); i < len; ++i) {
                Character c = line.charAt(i);
                if (c == ',')
                {
                	number++;
                }
            }
			
			if (number != 1) 
			{
				//Do not count empty lines
				if (line.length() != 0)
				{
					lineNumber++;
					mStatus +=  Integer.toString(lineNumber) + ". " + "Error adding, because of , = " + line +"\n";
				}
			}
			else
			{
				lineNumber++;
				mStatus += Integer.toString(lineNumber) + ". "; 
				pos = line.indexOf(",", 0);							
				name = line.substring(0, pos);	
				cardNumber = line.substring(pos + 1, line.length());
				cardNumber = cardNumber.replaceAll("[^\\d]", "");  //remove all non numerical digits

				if (!cardNumber.matches("\\d+"))
				{
					mStatus += "Error, cardnumber not a number\n";
				}
				else 
				{	
					Competitor competitorObject = new Competitor(name);
					
					competitorObject.cardNumber = Integer.parseInt(cardNumber);					
					mCompetitor.add(competitorObject);
					
					mStatus += name + ", " + cardNumber + " added\n";
				}									
			}
			
		}	
	}		
}
