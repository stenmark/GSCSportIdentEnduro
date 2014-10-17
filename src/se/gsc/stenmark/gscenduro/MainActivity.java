package se.gsc.stenmark.gscenduro;

import java.io.IOException;
import java.util.Arrays;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;


import android.app.Activity;
import android.content.Context;

import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity{
	public final static String EXTRA_MESSAGE = "se.gsc.stenmark.gscenduro.MESSAGE";
	
	private UsbSerialDriver driver;
	
	public static MainActivity instance;
	
	String msg = "";

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
        
//        EditText editTxt = new EditText(this);
//        editTxt.setTextSize(10);
//        editTxt.setText(text);
//        setContentView(editTxt);
    }
        
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
    	if( connectDriver() ){
    		if( handShakeWithRetry() ){
	    		msg += "Wait for input\n";
	    		byte[] readSiMessage = readSiMessage(100, 50000, false);
	    		if( readSiMessage.length >= 1 && readSiMessage[0]== SiMessage.STX ){
	    			msg += "STX\n";
	    			if( readSiMessage.length >= 2 && readSiMessage[1] == 0x66 ){
	    				msg += "Card6\n";
	    				
	    				sendSiMessage(SiMessage.request_si_card6, true);
	    				getCard6Data();
	    			
	    				sendSiMessage(SiMessage.ack_sequence, true);
	    				
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
    
    private void getCard6Data( ){
    	byte[] allData = new byte[128*3];
    	
    	boolean compact = false;
		for(int blockNumber = 0; blockNumber < 3 ; blockNumber++){
			msg += "Loop " + blockNumber + "\n";
			byte[] rawData = readSiMessage(256, 1000, false);
			MessageBuffer messageBuffer = new MessageBuffer(rawData);
			byte[] dleOutputPre = new byte[10];
			readBytesDle(messageBuffer, dleOutputPre, 0, 4);
			byte[] dleOutput = new byte[150];
			int bytesRead = readBytesDle(messageBuffer, dleOutput, 0, 128);
			
			byte[] dleBytesCorrectSize = new byte[bytesRead];
			for( int i = 0; i < dleBytesCorrectSize.length; i++){
				dleBytesCorrectSize[i] = dleOutput[i];
			}
			
			msg += "DLE READ: bytesRead " + bytesRead + "  ";
			for(int i = 0; i < dleOutput.length; i++){
				msg += i + "=0x" + byteToHex(dleOutput[i]) + ", ";
			}
			msg+= "\n";
			
			
			
			for(int i = 0; i < dleBytesCorrectSize.length; i++){
				allData[(blockNumber*128)+i] = dleBytesCorrectSize[i];
			}
			
			
//			msg += "Card6 block:  ";
//			for(int i = 0; i < card6Block.blockData.length; i++){
//				msg += "[" + i + "]=" + byteToHex(card6Block.blockData[i]) + ", ";
//			}
//			msg+= "\n";
//			if( !card6Block.readSuccesful ){
//				if( blockNumber <= 2 ){
//					msg += "Reading card6 block failed, less than 2 blocks\n";
//					return;
//				}
//				else{
//					msg += "Break from block read loop\n";
//					break;
//				}
//			}
//			
//			if( blockNumber > 2){
//				msg += "Compact is true\n";
//				compact = true;
//			}
			
		}
		
		parseCard6( allData );
    	
    }
    
    private Card parseCard6( byte[] card6Data ){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	long cardNumber = 0;
    	int cardHi = makeIntFromBytes( card6Data[11], card6Data[10] );
    	int cardLow = makeIntFromBytes( card6Data[13], card6Data[12] );
    	cardNumber = (cardHi*65536) + cardLow;
    	card.cardNumber = cardNumber;
    	
    	msg += "CardNumber= " + cardNumber + "\n";
    	
    	dataPos += 16;
    	
    	card.startPunch = analysePunch(card6Data, dataPos+8);	
    	card.finishPunch = analysePunch(card6Data, dataPos+4); 	
    	card.checkPunch = analysePunch(card6Data, dataPos+12); 
    	int numberOfPunches = card6Data[dataPos+2];
    	msg += "Number of punches = " + numberOfPunches + "\n";
    	card.numberOfPunches = numberOfPunches;

    	
    	dataPos+=128-16;
    	
    	for(int i = 0; i < card.numberOfPunches; i++)
    	{
//    		AnalysePunch(data+4*k, card.Punch[k].Time, card.Punch[k].Code);
    		Punch punch = analysePunch(card6Data, dataPos+(4*i) );
    		card.punches.add(punch);
    	}
    	
    	msg += card.toString() + "\n";

    	return card;
    	
//    	if( card6Data.length > 128){
//    		return true;
//    	}
//    	else{
//    		return false;
//    	}
    }
    
    private Punch analysePunch( byte[] data, int pos){
    	int time = 0;
    	int control = 0;
    	if( !( (data[pos] & 0xFF) == 0xEE && (data[pos+1] & 0xFF) == 0xEE && 
    		   (data[pos+2] & 0xFF) == 0xEE && (data[pos+3] & 0xFF) == 0xEE)  ){
    		byte ptd = data[pos];
    		byte cn = data[pos+1];
    		byte pth = data[pos+2];
    		byte ptl = data[pos+3];
    		
    		control = cn+256*((ptd>>6)%0x3);
    		time = makeIntFromBytes(ptl,pth)+3600*12*(ptd&0x1);
    	}
    	else{
    		control = -1;
    		time = 0;
    	}
    	
    	return new Punch(time, control);
    	
    }
    
    
    private boolean handShakeWithRetry(){
    	if( handShake(true) ){
    		return true;
    	}
    	else{
    		if( handShake(false) ){
    	    	//Flush buffer
    	    	readSiMessage(16, 500, false);
    	    	return true;
    		}
    		return false;
    	}

    }
    
    private boolean handShake( boolean withStartup){
		byte[] startupResponse = new byte[16];
    	if( withStartup ){
	    	msg += "Send startup sequence \n";
			sendSiMessage(SiMessage.startup_sequence, false);
			sleep(700);
			startupResponse = readSiMessage(16, 500, false);
		}
		if( (startupResponse.length >= 1 && startupResponse[0]== SiMessage.STX ) || (withStartup == false)) {
			msg += "Startup handshake \n ";
			
			sendMessage( appendMarkesAndCrcToMessage(SiMessage.read_system_data.sequence() ), false);
			msg += "Read system data sent\n";
			
			byte[] systemData = readSiMessage(32, 8000, false);
			if( (systemData[0] == SiMessage.STX) &&  
				(systemData.length > 14) ){
				int stationId = makeIntFromBytes( systemData[4], systemData[3] );
								
				byte pr = systemData[6+4];
				int mode = systemData[6+1] & 0xFF;

				boolean extended = (pr&0x1) !=0 ;
				boolean handShake= (pr&0x4) !=0;
				boolean autoSend = (pr&0x2)!=0;
				
				msg += "System data is read Station ID " + stationId + " extended: " + extended + 
						" handshake: " + handShake + " autosend: " + autoSend + " Mode: " + SiMessage.getStationMode(mode) + "\n";
			}
			else{
				msg += "Too few bytes read for systemdata\n";
				return false;
			}
		}
		else{
			msg += "Starup handshake failed\n";
			return false;
		}
		return true;
    }
    

    private void sendMessage(byte[] message, boolean verbose){
    	int bytesWritten = 0;
    	try {
    		bytesWritten = driver.write(message, 1000 );
		} catch (IOException e) {
			msg += " : Write expception: " + e.getMessage() + " cause: " + e.getCause();
		}
    	if( verbose ){
    		msg += " : bytes written " + bytesWritten+ "\n";
    	}
    }
    
    private void sendSiMessage(SiMessage message, boolean verbose){
    	int bytesWritten = 0;
    	try {
    		bytesWritten = driver.write(message.sequence(), 1000 );
		} catch (IOException e) {
			msg += " : Write expception: " + e.getMessage() + " cause: " + e.getCause();
		}
    	if( verbose){
    		msg += " : bytes written " + bytesWritten+ "\n";
    	}
    }
    
    private byte[] readSiMessage( int size, int timeout, boolean verbose){
	    byte buffer[] = new byte[size];
	    int numBytesRead = -2;
		try {
			numBytesRead = driver.read(buffer, timeout);
		} catch (IOException e) {
			msg += "Exceptiion when reading " + e.getMessage();
		}
	    
//		if( verbose){
//			msg += "Read numbytes: " + numBytesRead + " Data: ";
//		    for( int i = 0; i < numBytesRead; i++){
//		    	msg += "[" + i + "]=" + byteToHex(buffer[i]) + ", ";
//		    }
//		    	msg += "\n";
//	    }
		
		if (verbose) {
			msg += "Read numbytes: " + numBytesRead + " Data: ";
			for (int i = 0; i < numBytesRead; i++) {
				msg += "0x" + byteToHex(buffer[i]) + ", ";
			}
			msg += "\n";
		}
	   
		byte[] result = Arrays.copyOfRange(buffer, 0, numBytesRead);
	    return result;
	    
		
    }
    
	private boolean connectDriver() {
		msg += "Connecting driver \n";

		// Get UsbManager from Android.
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		if (manager == null) {
			msg += " : Manager Null";
			return false;
		}

		driver = UsbSerialProber.acquire(manager);

		if (driver != null) {
			try {
				driver.open();
			} catch (IOException e1) {
				msg += "IOException 1";
				return false;
			}
			try {
				driver.setParameters(38400, UsbSerialDriver.DATABITS_8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			} catch (IOException e) {
				msg += "Set parms exception " + e.getMessage();
			}

		} else {
			msg += ": driver null";
			return false;
		}

		msg += "Driver connected \n";
		return true;
	}
	
	private void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private byte[] appendMarkesAndCrcToMessage( byte[] message){
    	int crcInt = CRCCalculator.crc(message);
    	byte[] crcBytes = intToDubbleByte(crcInt);
    	
    	byte[] result = new byte[message.length+4];
    	result[0] = SiMessage.STX;
    	for(int i = 0; i < message.length; i++){
    		result[i+1] = message[i];
    	}
    	result[message.length+1] = crcBytes[0];
    	result[message.length+2] = crcBytes[1];
    	result[message.length+3] = SiMessage.ETX;
    	
    	return result;
    	
    }
    
    private byte[] intToDubbleByte(int input){
    	byte result[] = new byte[2];
    	
    	int high = (input & 0xFF00);
    	high = high /256;
    	result[0] = (byte) high;
    	
    	int low = (input & 0x00FF);
    	result[1] = (byte) low;
    	
    	return result;
    	
    }
    
    private int makeIntFromBytes( byte low, byte high){
    	int highInt = high & 0xFF;
    	int lowInt = low & 0XFF;
    	
    	return (highInt*256)+lowInt;
    }
    
    public String byteToHex(byte b){
    	  int i = b & 0xFF;
    	  return Integer.toHexString(i);
    	}
       	
    
    /**
     * Send in a preread data buffer and extract data between DLE
     * @param data preRead databuffer
     * @param bytes output data of all read bytes
     * @param pos where to start put the new data in the bytes area
     * @param len number of bytes to read from the databuffer
     * @return
     */
    private int readBytesDle( MessageBuffer data, byte[] bytes, int pos, int len ){
    	byte[] localBytes = data.readBytes( len );
	
    	if( localBytes.length > 0 ){
        	int ip = 0;
        	int op = 0;
        	
        	for( ip = 0; ip  < localBytes.length-1; ip++ ){
        		if( localBytes[ip] == 0x10 ){
        			localBytes[op++] = localBytes[++ip];
        		}
        		else{
        			localBytes[op++]=localBytes[ip];
        		}
        	}
    		
    		if( ip < localBytes.length ){
    			if( localBytes[ip] == 0x10 ){
    				byte[] readByte = data.readByte();
    				localBytes[op++] = readByte[0];
    			}
    			else{
    				localBytes[op++] = localBytes[ip];
    			}
    		}
    		
    		if( op < len ){
    			for(int i = 0; i < localBytes.length; i++ ){
    				bytes[i+pos] = localBytes[i];
    			}
    			return op+readBytesDle(data, bytes, op, len-op);
    		}
    		else{
    			for(int i = 0; i < localBytes.length; i++ ){
    				bytes[i+pos] = localBytes[i];
    			}
    			return len;
    		}
    	}

    	return 0;
    	 
    }
    
//    int SportIdent::ReadBytesDLE(BYTE *byte, DWORD len,  HANDLE hComm)
//    {
//    	if(!hComm)
//    		return -1;
//
//    	DWORD dwRead;
//
//    	if(ReadFile(hComm, byte, len, &dwRead, NULL))
//    	{
//    		if(dwRead > 0)
//    		{
//    			DWORD ip=0;
//    			DWORD op=0;
//
//    			for (ip=0;ip<dwRead-1;ip++) {
//    				if(byte[ip]==DLE) //if byte is DLE opy next byte to current
//    					byte[op++]=byte[++ip];
//    				else
//    					byte[op++]=byte[ip];
//    			}
//
//    			if (ip<dwRead) {
//    				if(byte[ip]==DLE)
//    					ReadByte(byte[op++], hComm);
//    				else
//    					byte[op++]=byte[ip];
//    			}
//
//    			if(op<len)
//    				return op+ReadBytesDLE(byte+op, len-op, hComm);
//    			else return len;
//    		}
//    		return 0;		
//    	}
//    	else return -1;
//    }
}
    
