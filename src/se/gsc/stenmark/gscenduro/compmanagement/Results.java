package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Results implements Serializable {
	
	private static final long serialVersionUID = 201111020001L; 	
	private String mTitle;	
	private ArrayList<StageResult> mStageResult = new ArrayList<StageResult>();
	
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
	
	public ArrayList<StageResult> getStageResult() {
		return mStageResult;
	}		
	
	public void setStageResult(ArrayList<StageResult> stageResult) {
		mStageResult = stageResult;	
	}		
		
	public void sortStageResult() {
		
		if (mStageResult.size() > 0) {		
			Collections.sort(mStageResult, new Comparator<StageResult>() {
				@Override
				public int compare(StageResult lhs, StageResult rhs) {
					return lhs.getStageTimes().compareTo(rhs.getStageTimes());
				}
			});
		
			//Calculate rank		
			int rank = 1;		
			if (mStageResult.get(0).getStageTimes() == Integer.MAX_VALUE)
			{
				//No results, set all to dnf
				for (int i = 0; i < mStageResult.size(); i++) {	
					mStageResult.get(i).setRank(Integer.MAX_VALUE);
				}
			}
			else
			{
				mStageResult.get(0).setRank(rank);			
				for (int i = 1; i < mStageResult.size(); i++) {				
					if (mStageResult.get(i).getStageTimes() > mStageResult.get(i - 1).getStageTimes()) {
						rank = i + 1;
					}
					
					if (mStageResult.get(i).getStageTimes() == Integer.MAX_VALUE) {
						mStageResult.get(i).setRank(Integer.MAX_VALUE);
					} else {			
						mStageResult.get(i).setRank(rank);
					}
				}
			}
		}
	}	
}
