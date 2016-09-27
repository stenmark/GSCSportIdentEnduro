package se.gsc.stenmark.gscenduro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.os.Environment;


public abstract class LogFileWriter {

	//Private constructor to avoid instantiation
	private LogFileWriter() {
	}
	
	public static void writeLog( String directory, String message){
    	BufferedWriter bw  = null;
    	File file = null;
    	String timeStamp = Calendar.getInstance().getTime().toString().replace(" ", "_").replace(":", "").replace("CEST", "");
    	try{
	    	File sdCard = Environment.getExternalStorageDirectory();
	    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro/" + directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if(directory == "debugLog"){
				file = new File(dir, "debug.log");
				message = timeStamp + " " + message;
			}
			else{
				file = new File(dir, "cardDebugData_" + timeStamp + ".card");
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(message);
    	} catch (IOException e) {
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
