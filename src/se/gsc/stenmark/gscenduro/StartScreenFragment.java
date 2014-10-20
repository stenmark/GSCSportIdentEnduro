package se.gsc.stenmark.gscenduro;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	public static StartScreenFragment newInstance(int sectionNumber) {
		StartScreenFragment fragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public StartScreenFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
	   Button button = (Button) rootView.findViewById(R.id.connectButton);
		   button.setOnClickListener(new OnClickListener()
		   {
		             @Override
		             public void onClick(View v)
		             {
		            	 TextView sampleTextView = (TextView) getView().findViewById(R.id.statusText);
		            	 String connectMsg = connectToSiMaster();
		            	 sampleTextView.setText(connectMsg);
		            	 new SiCardListener().execute(MainActivity.siDriver);
		             } 
		   }); 

		return rootView;
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
