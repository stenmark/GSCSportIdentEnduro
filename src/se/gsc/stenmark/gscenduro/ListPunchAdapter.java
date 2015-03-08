package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Punch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListPunchAdapter extends BaseAdapter {
	private Context mContext;
	private List<Punch> mPunches = new ArrayList<Punch>();

	public ListPunchAdapter(Context context, List<Punch> Items) {
		mContext = context;
		mPunches = Items;
	}		
	
	@Override
	public int getCount() {
		return mPunches.size();
	}

	@Override
	public Punch getItem(int position) {
		return mPunches.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PunchRowView PunchRowV = null;

		if (convertView == null) {
			PunchRowV = new PunchRowView(mContext);
		} else {
			PunchRowV = (PunchRowView) convertView;
		}
		
		PunchRowV.setControl(Long.toString(mPunches.get(position).control));		
		PunchRowV.setTime(Long.toString(mPunches.get(position).time));
		PunchRowV.setPosition(position);
	
		return PunchRowV;
	}
}