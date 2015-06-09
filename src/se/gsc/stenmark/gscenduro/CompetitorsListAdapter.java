package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CompetitorsListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Competitor> mCompetitor = new ArrayList<Competitor>();

	public CompetitorsListAdapter(Context context, List<Competitor> Items) {
		mContext = context;
		mCompetitor = Items;
	}		
	
	@Override
	public int getCount() {
		return mCompetitor.size();
	}

	@Override
	public Competitor getItem(int position) {
		return mCompetitor.get(position);
	}

	public List<Competitor> getData() {
	    return mCompetitor;
	}
	
	public void updateCompetitors () {
		mCompetitor = ((MainActivity) mContext).competition.getCompetitors().getCompetitors();
		this.notifyDataSetChanged();	
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompetitorsRowView competitorRowV = null;

		if (convertView == null) {
			competitorRowV = new CompetitorsRowView(mContext, this);
		} else {
			competitorRowV = (CompetitorsRowView) convertView;
		}
		
		competitorRowV.setPosition(position);
		competitorRowV.setStartNumber(String.valueOf(mCompetitor.get(position).getStartNumber()));
		competitorRowV.setName(mCompetitor.get(position).getName());
		competitorRowV.setCardNumber(String.valueOf(mCompetitor.get(position).getCardNumber()));
		competitorRowV.setTeam(mCompetitor.get(position).getTeam());
		competitorRowV.setCompetitorClass(mCompetitor.get(position).getCompetitorClass());				
		competitorRowV.setStartGroup(String.valueOf(mCompetitor.get(position).getStartGroup()));

		return competitorRowV;
	}
}