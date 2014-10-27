package se.gsc.stenmark.gscenduro;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartScreenFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private MainActivity mainActivity;
	OnNewCardListener newCardCallback;
	public SiDriver siDriver = null;
	
	public static final String CURRENT_COMPETITIOR_LIST_FILE = "current_comp_list";
	public static final String CURRENT_TRACK_FILE = "current_track";
	public static final String CURRENT_COMPETITION_NAME_FILE = "current_comp_name";
	
	public static long lastCalltime;
	public static int disconnectCounter;
	public static boolean disconected;


	
    // Container Activity must implement this interface
    public interface OnNewCardListener {
        public void onNewCard(Card card);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
        	newCardCallback = (OnNewCardListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNewCardListener");
        }
    }


	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static StartScreenFragment getInstance(int sectionNumber, MainActivity mainActivity) {
		StartScreenFragment fragment = null;
		fragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		fragment.mainActivity = mainActivity;
		return fragment;

	}

	public StartScreenFragment() {
	}

	
	@Override
	public void onResume(){
		super.onResume();
		updateTrackText();
		EditText nameOFCompEdit = (EditText) getView().findViewById(R.id.editSaveLoadComp);
		nameOFCompEdit.setText("New"); 
		
		if(mainActivity.track != null){
			if(!mainActivity.track.isEmpty()){
				nameOFCompEdit.setText(mainActivity.track.get(0).compName);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lastCalltime = System.currentTimeMillis();
		disconnectCounter = 0;
		disconected = true;
		
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
	   Button connectButton = (Button) rootView.findViewById(R.id.connectButton);
		   connectButton.setOnClickListener(new OnClickListener()
		   {
		             @Override
		             public void onClick(View v)
		             {
		            	 TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);
		            	 String connectMsg = connectToSiMaster();
		            	 statusTextView.setText(connectMsg);
		            	 disconected = false;
		            	 disconnectCounter = 0;
		            	 new SiCardListener().execute(siDriver);
		             } 
		   }); 
		   
	   Button addTrackButton = (Button) rootView.findViewById(R.id.addTrackButton);
	   addTrackButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 
	            	 EditText newTrack = (EditText) getView().findViewById(R.id.editTrackDefinition);
	            	 
	            	 parseNewTrack( newTrack.getText().toString() );
	            	 
	            	 updateTrackText();
	             } 
	   }); 
	   
	   Button addCompetitorButton = (Button) rootView.findViewById(R.id.addCompetitorButton);
	   addCompetitorButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 
	            	 EditText competitorName = (EditText) getView().findViewById(R.id.editCompetitorName);
	            	 Competitor competitor = new Competitor( competitorName.getText().toString() );
	            	 
	            	 EditText cardNumber = (EditText) getView().findViewById(R.id.editCardNumber);
	            	 String cardNumberAsString = cardNumber.getText().toString();
	            	 if( !cardNumberAsString.isEmpty() ){
	            		 try{
	            			 int cardNumberAsInt = Integer.parseInt(cardNumberAsString);
	            			 competitor.cardNumber = cardNumberAsInt;
	            			 
	            		 }
	            		 catch(Exception e){
	            			 
	            		 }
	            	 }
	            	 
	            	 mainActivity.competitors.add(competitor);	    
	            	 
	            	 TextView cardInfoTextView = (TextView) getView().findViewById(R.id.cardInfoTextView);
	            	 cardInfoTextView.setText("");
	            	 for(Competitor currentCompetitor : mainActivity.competitors){
	            		 cardInfoTextView.append( "Name: " + currentCompetitor.name + " Card "+ currentCompetitor.cardNumber + "\n");
	            	 }
	            	 
	             } 
	   }); 
	   
	   Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
	   saveButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 EditText nameOFCompToSave = (EditText) getView().findViewById(R.id.editSaveLoadComp);
	            	 String compName = nameOFCompToSave.getText().toString();
	            	 if(compName.isEmpty()){
	            		 return;
	            	 }
	            	 //TODO: work around, using trackmarker object to save competitions name...
	            	 if(mainActivity.track != null){
		            	 for(TrackMarker trackmarker : mainActivity.track){
		            		 trackmarker.compName = compName;
		            	 }
	            	 }
	            	 compName.replace(" ", "_");
	            	 mainActivity.instance.saveSessionData( compName );
	             } 
	   }); 
	   
	   Button loadButton = (Button) rootView.findViewById(R.id.loadButton);
	   loadButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 
	            	 EditText nameOFCompToLoad = (EditText) getView().findViewById(R.id.editSaveLoadComp);
	            	 mainActivity.instance.loadSessionData( nameOFCompToLoad.getText().toString(), true );
	             } 
	   }); 
	   
	   Button listButton = (Button) rootView.findViewById(R.id.listLoadedButton);
	   listButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 TextView statusText = (TextView) getView().findViewById(R.id.cardInfoTextView);
	            	 statusText.setText("Existing competitions \n");
	            	 String[] fileList = MainApplication.getAppContext().fileList();
	            	 for( String file : fileList){
	            		 if( file.contains("_list") ){
	            			 if(!file.equals(CURRENT_COMPETITIOR_LIST_FILE)){
		            			 String compName = file.replace("_list", "");
		            			 statusText.append(compName + "\n");
	            			 }
	            		 }
	            		 
	            	 }
	             } 
	   }); 
	   
	   Button newButton = (Button) rootView.findViewById(R.id.newCompButton);
	   newButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 //Hack to use loadseesiondata to update the GUI.
	            	 //Uses some ugly work arounds due to poor GUI design that i dont want to duplicate
	            	 mainActivity.track = new ArrayList<TrackMarker>();
	            	 mainActivity.competitors = new ArrayList<Competitor>();
	            	 mainActivity.instance.loadSessionData(null,false);
	             } 
	   }); 
	   
	   Button exportResultButton = (Button) rootView.findViewById(R.id.exportResultButton);
	   exportResultButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 exportResult();
	             } 
	   }); 

	   return rootView;
	} 	
	
	private void exportResult(){
		TextView status = (TextView) getView().findViewById(R.id.cardInfoTextView);
		String result = "Name,card number,total time,";
		for( int i = 0; i < mainActivity.track.size(); i++){
			result += "SS" + (i+1) + ",";
		}
		result += "\n";
		status.setText("");
		if( isExternalStorageWritable() ){	
			try{
				File sdCard = Environment.getExternalStorageDirectory();
				String compName = "New";
				if(mainActivity.track != null ){
					if(!mainActivity.track.isEmpty()){
						compName = mainActivity.track.get(0).compName;
					}	
				}
				compName.replace(" ", "_");
				
				File dir = new File (sdCard.getAbsolutePath() + "/gscEnduro");
				if( !dir.exists() ) {
					status.append("Dir does not exist " + dir.getAbsolutePath() );
					if( !dir.mkdirs() ){
						status.append("Could not create directory: " + dir.getAbsolutePath() );
						return;
					}
				}
				
				File file = new File(dir, compName + ".csv");
				status.append("Saving result to file:\n" + file.getAbsolutePath() + "\n");
					
				if(mainActivity.competitors != null && !mainActivity.competitors.isEmpty()){
					Collections.sort(mainActivity.competitors);
					for(Competitor competitor : mainActivity.competitors){
						status.append( competitor.name + "," + competitor.cardNumber + "," + competitor.getTotalTime(false) + ",");
						result += competitor.name + "," + competitor.cardNumber + "," + competitor.getTotalTime(false) + ",";				
						if(competitor.trackTimes != null ){
							for( long time : competitor.trackTimes){
								status.append( time + "," );
								result +=  time + "," ;
							}
						}
						else{
							for( int i = 0; i < mainActivity.track.size(); i++ ){
								status.append("0,");
								result += "0,";
							}
						}
						status.append("\n");
						result += "\n\n";
					}
				}
		        FileWriter fw = new FileWriter(file);
		        fw.write(result);
		        fw.close();
			}
			catch( Exception e){
				status.append("FAIL + " + e.getMessage() + "\n");
				for( StackTraceElement elem : e.getStackTrace()){
					status.append( elem.toString() + "\n");
				}
			}
		}
		else{
			status.setText("External file storage not available, coulr not export results");
		}
		
	}
	
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	
	public void updateTrackText(){
		 TextView trackInfoTextView = (TextView) getView().findViewById(R.id.trackInfoTextView);
		 if(mainActivity.track != null){
			 trackInfoTextView.setText("Current loaded Track: " );
			 int i = 0;
			 for( TrackMarker trackMarker : mainActivity.track){
				 i++;
				 trackInfoTextView.append( ", SS" + i + ": " + trackMarker.start + "->" + trackMarker.finish );
			 }
		 }
		 else{
			 trackInfoTextView.setText("No track loaded" );
		 }
		 
	}
	
    /** Called when the user clicks the Send button */
    public String connectToSiMaster() {
    	String msg = "";
    	siDriver = new SiDriver();
    	if( siDriver.connectDriver() ){
    		if( siDriver.connectToSiMaster() ){
    			msg = "SiMain " + siDriver.stationId + " connected";
    		}
    		else{
    			msg = "Failed ot connect SI master";
    		}
      	}
    	if( siDriver.mode != SiMessage.STATION_MODE_READ_CARD){
    		msg = "SiMain is not configured as Reas Si. Is configured as: " + SiMessage.getStationMode(siDriver.mode);
    	}
    	
    	return msg;
    }

    public void writeCard( Card card){
    	try{
	    	TextView cardText = (TextView) getView().findViewById(R.id.cardInfoTextView);
	    	if( card.cardNumber != 0){
	    		cardText.setText(card.toString());
	//    		cardText.append(card.toString()+"\n");
	    		cardText.append("\n" + card.errorMsg + "\n");
	    		newCardCallback.onNewCard(card);
	    	}
	    	else{
	    		cardText.append("\n" + card.errorMsg);
	    	}
	    	if(!disconected){
	    		new SiCardListener().execute(siDriver);
	    	}
    		else{
    			TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);
    			statusTextView.setText("Disconnected");
    		}
    	}
    	catch( Exception e){
    		if(!disconected){
    			new SiCardListener().execute(siDriver);
    		}

    	}
    }
    
    private void parseNewTrack( String newTrack){
    	String[] trackMarkers = newTrack.split(",");
    	mainActivity.track = new ArrayList<TrackMarker>();
    	String compName = "New";
    	if( mainActivity.track != null){
    		if(!mainActivity.track.isEmpty()){
    			compName = mainActivity.track.get(0).compName;
    		}
    	}
    	
    	for( int i = 0; i < trackMarkers.length; i+=2){
    		int startMarker = 0;
    		int finishMarker = 0;
    		try{
	    		startMarker = Integer.parseInt(trackMarkers[i]);
	    		finishMarker = Integer.parseInt(trackMarkers[i+1]);
    		}
    		catch( Exception e ){
    			return;
    		}
    		mainActivity.track.add( new TrackMarker(startMarker,finishMarker, compName));
    	}
    }
    
    private class SiCardListener extends AsyncTask<SiDriver, Void, Card> {    	
        /** The system calls this to perform work in a worker thread and
          * delivers it the parameters given to AsyncTask.execute() */
        protected Card doInBackground(SiDriver... siDriver) {
        	Card cardData = new Card();
        	while(true){
	    		byte[] readSiMessage = siDriver[0].readSiMessage(100, 50000, false);
	    		
//	        	Card card = new Card();
//	        	card.errorMsg = "Raw data ";
//	        	for( byte readbyte: readSiMessage){
//	        		card.errorMsg +=  "=0x" + SiDriver.byteToHex(readbyte) + ", ";
//	        	}
//	        	return card;
	    		
	    		
	    		if( readSiMessage.length >= 1 && readSiMessage[0]== SiMessage.STX ){
	    			if( readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF)  == 0x66 ){	    				
	    				siDriver[0].sendSiMessage(SiMessage.request_si_card6);
	    				cardData = siDriver[0].getCard6Data();
	    			
	    				siDriver[0].sendSiMessage(SiMessage.ack_sequence);
	    				
	    				return cardData;
	    			}
	    			else if( readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF)  == 0x46 ){
//	    				cardData.errorMsg += "Card5!!!";
	    				if( readSiMessage.length >= 3 && (readSiMessage[2] & 0xFF)  == 0x4f ){
	    					cardData.errorMsg += "Card pulled out";
	    					return cardData;
	    				}
	    				
	    				siDriver[0].sendSiMessage(SiMessage.request_si_card5);
	    				cardData = siDriver[0].getCard5Data();
	    				if( cardData == null ){
	    					cardData = new Card();
//	    					cardData.errorMsg = "Got null when reading Card5 data";
	    				}
	    				
	    				siDriver[0].sendSiMessage(SiMessage.ack_sequence);
	    				return cardData;
	    			}
	    			else{
	    				cardData.errorMsg += "not card6";
	    				return cardData;
	    			}
	    			
	    		}
	    		else{
	    			//Use this to check if we have been disconnected. If we have many faulty read outs from the Driver, assume disconenction.
	    			if( System.currentTimeMillis() - StartScreenFragment.lastCalltime < 1000 ){
	    				disconnectCounter++;
	    			}
	    			else{
	    				disconnectCounter = 0;
	    			}
	    			if(disconnectCounter > 10){
	    				disconected = true;
	    				siDriver[0].closeDriver();
	    			}
	    			StartScreenFragment.lastCalltime = System.currentTimeMillis();
    				cardData.errorMsg += "not STX or timeout";
    				return cardData;
	    		}
        	}		
        }
        
        /** The system calls this to perform work in the UI thread and delivers
          * the result from doInBackground() */
        protected void onPostExecute(Card newCard) {
        	writeCard(newCard);
        }
    }

	
}
