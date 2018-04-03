package se.gsc.stenmark.gscenduro.webtime;

import java.io.InputStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import se.gsc.stenmark.gscenduro.LogFileWriter;
import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class WebTimeHandler {
	private WebTimePeristentData webTimeData;
	private MainActivity mainActivity = null;
	private Timer pollFtpTimer = null;
	private PollFtpTask pollFtpTask = null;
	
	private static final int FTP_POLL_TIME_MS = 5000;
	private static final String COMPETITORS_ON_TRACK_FTP_FILE = "/homes/webTime/Competitors_on_track.txt";

	public WebTimeHandler( MainActivity mainActivity, WebTimePeristentData webTimeData ) {
		try{
			this.webTimeData = webTimeData;
			for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
				webTimeData.competitorsOnStage[i] = null;
			}
			this.mainActivity = mainActivity;
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	/**
	 * Callback from GUI thread. When the user presses Connect button this method is called.
	 * @param serverIp
	 */
	public void connectToIp(String serverIp) {
		try{
			webTimeData.serverIp = serverIp;
			pollFtpTimer = new Timer();
			pollFtpTask = new PollFtpTask( serverIp );
			pollFtpTimer.schedule(pollFtpTask,0,FTP_POLL_TIME_MS);
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
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
					return;
				}
			}
		}
		catch(Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	public void removeCompetitorOnStage( Competitor competitor){
		try{
			for( int i = 0; i < webTimeData.competitorsOnStage.length; i++){
				if(webTimeData.competitorsOnStage[i] == competitor ){
					webTimeData.competitorsOnStage[i] = null;
					return;
				}
			}
		}
		catch( Exception e){
			LogFileWriter.writeLog(e);
		}
	}

	public Competitor[] getCompetitorsOnstage(){
		return webTimeData.competitorsOnStage;
	}


	public WebTimePeristentData getPersistentData(){
		return webTimeData;
	}
	


	class PollFtpTask extends TimerTask {
		private String serverIp;
		private FTPClient ftpClient = null;
		
		public PollFtpTask(String serverIp) {
			this.serverIp = serverIp;
		}
		
		private void connectFtp(){
			try{
				ftpClient = new FTPClient();
				ftpClient.connect(serverIp, 21);
				ftpClient.login("", "");
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
				LogFileWriter.writeLog("debugLog", "connected to the FTP server: " +serverIp );
				mainActivity.updateStatus("Connected");
			}
			catch( Exception e){
				LogFileWriter.writeLog(e);
				LogFileWriter.writeLog("debugLog", "Could not connect to FTP server: " +serverIp );
				mainActivity.updateStatus("Disconnected");
			}
		}
		
		@Override
		public void run() {
			try{
				if( ftpClient != null ){
					if( ftpClient.isConnected() ){
						InputStream inputStream = ftpClient.retrieveFileStream(COMPETITORS_ON_TRACK_FTP_FILE);
						if( inputStream != null ){
							Scanner scanner = new Scanner(inputStream);
							while( scanner.hasNextLine() ){
								LogFileWriter.writeLog("debugLog", "FTP File content: " + scanner.nextLine() );
							}
							scanner.close();
							inputStream.close();
						}
						ftpClient.completePendingCommand();
					}
					else{
						connectFtp();
					}
				}
				else{
					connectFtp();
				}
			}
			catch( Exception e){
				LogFileWriter.writeLog(e);
			}
		}
	}
}
