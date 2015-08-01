package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.Serializable;

/**
 * Represents an SI card punch. I.e. when a competitor has either started a
 * stage of finished a stage. The punches are read from the SI card
 * 
 * @author Andreas
 * 
 */
public class Punch implements Serializable {

	private static final long serialVersionUID = 1L;
	private long mTime;
	private int mControl;

	public Punch(long time, int control) {
		setTime(time);
		setControl(control);
	}

	@Override
	public String toString() {
		return "Control=" + mControl + "  Time=" + mTime;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		mTime = time;
	}

	public long getControl() {
		return mControl;
	}

	public void setControl(int control) {
		mControl = control;
	}
}
