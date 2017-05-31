package se.gsc.stenmark.gscenduro.compmanagement;

public class NotAllStationsPunchedException extends RuntimeException {

	private static final long serialVersionUID = 7L;

	public NotAllStationsPunchedException() {
		super();
	}

	public NotAllStationsPunchedException(String message) {
		super(message);
	}

	public NotAllStationsPunchedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAllStationsPunchedException(Throwable cause) {
		super(cause);
	}

}
