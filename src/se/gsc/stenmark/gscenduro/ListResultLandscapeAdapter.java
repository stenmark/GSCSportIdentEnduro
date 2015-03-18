package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListResultLandscapeAdapter extends BaseAdapter {
	private Context mContext;
	private List<ResultLandscape> mResultLandscape = new ArrayList<ResultLandscape>();

	public ListResultLandscapeAdapter(Context context, List<ResultLandscape> Items) {
		mContext = context;
		mResultLandscape = Items;		
	}		
	
	@Override
	public int getCount() {
		return mResultLandscape.size();
	}
	
	@Override
	public ResultLandscape getItem(int position) {
		return mResultLandscape.get(position);
	}

	public List<ResultLandscape> getData() {
	    return mResultLandscape;
	}	
	
	public void updateResultLandscape (List<ResultLandscape> Items)
	{
		mResultLandscape = Items;
		this.notifyDataSetChanged();	
	}		
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultLandscapeRowView ResultLandscapeRowV = null;	
		
		if (convertView == null) {
			ResultLandscapeRowV = new ResultLandscapeRowView(mContext);
		} else {
			ResultLandscapeRowV = (ResultLandscapeRowView) convertView;
		}

		if (position == 0)
		{
			ResultLandscapeRowV.setTitle();
		}
			
		ResultLandscapeRowV.setComp();
		
		ResultLandscapeRowV.setResultLandscapeName(String.valueOf(position + 1) + ". " + mResultLandscape.get(position).getName());
		
		if (mResultLandscape.get(position).getTotalTime() == Integer.MAX_VALUE)
		{
			ResultLandscapeRowV.setResultLandscapeTotalTime("--:--");
		}
		else
		{
			String TotalTime = "";
			if (mResultLandscape.get(position).getTotalTime() == 0)
			{
				TotalTime = "--:--";
			}
			else
			{
				
				TotalTime = CompetitionHelper.secToMinSec(mResultLandscape.get(position).getTotalTime());
				
			}	
			ResultLandscapeRowV.setResultLandscapeTotalTime(TotalTime);
		}
		
		for(int i = 0; i < mResultLandscape.get(position).getRank().size(); i++) 
		{
			/*
			#00FF00 green
			#FF0000 red
			*/
				
			String StageTime = "";
			if (mResultLandscape.get(position).getTime().get(i) == Integer.MAX_VALUE)
			{
				StageTime = "--:--";
			}
			else
			{
				StageTime = CompetitionHelper.secToMinSec(mResultLandscape.get(position).getTime().get(i));
			}			
			
			int color;
			String StageRank = "";
			if (mResultLandscape.get(position).getRank().get(i) == (long) Integer.MAX_VALUE)
			{
				StageRank = "-";
				color = Color.RED;
			}
			else
			{
				StageRank = mResultLandscape.get(position).getRank().get(i).toString();									
						
				int diff = (((0xFF00 - 0x00FF) / ((MainActivity)mContext).competition.getCompetitors().size()));
				
				color = 0xFF000000 + diff * mResultLandscape.get(position).getRank().get(i) * 0x100;								
			}			
			
			ResultLandscapeRowV.setResultLandscapeStageTime(i + 1, StageTime + " (" + StageRank + ")", color);
		}
		
		
		ResultLandscapeRowV.setPosition(position);
		
		return ResultLandscapeRowV;
	}
}