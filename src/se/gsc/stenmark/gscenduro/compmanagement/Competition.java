package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.SporIdent.Card;

/**
 * Represents a competition with a stage, List of competitors and the name of
 * the competition. The competition can be saved to disc and loaded back from
 * disc.
 * 
 * @author Andreas
 * 
 */
public class Competition implements Serializable {

	private static final long serialVersionUID = 7L;

	public static final int SVART_VIT_TYPE = 0;
	public static final int ESS_TYPE = 1;	

	public static final long COMPETITION_DNF =         5000000L;
	public static final long NO_TIME_FOR_STAGE =       10000000L;
	public static final long NO_TIME_FOR_COMPETITION = 20000000L;
	public static final Long RANK_DNF =                30000000L;
	public static final String CURRENT_COMPETITION = "current_competition";

	//Circular buffer to hold the 6 most recently read cards
	public CircularFifoQueue<String> lastReadCards = new CircularFifoQueue<String>(6);
	private TreeMap<String,Stage> totalResults = null;  //Class as key, one result list for each class
	private TreeMap<String, ArrayList<Stage>> stages = new TreeMap<String, ArrayList<Stage>>(); //Class as key, one Stage per class
	private Competitors mCompetitors = null;
	private String mCompetitionName;
	private String mCompetitionDate = "";
	private int mCompetitionType = SVART_VIT_TYPE;
	private String[] stageControls;

	public Competition() {
		totalResults = new TreeMap<String, Stage>();
		mCompetitors = new Competitors();
		stageControls = new String[0];
		setCompetitionName("New");

		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd", new Locale("swedish", "sweden"));
		String currentDateandTime = sdf.format(new Date());
		setCompetitionDate(currentDateandTime);
	}

	public Stage getTotalResults( String compClass ){
		return totalResults.get(compClass);
	}

	public TreeMap<String, Stage> getTotalResultsForAllClasses( ){
		return totalResults;
	}

	public List<Stage> getStages( String compClass ) {
		return stages.get(compClass);	
	}

	public TreeMap<String, ArrayList<Stage>> getStagesForAllClasses() {
		return stages;
	}

	public List<Stage> getStageDefinition(){
		try{
			if( getAllClasses().isEmpty()){
				List<Stage> stages = new ArrayList<Stage>();
				for(int i = 0; i < stageControls.length; i+=2){
					String start = stageControls[i];
					String fin = stageControls[i+1];
					Stage stage = new Stage();
					stage.start = Integer.parseInt(start);
					stage.finish = Integer.parseInt(fin);
					stages.add(stage);		
				}
				return stages;
			}
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
			return new ArrayList<Stage>();
		}
		return stages.get( getAllClasses().get(0) );

	}

	public List<String> getAllClasses(){
		List<String> result = new ArrayList<String>();
		for( String compClass : stages.keySet() ){
			result.add(compClass);
		}
		return result;
	}

	public int getNumberOfStages(){
		return getStageDefinition().size();
	}

	public void addCompetitor(String name, int cardNumber, String team, String competitorClass, int startNumber, int startGroup, int type){
		name = name.replaceFirst("\\s+$", "");
		Competitor competitor = new Competitor(name, cardNumber, team, competitorClass, startNumber, startGroup);
		mCompetitors.add(cardNumber,competitor);
		//If class does not already exist, add new Stages and TotalResult for the new class
		if( !getAllClasses().contains(competitorClass) ){
			ArrayList<Stage> newStages = createNewStages();
			stages.put(competitorClass, newStages);
			totalResults.put(competitorClass, new Stage());
		}
	}

	public Competitors getCompetitors() {
		return mCompetitors;
	}

	public String getCompetitionName() {
		return mCompetitionName;
	}

	public String getCompetitionDate() {
		return mCompetitionDate;
	}		

	public void setCompetitionName(String competitionName) {
		mCompetitionName = competitionName;
	}	

	public int getCompetitionType() {
		return mCompetitionType;
	}

	public void setCompetitionType(int competitionType) {
		mCompetitionType = competitionType;
	}	

	public void setCompetitionDate(String competitionDate) {
		mCompetitionDate = competitionDate;
	}		

