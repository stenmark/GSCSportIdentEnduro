package se.gsc.stenmark.gscenduro.webtime;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

enum ConnectionState{
	NOT_ACTIVE,
	DISCONNECTED_LISTENING,
	CONNECTED_SERVER_MODE,
	CONNECTED_CLIENT_MODE,
	LOST_CONNECTION_RECONNECTING,
}

public class WebTimeHandler {
	private static final int SEND_HEARTBEAT_INTERVAL_MS = 5000; //milliseconds
	private static final int POLL_HEARTBEAT_INTERVAL_MS = 1000; //milliseconds
	private static final int CONNECTION_LOST_TIMEOUT_MS = 11000; //milliseconds
	private static final String HEARTBEAT_MSG = "HEARTBEAT";
	private static final String CONNECTED_MSG = "CONNECTED";
	
	private WebTimePeristentData webTimeData;
	private ClientConnectionHandler clientConnection = null;
	private MainActivity mainActivity = null;
	private Socket socket = null;
	private IncommingMessageListener incommingMessageListener;
	
	private Thread incommingMessageThread;
	private Timer timer = null;
	private HeartbeatTask heartbeatTask = null;
	private long lastHeartBeatTimeStamp = 0;

	public WebTimeHandler( MainActivity mainActivity, WebTimePeristentData webTimeData ) {
		this.webTimeData = webTimeData;
		for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
			webTimeData.competitorsOnStage[i] = null;
		}
		this.mainActivity = mainActivity;
	}

	/**
	 * Start a new server socket listening thread.
	 * Will callback on gotNewServerSocket() when a connection was established.
	 */
	public void startNewIncommingConnectionListener(){
		new IncommingConnectionListener(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		setNewState(ConnectionState.DISCONNECTED_LISTENING);
	}

	/**
	 * Callback from Server Listener thread. 
	 * When a client connected, the socket will be sent to this method
	 * @param socket
	 */
	void gotNewServerSocket( Socket socket){
		//We will alwyays have a task listening for new connections. 
		//But will will not always handle new sockets, new sockets will only be accepted in
		//DISCONNECTED_LISTENING and LOST_CONNECTION_RECONNECTING
		new IncommingConnectionListener(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		switch(webTimeData.state)
		{
		case DISCONNECTED_LISTENING:
		case LOST_CONNECTION_RECONNECTING:
		{
			this.socket = socket;
			if(incommingMessageThread != null){
				if(incommingMessageThread.isAlive()){
					incommingMessageThread.interrupt();
					LogFileWriter.writeLog("debugLog", "Got New server socket with an incomming message thread already running. Trying to kill the old Thread");
				}
			}
			//Start a new Listener to handle all future communication on this socket
			LogFileWriter.writeLog("debugLog", "Send new socket to socket listener");
			setNewState(ConnectionState.CONNECTED_SERVER_MODE);
			initiateNewConnection();
			mainActivity.updateStatus("Connected to " + socket.getInetAddress() );
			break;
		}
		case CONNECTED_SERVER_MODE:
		case CONNECTED_CLIENT_MODE:
		{
			LogFileWriter.writeLog("debugLog", "Got New server socket when already connected. Socket state is: " + socket.isConnected());
			if( !socket.isConnected() ){
				this.socket = socket;
				if(incommingMessageThread != null){
					if(incommingMessageThread.isAlive()){
						incommingMessageThread.interrupt();
						LogFileWriter.writeLog("debugLog", "Got New server socket with an incomming message thread already running. Trying to kill the old Thread");
					}
				}
				//Start a new Listener to handle all future communication on this socket
				LogFileWriter.writeLog("debugLog", "Send new socket to socket listener");
				initiateNewConnection();
				setNewState(ConnectionState.CONNECTED_SERVER_MODE);
				mainActivity.updateStatus("Connected to " + socket.getInetAddress() );
			}
			break;
		}
		default:
			LogFileWriter.writeLog("debugLog", "gotNewServerSocket: Cant handle state: " + webTimeData.state);
		}
	}

	/**
	 * Callback from GUI thread. When the user presses Connect button this method is called.
	 * Will start a thread to attempt a connection to the serverIp.
	 * If successful the thread will callback socketConnected(socket)
	 * if unsuccessful the thread will callback failedToConnectSocket()
	 * @param serverIp
	 */
	public void connectToIp(String serverIp) {
		webTimeData.serverIp = serverIp;
		switch(webTimeData.state){
		case DISCONNECTED_LISTENING:
		{		
			clientConnection  = new ClientConnectionHandler( serverIp, this );
			LogFileWriter.writeLog("debugLog", "WebTimeHandler:connectToIp");
			clientConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			mainActivity.updateStatus("Trying to connect to " + serverIp );
			break;
		}
		case CONNECTED_CLIENT_MODE:
		case CONNECTED_SERVER_MODE:
		case LOST_CONNECTION_RECONNECTING:
		{
			LogFileWriter.writeLog("debugLog", "Trying to connect when already in state: " + webTimeData.state + " Scoket isconnected=" );
			if( socket == null ){
				LogFileWriter.writeLog("debugLog", "Socket was null, trying to re-establish" );
				clientConnection  = new ClientConnectionHandler( serverIp, this );
				LogFileWriter.writeLog("debugLog", "WebTimeHandler:connectToIp");
				clientConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				setNewState(ConnectionState.DISCONNECTED_LISTENING);
				mainActivity.updateStatus("Trying to connect to " + serverIp );
			}
			else{
				if( socket.isConnected()){
					LogFileWriter.writeLog("debugLog", "Socket was not connected. Trying to create new connection" );
					clientConnection  = new ClientConnectionHandler( serverIp, this );
					LogFileWriter.writeLog("debugLog", "WebTimeHandler:connectToIp");
					clientConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					setNewState(ConnectionState.DISCONNECTED_LISTENING);
					mainActivity.updateStatus("Trying to connect to " + serverIp );
				}
			}
			break;
		}
		default:
			LogFileWriter.writeLog("debugLog", "connectToIp: Cant handle state: " + webTimeData.state);
		}

	}

	void socketConnected( Socket socket, String serverIp){
		if( webTimeData.state == ConnectionState.DISCONNECTED_LISTENING){
			this.socket = socket;
			sendMessage( CONNECTED_MSG );
			setNewState(ConnectionState.CONNECTED_CLIENT_MODE);
			initiateNewConnection();
			mainActivity.updateStatus("Connected to " + serverIp );
		}
		else{
			LogFileWriter.writeLog("debugLog", "Got unexpected socketConnected when in state: " + webTimeData.state);
		}
	}

	void failedToConnectSocket( String serverIp) {
		LogFileWriter.writeLog("debugLog", "Failed to connect to the server (other android app)");
		setNewState(ConnectionState.DISCONNECTED_LISTENING);
		mainActivity.updateStatus("Connection failed to " + serverIp );
	}

	void gotNewMsg( String msg){
		switch( msg ){
			case HEARTBEAT_MSG:
			{
				lastHeartBeatTimeStamp  = System.currentTimeMillis();
			}
			case CONNECTED_MSG:
			{
				LogFileWriter.writeLog("debugLog", "Connected");
				break;
			}
			default:{
				LogFileWriter.writeLog("debugLog", "Got unknown message: " + msg);
				mainActivity.updateStatus(msg);
			}	
		}
	}

	public void addCompetitorOnStage( Competitor competitor){
		try{
			LogFileWriter.writeLog("debugLog", "ENTER addCompetitorOnStage");
			for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
				LogFileWriter.writeLog("debugLog", "addCompetitorOnStage loop " + i);
				if(webTimeData.competitorsOnStage[i] == null ){
					webTimeData.competitorsOnStage[i] = competitor;
					LogFileWriter.writeLog("debugLog", "addCompetitorOnStage found and adding " + competitor.toString());
					sendMessage( "Add Competitor: " + competitor.getName());
					return;
				}
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	public void removeCompetitorOnStage( Competitor competitor){
		for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
			if(webTimeData.competitorsOnStage[i] == competitor ){
				webTimeData.competitorsOnStage[i] = null;
				return;
			}
		}
	}

	public Competitor[] getCompetitorsOnstage(){
		return webTimeData.competitorsOnStage;
	}


	public void sendMessage(String msg){
		try {
			OutputStream outstream = socket.getOutputStream(); 
			PrintWriter out = new PrintWriter(outstream);
			out.println(msg);
			out.flush();
			LogFileWriter.writeLog("debugLog", "ClientConnectionHandler: Sent message " +msg  );
		} catch (Exception e) {
			LogFileWriter.writeLog(e);
		}		
	}

	public WebTimePeristentData getPersistentData(){
		return webTimeData;
	}

	private void setNewState( ConnectionState newState ){
		LogFileWriter.writeLog("debugLog", "Change connection state " + webTimeData.state + " -> " + newState);
		webTimeData.state = newState;
	}
	
	private void initiateNewConnection(){
		if( timer != null){
			timer.cancel();
		}
		timer = new Timer();
		heartbeatTask  = new HeartbeatTask();
		//The client will send the heartbeat and the server will monitor that they are received.
		switch(webTimeData.state){
			case CONNECTED_CLIENT_MODE:
			{
				timer.schedule(heartbeatTask, SEND_HEARTBEAT_INTERVAL_MS, SEND_HEARTBEAT_INTERVAL_MS);
				break;
			}
			case CONNECTED_SERVER_MODE:
			{
				timer.schedule(heartbeatTask, POLL_HEARTBEAT_INTERVAL_MS, POLL_HEARTBEAT_INTERVAL_MS);
				break;
			}
			default:
			{
				LogFileWriter.writeLog("debugLog", "ERROR: Iniated connection from incorrect state: " + webTimeData.state);
				break;
			}
		}
		
		incommingMessageListener = new IncommingMessageListener(this, socket );
		incommingMessageThread = new Thread(incommingMessageListener);
		incommingMessageThread.start();
	}
	
	class HeartbeatTask extends TimerTask {
		@Override
		public void run() {
			switch(webTimeData.state){
				case CONNECTED_CLIENT_MODE:
				{
					sendMessage(HEARTBEAT_MSG);
					break;
				}
				case CONNECTED_SERVER_MODE:
				{
					if( System.currentTimeMillis()-lastHeartBeatTimeStamp > CONNECTION_LOST_TIMEOUT_MS){
						setNewState(ConnectionState.LOST_CONNECTION_RECONNECTING);
						mainActivity.updateStatus("Connection to the other unit was lost. Trying to reconnect");
					}
					break;
				}
				default:
				{
					LogFileWriter.writeLog("debugLog", "ERROR: Heartbeat task exectued from incorrect state: " + webTimeData.state);
					break;
				}			
			}
		}
	}
}
