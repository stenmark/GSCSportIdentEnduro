package se.gsc.stenmark.gscenduro.webtime;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class WebTimeHandler {
	private WebTimePeristentData webTimeData;
	private ClientConnectionHandler clientConnection = null;
	private MainActivity mainActivity = null;
	private Socket socket = null;
		
	public WebTimeHandler( ) {
		for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
			webTimeData.competitorsOnStage[i] = null;
		}
	}
	public WebTimeHandler( MainActivity mainActivity ) {
		for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
			webTimeData.competitorsOnStage[i] = null;
		}
		this.mainActivity = mainActivity;
	}
	
	public void startNewIncommingConnectionListener(){
		new IncommingConnectionListener(this).execute();
	}
	
	public void gotNewServerSocket( Socket socket){
		//Start to listen for more connections
		if( socket != null){
			LogFileWriter.writeLog("debugLog", "Already had an actoive socket when a new socket came in");
		}
		this.socket = socket;
		LogFileWriter.writeLog("debugLog", "reset IP Listener set socket from new Incomming connection");
		startNewIncommingConnectionListener();
		
		//Start a new Listener to handle all future communication on this socket
		LogFileWriter.writeLog("debugLog", "Send new socket to socket listener");
		new IncommingMessageListener(this).execute( socket );
	}
	
	public void gotNewMsg( String msg, Socket socket){
		//Start a new Listener to handle all future communication on this socket
		if( socket != null ){
			if( socket.isConnected() ){
				new IncommingMessageListener(this).execute( socket );
			}
			else{
				LogFileWriter.writeLog("debugLog", "Stopped listening to socket. Socket is not connected");
			}
		}
		else{
			LogFileWriter.writeLog("debugLog", "Stopped listening to socket. It became NULL");
		}
		mainActivity.updateStatus(msg);
	}

	public void connectToIp(String serverIp) {
		clientConnection  = new ClientConnectionHandler( serverIp, this );
		LogFileWriter.writeLog("debugLog", "WebTimeHandler:connectToIp");
		new Thread(clientConnection).start();
	}
	
	public void socketConnected( Socket socket){
		this.socket = socket;
		sendMessage(socket, "Connected with socket");
		new IncommingMessageListener(this).execute( socket );
	}
	
	public void addCompetitorOnStage( Competitor competitor){
		try{
			LogFileWriter.writeLog("debugLog", "ENTER addCompetitorOnStage");
			for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
				LogFileWriter.writeLog("debugLog", "addCompetitorOnStage loop " + i);
				if(webTimeData.competitorsOnStage[i] == null ){
					webTimeData.competitorsOnStage[i] = competitor;
					LogFileWriter.writeLog("debugLog", "addCompetitorOnStage found and adding " + competitor.toString());
					sendMessage(socket, "Add Competitor: " + competitor.getName());
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
	
		
	public void sendMessage(Socket socket, String msg){
		try {
			OutputStream outstream = socket .getOutputStream(); 
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
	
	public void setPersistentData(WebTimePeristentData webTimeData){
		this.webTimeData = webTimeData;
	}

}
