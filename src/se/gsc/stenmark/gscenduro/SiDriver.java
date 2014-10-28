package se.gsc.stenmark.gscenduro;

import java.io.IOException;
import java.util.Arrays;
import android.hardware.usb.UsbManager;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class SiDriver {
	private UsbSerialDriver driver;
	
	public int stationId;
	public int mode;
	public boolean extended;
	public boolean handShake;
	public boolean autoSend;

	SiDriver( ){
		stationId = -1;
		mode = -1;
		extended = false;
		handShake = false;
		autoSend = false;
	}
	
    public boolean connectToSiMaster(){
    	if( performHandShake(true) ){
    		return true;
    	}
    	else{
    		if( performHandShake(false) ){
    	    	//Flush buffer
    	    	readSiMessage(16, 500, false);
    	    	return true;
    		}
    		return false;
    	}

    }
    
    private boolean performHandShake( boolean withStartup){
		byte[] startupResponse = new byte[16];
    	if( withStartup ){
			sendSiMessage(SiMessage.startup_sequence);
			sleep(700);
			startupResponse = readSiMessage(16, 500, false);
		}
		if( (startupResponse.length >= 1 && startupResponse[0]== SiMessage.STX ) || (withStartup == false)) {
			sendMessage( appendMarkesAndCrcToMessage(SiMessage.read_system_data.sequence() ));
			byte[] systemData = readSiMessage(32, 8000, false);
			if( (systemData[0] == SiMessage.STX) &&  
				(systemData.length > 14) ){
				stationId = makeIntFromBytes( systemData[4], systemData[3] );
								
				byte pr = systemData[6+4];
				mode = systemData[6+1] & 0xFF;

				extended = (pr&0x1) !=0 ;
				handShake= (pr&0x4) !=0;
				autoSend = (pr&0x2)!=0;
							}
			else{
				return false;
			}
		}
		else{
			return false;
		}
		return true;
    }
    
	private void sendMessage(byte[] message) {
		try {
			driver.write(message, 1000);
		} 
		catch (IOException e) {
			
		}
	}
    
    public void sendSiMessage(SiMessage message){
    	try {
    		 driver.write(message.sequence(), 1000 );
		} catch (IOException e) {

		}

    }
    
    //Work around, ReadDle is not woeking as expected for card5, not found why yet.
    //This one hacks the SI interface by finding the Fist station position in the data array and
    //then just hard fetches 4 bytes at a time from there, removing DLE (0x10) marker and extract station ID and time
    private Card parseCard5Alt( byte[] dleData, byte[] allData){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	long cardNumber = 0;
    	cardNumber = makeIntFromBytes( dleData[5], dleData[4] );
    	
    	if(dleData[6]==1)
    		card.cardNumber=cardNumber;
    	else
    		card.cardNumber=100000*dleData[6]+cardNumber;
    	card.errorMsg += "Card Number: " + cardNumber + "\n"; 
    	
    	dataPos += 16;
    	
    	card.startPunch = analyseSi5Time(dleData, dataPos+3);
    	card.finishPunch = analyseSi5Time(dleData, dataPos+5);
    	card.checkPunch = analyseSi5Time(dleData, dataPos+9);
    	
    	int numberOfPunches = dleData[dataPos+7]-1;
    	card.numberOfPunches = numberOfPunches;
    	card.errorMsg += "Number of punches: " + numberOfPunches + "\n"; 
    	
    	byte firstStartMaker = (byte) MainActivity.track.get(0).start;
    	int firstMarkerPos = 0;
    	int i = 0;
    	for( byte data : allData ){
    		if( firstStartMaker == data){
    			firstMarkerPos = i;
    			break;
    		}
    		i++;
    	}
    	card.errorMsg += "firstMarkerPos " + firstMarkerPos + "\n"; 
    	
    	int currentPos = 0;
    	for(int k = 0; k < card.numberOfPunches; k++)
    	{
    		byte stationId = allData[firstMarkerPos + currentPos];
    		currentPos++;
    	
    		byte byte1;
    		int byte1Pos;
    		if( allData[firstMarkerPos + currentPos] == 0x10){
    			currentPos++;
    		}
    		byte1 = allData[firstMarkerPos + currentPos];
    		byte1Pos = firstMarkerPos + currentPos;
    		int byte2Pos = 0;
    		currentPos++;
    		byte byte2;
    		if( allData[firstMarkerPos + currentPos] == 0x10){
    			byte2 = allData[firstMarkerPos + currentPos+1];
    			byte2Pos = firstMarkerPos + currentPos+1;
    			currentPos++;
    			currentPos++;
//    			if( allData[firstMarkerPos + currentPos] == 0x00){
//    				currentPos++;
//    			}
    		}
    		else{
    			byte2 = allData[firstMarkerPos + currentPos];
    			byte2Pos = firstMarkerPos + currentPos;
    			currentPos++;
    		}
    		int time = makeIntFromBytes(byte2, byte1 );
    		
    		if( allData[firstMarkerPos + currentPos] == 0x10){
    			currentPos++;
    			if(allData[firstMarkerPos + currentPos] == 0x00){
    				currentPos++;
    			}
    		}
    		
    		Punch punch = new Punch(time, stationId);
    		
    		card.errorMsg += "loop nr " + k + " byte1: " + byte1Pos + " : " + byte1 + "  byte2: "+ byte2Pos + " : " + byte2 + "\n";
    		
    		card.punches.add(punch);
    	}
    	
//    	card.errorMsg += "First marker pos = " +firstMarkerPos + "\n";
//    	card.errorMsg += "First marker: " + allData[i] + " " + allData[i+1] + " " + allData[i+2] + " " + allData[i+3] + "\n";
//    	card.errorMsg += "Second marker: " + allData[i+4] + " " + allData[i+5] + " " + allData[i+6] + " " + allData[i+7] + "\n";
//    	card.errorMsg += "Third marker: " + allData[i+8] + " " + allData[i+9] + " " + allData[i+10] + " " + allData[i+11] + "\n";
//    	
    	return card;
    }
    
    private Card parseCard5( byte[] card5Data ){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	long cardNumber = 0;
    	cardNumber = makeIntFromBytes( card5Data[5], card5Data[4] );
    	
    	if(card5Data[6]==1)
    		card.cardNumber=cardNumber;
    	else
    		card.cardNumber=100000*card5Data[6]+cardNumber;
    	
    	dataPos += 16;
    	
    	card.startPunch = analyseSi5Time(card5Data, dataPos+3);
    	card.finishPunch = analyseSi5Time(card5Data, dataPos+5);
    	card.checkPunch = analyseSi5Time(card5Data, dataPos+9);
    	
    	int numberOfPunches = card5Data[dataPos+7]-1;
    	card.numberOfPunches = numberOfPunches;
    	dataPos += 16;
    	
    	for(int i = 0; i < card.numberOfPunches; i++)
    	{
    		if(i<30){
    			int basepointer=3*(i%5)+1+(i/5)*16;
    			int code=card5Data[dataPos+basepointer];
    			Punch punch = analyseSi5Time(card5Data, dataPos+basepointer+1);
    			punch.control = code;
    			card.punches.add(punch);
    		}
    		else{
    			return null;    			
    		}
    	}
    	
//    	androidActivity.msg += card.toString() + "\n";

    	return card;
    	
    }
    
    private Card parseCard6( byte[] card6Data ){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	long cardNumber = 0;
    	int cardHi = makeIntFromBytes( card6Data[11], card6Data[10] );
    	int cardLow = makeIntFromBytes( card6Data[13], card6Data[12] );
    	cardNumber = (cardHi*65536) + cardLow;
    	card.cardNumber = cardNumber;
    	
//    	androidActivity.msg += "CardNumber= " + cardNumber + "\n";
    	
    	dataPos += 16;
    	
    	card.startPunch = analysePunch(card6Data, dataPos+8);	
    	card.finishPunch = analysePunch(card6Data, dataPos+4); 	
    	card.checkPunch = analysePunch(card6Data, dataPos+12); 
    	int numberOfPunches = card6Data[dataPos+2];
//    	androidActivity.msg += "Number of punches = " + numberOfPunches + "\n";
    	card.numberOfPunches = numberOfPunches;

    	
    	dataPos+=128-16;
    	
    	for(int i = 0; i < card.numberOfPunches; i++)
    	{
    		Punch punch = analysePunch(card6Data, dataPos+(4*i) );
    		card.punches.add(punch);
    	}
    	
//    	androidActivity.msg += card.toString() + "\n";

    	return card;
    	
    }
    
    public Card getCard5Data( ){
    	String msg = "";
    	byte[] allData = new byte[256];
    	byte[] rawData = readSiMessage(256, 1000, false);
    	MessageBuffer messageBuffer = new MessageBuffer(rawData);
    	byte[] dleOutputPre = new byte[10];
    	
//    	Card card = new Card();
//    	msg += "Raw data ";
//    	int k = 0;
//    	for( byte readbyte: rawData){
//    		msg +=  k+ "=0x" + byteToHex(readbyte) + ", ";
//    		k++;
//    	}
    	
    	if( rawData.length < 2){
    		return null;
    	}
    	
    	int numberOfBytesRead = readBytesDle(messageBuffer, dleOutputPre, 0, 3);
    	if( numberOfBytesRead < 2){
    		return null;
    	}
    	if( dleOutputPre[0] == SiMessage.STX && (dleOutputPre[1] & 0xFF)  == 0x31 ){
    		readBytesDle(messageBuffer, allData, 0, 128);
//    		msg += "Parse card\n";
//    		int i = 0;
//        	for( byte readbyte: allData){
//        		msg +=  i + "=0x" + byteToHex(readbyte) + ", ";
//        		i++;
//        	}
    		Card card = parseCard5Alt( allData, rawData );
    		card.errorMsg += "\n" + msg;
    		return card;
    		
    	}
		
		return null;
    	
    }
    
    public Card getCard6Data( ){
    	byte[] allData = new byte[128*3];
    	
    	boolean compact = false;
		for(int blockNumber = 0; blockNumber < 3 ; blockNumber++){
//			androidActivity.msg += "Loop " + blockNumber + "\n";
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
			
//			androidActivity.msg += "DLE READ: bytesRead " + bytesRead + "  ";
			for(int i = 0; i < dleOutput.length; i++){
//				androidActivity.msg += i + "=0x" + byteToHex(dleOutput[i]) + ", ";
			}
//			androidActivity.msg+= "\n";
			
			
			
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
		
		return parseCard6( allData );
    	
    }
    
    
    private Punch analyseSi5Time( byte[] data, int pos){
    	int time = 0;
    	int control = 0;
    	if( (data[pos] & 0xFF) != 0xEE ){
    		time = makeIntFromBytes(data[pos+1], data[pos]);
    	}
    	else{
    		control = -1;
    		time = 0;
    	}
    	
    	return new Punch(time, control);
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
    
	public boolean connectDriver(UsbManager manager) {
		if (manager == null) {
			return false;
		}

		driver = UsbSerialProber.acquire(manager);

		if (driver != null) {
			try {
				driver.open();
			} catch (IOException e1) {
				return false;
			}
			try {
				driver.setParameters(38400, UsbSerialDriver.DATABITS_8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			} catch (IOException e) {
				return false;
			}

		} else {
			return false;
		}

		return true;
	}
    
	public void closeDriver(){
		try {
			driver.close();
		} catch (IOException e) {

		}
	}
	
    public byte[] readSiMessage( int size, int timeout, boolean verbose){
	    byte buffer[] = new byte[size];
	    int numBytesRead = -2;
		try {
			numBytesRead = driver.read(buffer, timeout);
			if(numBytesRead <= 0){
				return new byte[0];
			}
		} catch (IOException e) {
//			androidActivity.msg += "Exceptiion when reading " + e.getMessage();
		}
	    		
		if (verbose) {
//			androidActivity.msg += "Read numbytes: " + numBytesRead + " Data: ";
			for (int i = 0; i < numBytesRead; i++) {
//				androidActivity.msg += "0x" + byteToHex(buffer[i]) + ", ";
			}
//			androidActivity.msg += "\n";
		}
	   
		byte[] result = Arrays.copyOfRange(buffer, 0, numBytesRead);
	    return result;
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
    
    public static String byteToHex(byte b){
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
	
}
