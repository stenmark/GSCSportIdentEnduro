package se.gsc.stenmark.gscenduro;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity{
	private static SiDriver siDriver = null;
	public static MainActivity instance;
	
	public String msg = "";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }

        
        return super.onOptionsItemSelected(item);
    }
    
    private void writeInfo( String text){
        TextView textView = new TextView(this);
        textView.setTextSize(10);
        textView.setText(text);
        setContentView(textView);
    }
        
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
    	siDriver = new SiDriver( this );
    	if( siDriver.connectDriver() ){
    		if( siDriver.connectToSiMaster() ){
    			
	    		msg += "Wait for input\n";
	    		byte[] readSiMessage = siDriver.readSiMessage(100, 50000, false);
	    		if( readSiMessage.length >= 1 && readSiMessage[0]== SiMessage.STX ){
	    			msg += "STX\n";
	    			if( readSiMessage.length >= 2 && readSiMessage[1] == 0x66 ){
	    				msg += "Card6\n";
	    				
	    				siDriver.sendSiMessage(SiMessage.request_si_card6, true);
	    				siDriver.getCard6Data();
	    			
	    				siDriver.sendSiMessage(SiMessage.ack_sequence, true);
	    				
	    			}
	    			else{
	    				msg += "not card6\n";
	    			}
	    			
	    		}
	    		else{
	    			msg += "not STX";
	    		}
    		}
      	}
    	
    	
    	writeInfo(msg);     
    }
    

    


    

	

	

    
}
    
