package se.gsc.stenmark.gscenduro;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultRowView extends LinearLayout {
	Context mContext;
	ListFragment mListFragment;
	TextView mName;
	TextView mTrackTime;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context, ListFragment listFragment) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;
		mListFragment = listFragment;

		mCompoundView = (LinearLayout) inflater.inflate(
				R.layout.result_row, this);

		mName = (TextView) mCompoundView.findViewById(R.id.result_name);
		mTrackTime = (TextView) mCompoundView.findViewById(R.id.result_tracktime);
	}

	public ResultRowView(Context context, ListFragment listFragment) {
		super(context);
		Init(context, listFragment);
	}

	public void setName(String Name) {
		if (mName != null) {
			mName.setText(Name);
		}
	}

	public void setPosition(int Position) {
		mPosition = Position;
	}
	
	public void setTrackTime(String TrackTime) {
		if (mTrackTime != null) {
			mTrackTime.setText(TrackTime);
		}
	}
}
