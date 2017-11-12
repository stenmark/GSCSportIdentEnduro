package se.gsc.stenmark.gscenduro.webtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.AsyncTask;
import se.gsc.stenmark.gscenduro.LogFileWriter;

public class IncommingMessageListener extends AsyncTask<Socket, Void, String> {
	private Socket socket;
	private WebTimeHandler webTimeHandler;
	
	public IncommingMessageListener(WebTimeHandler webTimeHandler){
		this.webTimeHandler = webTimeHandler;
	}
	
	@Override
	protected String doInBackground(Socket... sockets) {
		socket = sockets[0];
		String newMessage ="";
		try{
			LogFileWriter.writeLog("debugLog", "Wait for new messages on listening socket");
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			newMessage = inputReader.readLine();
			LogFileWriter.writeLog("debugLog", "Got new IP message on listening socket: " + newMessage);
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
		return newMessage;
	}	
	
	protected void onPostExecute(String msg) {
		webTimeHandler.gotNewMsg(msg, socket);
	}
}

