package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultRowView extends LinearLayout {
	
	Context mContext;
	TextView mTitle;
	TextView mResultName;
	TextView mResultTime;
	TextView mResultTimeBack;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;

		mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_row, this);

		mTitle = (TextView) mCompoundView.findViewById(R.id.result_title);
		mResultName = (TextView) mCompoundView.findViewById(R.id.result_name);
		mResultTime = (TextView) mCompoundView.findViewById(R.id.result_time);
		mResultTimeBack = (TextView) mCompoundView.findViewById(R.id.result_time_back);
	}

	public ResultRowView(Context context) {
		super(context);
		Init(context);
	}

	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setPosition(int Position) {
		mPosition = Position;
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
