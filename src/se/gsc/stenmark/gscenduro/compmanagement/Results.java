package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Results implements Serializable {
	
	private static final long serialVersionUID = 201111020001L; 	
	private String mTitle;	
	private List<StageResult> mStageResult = new ArrayList<StageResult>();
	
	public Results() {
		setTitle("");
	}
	
	public Results(String title) {
		setTitle(title);
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
	
	public List<StageResult> getStageResult() {
		return mStageResult;
	}		
	
	public void setStageResult(List<StageResult> stageResult) {
		mStageResult = stageResult;	
	}		
		
	public void sortStageResult() {
		
		if (mStageResult.size() > 0) {		
			Collections.sort(mStageResult, new Comparator<StageResult>() {
				@Override
				public int compare(StageResult lhs, StageResult rhs) {
					return lhs.getStageTimeForSorting().compareTo(rhs.getStageTimeForSorting());
				}
			});
		
			//Calculate rank		
			int rank = 1;		
			if (mStageResult.get(0).getStageTime() == Competition.NO_TIME_FOR_STAGE)
			{
				//No results, set all to dnf
				for (int i = 0; i < mStageResult.size(); i++) {	
					mStageResult.get(i).setRank(Competition.RANK_DNF);
				}
			}
			else
			{
				mStageResult.get(0).setRank(rank);			
				for (int i = 1; i < mStageResult.size(); i++) {				
					if (mStageResult.get(i).getStageTime() > mStageResult.get(i - 1).getStageTime()) {
						rank = i + 1;
					}
					
					if (mStageResult.get(i).getStageTime() == Competition.NO_TIME_FOR_STAGE) {
						mStageResult.get(i).setRank(Competition.RANK_DNF);
					} else {			
						mStageResult.get(i).setRank(rank);
					}
				}
			}
		}
	}	
}
