package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.content.Context;
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
		ResultRowView ResultRowV = null;

		if (convertView == null) {
			ResultRowV = new ResultRowView(mContext);
		} else {
			ResultRowV = (ResultRowView) convertView;
		}
		
		String track = "";
		if (position != 0)
		{
			track = " (" + ((MainActivity) mContext).competition.getTrack().get(position - 1).start + " -> " + ((MainActivity) mContext).competition.getTrack().get(position - 1).finish + ")";
		}
		
		ResultRowV.setTitle(mResult.get(position).getTitle() + track);
		
		String Name = "";
		String Time = "";
		String TimeBack = "";
		
		for(int i = 0; i < mResult.get(position).getTrackResult().size(); i++)
		{
			Name += Integer.toString((i + 1)) + ". " + mResult.get(position).getTrackResult().get(i).getName() + "\n";	
			Time += CompetitionHelper.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimes()) + "\n";
			TimeBack += CompetitionHelper.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimesBack()) + "\n";
			
		}
				
		ResultRowV.setResultName(Name);
		ResultRowV.setResultTime(Time);
		ResultRowV.setResultTimeBack(TimeBack);	
		
		ResultRowV.setPosition(position);

		return ResultRowV;
	}
}