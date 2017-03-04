package se.gsc.stenmark.gscenduro;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.Competitors;
import se.gsc.stenmark.gscenduro.compmanagement.Stage;
import se.gsc.stenmark.gscenduro.compmanagement.StageResult;

import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResultsListLandscapeAdapter extends BaseAdapter {

	private Context mContext;
	private Competition competition;

	public ResultsListLandscapeAdapter(Context context, Competition Item) {
		mContext = context;
		competition = Item;			
	}		

	@Override
	public int getCount() {
		int size = 0;
		try{
			size = competition.getCompetitors().size();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return size;
	}

	@Override
	public Competitor getItem(int position) {
		Competitor c = null;
		try{
			c = competition.getCompetitors().get(position);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return c;
	}

	public Competitors getData() {
		Competitors cs = null;
		try{
			cs =competition.getCompetitors();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return cs;
	}	

	public void updateResultLandscape () {
		try{
			competition = ((MainActivity) mContext).competition;
			this.notifyDataSetChanged();	
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}		

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		try{
			ResultsLandscapeRowView resultLandscapeRowV = null;			
			if (convertView == null) {
				resultLandscapeRowV = new ResultsLandscapeRowView(mContext);
			} else {
				resultLandscapeRowV = (ResultsLandscapeRowView) convertView;
			}

			resultLandscapeRowV.clearTitle();
			resultLandscapeRowV.clearTimes();
			resultLandscapeRowV.setTimes(competition.getNumberOfStages() );

			if(competition.getAllClasses().isEmpty() ){
				return resultLandscapeRowV;
			}	
			TreeMap<String, Stage> totalResults = competition.getTotalResultsForAllClasses();
			resultLandscapeRowV.setComp();
			StageResult totalTimeResult = null;
			int i = 0;
			boolean done = false;
			String foundCompClass = "";
			if(position==0){
				resultLandscapeRowV.setTitle( competition.getNumberOfStages() );
			}
			for(String compClass : totalResults.keySet()){
				resultLandscapeRowV.setResultLandscapeCompetitorClass(  totalResults.get(compClass).title );
				for( StageResult totalTimeRes : totalResults.get(compClass).getCompetitorResults() ){
					if( i == position){
						totalTimeResult = totalTimeRes;
						done = true;
						foundCompClass = compClass;
						break;
					}
					i++;
				}
				if(done){
					break;
				}
			}
			if(totalTimeResult==null){
				return resultLandscapeRowV;
			}
			List<Stage> stages = competition.getStages(foundCompClass);
			
			int cardNumber = totalTimeResult.getCardNumber();

			int rank = totalTimeResult.getRank();
			String name = "";
			if (rank == Competition.RANK_DNF) {			
				name += "-. ";
			} else {
				name += rank + ". ";
			}

			name += competition.getCompetitors().getByCardNumber(cardNumber).getName();
			resultLandscapeRowV.setResultLandscapeName(name);

			String startNumber = String.valueOf(competition.getCompetitors().getByCardNumber(cardNumber).getStartNumber());	
			resultLandscapeRowV.setResultLandscapeStartNumber(startNumber);			

			String team = competition.getCompetitors().getByCardNumber(cardNumber).getTeam();	
			resultLandscapeRowV.setResultLandscapeTeam(team);

			long totalTime = totalTimeResult.getStageTime();			
			resultLandscapeRowV.setResultLandscapeTotalTime(CompetitionHelper.milliSecToMinSecMilliSec(totalTime));				

			//First clear all stages
			for(int stageNumber = 0; stageNumber < 10; stageNumber++) {
				resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, "", Color.WHITE);
			}

			try{
				for(int stageNumber = 0; stageNumber < competition.getNumberOfStages(); stageNumber++) {
					Long fastestTimeOnStage = stages.get(stageNumber).getFastestTime();
					Long slowestTimeOnStage = stages.get(stageNumber).calculateSlowestOnStage();
					Long competitorStageTime = stages.get(stageNumber).getStageResultByCardnumber(cardNumber).getStageTime();			
					String StageTime = CompetitionHelper.milliSecToMinSecMilliSec(competitorStageTime);
					rank = stages.get(stageNumber).getStageResultByCardnumber(cardNumber).getRank();

					int color = AndroidHelper.generateRedToGreenColorTransition(fastestTimeOnStage, slowestTimeOnStage, competitorStageTime, rank);
					if (rank == Competition.RANK_DNF) {
						StageTime = "";
					} else {
						StageTime += "\n(" + String.valueOf(rank) + ")";											
					}			

					resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, StageTime, color);
				}	
			}
			catch( Exception  e){
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show( ((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			}

			resultLandscapeRowV.setPosition(position);

			return resultLandscapeRowV;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return null;
	}

}