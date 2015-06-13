package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResultsListLandscapeAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Results> mResultLandscape = new ArrayList<Results>();

	public ResultsListLandscapeAdapter(Context context, List<Results> Items) {
		mContext = context;
		mResultLandscape = Items;		
	}		
	
	@Override
	public int getCount() {
		return mResultLandscape.size();
	}
	
	@Override
	public Results getItem(int position) {
		return mResultLandscape.get(position);
	}

	public List<Results> getData() {
	    return mResultLandscape;
	}	
	
	public void updateResultLandscape ()
	{
		mResultLandscape = ((MainActivity) mContext).competition.getResultLandscape();
		this.notifyDataSetChanged();	
	}		
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{			
			ResultsLandscapeRowView resultLandscapeRowV = null;	
			
			if (convertView == null) {
				resultLandscapeRowV = new ResultsLandscapeRowView(mContext);
			} else {
				resultLandscapeRowV = (ResultsLandscapeRowView) convertView;
			}
	
			if (position == 0) {
				resultLandscapeRowV.setTitle();
				resultLandscapeRowV.setResultLandscapeCompetitorClass(mResultLandscape.get(position).getTitle());
			} else {
				if (mResultLandscape.get(position).getTitle() == mResultLandscape.get(position - 1).getTitle()) {
					resultLandscapeRowV.clearTitle();
				} else {
					resultLandscapeRowV.setTitle();	
					resultLandscapeRowV.setResultLandscapeCompetitorClass(mResultLandscape.get(position).getTitle());
				}
			}
				
			resultLandscapeRowV.setComp();
			int cardNumber = mResultLandscape.get(position).getStageResult().get(0).getCardNumber();
			
			int rank = mResultLandscape.get(position).getStageResult().get(0).getRank();
			String name = "";
			if (rank == Integer.MAX_VALUE) {			
				name += "-. ";
			} else {
				name += rank + ". ";
			}
			
			name += ((MainActivity) mContext).competition.getCompetitors().getByCardNumber(cardNumber).getName();
			resultLandscapeRowV.setResultLandscapeName(name);
			
			String startNumber = String.valueOf(((MainActivity) mContext).competition.getCompetitors().getByCardNumber(cardNumber).getStartNumber());	
			resultLandscapeRowV.setResultLandscapeStartNumber(startNumber);			
			
			String team = ((MainActivity) mContext).competition.getCompetitors().getByCardNumber(cardNumber).getTeam();	
			resultLandscapeRowV.setResultLandscapeTeam(team);
			
			long totalTime = mResultLandscape.get(position).getStageResult().get(0).getStageTimes();			
			resultLandscapeRowV.setResultLandscapeTotalTime(CompetitionHelper.secToMinSec(totalTime));				
			
			//First clear all stages
			for(int stageNumber = 1; stageNumber < 11; stageNumber++) {
				resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, "", Color.WHITE);
			}
			
			for(int stageNumber = 1; stageNumber < mResultLandscape.get(position).getStageResult().size(); stageNumber++) {
				
				Long fastestTimeOnStage = ((MainActivity) mContext).competition.getCompetitors().getFastestOnStage(mResultLandscape.get(position).getTitle(), stageNumber); 
				Long slowestTimeOnStage = ((MainActivity) mContext).competition.getCompetitors().getSlowestOnStage(mResultLandscape.get(position).getTitle(), stageNumber);
				String StageTime = CompetitionHelper.secToMinSec(mResultLandscape.get(position).getStageResult().get(stageNumber).getStageTimes());
				
				rank = mResultLandscape.get(position).getStageResult().get(stageNumber).getRank();
				int color;
				if (rank == Integer.MAX_VALUE) {
					StageTime = "";
					color = Color.WHITE;
				} else {
					StageTime += "\n(" + String.valueOf(rank) + ")";											
					Long competitorStageTime = mResultLandscape.get(position).getStageResult().get(stageNumber).getStageTimes();		
					float myTimeDiff = competitorStageTime - fastestTimeOnStage;
					float stageTimeDiff = slowestTimeOnStage - fastestTimeOnStage;
					color = generateRedToGreenColorTransition(1f - (myTimeDiff / stageTimeDiff));	  //1.0 => red, 0.0 => green	
				}			
				
				resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, StageTime, color);
			}			
			
			resultLandscapeRowV.setPosition(position);
			
			return resultLandscapeRowV;
		} catch(Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			return new ResultsLandscapeRowView(mContext);
		}
	}
	
	/**
	 * Generate an RGB value for a transition from Red to Green.
	 * @param value 0.0 equals 100% red. 1.0 equals 100% green
	 * @return RGB coded color
	 */
	private int generateRedToGreenColorTransition(float value){
		float hue = 20f + (value*70f);  //the full red and full green are very close to each other for the eye. So dont use full red and full green
	    return android.graphics.Color.HSVToColor(new float[]{hue,1f,1f});
	}
}