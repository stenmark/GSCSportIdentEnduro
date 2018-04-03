package se.gsc.stenmark.gscenduro.webtime;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.AsyncTask;
import se.gsc.stenmark.gscenduro.LogFileWriter;

public class IncommingConnectionListener extends AsyncTask<Void, Void, Socket> {
		public static final int SERVER_PORT = 9090;
		private WebTimeHandler_ClientServerSocket webTimeHandler;

		public IncommingConnectionListener( WebTimeHandler_ClientServerSocket webTimeHandler2){
			this.webTimeHandler = webTimeHandler2;
		}
		
		@Override
		protected Socket doInBackground(Void... params) {
			try{
				ServerSocket serverSocket = null;
				Socket socket = null;
				try {
					serverSocket = new ServerSocket( SERVER_PORT );
					LogFileWriter.writeLog("debugLog", "Waiting for new webTime connection");
					socket = serverSocket.accept();
					LogFileWriter.writeLog("debugLog", "Got new webTime connection");
				} catch (Exception e) {
					LogFileWriter.writeLog(e);
				}
				finally{
					try {
						if( serverSocket != null){
							serverSocket.close();
						}
					} catch (IOException e) {
						LogFileWriter.writeLog(e);
					}
				}
				return socket;
			}
			catch( Exception e){
				LogFileWriter.writeLog(e);
				return null;
			}
		}	
		
		protected void onPostExecute(Socket socket) {
			try{
				webTimeHandler.gotNewServerSocket(socket);
			}
			catch( Exception e){
				LogFileWriter.writeLog(e);
			}
		}
	}
