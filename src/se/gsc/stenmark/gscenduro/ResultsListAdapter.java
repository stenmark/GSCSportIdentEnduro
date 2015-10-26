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
		
		String name = "";
		String startNumber = "";
		String team = "";
		String time = "";
		String timeBack = "";
		
		for(int i = 0; i < mResult.get(position).getStageResult().size(); i++) {
			int rank = mResult.get(position).getStageResult().get(i).getRank();			
			if (rank == Competition.RANK_DNF) {			
				name += "-. ";
			} else {
				name += rank + ". ";
			}		
			Competitor currentCompetitor = ((MainActivity)        mContext).competition.getCompetitors().getByCardNumber(mResult.get(position).getStageResult().get(i).getCardNumber());
			name += currentCompetitor.getName() + "\n";
			startNumber += currentCompetitor.getStartNumber() + "\n";
			team += currentCompetitor.getTeam() + "\n";
					
			time += AndroidIndependantCompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTime()) + "\n";
			timeBack += AndroidIndependantCompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTimesBack()) + "\n";			
		}
				
		resultRowV.setResultName(name);
		resultRowV.setResultStartNumber(startNumber);
		resultRowV.setResultTeam(team);
		resultRowV.setResultTime(time);
		resultRowV.setResultTimeBack(timeBack);	
		
		resultRowV.setPosition(position);

		return resultRowV;
	}
}