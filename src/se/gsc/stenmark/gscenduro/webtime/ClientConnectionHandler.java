package se.gsc.stenmark.gscenduro.webtime;

import java.net.InetAddress;
import java.net.Socket;

import se.gsc.stenmark.gscenduro.LogFileWriter;

public class ClientConnectionHandler implements Runnable{
	private String serverIp;
	private InetAddress serverAddr;
	private Socket socket = null;
	private WebTimeHandler webTimeHandler = null;

	public ClientConnectionHandler(String serverIp, WebTimeHandler webTimeHandler){
		LogFileWriter.writeLog("debugLog", "ClientConnectionHandler: Created new handler for IP: " +serverIp );
		this.serverIp = serverIp;
		this.webTimeHandler = webTimeHandler;
	}

	@Override
	public void run() {
		try {
			LogFileWriter.writeLog("debugLog", "ClientConnectionHandler: creating new socket to " +serverIp );
			serverAddr = InetAddress.getByName(serverIp);
			socket = new Socket(serverAddr, IncommingConnectionListener.SERVER_PORT);
			webTimeHandler.socketConnected(socket);
		} catch (Exception e) {
			LogFileWriter.writeLog(e);
		}		
	}

}
