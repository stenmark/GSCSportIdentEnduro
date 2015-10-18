package se.gsc.stenmark.gscenduro.compmanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	
	Map<String,CompetitionStartStopMarker> competitionClassStartStopMarkers = new HashMap<String, ResultList<E>.CompetitionStartStopMarker>(); 
	
	public ResultList() {
		super();
	}

	public ResultList(int capacity) {
		super(capacity);
	}

	public ResultList(Collection<? extends E> collection) {
		super(collection);
	}
	
	public boolean addTotalResult( E totalResult, String competitionClass ){
		if( competitionClassStartStopMarkers.containsKey(competitionClass) ){
			competitionClassStartStopMarkers.get(competitionClass).startMarker = size();
		}
		else{
			competitionClassStartStopMarkers.put(competitionClass, new CompetitionStartStopMarker(size(), 0));
		}
		return super.add(totalResult);
	}
	
	public boolean addAllStageResults( List<E> allStageResults, String competitionClass){
		boolean addAllResponse = addAll(allStageResults);
		if( competitionClassStartStopMarkers.containsKey(competitionClass) ){
			competitionClassStartStopMarkers.get(competitionClass).stopMarker = size();
		}
		else{
			competitionClassStartStopMarkers.put(competitionClass, new CompetitionStartStopMarker(0, size()));
		}		
		return addAllResponse;
	}
	
	public E getTotalResult(String compClass){
		return super.get( competitionClassStartStopMarkers.get(compClass).startMarker );
	}
	
	public E getStageResult( int stageNumber, String compClass){
		return super.get(stageNumber + competitionClassStartStopMarkers.get(compClass).startMarker);
	}
	
	/**
	 * Return only stage results for a specific Competition Class, excluding Total Results for the whole competition class.
	 * Compare to getAllResults, which includes Total Results
	 * @param compClass
	 * @return
	 */
	public List<E> getAllStageResults( String compClass ){
		return super.subList(competitionClassStartStopMarkers.get(compClass).startMarker+1, competitionClassStartStopMarkers.get(compClass).stopMarker );
	}
	
	/**
	 * Return all results for a specific Competition Class, including Total Results for the whole competition class.
	 * Compare to getAllStageResults, which excludes Total Results
	 * @param compClass
	 * @return
	 */
	public List<E> getAllResults( String compClass ){
		return super.subList(competitionClassStartStopMarkers.get(compClass).startMarker, competitionClassStartStopMarkers.get(compClass).stopMarker );
	}
	
	@Override
	@Deprecated
	public boolean add( E e){
		throw new RuntimeException("DO NOT USE add with ResultList Class. Use addTotalResult and addAllStageResults instead");
	}
	
//	@Override
//	@Deprecated
//	public E get( int i ){
//		throw new RuntimeException("DO NOT USE get with ResultList Class. Use getAllResults, getAllStageResults, getStageResult and getTotalResult  instead");
//	}
	
	private class CompetitionStartStopMarker implements Serializable{
		private static final long serialVersionUID = 1L;
		private int startMarker;
		private int stopMarker;
		private CompetitionStartStopMarker( int startMarker, int stopMarker){
			this.startMarker = startMarker;
			this.stopMarker = stopMarker;
		}
		
	}

}
