package se.gsc.stenmark.gscenduro.webtime;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.AsyncTask;
import se.gsc.stenmark.gscenduro.LogFileWriter;

public class ClientConnectionHandler extends AsyncTask<Void, Void, Socket> {
	private String serverIp;
	private InetAddress serverAddr;
	private Socket socket = null;
	private WebTimeHandler_ClientServerSocket webTimeHandler = null;

	public ClientConnectionHandler(String serverIp, WebTimeHandler_ClientServerSocket webTimeHandler){
		try{
			LogFileWriter.writeLog("debugLog", "ClientConnectionHandler: Created new handler for IP: " +serverIp );
			this.serverIp = serverIp.replace(" ", "");
			this.webTimeHandler = webTimeHandler;
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	@Override
	protected Socket doInBackground(Void... params) {
		try {
			LogFileWriter.writeLog("debugLog", "ClientConnectionHandler: creating new socket to " +serverIp );
			serverAddr = InetAddress.getByName(serverIp);
			socket = new Socket();

			socket.connect(new InetSocketAddress(serverAddr,IncommingConnectionListener.SERVER_PORT), 5000);
		} catch (Exception e) {
			LogFileWriter.writeLog(e);
			return null;
		}	
		return socket;
	}
	
	protected void onPostExecute(Socket socket) {
		try{
			if(socket == null){
				webTimeHandler.failedToConnectSocket(serverIp);
			}
			else{
				webTimeHandler.socketConnected(socket, serverIp);
			}
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
		}
	}

}
