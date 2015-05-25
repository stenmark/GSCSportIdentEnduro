package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListResultAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Result> mResult = new ArrayList<Result>();

	public ListResultAdapter(Context context, List<Result> Items) {
		mContext = context;
		mResult = Items;		
	}		
	
	@Override
	public int getCount() {
		return mResult.size();
	}

	@Override
	public Result getItem(int position) {
		return mResult.get(position);
	}

	public List<Result> getData() {
	    return mResult;
	}
	
	public void updateResult ()
	{
		mResult = ((MainActivity) mContext).competition.getResults();
		this.notifyDataSetChanged();	
	}	
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultRowView resultRowV = null;

		if (convertView == null) {
			resultRowV = new ResultRowView(mContext);
		} else {
			resultRowV = (ResultRowView) convertView;
		}
		
		resultRowV.setTitle(mResult.get(position).getTitle());
		
		String Name = "";
		String StartNumber = "";
		String Team = "";
		String Time = "";
		String TimeBack = "";
		
		for(int i = 0; i < mResult.get(position).getTrackResult().size(); i++)
		{
			int rank = mResult.get(position).getTrackResult().get(i).getRank();			
			if (rank == Integer.MAX_VALUE)
			{			
				Name += "-. ";
			} else {
				Name += rank + ". ";
			}			
			Name += ((MainActivity) mContext).competition.getCompetitor(mResult.get(position).getTrackResult().get(i).getCardNumber()).getName() + "\n";
			StartNumber += ((MainActivity) mContext).competition.getCompetitor(mResult.get(position).getTrackResult().get(i).getCardNumber()).getStartNumber() + "\n";
			Team += ((MainActivity) mContext).competition.getCompetitor(mResult.get(position).getTrackResult().get(i).getCardNumber()).getTeam() + "\n";
					
			Time += CompetitionHelper.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimes()) + "\n";
			TimeBack += CompetitionHelper.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimesBack()) + "\n";			
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