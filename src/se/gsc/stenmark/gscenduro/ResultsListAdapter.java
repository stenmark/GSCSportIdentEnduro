package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResultsListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Results> mResult = new ArrayList<Results>();

	public ResultsListAdapter(Context context, List<Results> Items) {
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
		
		String Name = "";
		String StartNumber = "";
		String Team = "";
		String Time = "";
		String TimeBack = "";
		
		for(int i = 0; i < mResult.get(position).getStageResult().size(); i++) {
			int rank = mResult.get(position).getStageResult().get(i).getRank();			
			if (rank == Competition.RANK_DNF) {			
				Name += "-. ";
			} else {
				Name += rank + ". ";
			}			
			Name += ((MainActivity) mContext).competition.getCompetitors().getByCardNumber(mResult.get(position).getStageResult().get(i).getCardNumber()).getName() + "\n";
			StartNumber += ((MainActivity) mContext).competition.getCompetitors().getByCardNumber(mResult.get(position).getStageResult().get(i).getCardNumber()).getStartNumber() + "\n";
			Team += ((MainActivity) mContext).competition.getCompetitors().getByCardNumber(mResult.get(position).getStageResult().get(i).getCardNumber()).getTeam() + "\n";
					
			Time += CompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTime()) + "\n";
			TimeBack += CompetitionHelper.secToMinSec(mResult.get(position).getStageResult().get(i).getStageTimesBack()) + "\n";			
		}
				
		resultRowV.setResultName(Name);
		resultRowV.setResultStartNumber(StartNumber);
		resultRowV.setResultTeam(Team);
		resultRowV.setResultTime(Time);
		resultRowV.setResultTimeBack(TimeBack);	
		
		resultRowV.setPosition(position);

		return resultRowV;
	}
}