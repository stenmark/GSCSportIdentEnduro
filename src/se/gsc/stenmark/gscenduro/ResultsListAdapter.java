package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResultsListAdapter extends BaseAdapter {
	
	private Context mContext;
	//OLD VERSION
//	private ResultList<Results> mResult = new ResultList<Results>();
	private TreeMap<String, ArrayList<Stage>> stagesForAllClasses;
	private TreeMap<String,Stage> totalResultsForAllClasses;

	//OLD VERSION
//	public ResultsListAdapter(Context context, ResultList<Results> Items) {
//		mContext = context;
//		mResult = Items;		
//	}	
	public ResultsListAdapter(Context context, TreeMap<String, ArrayList<Stage>> stages, TreeMap<String,Stage> totalResults ) {
		mContext = context;
		stagesForAllClasses = stages;		
		totalResultsForAllClasses = totalResults;
	}	
	
	@Override
	public int getCount() {
		int size = 0;
	
		//OLD VERSION
//		for (int stage = 0; stage < mResult.size(); stage++)
//		{
//			size += mResult.get(stage).getStageResult().size();
//		}
		for( String compClass : totalResultsForAllClasses.keySet() )
		{
			size += totalResultsForAllClasses.get(compClass).numberOfCompetitors();
		}
		for( String compClass : stagesForAllClasses.keySet() )
		{
			for( Stage stage : stagesForAllClasses.get(compClass)){
				size += stage.numberOfCompetitors();
			}
		}
		
		return size;
	}

	//OLD VERSION
//	@Override
//	public Results getItem(int position) {
//		return mResult.get(position);
//	}
//
//	public List<Results> getData() {
//	    return mResult;
//	}
	
	@Override
	public Stage getItem(int position) {
		return concatAllClasses().get(position);
	}
	
	private List<Stage> concatAllClasses(){
		List<Stage> stages = new ArrayList<Stage>();
		for( String compClass : totalResultsForAllClasses.keySet() ){
			stages.add( totalResultsForAllClasses.get(compClass));
		}
		for( String compClass : stagesForAllClasses.keySet() ){
			stages.addAll( stagesForAllClasses.get(compClass));
		}
		return stages;
	}

	public TreeMap<String, ArrayList<Stage>> getData() {
	    return stagesForAllClasses;
	}
	
	public void updateResult () {
		//OLD VERSION
//		mResult = ((MainActivity) mContext).competition.getResults();
		stagesForAllClasses = ((MainActivity) mContext).competition.getStagesForAllClasses();
		totalResultsForAllClasses = ((MainActivity) mContext).competition.getTotalResultsForAllClasses();
		this.notifyDataSetChanged();	
	}	
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultsRowView resultRowV = null;

		if (convertView == null) {
			resultRowV = new ResultsRowView(mContext);
		} else {
			resultRowV = (ResultsRowView) convertView;
		}
		/*
		resultRowV.setTitle(mResult.get(position).getTitle());
		
		int numberOfCompetitorsOnStage = mResult.get(position).getStageResult().size();
		//Use stringbuilder for more efficient string concatenation
		//Init the string builder for each string to a multiple of the number of competitors. Estimate how many characters each competitor need.
		StringBuilder name = new StringBuilder(  mResult.get(position).getStageResult().size()*20 ); 
		StringBuilder startNumber = new StringBuilder(  mResult.get(position).getStageResult().size()*5 ); 
		StringBuilder team = new StringBuilder(  mResult.get(position).getStageResult().size()*15 ); 
		StringBuilder time = new StringBuilder(  mResult.get(position).getStageResult().size()*10 ); 
		StringBuilder timeBack = new StringBuilder(  mResult.get(position).getStageResult().size()*10 ); 
		
		for(int i = 0; i < numberOfCompetitorsOnStage; i++) {
			int rank = mResult.get(position).getStageResult().get(i).getRank();			
			if (rank == Competition.RANK_DNF) {			
				name.append("-. ");
			} else {
				name.append( rank + ". " );
			}		
			Competitor currentCompetitor = ((MainActivity)mContext).competition.getCompetitors().getByCardNumber(mResult.get(position).getStageResult().get(i).getCardNumber());
			name.append(  currentCompetitor.getName() + "\n");
			startNumber.append( currentCompetitor.getStartNumber() + "\n");
			team.append( currentCompetitor.getTeam() + "\n" );
					
			time.append( AndroidIndependantCompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTime()) + "\n" );
			timeBack.append( AndroidIndependantCompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTimesBack()) + "\n" );			
		}		
		*/
		
		int index = 0;
		int stageNumber = 0;
		//OLD VERSION
//		for (; stage < mResult.size(); stage++)
//		for(){
		for ( Stage stage : concatAllClasses() )
		{
			//OLD VERSION
//			if (index + mResult.get(stage).getStageResult().size() > position)
			if (index + stage.getCompetitorResults().size() > position)
			{
				index = position - index;
				break;
			}	
				
			//OLD VERSION
//			index += mResult.get(stage).getStageResult().size();
			index += stage.getCompetitorResults().size();
			stageNumber++;
		}

		Stage stage = concatAllClasses().get(stageNumber);
		String name; 
		//OLD VERSION
//		int rank = mResult.get(stage).getStageResult().get(index).getRank();		
		int rank =stage.getCompetitorResults().get(index).getRank();	
		if (rank == Competition.RANK_DNF) {			
			name = "-. ";
		} else {
			name = rank + ". ";
		}		
		//OLD VERSION
//		Competitor currentCompetitor = ((MainActivity)mContext).competition.getCompetitors().getByCardNumber(mResult.get(stage).getStageResult().get(index).getCardNumber());
		Competitor currentCompetitor = ((MainActivity)mContext).competition.getCompetitors().getByCardNumber(stage.getCompetitorResults().get(index).getCardNumber());
		name += currentCompetitor.getName();
		String startNumber = String.valueOf(currentCompetitor.getStartNumber());
		String team = currentCompetitor.getTeam();
		//OLD VERSION
//		String time = AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(mResult.get(stage).getStageResult().get(index).getStageTime());
//		String timeBack = AndroidIndependantCompetitionHelper.milliSecToMinSecMilliSec(mResult.get(stage).getStageResult().get(index).getStageTimesBack());			
		String time = CompetitionHelper.milliSecToMinSecMilliSec(stage.getCompetitorResults().get(index).getStageTime());
		String timeBack = CompetitionHelper.milliSecToMinSecMilliSec(stage.getCompetitorResults().get(index).getStageTimesBack());			
		
		resultRowV.setResultName(name);
		resultRowV.setResultStartNumber(startNumber);
		resultRowV.setResultTeam(team);
		resultRowV.setResultTime(time);
		resultRowV.setResultTimeBack(timeBack);	
		
		if (index == 0) {
			//OLD VERSION
//			resultRowV.setTitle(mResult.get(stage).getTitle(), View.VISIBLE);	
			resultRowV.setTitle(stage.title, View.VISIBLE);
		} else {
			resultRowV.setTitle("", View.GONE);
		}
		
		return resultRowV;
	}
}