package se.gsc.stenmark.gscenduro.webtime;

import java.io.Serializable;

import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class WebTimePeristentData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Competitor competitorsOnStage[] = new Competitor[3];
	public ConnectionState state = ConnectionState.NOT_ACTIVE;
	public String serverIp;

}
