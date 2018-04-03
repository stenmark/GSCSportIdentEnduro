package se.gsc.stenmark.gscenduro.webtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import se.gsc.stenmark.gscenduro.LogFileWriter;

public class IncommingMessageListener implements Runnable{
	private WebTimeHandler_ClientServerSocket webTimeHandler;
	private Socket socket;

	public IncommingMessageListener(WebTimeHandler_ClientServerSocket webTimeHandler, Socket socket){
		this.webTimeHandler = webTimeHandler;
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String newMessage ="";
				LogFileWriter.writeLog("debugLog", "Wait for new messages on listening socket");
				newMessage = inputReader.readLine();
				LogFileWriter.writeLog("debugLog", "Got new IP message on listening socket: " + newMessage);
				webTimeHandler.gotNewMsg(newMessage);
				if( newMessage == null){
					//If we got null, it means we lost the socket. Stop the listening thread. The caller needs to restart the thread when a new socket has been established
					break;					
				}
				if( newMessage.equals(WebTimeHandler_ClientServerSocket.MSG_ACK)){
					webTimeHandler.lastMsgAcked = true;
				}
				else{
					webTimeHandler.sendMessage(WebTimeHandler_ClientServerSocket.MSG_ACK);
				}
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}
}

