package se.gsc.stenmark.gscenduro.compmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int currentTotalResult = 0;
	Map<String,CompetitionStartStopMarker> competitionClassStartStopMarkers = new HashMap<String, ResultList<E>.CompetitionStartStopMarker>(); 
	
	public void setTotalResultPosition( String competitionClassForResults ){
		currentTotalResult = size();
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
		return super.add(totalResult);
	}
	
	public E getTotalResult(){
		return super.get(currentTotalResult);
	}
	
	public E getStageResult( int stageNumber){
		return super.get(stageNumber+currentTotalResult);
	}
	
	/**
	 * Return only stage results for a specific Competition Class, excluding Total Results for the whole competition class.
	 * Compare to getAllResultsForCompetitionClass, which includes Total Results
	 * @param compClass
	 * @return
	 */
	public List<E> getAllStageResultsForCompetitionClass( String compClass ){
		return super.subList(currentTotalResult+1, size() );
	}
	
	/**
	 * Return all results for a specific Competition Class, including Total Results for the whole competition class.
	 * Compare to getAllStageResultsForCompetitionClass, which excludes Total Results
	 * @param compClass
	 * @return
	 */
	public List<E> getAllResultsForCompetitionClass( String compClass ){
		return super.subList(currentTotalResult, size() );
	}
	
	private class CompetitionStartStopMarker{
		private int startMarker;
		private int stopMarker;
		private CompetitionStartStopMarker( int startMarker, int stopMarker){
			this.startMarker = startMarker;
			this.stopMarker = stopMarker;
		}
		
	}

}
