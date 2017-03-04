package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
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

	TextView mResultLandscapeStageTitle0;
	TextView mResultLandscapeStageTitle1;
	TextView mResultLandscapeStageTitle2;
	TextView mResultLandscapeStageTitle3;
	TextView mResultLandscapeStageTitle4;
	TextView mResultLandscapeStageTitle5;
	TextView mResultLandscapeStageTitle6;
	TextView mResultLandscapeStageTitle7;
	TextView mResultLandscapeStageTitle8;
	TextView mResultLandscapeStageTitle9;

	TextView mResultLandscapeStageTime0;
	TextView mResultLandscapeStageTime1;
	TextView mResultLandscapeStageTime2;
	TextView mResultLandscapeStageTime3;
	TextView mResultLandscapeStageTime4;
	TextView mResultLandscapeStageTime5;
	TextView mResultLandscapeStageTime6;
	TextView mResultLandscapeStageTime7;
	TextView mResultLandscapeStageTime8;
	TextView mResultLandscapeStageTime9;

	int mPosition;
	LinearLayout mCompoundView;

	protected void init(Context context) {
		try{
			mContext = context;

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
			mCompoundView = (LinearLayout) inflater.inflate(R.layout.result_landscape_row, this);

			mResultLandscapeStartNumberTitle = (TextView) mCompoundView.findViewById(R.id.result_landscape_startnumber_title);
			mResultLandscapeTeamTitle = (TextView) mCompoundView.findViewById(R.id.result_landscape_team_title);

			mResultLandscapeCompetitorClass = (TextView) mCompoundView.findViewById(R.id.result_landscape_competitor_class);
			mResultLandscapeStartNumber = (TextView) mCompoundView.findViewById(R.id.result_landscape_start_number);
			mResultLandscapeTeam = (TextView) mCompoundView.findViewById(R.id.result_landscape_team);

			if (((MainActivity) mContext).competition.getCompetitionType() == Competition.SVART_VIT_TYPE) {		
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

			mResultLandscapeStageTitle0 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss1_title);
			mResultLandscapeStageTitle1 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss2_title);
			mResultLandscapeStageTitle2 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss3_title);
			mResultLandscapeStageTitle3 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss4_title);
			mResultLandscapeStageTitle4 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss5_title);
			mResultLandscapeStageTitle5 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss6_title);
			mResultLandscapeStageTitle6 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss7_title);
			mResultLandscapeStageTitle7 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss8_title);
			mResultLandscapeStageTitle8 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss9_title);
			mResultLandscapeStageTitle9 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss10_title);

			mResultLandscapeStageTime0 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss1_time);
			mResultLandscapeStageTime1 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss2_time);
			mResultLandscapeStageTime2 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss3_time);
			mResultLandscapeStageTime3 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss4_time);
			mResultLandscapeStageTime4 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss5_time);
			mResultLandscapeStageTime5 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss6_time);
			mResultLandscapeStageTime6 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss7_time);
			mResultLandscapeStageTime7 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss8_time);
			mResultLandscapeStageTime8 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss9_time);		
			mResultLandscapeStageTime9 = (TextView) mCompoundView.findViewById(R.id.result_landscape_ss10_time);
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}

	public ResultsLandscapeRowView(Context context) {

		super(context);
		try{
			init(context);
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}

	public void setResultLandscapeCompetitorClass(String CompetitorClass) {
		try{
			if (mResultLandscapeCompetitorClass != null) {
				mResultLandscapeCompetitorClass.setText(CompetitorClass);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}		

	public void setResultLandscapeName(String Name) {
		try{
			if (mResultLandscapeName != null) {
				mResultLandscapeName.setText(Name);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}	

	public void setResultLandscapeStartNumber(String StartNumber) {
		try{
			if (mResultLandscapeStartNumber != null) {
				mResultLandscapeStartNumber.setText(StartNumber);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}		

	public void setResultLandscapeTeam(String Team) {
		try{
			if (mResultLandscapeTeam != null) {
				mResultLandscapeTeam.setText(Team);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}

	}		

	public void setResultLandscapeTotalTime(String TotalTime) {
		try{
			if (mResultLandscapeTotalTime != null) {
				mResultLandscapeTotalTime.setText(TotalTime);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}		

	public void setTitle(int numberOfStages) {
		try{
			if (mResultLandscapeTitle != null) {
				mResultLandscapeTitle.setVisibility(VISIBLE);
			}

			switch(numberOfStages) {
			case 10:
				if (mResultLandscapeStageTitle9 != null) {
					mResultLandscapeStageTitle9.setVisibility(VISIBLE);
				}
				//no break
			case 9:
				if (mResultLandscapeStageTitle8 != null) {
					mResultLandscapeStageTitle8.setVisibility(VISIBLE);
				}
				//no break
			case 8:
				if (mResultLandscapeStageTitle7 != null) {
					mResultLandscapeStageTitle7.setVisibility(VISIBLE);
				}
				//no break
			case 7:
				if (mResultLandscapeStageTitle6 != null) {
					mResultLandscapeStageTitle6.setVisibility(VISIBLE);
				}
				//no break
			case 6:
				if (mResultLandscapeStageTitle5 != null) {
					mResultLandscapeStageTitle5.setVisibility(VISIBLE);
				}
				//no break
			case 5:
				if (mResultLandscapeStageTitle4 != null) {
					mResultLandscapeStageTitle4.setVisibility(VISIBLE);
				}
				//no break
			case 4:
				if (mResultLandscapeStageTitle3 != null) {
					mResultLandscapeStageTitle3.setVisibility(VISIBLE);
				}
				//no break			
			case 3:
				if (mResultLandscapeStageTitle2 != null) {
					mResultLandscapeStageTitle2.setVisibility(VISIBLE);
				}
				//no break
			case 2:
				if (mResultLandscapeStageTitle1 != null) {
					mResultLandscapeStageTitle1.setVisibility(VISIBLE);
				}
				//no break
			case 1:
				if (mResultLandscapeStageTitle0 != null) {
					mResultLandscapeStageTitle0.setVisibility(VISIBLE);
				}
				break;
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}

	public void clearTitle() {
		try{
			if (mResultLandscapeTitle != null) {
				mResultLandscapeTitle.setVisibility(GONE);
			}

			if (mResultLandscapeStageTitle0 != null) {
				mResultLandscapeStageTitle0.setVisibility(GONE);
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
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}	

	public void setTimes(int numberOfStages) {
		try{
			switch(numberOfStages) {
			case 10:
				if (mResultLandscapeStageTime9 != null) {
					mResultLandscapeStageTime9.setVisibility(VISIBLE);
				}
				//no break
			case 9:
				if (mResultLandscapeStageTime8 != null) {
					mResultLandscapeStageTime8.setVisibility(VISIBLE);
				}
				//no break
			case 8:
				if (mResultLandscapeStageTime7 != null) {
					mResultLandscapeStageTime7.setVisibility(VISIBLE);
				}
				//no break
			case 7:
				if (mResultLandscapeStageTime6 != null) {
					mResultLandscapeStageTime6.setVisibility(VISIBLE);
				}
				//no break
			case 6:
				if (mResultLandscapeStageTime5 != null) {
					mResultLandscapeStageTime5.setVisibility(VISIBLE);
				}
				//no break
			case 5:
				if (mResultLandscapeStageTime4 != null) {
					mResultLandscapeStageTime4.setVisibility(VISIBLE);
				}
				//no break
			case 4:
				if (mResultLandscapeStageTime3 != null) {
					mResultLandscapeStageTime3.setVisibility(VISIBLE);
				}
				//no break			
			case 3:
				if (mResultLandscapeStageTime2 != null) {
					mResultLandscapeStageTime2.setVisibility(VISIBLE);
				}
				//no break
			case 2:
				if (mResultLandscapeStageTime1 != null) {
					mResultLandscapeStageTime1.setVisibility(VISIBLE);
				}
				//no break
			case 1:
				if (mResultLandscapeStageTime0 != null) {
					mResultLandscapeStageTime0.setVisibility(VISIBLE);
				}
				break;
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}	

	public void clearTimes() {
		try{
			if (mResultLandscapeStageTime0 != null) {
				mResultLandscapeStageTime0.setVisibility(GONE);
			}

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
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}

	public void setComp() {
		try{
			if (mResultLandscapeComp != null) {
				mResultLandscapeComp.setVisibility(VISIBLE);
			}
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}

	public TextView getStageTimeView(int Time) {
		try{
			switch(Time) {
			case 0:
				return mResultLandscapeStageTime0;
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
			}
			return null;
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
			return null;
		}
	}

	public void setResultLandscapeStageTime(int Stage, String Time, int BackgroundColor) {	
		try{
			if (getStageTimeView(Stage) != null) {		
				getStageTimeView(Stage).setText(Time);
				getStageTimeView(Stage).setTextColor(Color.BLACK);
				getStageTimeView(Stage).setBackgroundColor(BackgroundColor);
			}	
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}		

	public void setPosition(int Position) {
		try{
			mPosition = Position;
		}
		catch( Exception e){
			MainActivity.generateErrorMessage(e);
		}
	}	
}
