package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListResultAdapter extends BaseAdapter {
	private Context mContext;
	private ListFragment mListFragment;
	private List<Competitor> mCompetitor = new ArrayList<Competitor>();

	public ListResultAdapter(Context context, ListFragment listFragment, List<Competitor> Items) {
		mContext = context;
		mCompetitor = Items;
		mListFragment = listFragment;
	}		
	
	@Override
	public int getCount() {
		return mCompetitor.size();
	}

	@Override
	public Competitor getItem(int position) {
		return mCompetitor.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultRowView ResultRowV = null;
		String TrackTime = "";

		if (convertView == null) {
			ResultRowV = new ResultRowView(mContext, mListFragment);
		} else {
			ResultRowV = (ResultRowView) convertView;
		}

		ResultRowV.setName(Integer.toString(position + 1) + ". " + mCompetitor.get(position).getName());
		
		if (mCompetitor.get(position).hasResult()) {
			int i = 0;
			for (long trackTime : mCompetitor.get(position).trackTimes) {
				i++;
				TrackTime += "SS" + i + ": " + trackTime + "\n";
			}

			TrackTime += "Total time: " + mCompetitor.get(position).getTotalTime(true);

		} else {
			TrackTime = "no result\n";
		}
		
		ResultRowV.setTrackTime(TrackTime);
		ResultRowV.setPosition(position);

		return ResultRowV;
	}
}