package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListCompetitorAdapter extends BaseAdapter {
	private Context mContext;
	private List<Competitor> mCompetitor = new ArrayList<Competitor>();

	public ListCompetitorAdapter(Context context, List<Competitor> Items) {
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

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompetitorRowView CompetitorRowV = null;

		if (convertView == null) {
			CompetitorRowV = new CompetitorRowView(mContext);
		} else {
			CompetitorRowV = (CompetitorRowView) convertView;
		}

		CompetitorRowV.setName(mCompetitor.get(position).getName());
		CompetitorRowV.setCardNumber(String.valueOf(mCompetitor.get(position).getCardNumber()));
		CompetitorRowV.setPosition(position);

		return CompetitorRowV;
	}
}