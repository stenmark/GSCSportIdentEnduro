package se.gsc.stenmark.gscenduro;

import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.AndroidIndependantCompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.ResultList;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResultsListAdapter extends BaseAdapter {
	
	private Context mContext;
	private ResultList<Results> mResult = new ResultList<Results>();

	public ResultsListAdapter(Context context, ResultList<Results> Items) {
		mContext = context;
		mResult = Items;		
	}		
	
	@Override
	public int getCount() {
		return mResult.size();
	}

	@Override
	public Results getItem(int position) {
		return mResult.get(position);
	}

	public List<Results> getData() {
	    return mResult;
	}
	
	public void updateResult () {
		mResult = ((MainActivity) mContext).competition.getResults();
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
				
		resultRowV.setResultName(name.toString());
		resultRowV.setResultStartNumber(startNumber.toString());
		resultRowV.setResultTeam(team.toString());
		resultRowV.setResultTime(time.toString());
		resultRowV.setResultTimeBack(timeBack.toString());	
		
		return resultRowV;
	}
}