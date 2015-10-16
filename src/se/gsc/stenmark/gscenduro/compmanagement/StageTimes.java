package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;

public class StageTimes implements Serializable {

	private static final long serialVersionUID = 3L;
	private ArrayList<Integer> mStages;
	private ArrayList<Long> mTimes;
	
	public StageTimes() {
		mStages = new ArrayList<Integer>();
		mTimes = new ArrayList<Long>();		
	}
	
	public long getTimesOfStage(int stage) {		
		if (mStages != null) {
			for (int i = 0; i < mStages.size(); i++) {
				if (mStages.get(i) == stage) {
					return mTimes.get(i);
				}
			}
		}
		
		return Competition.NO_TIME_FOR_STAGE;
	}

	public void setTimesOfStage(int stage, long time) {
		if (mStages != null) {
			for (int i = 0; i < mStages.size(); i++) {
				if (mStages.get(i) == stage) {
					//already in list update stage time
					mTimes.set(i, time);
					return;
				}
			}	
			
			//Not in list add new stage time
			mTimes.add(time);
			mStages.add(stage);			
		}
	}
	
	public void clear() {
		mTimes.clear();
		mStages.clear();
	}
	
	public int size() {
		return mStages.size();
	}
}
