package se.gsc.stenmark.gscenduro;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultLandscape implements Serializable {
	
	private static final long serialVersionUID = 201121020001L; 	
	private String mName;
	private String mTeam;
	private int mCardNumber;
	private Long mTotalTime;
	private ArrayList<Long> mTime = new ArrayList<Long>();
	private ArrayList<Integer> mRank = new ArrayList<Integer>();
		
	public ResultLandscape() {
	}
		
	public int getCardNumber() {
		return mCardNumber;
	}

	public void setCardNumber(int mCardNumber) {
		this.mCardNumber = mCardNumber;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}	
	
	public String getTeam() {
		return mTeam;
	}	

	public void setTeam(String mTeam) {
		this.mTeam = mTeam;
	}	

	public Long getTotalTime() {
		return mTotalTime;
	}

	public void setTotalTime(Long mTotalTime) {
		this.mTotalTime = mTotalTime;
	}

	public ArrayList<Long> getTime() {
		return mTime;
	}

	public void setTime(ArrayList<Long> mTime) {
		this.mTime = mTime;
	}

	public ArrayList<Integer> getRank() {
		return mRank;
	}

	public void setRank(ArrayList<Integer> mRank) {
		this.mRank = mRank;
	}			
}
