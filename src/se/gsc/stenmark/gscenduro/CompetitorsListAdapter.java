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
		try{
			mContext = context;
			mCompetitors = Items;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}		

	@Override
	public int getCount() {
		try{
			return mCompetitors.size();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return 0;
		}
	}

	@Override
	public Competitor getItem(int position) {
		try{
			return mCompetitors.get(position);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return null;
		}
	}

	public Competitors getData() {
		return mCompetitors;
	}

	public void updateCompetitors () {
		try{
			mCompetitors = ((MainActivity) mContext).competition.getCompetitors();
			this.notifyDataSetChanged();	
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	@Override
	public long getItemId(int position) {
		try{
			return position;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return 0L;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompetitorsRowView competitorRowV = null;
		try{
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
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return competitorRowV;
	}
}