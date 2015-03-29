package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;

import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListResultLandscapeAdapter extends BaseAdapter {
	private Context mContext;
	private List<ResultLandscape> mResultLandscape = new ArrayList<ResultLandscape>();

	public ListResultLandscapeAdapter(Context context, List<ResultLandscape> Items) {
		mContext = context;
		mResultLandscape = Items;		
	}		
	
	@Override
	public int getCount() {
		return mResultLandscape.size();
	}
	
	@Override
	public ResultLandscape getItem(int position) {
		return mResultLandscape.get(position);
	}

	public List<ResultLandscape> getData() {
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
	public View getView(int competitionRank, View convertView, ViewGroup parent) {
		try{
			ResultLandscapeRowView ResultLandscapeRowV = null;	
			
			if (convertView == null) {
				ResultLandscapeRowV = new ResultLandscapeRowView(mContext);
			} else {
				ResultLandscapeRowV = (ResultLandscapeRowView) convertView;
			}
	
			if (competitionRank == 0)
			{
				ResultLandscapeRowV.setTitle();
			}
				
			ResultLandscapeRowV.setComp();
			
			ResultLandscapeRowV.setResultLandscapeName(String.valueOf(competitionRank + 1) + ". " + mResultLandscape.get(competitionRank).getName());
			
			if (mResultLandscape.get(competitionRank).getTotalTime() == Integer.MAX_VALUE)
			{
				ResultLandscapeRowV.setResultLandscapeTotalTime("--:--");
			}
			else
			{
				String TotalTime = "";
				if (mResultLandscape.get(competitionRank).getTotalTime() == 0)
				{
					TotalTime = "--:--";
				}
				else
				{				
					TotalTime = CompetitionHelper.secToMinSec(mResultLandscape.get(competitionRank).getTotalTime());				
				}	
				ResultLandscapeRowV.setResultLandscapeTotalTime(TotalTime);
			}
			
	
			
			for(int stageNumber = 0; stageNumber < mResultLandscape.get(competitionRank).getRank().size(); stageNumber++) 
			{
				Long fastestTimeOnStage = Long.MAX_VALUE;
				for( ResultLandscape result : mResultLandscape){
					try{
						fastestTimeOnStage = Math.min(fastestTimeOnStage, result.getTime().get(stageNumber) );
					}
					catch( IndexOutOfBoundsException e){	
					}
							
				}
				
				Long slowestTimeOnStage = Long.MIN_VALUE;
				for( ResultLandscape result : mResultLandscape){
					try{
						slowestTimeOnStage = Math.max(slowestTimeOnStage, result.getTime().get(stageNumber) );
					}
					catch( IndexOutOfBoundsException e){	
					}
				}
				
					
				String StageTime = "";
				if (mResultLandscape.get(competitionRank).getTime().get(stageNumber) == Integer.MAX_VALUE)
				{
					StageTime = "--:--";
				}
				else
				{
					StageTime = CompetitionHelper.secToMinSec(mResultLandscape.get(competitionRank).getTime().get(stageNumber));
				}			
				
				int color;
				String StageRank = "";
				if (mResultLandscape.get(competitionRank).getRank().get(stageNumber) == (long) Integer.MAX_VALUE)
				{
					StageRank = "-";
					color = Color.RED;
				}
				else
				{
					StageRank = mResultLandscape.get(competitionRank).getRank().get(stageNumber).toString();											
					Long competitorStageTime = mResultLandscape.get(competitionRank).getTime().get(stageNumber);		
					float myTimeDiff = competitorStageTime - fastestTimeOnStage;
					float stageTimeDiff = slowestTimeOnStage - fastestTimeOnStage;
					color = generateRedToGreenColorTransition( 1f -(myTimeDiff / stageTimeDiff) );	  //1.0 => red, 0.0 => green	
				}			
				
				ResultLandscapeRowV.setResultLandscapeStageTime(stageNumber + 1, StageTime + " (" + StageRank + ")" , color);
			}
			
			
			ResultLandscapeRowV.setPosition(competitionRank);
			
			return ResultLandscapeRowV;
		}
		catch(Exception e){
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "popUp");
			return new ResultLandscapeRowView(mContext);
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