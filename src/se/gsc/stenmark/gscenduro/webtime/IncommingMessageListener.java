package se.gsc.stenmark.gscenduro.webtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import se.gsc.stenmark.gscenduro.LogFileWriter;

public class IncommingMessageListener implements Runnable{
	private WebTimeHandler webTimeHandler;
	private Socket socket;

	public IncommingMessageListener(WebTimeHandler webTimeHandler, Socket socket){
		this.webTimeHandler = webTimeHandler;
		this.socket = socket;
	}

	@Override
	public void run() {
		while(true){
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
			webTimeHandler.gotNewMsg(newMessage);
		}

	}
}

