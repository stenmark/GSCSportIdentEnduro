package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
	OnNewCardListener newCardCallback;
	public static StartScreenFragment instance = null;
	
	public static final String CURRENT_COMPETITIOR_LIST_FILE = "current_comp_list";
	public static final String CURRENT_TRACK_FILE = "current_track";


	
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
	public static StartScreenFragment getInstance(int sectionNumber) {
		StartScreenFragment fragment = null;
//		if( instance == null ){
			fragment = new StartScreenFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			instance = fragment;
//		}
		 
		return instance;
	}

	public StartScreenFragment() {
	}

	
	@Override
	public void onResume(){
		super.onResume();
		updateTrackText();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		            	 new SiCardListener().execute(MainActivity.siDriver);
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
	            	 
	            	 MainActivity.competitors.add(competitor);	    
	            	 
	            	 TextView cardInfoTextView = (TextView) getView().findViewById(R.id.cardInfoTextView);
	            	 cardInfoTextView.setText("");
	            	 for(Competitor currentCompetitor : MainActivity.competitors){
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
	            	 compName.replace(" ", "_");
	            	 MainActivity.instance.saveSessionData( compName );
	             } 
	   }); 
	   
	   Button loadButton = (Button) rootView.findViewById(R.id.loadButton);
	   loadButton.setOnClickListener(new OnClickListener()
	   {
	             @Override
	             public void onClick(View v)
	             {
	            	 
	            	 EditText nameOFCompToLoad = (EditText) getView().findViewById(R.id.editSaveLoadComp);
	            	 MainActivity.instance.loadSessionData( nameOFCompToLoad.getText().toString(), true );
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
	            	 MainActivity.instance.track = new ArrayList<TrackMarker>();
	            	 MainActivity.instance.competitors = new ArrayList<Competitor>();
	            	 MainActivity.instance.loadSessionData(null,false);
	             } 
	   }); 

	   return rootView;
	} 	
	
	public void updateTrackText(){
		 TextView trackInfoTextView = (TextView) getView().findViewById(R.id.trackInfoTextView);
		 if(MainActivity.track != null){
			 trackInfoTextView.setText("Current loaded Track: " );
			 int i = 0;
			 for( TrackMarker trackMarker : MainActivity.track){
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
    	MainActivity.siDriver = new SiDriver();
    	if( MainActivity.siDriver.connectDriver() ){
    		if( MainActivity.siDriver.connectToSiMaster() ){
    			msg += "SiMaster connected";
    		}
    		else{
    			msg += "Failed ot connect SI master";
    		}
      	}
    	
    	return msg;
    }

    public void writeCard( Card card){
    	TextView cardText = (TextView) getView().findViewById(R.id.cardInfoTextView);
    	if( card.errorMsg.isEmpty()){
    		cardText.setText(card.toString());
    		newCardCallback.onNewCard(card);
    	}
    	else{
    		cardText.append("\n" + card.errorMsg);
    	}
    	
    	new SiCardListener().execute(MainActivity.siDriver);
    }
    
    private void parseNewTrack( String newTrack){
    	String[] trackMarkers = newTrack.split(",");
    	MainActivity.track = new ArrayList<TrackMarker>();
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
			MainActivity.track.add( new TrackMarker(startMarker,finishMarker));
    	}
    }
    
    private class SiCardListener extends AsyncTask<SiDriver, Void, Card> {
        /** The system calls this to perform work in a worker thread and
          * delivers it the parameters given to AsyncTask.execute() */
        protected Card doInBackground(SiDriver... siDriver) {
        	Card card6Data = new Card();
        	while(true){
	    		byte[] readSiMessage = siDriver[0].readSiMessage(100, 50000, false);
	    		if( readSiMessage.length >= 1 && readSiMessage[0]== SiMessage.STX ){
	    			if( readSiMessage.length >= 2 && readSiMessage[1] == 0x66 ){	    				
	    				siDriver[0].sendSiMessage(SiMessage.request_si_card6, true);
	    				card6Data = siDriver[0].getCard6Data();
	    			
	    				siDriver[0].sendSiMessage(SiMessage.ack_sequence, true);
	    				
	    				return card6Data;
	    			}
	    			else{
	    				card6Data.errorMsg += "not card6";
	    				return card6Data;
	    			}
	    			
	    		}
	    		else{
    				card6Data.errorMsg += "not STX or timeout";
    				return card6Data;
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
