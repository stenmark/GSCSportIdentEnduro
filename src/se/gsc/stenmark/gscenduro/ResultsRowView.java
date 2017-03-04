package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsRowView extends LinearLayout {

	private Context mContext;
	private TextView mTitle;
	private LinearLayout mHeader;

	private TextView mResultName;
	private TextView mResultStartNumberTitle;
	private TextView mResultTeamTitle;

	private TextView mResultStartNumber;
	private TextView mResultTeam;
	private TextView mResultTime;
	private TextView mResultTimeBack;
	private LinearLayout mCompoundView;

	protected void Init(Context context) {
		try{
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
			mHeader = (LinearLayout) mCompoundView.findViewById(R.id.result_header);
			mResultName = (TextView) mCompoundView.findViewById(R.id.result_name);
			mResultTime = (TextView) mCompoundView.findViewById(R.id.result_time);
			mResultTimeBack = (TextView) mCompoundView.findViewById(R.id.result_time_back);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public ResultsRowView(Context context) {
		super(context);
		Init(context);
	}

	public void setTitle(String Title, int visibility) {
		try{
			if (mTitle != null) {
				mTitle.setText(Title);
			}

			mHeader.setVisibility(visibility);
			mTitle.setVisibility(visibility);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void setResultName(String ResultName) {
		try{
			if (mResultName != null) {
				mResultName.setText(ResultName);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void setResultStartNumber(String ResultStartNumber) {
		try{
			if (mResultStartNumber != null) {
				mResultStartNumber.setText(ResultStartNumber);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}


	public void setResultTeam(String ResultTeam) {
		try{
			if (mResultTeam != null) {
				mResultTeam.setText(ResultTeam);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}	

	public void setResultTime(String ResultTime) {
		try{
			if (mResultTime != null) {
				mResultTime.setText(ResultTime);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	public void setResultTimeBack(String ResultTimeBack) {
		try{
			if (mResultTimeBack != null) {
				mResultTimeBack.setText(ResultTimeBack);
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}	
}
