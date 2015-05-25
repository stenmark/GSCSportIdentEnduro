package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultLandscapeRowView extends LinearLayout {
	
	Context mContext;	
	
	LinearLayout mResultLandscapeTitle;
	LinearLayout mResultLandscapeComp;
	
	TextView mResultLandscapeCompetitorClass;
	
	TextView mResultLandscapeName;
	TextView mResultLandscapeStartNumber;
	TextView mResultLandscapeTeam;
	TextView mResultLandscapeTotalTime;
	
	TextView mResultLandscapeStageTime1;
	TextView mResultLandscapeStageTime2;
	TextView mResultLandscapeStageTime3;
	TextView mResultLandscapeStageTime4;
	TextView mResultLandscapeStageTime5;
	TextView mResultLandscapeStageTime6;
	TextView mResultLandscapeStageTime7;
	TextView mResultLandscapeStageTime8;
	TextView mResultLandscapeStageTime9;
	TextView mResultLandscapeStageTime10;
	
	int mPosition;
	LinearLayout mCompoundView;

	protected void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mContext = context;
		
		if (((MainActivity) mContext).competition.getCompetitionType() == ((MainActivity) mContext).competition.SVARTVITT_TYPE)
		{		
			mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_landscape_row, this);
		}
		else
		{
			mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_landscape_ess_row, this);
			mResultLandscapeCompetitorClass = (TextView) mCompoundView.findViewById(R.id.result_landscape_competitor_class);
			mResultLandscapeStartNumber = (TextView) mCompoundView.findViewById(R.id.result_landscape_start_number);
			mResultLandscapeTeam = (TextView) mCompoundView.findViewById(R.id.result_landscape_team);
		}
		mResultLandscapeTitle = (LinearLayout) mCompoundView.findViewById(R.id.result_landscape_title);
		mResultLandscapeComp = (LinearLayout) mCompoundView.findViewById(R.id.result_landscape_comp);
		
		mResultLandscapeName = (TextView) mCompoundView.findViewById(R.id.result_landscape_name);		
		mResultLandscapeTotalTime = (TextView) mCompoundView.findViewById(R.id.result_landscape_total_time);
		
		mResultLandscapeStageTime1 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss1_time);
		mResultLandscapeStageTime2 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss2_time);
		mResultLandscapeStageTime3 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss3_time);
		mResultLandscapeStageTime4 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss4_time);
		mResultLandscapeStageTime5 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss5_time);
		mResultLandscapeStageTime6 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss6_time);
		mResultLandscapeStageTime7 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss7_time);
		mResultLandscapeStageTime8 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss8_time);
		mResultLandscapeStageTime9 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss9_time);		
		mResultLandscapeStageTime10 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss10_time);
	}

	public ResultLandscapeRowView(Context context) {
		super(context);
		init(context);
	}

	public void setResultLandscapeCompetitorClass(String CompetitorClass) {
		if (mResultLandscapeCompetitorClass != null) {
			mResultLandscapeCompetitorClass.setText(CompetitorClass);
		}
	}		
	
	public void setResultLandscapeName(String Name) {
		if (mResultLandscapeName != null) {
			mResultLandscapeName.setText(Name);
		}
	}	

	public void setResultLandscapeStartNumber(String StartNumber) {
		if (mResultLandscapeStartNumber != null) {
			mResultLandscapeStartNumber.setText(StartNumber);
		}
	}		
	
	public void setResultLandscapeTeam(String Team) {
		if (mResultLandscapeTeam != null) {
			mResultLandscapeTeam.setText(Team);
		}
	}		
	
	public void setResultLandscapeTotalTime(String TotalTime) {
		if (mResultLandscapeTotalTime != null) {
			mResultLandscapeTotalTime.setText(TotalTime);
		}
	}		
	
	public void setTitle() {
		if (mResultLandscapeTitle != null) {
			mResultLandscapeTitle.setVisibility(VISIBLE);
		}
	}
	
	public void clearTitle() {
		if (mResultLandscapeTitle != null) {
			mResultLandscapeTitle.setVisibility(GONE);
		}
	}	

	public void setComp() {
		if (mResultLandscapeComp != null) {
			mResultLandscapeComp.setVisibility(VISIBLE);
		}
	}
	
	public TextView getStageTimeView(int Time) {
		switch(Time)
		{
		case 1:
			return mResultLandscapeStageTime1;
		case 2:
			return mResultLandscapeStageTime2;
		case 3:
			return mResultLandscapeStageTime3;
		case 4:
			return mResultLandscapeStageTime4;
		case 5:
			return mResultLandscapeStageTime5;
		case 6:
			return mResultLandscapeStageTime6;
		case 7:
			return mResultLandscapeStageTime7;
		case 8:
			return mResultLandscapeStageTime8;
		case 9:
			return mResultLandscapeStageTime9;
		case 10:
			return mResultLandscapeStageTime10;	
		}
		return null;				
	}

	public void setResultLandscapeStageTime(int Stage, String Time, int BackgroundColor) {		
		if (getStageTimeView(Stage) != null) {		
			getStageTimeView(Stage).setText(Time);
			getStageTimeView(Stage).setVisibility(VISIBLE);
			getStageTimeView(Stage).setTextColor(Color.BLACK);
			getStageTimeView(Stage).setBackgroundColor(BackgroundColor);
		}		
	}		
		
	public void setPosition(int Position) {
		mPosition = Position;
	}	
}