	public String processNewCard(Card card, Boolean calculateResultsAfterAdd) {
		Competitor foundCompetitor = mCompetitors.findByCard(card);
		if (foundCompetitor == null) {
			return "WARNING! Could not find any competitor with card number: " + card.getCardNumber() + "\n";
		}
		ArrayList<Stage> stagesForClass = stages.get( foundCompetitor.getCompetitorClass() );
		if( stagesForClass == null ){
			return "WARNIGN! Could not find Class " + foundCompetitor.getCompetitorClass() +  " for " + foundCompetitor.getName();
		}

		String status = foundCompetitor.processCard(card, stagesForClass, mCompetitionType);

		if (calculateResultsAfterAdd) {				
			calculateResults();
		}

		return status;
	}

	public void calculateResults() {
		try{
			clearResults();

			//One result for each competitor class
			for( String competitorClass : totalResults.keySet() ) {
				if( !stages.containsKey(competitorClass) ){
					//Something went very wrong, the stages and totalresults does not have the same classes defined
					break;
				}

				String classString = "";
				if ( !competitorClass.isEmpty() ) {
					classString = competitorClass + " - ";
				} 		

				// Add total title
				totalResults.get(competitorClass).title = classString + "Total time";

				for (int i = 0; i < getNumberOfStages(); i++) {
					stages.get(competitorClass).get(i).title = classString + "Stage " + (i+1);
				}

				// Add total times		
				for( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
					Competitor currentCompetitor = currentCompetitorEntry.getValue();
					StageResult totalTimeResult;
					
										
					if( (currentCompetitor.getCompetitorClass().equals(competitorClass)) || (competitorClass.equals("")) ){
						if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() == 0) ) {
							totalTimeResult = new StageResult(currentCompetitor.getCardNumber(),NO_TIME_FOR_COMPETITION);
						} else if ((currentCompetitor.getStageTimes() == null) || (currentCompetitor.getStageTimes().size() < getNumberOfStages() )) {
							totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), COMPETITION_DNF);
						} 
						else {
							totalTimeResult = new StageResult(currentCompetitor.getCardNumber(), currentCompetitor.getTotalTime( getNumberOfStages() ));							
						}
						totalResults.get(competitorClass).addCompetitorResult(totalTimeResult);
					}
				}		
				//Sort total times
				totalResults.get(competitorClass).sortStageResult(NO_TIME_FOR_COMPETITION);

				StageResult stageResult;
				// Add stage times
				for (int stageNumber = 0; stageNumber <  getNumberOfStages(); stageNumber++) {
					for ( Entry<Integer, Competitor> currentCompetitorEntry : mCompetitors.getCompetitors().entrySet() ) {
						Competitor currentCompetitor = currentCompetitorEntry.getValue();
						if( (currentCompetitor.getCompetitorClass().equals(competitorClass)) || (competitorClass.equals("")) ) {			
							Long stageTime = NO_TIME_FOR_STAGE;
							if ((currentCompetitor.hasResult()) && (currentCompetitor.getStageTimes().size() > stageNumber)) { 
								stageTime = currentCompetitor.getStageTimes().getTimesOfStage(stageNumber); 
							} 

							stageResult = new StageResult(currentCompetitor.getCardNumber(), stageTime);
							stages.get(competitorClass).get(stageNumber).addCompetitorResult(stageResult);
						}						
					}

					//Sort stages times
					stages.get(competitorClass).get(stageNumber).sortStageResult(NO_TIME_FOR_STAGE);
				}

				//Calculate time back
				totalResults.get(competitorClass).calculateTimeBack();
				for( Stage currentStage : stages.get(competitorClass) ) {
					currentStage.calculateTimeBack();
				}
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	private ArrayList<Stage> createNewStages(){
		ArrayList<Stage> result = new ArrayList<Stage>();
		for (int i = 0; i < stageControls.length; i += 2) {
			int startControl = 0;
			int finishControl = 0;
			startControl = Integer.parseInt(stageControls[i]);
			finishControl = Integer.parseInt(stageControls[i + 1]);
			result.add(new Stage(startControl, finishControl));
		}	
		return result;
	}

	public void importStages(String stagesToImport) {
		stageControls = stagesToImport.split(",");				
		stages.clear();
	}

	public void clearResults() {
		for( String compClass : stages.keySet()){
			for( Stage stage : stages.get(compClass)){
				stage.clearResult();
			}
		}
		for( String compClass : totalResults.keySet()){
			totalResults.get(compClass).clearResult();
		}
	}

	public List<Integer> getControls() {
		List<Integer> controls = new ArrayList<Integer>();
		for (Stage stage : getStageDefinition()) {
			if (!controls.contains(stage.start)) {
				controls.add(stage.start);
			}
			if (!controls.contains(stage.finish)) {
				controls.add(stage.finish);
			}
		}
		return controls;
	}

}
