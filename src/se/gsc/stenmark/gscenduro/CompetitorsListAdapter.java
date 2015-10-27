package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.Competitors;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CompetitorsListAdapter extends BaseAdapter {
	
	private Context mContext;
	private Competitors mCompetitors = null;

	public CompetitorsListAdapter(Context context, Competitors Items) {
		mContext = context;
		mCompetitors = Items;
	}		
	
	@Override
	public int getCount() {
		return mCompetitors.size();
	}

	@Override
	public Competitor getItem(int position) {
		return mCompetitors.get(position);
	}

	public Competitors getData() {
	    return mCompetitors;
	}
	
	public void updateCompetitors () {
		mCompetitors = ((MainActivity) mContext).competition.getCompetitors();
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
		
		Competitor competitor = mCompetitors.get(position);
		competitorRowV.setPosition(position);
		competitorRowV.setStartNumber(String.valueOf(competitor.getStartNumber()));
		competitorRowV.setName(competitor.getName());
		competitorRowV.setCardNumber(String.valueOf(competitor.getCardNumber()));
		competitorRowV.setTeam(competitor.getTeam());
		competitorRowV.setCompetitorClass(competitor.getCompetitorClass());				
		competitorRowV.setStartGroup(String.valueOf(competitor.getStartGroup()));

		return competitorRowV;
	}
}