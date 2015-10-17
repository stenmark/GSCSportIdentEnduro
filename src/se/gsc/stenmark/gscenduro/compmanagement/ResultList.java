package se.gsc.stenmark.gscenduro.compmanagement;

import java.util.ArrayList;
import java.util.Collection;

public class ResultList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int currentTotalResult = 0;
	
	public void setTotalResultPosition(  ){
		currentTotalResult = size();
	}
	
	@Deprecated
	public int getTotalResultPosition(){
		return currentTotalResult;
	}

	public ResultList() {
		super();
	}

	public ResultList(int capacity) {
		super(capacity);
	}

	public ResultList(Collection<? extends E> collection) {
		super(collection);
	}
	
	public boolean addTotalResult( E totalResult ){
		return add(totalResult);
	}
	
	public E getTotalResult(){
		return get(currentTotalResult);
	}
	
	public E getStageResult( int stageNumber){
		return super.get(stageNumber+currentTotalResult);
	}

}
