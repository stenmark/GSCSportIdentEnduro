package se.gsc.stenmark.gscenduro;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.Competitors;
import se.gsc.stenmark.gscenduro.compmanagement.StageResult;
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
		return competition.getCompetitors().size();
	}
	
	@Override
	public Competitor getItem(int position) {
		return competition.getCompetitors().get(position);
	}

	public Competitors getData() {
	    return competition.getCompetitors();
	}	
	
	public void updateResultLandscape () {
		competition = ((MainActivity) mContext).competition;
		this.notifyDataSetChanged();	
	}		
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		ResultsLandscapeRowView resultLandscapeRowV = null;	
		//OLD VERSION
//		Competition competition = ((MainActivity) mContext).competition;
		
		if (convertView == null) {
			resultLandscapeRowV = new ResultsLandscapeRowView(mContext);
		} else {
			resultLandscapeRowV = (ResultsLandscapeRowView) convertView;
		}

		resultLandscapeRowV.clearTitle();
		resultLandscapeRowV.clearTimes();
		resultLandscapeRowV.setTimes(competition.getNumberOfStages() );
		if (position == 0) {
			resultLandscapeRowV.setTitle( competition.getNumberOfStages() );
			//OLD VERSION
//			resultLandscapeRowV.setResultLandscapeCompetitorClass(mResultLandscape.get(position).getTitle());
			resultLandscapeRowV.setResultLandscapeCompetitorClass( competition.getTotalResults().title );
		}
		//OLD VERSION
//		else {
			
//			if (mResultLandscape.get(position).getTitle() != mResultLandscape.get(position - 1).getTitle()) {
//				resultLandscapeRowV.setTitle(competition.getStages().size());	
//				resultLandscapeRowV.setResultLandscapeCompetitorClass(mResultLandscape.get(position).getTitle());
//			}
//		}
			
		resultLandscapeRowV.setComp();
		//OLD VERSION
//		StageResult totalTimeResult = mResultLandscape.get(position).getStageResult().get(0);
		StageResult totalTimeResult = competition.getTotalResults().getCompetitorResults().get(position);
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
		for(int stageNumber = 1; stageNumber < 11; stageNumber++) {
			resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, "", Color.WHITE);
		}
		
		try{
			//OLD VERSION
//			for(int stageNumber = 1; stageNumber < mResultLandscape.get(position).getStageResult().size(); stageNumber++) {
//				Long fastestTimeOnStage = competition.getFastestOnStage(mResultLandscape.get(position).getTitle(), stageNumber); 
//				Long slowestTimeOnStage = competition.calculateSlowestOnStage(mResultLandscape.get(position).getTitle(), stageNumber);
//				String StageTime = AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(mResultLandscape.get(position).getStageResult().get(stageNumber).getStageTime());
//				Long competitorStageTime = mResultLandscape.get(position).getStageResult().get(stageNumber).getStageTime();				
//				rank = mResultLandscape.get(position).getStageResult().get(stageNumber).getRank();
				
			
				for(int stageNumber = 0; stageNumber < competition.getStages().size(); stageNumber++) {
					Long fastestTimeOnStage = competition.getFastestOnStage(competition.getTotalResults().title, stageNumber); 
					Long slowestTimeOnStage = competition.calculateSlowestOnStage(competition.getTotalResults().title, stageNumber);
					Long competitorStageTime = competition.getStages().get(stageNumber).getStageResultByCardnumber(cardNumber).getStageTime();			
					String StageTime = CompetitionHelper.milliSecToMinSecMilliSec(competitorStageTime);
					rank = competition.getStages().get(stageNumber).getStageResultByCardnumber(cardNumber).getRank();
				
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
}