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
	private TreeMap<String, ArrayList<Stage>> stagesForAllClasses;
	private TreeMap<String,Stage> totalResultsForAllClasses;

	public ResultsListAdapter(Context context, TreeMap<String, ArrayList<Stage>> stages, TreeMap<String,Stage> totalResults ) {
		mContext = context;
		stagesForAllClasses = stages;		
		totalResultsForAllClasses = totalResults;
	}	

	@Override
	public int getCount() {
		int size = 0;
		try{
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
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}

		return size;
	}

	@Override
	public Stage getItem(int position) {
		return concatAllClasses().get(position);
	}

	private List<Stage> concatAllClasses(){

		List<Stage> stages = new ArrayList<Stage>();
		try{
			for( String compClass : totalResultsForAllClasses.keySet() ){
				stages.add( totalResultsForAllClasses.get(compClass));
			}
			for( String compClass : stagesForAllClasses.keySet() ){
				stages.addAll( stagesForAllClasses.get(compClass));
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
		return stages;
	}

	public TreeMap<String, ArrayList<Stage>> getData() {
		return stagesForAllClasses;
	}

	public void updateResult () {
		try{
			stagesForAllClasses = ((MainActivity) mContext).competition.getStagesForAllClasses();
			totalResultsForAllClasses = ((MainActivity) mContext).competition.getTotalResultsForAllClasses();
			this.notifyDataSetChanged();	
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}	

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ResultsRowView resultRowV = null;
		try{

			if (convertView == null) {
				resultRowV = new ResultsRowView(mContext);
			} else {
				resultRowV = (ResultsRowView) convertView;
			}


			int index = 0;
			int stageNumber = 0;

			for ( Stage stage : concatAllClasses() )
			{

				if (index + stage.getCompetitorResults().size() > position)
				{
					index = position - index;
					break;
				}	


				index += stage.getCompetitorResults().size();
				stageNumber++;
			}

			Stage stage = concatAllClasses().get(stageNumber);
			String name; 

			long rank =stage.getCompetitorResults().get(index).getRank();	
			if (rank == Competition.RANK_DNF) {			
				name = "-. ";
			} else {
				name = rank + ". ";
			}		
			Competitor currentCompetitor = ((MainActivity)mContext).competition.getCompetitors().getByCardNumber(stage.getCompetitorResults().get(index).getCardNumber());
			name += currentCompetitor.getName();
			String startNumber = String.valueOf(currentCompetitor.getStartNumber());
			String team = currentCompetitor.getTeam();
			String time = CompetitionHelper.milliSecToMinSecMilliSec(stage.getCompetitorResults().get(index).getStageTime(), currentCompetitor.hasCardBeenRead());
			String timeBack = CompetitionHelper.milliSecToMinSecMilliSec(stage.getCompetitorResults().get(index).getStageTimesBack(), currentCompetitor.hasCardBeenRead());			

			resultRowV.setResultName(name);
			resultRowV.setResultStartNumber(startNumber);
			resultRowV.setResultTeam(team);
			resultRowV.setResultTime(time);
			resultRowV.setResultTimeBack(timeBack);	

			if (index == 0) {
				resultRowV.setTitle(stage.title, View.VISIBLE);
			} else {
				resultRowV.setTitle("", View.GONE);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}

		return resultRowV;
	}
}