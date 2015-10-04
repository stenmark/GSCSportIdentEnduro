package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.List;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import android.content.Context;
import android.graphics.Color;
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
	
	public void updateResultLandscape () {
		mResultLandscape = ((MainActivity) mContext).competition.getResultLandscape();
		this.notifyDataSetChanged();	
	}		
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		ResultsLandscapeRowView resultLandscapeRowV = null;	
		
		if (convertView == null) {
			resultLandscapeRowV = new ResultsLandscapeRowView(mContext);
		} else {
			resultLandscapeRowV = (ResultsLandscapeRowView) convertView;
		}

		resultLandscapeRowV.clearTitle();
		resultLandscapeRowV.clearTimes();
		resultLandscapeRowV.setTimes(((MainActivity) mContext).competition.getStages().size());
		if (position == 0) {
			resultLandscapeRowV.setTitle(((MainActivity) mContext).competition.getStages().size());
			resultLandscapeRowV.setResultLandscapeCompetitorClass(mResultLandscape.get(position).getTitle());
		} else {
			if (mResultLandscape.get(position).getTitle() != mResultLandscape.get(position - 1).getTitle()) {
				resultLandscapeRowV.setTitle(((MainActivity) mContext).competition.getStages().size());	
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
			Long competitorStageTime = mResultLandscape.get(position).getStageResult().get(stageNumber).getStageTimes();				
			rank = mResultLandscape.get(position).getStageResult().get(stageNumber).getRank();
			
			int color = CompetitionHelper.generateRedToGreenColorTransition(fastestTimeOnStage, slowestTimeOnStage, competitorStageTime, rank);
			if (rank == Integer.MAX_VALUE) {
				StageTime = "";
			} else {
				StageTime += "\n(" + String.valueOf(rank) + ")";											
			}			
			
			resultLandscapeRowV.setResultLandscapeStageTime(stageNumber, StageTime, color);
		}			
		
		resultLandscapeRowV.setPosition(position);
		
		return resultLandscapeRowV;
	}
}