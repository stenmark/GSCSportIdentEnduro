package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;



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
		
		ResultRowV.setTitle(mResult.get(position).getTitle());
		
		String Rank = "";
		String Name = "";
		String Time = "";
		String TimeBack = "";
		
		for(int i = 0; i < mResult.get(position).getTrackResult().size(); i++)
		{
			Rank += Integer.toString((i + 1)) +"\n";
			Name += mResult.get(position).getTrackResult().get(i).getName() + "\n";	
			Time += ((MainActivity)mContext).competition.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimes()) + "\n";
			TimeBack += ((MainActivity)mContext).competition.secToMinSec(mResult.get(position).getTrackResult().get(i).getTrackTimesBack()) + "\n";
			
		}
		
		ResultRowV.setResultRank(Rank);		
		ResultRowV.setResultName(Name);
		ResultRowV.setResultTime(Time);
		ResultRowV.setResultTimeBack(TimeBack);	
		
		ResultRowV.setPosition(position);

		return ResultRowV;
	}
}