package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stages implements Serializable {

	private static final long serialVersionUID = 2L;
	private List<Stage> mStages = new ArrayList<Stage>();
	private ArrayList<String> mControls = null;

	public Stages() {
	}
		
	public Stages( Stages stages) {
		mStages = stages.mStages;
		mControls = addControls();		
	}

	public Stage get(int index) {
		return mStages.get(index);
	}

	public void clear() {
		mStages.clear();
	}
		
	public int getStageStartStation( int stageNumber ){
		return mStages.get(stageNumber).start;
	}
	
	public int getStageFinishStation( int stageNumber ){
		return mStages.get(stageNumber).finish;
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
			for (Stage stageControls : mStages) {
				if (!controls.contains(Integer.toString(stageControls.start))) {
					controls.add(Integer.toString(stageControls.start));
				}
				if (!controls.contains(Integer.toString(stageControls.finish))) {
					controls.add(Integer.toString(stageControls.finish));
				}				
			}
		}
		return controls;
	}	
	
	public String toString() {
		String stagesAsString = "";
		if (!mStages.isEmpty() && mStages != null) {
			int i = 0;
			for (Stage stageControls : mStages) {
				i++;
				if (i != 1) {
					stagesAsString += "\n";
				}
				stagesAsString += "Stage " + i + ": " + stageControls.start + "->" + stageControls.finish;
			}
		} else {
			stagesAsString += " No stages loaded";
		}
		return stagesAsString;
	}	
	
	public String exportStagesCsvString() {
		String exportString = "";
		int i = 0;
		for (Stage stageControls : mStages) {
			if (i != 0) {
				exportString += ",";
			}
			exportString += stageControls.start + "," + stageControls.finish;
			i++;
		}
		return exportString;
	}		
	
	public boolean stringContainsItemFromList(String inputString, ArrayList<String> items) {
	    for(int i = 0; i < items.size(); i++) {
	        if(inputString.equals(items.get(i))) {
	            return true;
	        }
	    }
	    return false;
	}		
	
	public String checkStagesData(String stages, int type) {
		String[] controlsList = stages.split(",");				
		if ((controlsList.length % 2) == 0) {	//Is even
			//Check that all controls are digits only
			for (int i = 0; i < controlsList.length; i++) {
				if (!android.text.TextUtils.isDigitsOnly(controlsList[i])) {
					return "Controls are not digits only\n";
				}
			}

			ArrayList<String> checkedControlsList = new ArrayList<String>();		
			checkedControlsList.add(controlsList[0]);
			
			if (type == 1) {
				//Check that all controls are unique 
				for (int i = 1; i < controlsList.length; i++) {
					if (stringContainsItemFromList(controlsList[i], checkedControlsList)) {
						return "Not all controls are unique\n";
					}				
					checkedControlsList.add(controlsList[i]);
				}
			} else {
				int startControl = Integer.parseInt(controlsList[0]);
				int finishControl = Integer.parseInt(controlsList[1]);
				for (int i = 2; i < controlsList.length; i += 2) {					
					if (startControl != Integer.parseInt(controlsList[i])) {
						return "More than two controls\n";		
					}
					
					if (finishControl != Integer.parseInt(controlsList[i + 1])) {
						return "More than two controls\n";		
					}					
				}
				
			}
			return "";			
		} else {	//Is odd
			return "Not an even number of controls\n";
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
			mStages.add(new Stage(startControl, finishControl));
		}
	}	
	
	public boolean validStageControl(int control) {
		for (Stage stageControls : mStages) {
			if (control == stageControls.start) {
				return true;
			}			
			
			if (control == stageControls.finish) {
				return true;
			}			
		}
			
		return false;
	}
}
