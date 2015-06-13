package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

/**
 * This class is created when a new SI card is read by the SI main unit. All
 * essential data is parsed from the SI Card and stored in this class.
 * 
 * @author Andreas
 * 
 */
public class Card implements Serializable {

	private static final long serialVersionUID = 1L;
	private int mNumberOfPunches;
	private int mCardNumber;
	private Punch mStartPunch; // Not used by GSC competitions
	private Punch mFinishPunch; // Not used by GSC competitions
	private Punch mCheckPunch; // Not used by GSC competitions
	private ArrayList<Punch> mPunches;

	public Card() {
		setCardNumber(0);
		setNumberOfPunches(0);
		setStartPunch(new Punch(-1, -1));
		setFinishPunch(new Punch(-1, -1));
		setCheckPunch(new Punch(-1, -1));
		setPunches(new ArrayList<Punch>());
	}

	@Override
	public String toString() {
		String result = "CardNumber = " + mCardNumber + "  Number of punches = "
				+ mNumberOfPunches;
		// "\nstart punch " + startPunch.toString() +
		// "\nfinsh punch " + finishPunch.toString() +
		// "\ncheck punch " + checkPunch.toString();

		for (Punch punch : mPunches) {
			result += "\n" + punch.toString();
		}

		return result;
	}

	public int getNumberOfPunches() {
		return mNumberOfPunches;
	}

	public void setNumberOfPunches(int numberOfPunches) {
		mNumberOfPunches = numberOfPunches;
	}

	public int getCardNumber() {
		return mCardNumber;
	}

	public void setCardNumber(int cardNumber) {
		mCardNumber = cardNumber;
	}

	public Punch getStartPunch() {
		return mStartPunch;
	}

	public void setStartPunch(Punch startPunch) {
		mStartPunch = startPunch;
	}

	public Punch getFinishPunch() {
		return mFinishPunch;
	}

	public void setFinishPunch(Punch finishPunch) {
		mFinishPunch = finishPunch;
	}

	public Punch getCheckPunch() {
		return mCheckPunch;
	}

	public void setCheckPunch(Punch checkPunch) {
		mCheckPunch = checkPunch;
	}

	public ArrayList<Punch> getPunches() {
		return mPunches;
	}

	public void setPunches(ArrayList<Punch> punches) {
		mPunches = punches;
	}
	
	public Long getTimeOfControlEss(int control, Boolean startControl) {
		long time;
		
		if (startControl) {
			time = 0;
		} else {
			time = (long) Integer.MAX_VALUE;
		}			
		
		for (Punch punch : mPunches) {
			if (punch.getControl() == control) {
				if (startControl) {
					if (punch.getTime() > time) {
						time = punch.getTime();
					}
				} else {
					if (punch.getTime() < time) {
						time = punch.getTime();
					}
				}											
			}
		}
		return time;
	}		
	
	public Long getStageTimeEss(int startControl, int finishControl) {
		return getTimeOfControlEss(finishControl, false) - getTimeOfControlEss(startControl, true);
	}	
	
	public Long getTimeOfControlSvartVitt(int control, Boolean startControl, int stageNumber) {
		long time;
		int currentStage = 1;
		
		if (startControl) {
			time = 0;
		} else {
			time = (long) Integer.MAX_VALUE;
		}			
		
		for (int i = 0; i < mPunches.size(); i++) {
			//Found one punch with that control
			if (mPunches.get(i).getControl() == control) {				
				if (startControl) {
					if (mPunches.get(i).getTime() > time) {
						time = mPunches.get(i).getTime();
					}
				} else {
					if (mPunches.get(i).getTime() < time) {
						time = mPunches.get(i).getTime();
					}
				}	
				
				//Is there more punches made?
				if ((i + 1) == mPunches.size()) {
					//No more punches
					if (stageNumber == currentStage) {
						//Correct stage
						return time;
					} else {
						//Not correct stage, continue search, reset time
						if (startControl) {
							time = 0;
						} else {
							time = (long) Integer.MAX_VALUE;
						}	
						currentStage++;
					}
				} else {	
					//Yes there are more punches
					if (mPunches.get(i).getControl() != mPunches.get(i + 1).getControl()) {				
						//Next punch is a different control 						
						if (stageNumber == currentStage) {
							//Correct stage
							return time;
						} else {
							//Not correct stage, continue search, reset time
							if (startControl) {
								time = 0;
							} else {
								time = (long) Integer.MAX_VALUE;
							}	
							currentStage++;
						}
					}
				}
			}
		}

		return time;
	}	
	
	public Long getStageTimeSvartVitt(int startControl, int finishControl, int stageNumber) {
		return getTimeOfControlSvartVitt(finishControl, false, stageNumber) - getTimeOfControlSvartVitt(startControl, true, stageNumber);
	}		
}
