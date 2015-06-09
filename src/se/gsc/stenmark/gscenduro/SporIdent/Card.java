package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;
import java.util.ArrayList;

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
	private long mCardNumber;
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

	public void findDoublePunches() {
		for (int i = 0; i < mPunches.size() - 1; i++) {
			if (mPunches.get(i).getControl() == mPunches.get(i + 1).getControl()) {
				mPunches.get(i).setMarkAsDoublePunch(true);
			}
		}
	}

	public int getNumberOfPunches() {
		return mNumberOfPunches;
	}

	public void setNumberOfPunches(int numberOfPunches) {
		mNumberOfPunches = numberOfPunches;
	}

	public long getCardNumber() {
		return mCardNumber;
	}

	public void setCardNumber(long cardNumber) {
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
}
