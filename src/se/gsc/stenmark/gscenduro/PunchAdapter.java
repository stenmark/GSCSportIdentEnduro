package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.SporIdent.Punch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PunchAdapter extends BaseAdapter {

	private Context mContext;
	private List<Punch> mPunches = new ArrayList<Punch>();

	public PunchAdapter(Context context, List<Punch> Items) {
		try{
			mContext = context;
			mPunches = Items;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}		

	@Override
	public int getCount() {
		try{
			return mPunches.size();
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return 0;
		}
	}

	@Override
	public Punch getItem(int position) {
		try{
			return mPunches.get(position);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return new Punch(0, 0);
		}
	}

	@Override
	public long getItemId(int position) {
		try{
			return position;
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PunchRowView punchRowV = null;
		try{
			if (convertView == null) {
				punchRowV = new PunchRowView(mContext);
			} else {
				punchRowV = (PunchRowView) convertView;
			}

			punchRowV.setControl(Long.toString(mPunches.get(position).getControl()));		
			punchRowV.setTime(Long.toString(mPunches.get(position).getTime()));
			punchRowV.setPosition(position);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
		return punchRowV;
	}
}