package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stages implements Serializable {

	private static final long serialVersionUID = 2L;
	private List<StageControls> mStages = new ArrayList<StageControls>();
	private ArrayList<String> mControls = null;

	public Stages() {
	}
	
	public Stages(List<StageControls> stages) {
		mStages = stages;
		mControls = addControls();		
	}

	public StageControls get(int index) {
		return mStages.get(index);
	}

	public void clear() {
		mStages.clear();
	}
	
	public List<StageControls> getStages() {
		return mStages;
	}
	
	public ArrayList<String> getControls() {
		return mControls;		
	}
	
	public int size() {
		if (mStages != null) {
			return mStages.size();
		}
		return 0;
	}	
	
	public ArrayList<String> addControls() {
		ArrayList<String> controls = new ArrayList<String>();
		if (!mStages.isEmpty() && mStages != null) {
			for (StageControls stageControls : mStages) {
				if (!controls.contains(Integer.toString(stageControls.getStart()))) {
					controls.add(Integer.toString(stageControls.getStart()));
				}
				if (!controls.contains(Integer.toString(stageControls.getFinish()))) {
					controls.add(Integer.toString(stageControls.getFinish()));
				}				
			}
		}
		return controls;
	}	
	
	public String toString() {
		String stagesAsString = "";
		if (!mStages.isEmpty() && mStages != null) {
			int i = 0;
			for (StageControls stageControls : mStages) {
				i++;
				if (i != 1) {
					stagesAsString += "\n";
				}
				stagesAsString += "Stage " + i + ": " + stageControls.getStart() + "->" + stageControls.getFinish();
			}
		} else {
			stagesAsString += " No stages loaded";
		}
		return stagesAsString;
	}	
	
	public String exportStagesCvsString() {
		String exportString = "";
		int i = 0;
		for (StageControls stageControls : mStages) {
			if (i != 0) {
				exportString += ",";
			}
			exportString += stageControls.getStart() + "," + stageControls.getFinish();
			i++;
		}
		return exportString;
	}		
	
	public boolean stringContainsItemFromList(String inputString, ArrayList<String> items) {
	    for(int i = 0; i < items.size(); i++) {
	        if(inputString.contains(items.get(i))) {
	            return true;
	        }
	    }
	    return false;
	}		
	
	public String checkStagesData(String stages) {
		String[] controlsList = stages.split(",");		
		
		if ((controlsList.length % 2) == 0) {
		    //Is even
			for (int i = 0; i < controlsList.length; i++) {
				if (!android.text.TextUtils.isDigitsOnly(controlsList[i])) {
					return "Controls are not digits only";
				}
			}

			ArrayList<String> checkedControlsList = new ArrayList<String>();		
			checkedControlsList.add(controlsList[0]);
			
			for (int i = 1; i < controlsList.length; i++) {
				if (stringContainsItemFromList(controlsList[i], checkedControlsList)) {
					return "Not all controls are unique";
				}				
				checkedControlsList.add(controlsList[i]);
			}
			
			return "";			
		} else {
		    //Is odd
			return "Not an even number of controls";
		}		
	}
	
	public void importStages(String stages) {
		String[] stageControls = stages.split(",");
		mStages.clear();
		for (int i = 0; i < stageControls.length; i += 2) {
			int startControl = 0;
			int finishControl = 0;
			startControl = Integer.parseInt(stageControls[i]);
			finishControl = Integer.parseInt(stageControls[i + 1]);
			mStages.add(new StageControls(startControl, finishControl));
		}
	}	
}
