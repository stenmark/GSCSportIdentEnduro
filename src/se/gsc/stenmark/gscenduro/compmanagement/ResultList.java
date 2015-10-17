package se.gsc.stenmark.gscenduro.compmanagement;

import java.util.ArrayList;
import java.util.Collection;

public class ResultList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultList() {
		super();
	}

	public ResultList(int capacity) {
		super(capacity);
	}

	public ResultList(Collection<? extends E> collection) {
		super(collection);
	}

}
