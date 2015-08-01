package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsLandscapeRowView extends LinearLayout {
	
	Context mContext;	
	
	LinearLayout mResultLandscapeTitle;
	LinearLayout mResultLandscapeComp;
	
	TextView mResultLandscapeStartNumberTitle;
	TextView mResultLandscapeTeamTitle;
	
	TextView mResultLandscapeCompetitorClass;
	
	TextView mResultLandscapeName;
	TextView mResultLandscapeStartNumber;
	TextView mResultLandscapeTeam;
	TextView mResultLandscapeTotalTime;
	
	TextView mResultLandscapeStageTitle1;
	TextView mResultLandscapeStageTitle2;
	TextView mResultLandscapeStageTitle3;
	TextView mResultLandscapeStageTitle4;
	TextView mResultLandscapeStageTitle5;
	TextView mResultLandscapeStageTitle6;
	TextView mResultLandscapeStageTitle7;
	TextView mResultLandscapeStageTitle8;
	TextView mResultLandscapeStageTitle9;
	TextView mResultLandscapeStageTitle10;
	
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
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_landscape_row, this);
		
		mResultLandscapeStartNumberTitle = (TextView) mCompoundView.findViewById(R.id.result_landscape_startnumber_title);
		mResultLandscapeTeamTitle = (TextView) mCompoundView.findViewById(R.id.result_landscape_team_title);
		
		mResultLandscapeCompetitorClass = (TextView) mCompoundView.findViewById(R.id.result_landscape_competitor_class);
		mResultLandscapeStartNumber = (TextView) mCompoundView.findViewById(R.id.result_landscape_start_number);
		mResultLandscapeTeam = (TextView) mCompoundView.findViewById(R.id.result_landscape_team);
		
		if (((MainActivity) mContext).competition.getCompetitionType() == ((MainActivity) mContext).competition.SVARTVITT_TYPE) {		
			mResultLandscapeCompetitorClass.setVisibility(View.GONE);
			mResultLandscapeStartNumber.setVisibility(View.GONE);
			mResultLandscapeTeam.setVisibility(View.GONE);
			mResultLandscapeStartNumberTitle.setVisibility(View.GONE);
			mResultLandscapeTeamTitle.setVisibility(View.GONE);
		} else {
			mResultLandscapeCompetitorClass.setVisibility(View.VISIBLE);
			mResultLandscapeStartNumber.setVisibility(View.VISIBLE);
			mResultLandscapeTeam.setVisibility(View.VISIBLE);		
			mResultLandscapeStartNumberTitle.setVisibility(View.VISIBLE);
			mResultLandscapeTeamTitle.setVisibility(View.VISIBLE);
		}
		mResultLandscapeTitle = (LinearLayout) mCompoundView.findViewById(R.id.result_landscape_title);
		mResultLandscapeComp = (LinearLayout) mCompoundView.findViewById(R.id.result_landscape_comp);
		
		mResultLandscapeName = (TextView) mCompoundView.findViewById(R.id.result_landscape_name);		
		mResultLandscapeTotalTime = (TextView) mCompoundView.findViewById(R.id.result_landscape_total_time);
		
		mResultLandscapeStageTitle1 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss1_title);
		mResultLandscapeStageTitle2 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss2_title);
		mResultLandscapeStageTitle3 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss3_title);
		mResultLandscapeStageTitle4 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss4_title);
		mResultLandscapeStageTitle5 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss5_title);
		mResultLandscapeStageTitle6 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss6_title);
		mResultLandscapeStageTitle7 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss7_title);
		mResultLandscapeStageTitle8 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss8_title);
		mResultLandscapeStageTitle9 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss9_title);
		mResultLandscapeStageTitle10 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss10_title);
		
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

	public ResultsLandscapeRowView(Context context) {
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
	
	public void setTitle(int numberOfStages) {
		if (mResultLandscapeTitle != null) {
			mResultLandscapeTitle.setVisibility(VISIBLE);
		}
		
		switch(numberOfStages) {
		case 10:
			if (mResultLandscapeStageTitle10 != null) {
				mResultLandscapeStageTitle10.setVisibility(VISIBLE);
			}
			//no break
		case 9:
			if (mResultLandscapeStageTitle9 != null) {
				mResultLandscapeStageTitle9.setVisibility(VISIBLE);
			}
			//no break
		case 8:
			if (mResultLandscapeStageTitle8 != null) {
				mResultLandscapeStageTitle8.setVisibility(VISIBLE);
			}
			//no break
		case 7:
			if (mResultLandscapeStageTitle7 != null) {
				mResultLandscapeStageTitle7.setVisibility(VISIBLE);
			}
			//no break
		case 6:
			if (mResultLandscapeStageTitle6 != null) {
				mResultLandscapeStageTitle6.setVisibility(VISIBLE);
			}
			//no break
		case 5:
			if (mResultLandscapeStageTitle5 != null) {
				mResultLandscapeStageTitle5.setVisibility(VISIBLE);
			}
			//no break
		case 4:
			if (mResultLandscapeStageTitle4 != null) {
				mResultLandscapeStageTitle4.setVisibility(VISIBLE);
			}
			//no break			
		case 3:
			if (mResultLandscapeStageTitle3 != null) {
				mResultLandscapeStageTitle3.setVisibility(VISIBLE);
			}
			//no break
		case 2:
			if (mResultLandscapeStageTitle2 != null) {
				mResultLandscapeStageTitle2.setVisibility(VISIBLE);
			}
			//no break
		case 1:
			if (mResultLandscapeStageTitle1 != null) {
				mResultLandscapeStageTitle1.setVisibility(VISIBLE);
			}
			break;
		}
	}
	
	public void clearTitle() {
		if (mResultLandscapeTitle != null) {
			mResultLandscapeTitle.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle1 != null) {
			mResultLandscapeStageTitle1.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle2 != null) {
			mResultLandscapeStageTitle2.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle3 != null) {
			mResultLandscapeStageTitle3.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle4 != null) {
			mResultLandscapeStageTitle4.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle5 != null) {
			mResultLandscapeStageTitle5.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle6 != null) {
			mResultLandscapeStageTitle6.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle7 != null) {
			mResultLandscapeStageTitle7.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle8 != null) {
			mResultLandscapeStageTitle8.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle9 != null) {
			mResultLandscapeStageTitle9.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTitle10 != null) {
			mResultLandscapeStageTitle10.setVisibility(GONE);
		}		
	}	

	public void setTimes(int numberOfStages) {
		switch(numberOfStages) {
		case 10:
			if (mResultLandscapeStageTime10 != null) {
				mResultLandscapeStageTime10.setVisibility(VISIBLE);
			}
			//no break
		case 9:
			if (mResultLandscapeStageTime9 != null) {
				mResultLandscapeStageTime9.setVisibility(VISIBLE);
			}
			//no break
		case 8:
			if (mResultLandscapeStageTime8 != null) {
				mResultLandscapeStageTime8.setVisibility(VISIBLE);
			}
			//no break
		case 7:
			if (mResultLandscapeStageTime7 != null) {
				mResultLandscapeStageTime7.setVisibility(VISIBLE);
			}
			//no break
		case 6:
			if (mResultLandscapeStageTime6 != null) {
				mResultLandscapeStageTime6.setVisibility(VISIBLE);
			}
			//no break
		case 5:
			if (mResultLandscapeStageTime5 != null) {
				mResultLandscapeStageTime5.setVisibility(VISIBLE);
			}
			//no break
		case 4:
			if (mResultLandscapeStageTime4 != null) {
				mResultLandscapeStageTime4.setVisibility(VISIBLE);
			}
			//no break			
		case 3:
			if (mResultLandscapeStageTime3 != null) {
				mResultLandscapeStageTime3.setVisibility(VISIBLE);
			}
			//no break
		case 2:
			if (mResultLandscapeStageTime2 != null) {
				mResultLandscapeStageTime2.setVisibility(VISIBLE);
			}
			//no break
		case 1:
			if (mResultLandscapeStageTime1 != null) {
				mResultLandscapeStageTime1.setVisibility(VISIBLE);
			}
			break;
		}
	}	
	
	public void clearTimes() {
		if (mResultLandscapeStageTime1 != null) {
			mResultLandscapeStageTime1.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime2 != null) {
			mResultLandscapeStageTime2.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime3 != null) {
			mResultLandscapeStageTime3.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime4 != null) {
			mResultLandscapeStageTime4.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime5 != null) {
			mResultLandscapeStageTime5.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime6 != null) {
			mResultLandscapeStageTime6.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime7 != null) {
			mResultLandscapeStageTime7.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime8 != null) {
			mResultLandscapeStageTime8.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime9 != null) {
			mResultLandscapeStageTime9.setVisibility(GONE);
		}
		
		if (mResultLandscapeStageTime10 != null) {
			mResultLandscapeStageTime10.setVisibility(GONE);
		}				
	}
	
	public void setComp() {
		if (mResultLandscapeComp != null) {
			mResultLandscapeComp.setVisibility(VISIBLE);
		}
	}
	
	public TextView getStageTimeView(int Time) {
		switch(Time) {
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
			getStageTimeView(Stage).setTextColor(Color.BLACK);
			getStageTimeView(Stage).setBackgroundColor(BackgroundColor);
		}		
	}		
		
	public void setPosition(int Position) {
		mPosition = Position;
	}	
}
