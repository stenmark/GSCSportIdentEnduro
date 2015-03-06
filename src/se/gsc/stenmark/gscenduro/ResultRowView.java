package se.gsc.stenmark.gscenduro;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultRowView extends LinearLayout {
	Context mContext;
	ListFragment mListFragment;
	TextView mTitle;
	TextView mResultRank;
	TextView mResultName;
	TextView mResultTime;
	TextView mResultTimeBack;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context, ListFragment listFragment) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;
		mListFragment = listFragment;

		mCompoundView = (LinearLayout) inflater.inflate(
				R.layout.result_row, this);

		mTitle = (TextView) mCompoundView.findViewById(R.id.result_title);
		mResultRank = (TextView) mCompoundView.findViewById(R.id.result_rank);
		mResultName = (TextView) mCompoundView.findViewById(R.id.result_name);
		mResultTime = (TextView) mCompoundView.findViewById(R.id.result_time);
		mResultTimeBack = (TextView) mCompoundView.findViewById(R.id.result_time_back);
	}

	public ResultRowView(Context context, ListFragment listFragment) {
		super(context);
		Init(context, listFragment);
	}

	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setPosition(int Position) {
		mPosition = Position;
	}
	
	public void setResultRank(String ResultRank) {
		if (mResultRank != null) {
			mResultRank.setText(ResultRank);
		}
	}
	
	public void setResultName(String ResultName) {
		if (mResultName != null) {
			mResultName.setText(ResultName);
		}
	}
	
	public void setResultTime(String ResultTime) {
		if (mResultTime != null) {
			mResultTime.setText(ResultTime);
		}
	}
	
	public void setResultTimeBack(String ResultTimeBack) {
		if (mResultTimeBack != null) {
			mResultTimeBack.setText(ResultTimeBack);
		}
	}	
}
