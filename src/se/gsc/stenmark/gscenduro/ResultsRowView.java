package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsRowView extends LinearLayout {
	
	Context mContext;
	TextView mTitle;
	TextView mResultName;
	TextView mResultStartNumberTitle;
	TextView mResultTeamTitle;
	
	TextView mResultStartNumber;
	TextView mResultTeam;
	TextView mResultTime;
	TextView mResultTimeBack;
	int mPosition;
	LinearLayout mCompoundView;

	protected void Init(Context context) {
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_row, this);
		mResultStartNumberTitle = (TextView) mCompoundView.findViewById(R.id.result_start_number_title);
		mResultTeamTitle = (TextView) mCompoundView.findViewById(R.id.result_team_title);
		mResultStartNumber = (TextView) mCompoundView.findViewById(R.id.result_start_number);
		mResultTeam = (TextView) mCompoundView.findViewById(R.id.result_team);
		
		if (((MainActivity) mContext).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {
			mResultStartNumberTitle.setVisibility(View.GONE);
			mResultTeamTitle.setVisibility(View.GONE);
			mResultStartNumber.setVisibility(View.GONE);
			mResultTeam.setVisibility(View.GONE);
		} else {
			mResultStartNumberTitle.setVisibility(View.VISIBLE);
			mResultTeamTitle.setVisibility(View.VISIBLE);
			mResultStartNumber.setVisibility(View.VISIBLE);
			mResultTeam.setVisibility(View.VISIBLE);
		}
			
		mTitle = (TextView) mCompoundView.findViewById(R.id.result_title);
		mResultName = (TextView) mCompoundView.findViewById(R.id.result_name);
		mResultTime = (TextView) mCompoundView.findViewById(R.id.result_time);
		mResultTimeBack = (TextView) mCompoundView.findViewById(R.id.result_time_back);
	}

	public ResultsRowView(Context context) {
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
	
	public void setResultStartNumber(String ResultStartNumber) {
		if (mResultStartNumber != null) {
			mResultStartNumber.setText(ResultStartNumber);
		}
	}		
	
	public void setResultTeam(String ResultTeam) {
		if (mResultTeam != null) {
			mResultTeam.setText(ResultTeam);
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
