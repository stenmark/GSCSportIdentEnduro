package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;

/**
 * This class is created when a new SI card is read by the SI main unit. All
 * essential data is parsed from the SI Card and stored in this class.
 * 
 * @author Andreas
 * 
 */
public class Card implements Serializable {

	private static final long serialVersionUID = 8L;
	private int mNumberOfPunches;
	private int mCardNumber;
	private Punch mStartPunch; 
	private Punch mFinishPunch;
	private Punch mCheckPunch; 
	private Punch mClearPunch;
	private List<Punch> mPunches;
	private boolean hasCardBeenRead = false;
	private boolean isSiacCard = false;

	public Card() {
		setCardNumber(0);
		setNumberOfPunches(0);
		setStartPunch(new Punch(-1, -1));
		setFinishPunch(new Punch(-1, -1));
		setCheckPunch(new Punch(-1, -1));
		setClearPunch(new Punch(-1, -1));
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

	public Punch getClearPunch() {
		return mClearPunch;
	}

	public void setClearPunch(Punch clearPunch) {
		mClearPunch = clearPunch;
	}

	public List<Punch> getPunches() {
		return mPunches;
	}

	public void setPunches(List<Punch> punches) {
		mPunches = punches;
	}

	public Long getTimeOfControlEss(int control, Boolean startControl) {
		long time;

		if (startControl) {
			time = 0;
		} else {
			time = Long.MAX_VALUE;
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
		long finish = getTimeOfControlEss(finishControl, false);
		long start = getTimeOfControlEss(startControl, true);

		if ((start == Long.MAX_VALUE) || (finish == Long.MAX_VALUE)) {
			return Competition.NO_TIME_FOR_STAGE;
		} else {			
			return finish - start;
		}		
	}	

	public Long getTimeOfControlSvartVitt(int control, Boolean startControl, int stageNumber) {
		long time;
		int currentStage = 1;

		if (startControl) {
			time = 0;
		} else {
			time = Long.MAX_VALUE;
		}			


		for( int i = 0; i < mPunches.size(); i++){
			Punch currentPunch = mPunches.get(i);

			//Found one punch with that control
			if( !currentPunch.getIsFinishPunchBeforeStart()){
				if (currentPunch.getControl() == control) {				
					if (startControl) {
						if (currentPunch.getTime() > time) {
							time = currentPunch.getTime();
						}
					} else {
						if (currentPunch.getTime() < time) {
							time = currentPunch.getTime();
						}
					}	

					//Is this the last punch?
					if( (i+1) == mPunches.size() ) {
						//No more punches
						if (stageNumber == currentStage) {
							//Correct stage
							return time;
						} else {
							//Not correct stage, continue search, reset time
							if (startControl) {
								time = 0;
							} else {
								time = Long.MAX_VALUE;
							}	
							currentStage++;
						}
					} else {	
						//Yes there are more punches
						Punch nextPunch = mPunches.get(i+1);
						if (currentPunch.getControl() != nextPunch.getControl()) {				
							//Next punch is a different control 						
							if (stageNumber == currentStage) {
								//Correct stage
								return time;
							} else {
								//Not correct stage, continue search, reset time
								if (startControl) {
									time = 0;
								} else {
									time = Long.MAX_VALUE;
								}	
								currentStage++;
							}
						}
						currentPunch = nextPunch;
					}
				}
			}
		}

		return time;
	}	

	public Long getStageTimeSvartVitt(int startControl, int finishControl, int stageNumber) {
		long finish = getTimeOfControlSvartVitt(finishControl, false, stageNumber);
		long start = getTimeOfControlSvartVitt(startControl, true, stageNumber);

		if ((start == Long.MAX_VALUE) || (finish == Long.MAX_VALUE)) {
			return Competition.NO_TIME_FOR_STAGE;
		} else {			
			return finish - start;
		}
	}

	public boolean hasCardBeenRead() {
		return hasCardBeenRead;
	}

	public void setCardAsRead() {
		hasCardBeenRead = true;
		LogFileWriter.writeLog("cardLog", getPunchesAsString() );
	}
	
	public boolean getIsSiacCard() {
		return isSiacCard;
	}
	
	public void setIsSiacCard(){
		isSiacCard = true;
	}

	public String getPunchesAsString(){
		String punchesAsCsv = "";

		Collections.sort(getPunches(),
				new Comparator<Punch>() {
			@Override
			public int compare(Punch s1, Punch s2) {
				return Long.valueOf(s1.getTime()).compareTo(s2.getTime());
			}
		});

		punchesAsCsv += getCardNumber() + ",";
		int i = 0;					
		for (Punch punch : getPunches()) {
			if (i != 0) {
				punchesAsCsv += ",";	
			}
			punchesAsCsv += punch.getControl() + "," + punch.getTime();
			i++;
		}
		punchesAsCsv += "\n";

		return punchesAsCsv;
	}
}
