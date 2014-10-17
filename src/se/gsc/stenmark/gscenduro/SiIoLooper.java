package se.gsc.stenmark.gscenduro;

import java.io.IOException;
import java.io.InputStream;

import android.widget.TextView;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;

public class SiIoLooper implements IOIOLooper {
	private Uart uart; 
    private InputStream in; 
//    private OutputStream out;
    private Boolean imBusy = false; 
	private int messageIndex = 0;
	private final int bufferSize = 1000; 
	private byte[] message = new byte[bufferSize];
	private Boolean waitingForETX = false;
	
	private static final int INPIN 	= 4;
	private static final int OUTPIN = 5;
	private static final int BAUD 	= 38400;
	
	private static final byte STX 	= 0x02;
	private static final byte ETX 	= 0x03;
	
	private int i = 0;
	
	
	
	@Override
	public void setup(IOIO ioio) throws ConnectionLostException,
			InterruptedException {
		

        // Create the text view
        TextView textView = new TextView(MainActivity.instance);
        textView.setTextSize(40);
        textView.setText("setup");

        // Set the text view as the activity layout
        MainActivity.instance.setContentView(textView);
		
		try { 
			uart = ioio.openUart(INPIN, OUTPIN, BAUD, Uart.Parity.NONE, Uart.StopBits.ONE); 
            in = uart.getInputStream();
//            out = uart.getOutputStream();
		} 
		catch(ConnectionLostException e) 
		{ 
		    // iWrite(e.getMessage()); 
			// iWrite(Log.getStackTraceString(e)); 
		} 

	}

	@Override
	public void loop() throws ConnectionLostException, InterruptedException {
		i++;
//        // Create the text view
//        TextView textView = new TextView(MainActivity.instance);
//        textView.setTextSize(40);
//        textView.setText("i " + i);
//
//        // Set the text view as the activity layout
//        MainActivity.instance.setContentView(textView);

		
//		if(!imBusy) 
//        { 
//            imBusy = true; 
//            checkData();
//        } 

	}
	
	private void checkData() {	
    	try { 
    		int availableBytes =in.available(); 
    		if (availableBytes > 0) { 
    			byte[] readBuffer = new byte[bufferSize]; 
                in.read(readBuffer, 0, availableBytes);
                int start = findByte(readBuffer, availableBytes, STX);
                int stop = findByte(readBuffer, availableBytes, ETX);
                
                if (start < availableBytes)
                {
                	messageIndex = 0;
            		for (int index = start + 1; index < stop; index++){
            			message[messageIndex++] = readBuffer[index];
            		}
                	if (stop < availableBytes)
                	{
                		// TODO: parse message!
                	}
                	else
                		waitingForETX = true;
                }
                else if (waitingForETX)
                {
                	for (int index = 0; index < stop; index++)
            			message[messageIndex++] = readBuffer[index];
                	if (stop < availableBytes)
                	{
                		// TODO: parse message!
                		waitingForETX = false;
                	}
                }
    		} 
    		Thread.sleep(500); 
    	} catch (InterruptedException e) { 
    		//iWrite("Error: " +e ); 
    	} catch (IOException e) { 
    		// TODO Auto-generated catch block 
            // 
            // iWrite("Error: " +e );e.printStackTrace(); 
    	} 
    	imBusy = false; 
	}
	
	private int findByte(byte[] inBuffer, int length, byte data) {
		for (int index = 0; index < length; index++)
		{
			if (inBuffer[index] == data)
				return index;
		}
		return length;
	}
	

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incompatible() {
        // Create the text view
        TextView textView = new TextView(MainActivity.instance);
        textView.setTextSize(40);
        textView.setText("incompatible");

        // Set the text view as the activity layout
        MainActivity.instance.setContentView(textView);

	}

	@Override
	public void incompatible(IOIO ioio) {
        // Create the text view
        TextView textView = new TextView(MainActivity.instance);
        textView.setTextSize(40);
        textView.setText("incompatible IOIO");

        // Set the text view as the activity layout
        MainActivity.instance.setContentView(textView);

	}

}
