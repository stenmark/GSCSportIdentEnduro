package se.gsc.stenmark.gscenduro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;


public abstract class LogFileWriter {

	//Private constructor to avoid instantiation
	private LogFileWriter() {
	}
	
	public static void writeLog( Exception e){
		String errorMessage = "\n" + e.getMessage() + "\n";
		for (StackTraceElement element : e.getStackTrace()) {
			errorMessage += element.toString() + "\n";
		}
		LogFileWriter.writeLog("stacktrace", errorMessage);
	}
	public static void writeLog( String directory, String message){		
		BufferedWriter bw  = null;
    	File file = null;
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
    	String timeStamp = format.format(Calendar.getInstance().getTime());

    	try{
	    	File sdCard = Environment.getExternalStorageDirectory();
	    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro/" + directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if(directory.equals("debugLog")){
				file = new File(dir, "debug.log");
				message = timeStamp + " " + message + "\n";
			}
			else if(directory.equals("stacktrace")){
				file = new File(dir, "stacktrace.log");
				message = timeStamp + " " + message + "\n";
			}
			else if(directory.equals("cardLog")){
				file = new File(dir, "cardLog.log");
				message = timeStamp + " " + message + "\n";
			}
			else{
				file = new File(dir, "cardDebugData_" + timeStamp + ".card");
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(message);
    	} catch (IOException e) {
		}
    	catch (NoClassDefFoundError e) {
    		return;
		}
    	finally{
			try {
				if( bw != null){
					bw.close();
	    		}
			} 
	    	catch (IOException e1) { }	
    	}
		
	}

}
