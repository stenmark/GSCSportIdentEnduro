package se.gsc.stenmark.gscenduro.SporIdent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import se.gsc.stenmark.gscenduro.MainActivity;
import se.gsc.stenmark.gscenduro.SporIdent.CRCCalculator;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.hardware.usb.UsbManager;
import android.os.Environment;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class SiDriver {
	private UsbSerialDriver driver;
	
	public int stationId;
	public int mode;
	public boolean extended;
	public boolean handShake;
	public boolean autoSend;

	public SiDriver( ){
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
    	    	readSiMessage(16, 500, false, null);
    	    	return true;
    		}
    		return false;
    	}

    }
    
    public void setUsbDriver( UsbSerialDriver usbDriver){
    	driver = usbDriver;
    }
    
    private boolean performHandShake( boolean withStartup){
		byte[] startupResponse = new byte[16];
    	if( withStartup ){
			sendSiMessage(SiMessage.startup_sequence.sequence());
			sleep(700);
			startupResponse = readSiMessage(16, 500, false, null);
		}
		if( (startupResponse.length >= 1 && startupResponse[0]== SiMessage.STX ) || (withStartup == false)) {
			sendMessage( appendMarkesAndCrcToMessage(SiMessage.read_system_data.sequence() ));
			byte[] systemData = readSiMessage(32, 8000, false, null);
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
    
    public void sendSiMessage(byte[] message){
    	try {
    		 driver.write(message, 1000 );
		} catch (IOException e) {

		}

    }
    
    //Work around, ReadDle is not woeking as expected for card5, not found why yet.
    //This one hacks the SI interface by finding the Fist station position in the data array and
    //then just hard fetches 4 bytes at a time from there, removing DLE (0x10) marker and extract station ID and time
    private Card parseCard5Alt( byte[] dleData, byte[] allData, Competition comp){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	int cardNumber = 0;
    	cardNumber = makeIntFromBytes( dleData[5], dleData[4] );
    	
    	if(dleData[6]==1)
    		card.setCardNumber(cardNumber);
    	else
    		card.setCardNumber(100000*dleData[6]+cardNumber);
    	//Log.d("parseCard5Alt", "Card Number: " + cardNumber + "\n"); 
    	
    	dataPos += 16;
    	
    	card.setStartPunch(analyseSi5Time(dleData, dataPos+3));
    	card.setFinishPunch(analyseSi5Time(dleData, dataPos+5));
    	card.setCheckPunch(analyseSi5Time(dleData, dataPos+9));
    	
    	int numberOfPunches = dleData[dataPos+7]-1;
    	card.setNumberOfPunches(numberOfPunches);
    	//Log.d("parseCard5Alt", "Number of punches: " + numberOfPunches + "\n"); 
    	
    	byte firstStartMaker = (byte) comp.getStages().get(0).getStart();
    	int firstMarkerPos = 0;
    	int i = 0;
    	for( byte data : allData ){
    		if( firstStartMaker == data){
    			firstMarkerPos = i;
    			break;
    		}
    		i++;
    	}
    	//Log.d("parseCard5Alt", "firstMarkerPos " + firstMarkerPos + "\n"); 
    	
    	int currentPos = 0;
    	for(int k = 0; k < card.getNumberOfPunches(); k++)
    	{
    		byte stationId = allData[firstMarkerPos + currentPos];
    		currentPos++;
    	
    		byte byte1;
    		//int byte1Pos;
    		if( allData[firstMarkerPos + currentPos] == 0x10){
    			currentPos++;
    		}
    		byte1 = allData[firstMarkerPos + currentPos];
    		//byte1Pos = firstMarkerPos + currentPos;
    		//int byte2Pos = 0;
    		currentPos++;
    		byte byte2;
    		if( allData[firstMarkerPos + currentPos] == 0x10){
    			byte2 = allData[firstMarkerPos + currentPos+1];
    			//byte2Pos = firstMarkerPos + currentPos+1;
    			currentPos++;
    			currentPos++;
//    			if( allData[firstMarkerPos + currentPos] == 0x00){
//    				currentPos++;
//    			}
    		}
    		else{
    			byte2 = allData[firstMarkerPos + currentPos];
    			//byte2Pos = firstMarkerPos + currentPos;
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
    		
    		//Log.d("parseCard5Alt", "loop nr " + k + " byte1: " + byte1Pos + " : " + byte1 + "  byte2: "+ byte2Pos + " : " + byte2 + "\n");
    		
    		card.getPunches().add(punch);
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
    	
    	int cardNumber = 0;
    	cardNumber = makeIntFromBytes( card5Data[5], card5Data[4] );
    	
    	if(card5Data[6]==1)
    		card.setCardNumber(cardNumber);
    	else
    		card.setCardNumber(100000*card5Data[6]+cardNumber);
    	
    	dataPos += 16;
    	
    	card.setStartPunch(analyseSi5Time(card5Data, dataPos+3));
    	card.setFinishPunch(analyseSi5Time(card5Data, dataPos+5));
    	card.setCheckPunch(analyseSi5Time(card5Data, dataPos+9));
    	
    	int numberOfPunches = card5Data[dataPos+7]-1;
    	card.setNumberOfPunches(numberOfPunches);
    	dataPos += 16;
    	
    	for(int i = 0; i < card.getNumberOfPunches(); i++)
    	{
    		if(i<30){
    			int basepointer=3*(i%5)+1+(i/5)*16;
    			int code=card5Data[dataPos+basepointer];
    			Punch punch = analyseSi5Time(card5Data, dataPos+basepointer+1);
    			punch.setControl(code);
    			card.getPunches().add(punch);
    		}
    		else{
    			return null;    			
    		}
    	}
    	
//    	androidActivity.msg += card.toString() + "\n";

    	return card;
    	
    }
    
    private Card parseSiacCard( List<Byte> siacCardData, int series, int numberOfPunches ){
    	Card card = new Card();
    	int cardNoLoWord = makeIntFromBytes( siacCardData.get(27), siacCardData.get(26) );
    	int cardNoHiWord = makeIntFromBytes( siacCardData.get(25), (byte)0 );
    	card.setCardNumber(cardNoLoWord + (cardNoHiWord*65536)); 
    	card.setNumberOfPunches(numberOfPunches);
    	
    	if( series == 15 ){
    		for( int i = 0; i < numberOfPunches; i++){
    			Punch punch = analysePunch(siacCardData, 256 + 4*i);
    			card.getPunches().add(punch);
    		}
    	}
    	
    	return card;
    }
    
    private Card parseCard6( byte[] card6Data ){
    	Card card = new Card();;
    	int dataPos = 0;
    	
    	int cardNumber = 0;
    	int cardHi = makeIntFromBytes( card6Data[11], card6Data[10] );
    	int cardLow = makeIntFromBytes( card6Data[13], card6Data[12] );
    	cardNumber = (cardHi*65536) + cardLow;
    	card.setCardNumber(cardNumber);
    	
//    	androidActivity.msg += "CardNumber= " + cardNumber + "\n";
    	
    	dataPos += 16;
    	
    	card.setStartPunch(analysePunch(card6Data, dataPos+8));	
    	card.setFinishPunch(analysePunch(card6Data, dataPos+4)); 	
    	card.setCheckPunch(analysePunch(card6Data, dataPos+12)); 
    	int numberOfPunches = card6Data[dataPos+2];
//    	androidActivity.msg += "Number of punches = " + numberOfPunches + "\n";
    	card.setNumberOfPunches(numberOfPunches);

    	
    	dataPos+=128-16;
    	
    	for(int i = 0; i < card.getNumberOfPunches(); i++)
    	{
    		Punch punch = analysePunch(card6Data, dataPos+(4*i) );
    		card.getPunches().add(punch);
    	}
    	
//    	androidActivity.msg += card.toString() + "\n";

    	return card;
    	
    }
    
    public Card getCard5Data( Competition comp){
//    	String msg = "";
    	byte[] allData = new byte[256];
    	byte[] rawData = readSiMessage(256, 1000, false, null);
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
    	
    	int numberOfBytesRead = readBytesDle(messageBuffer, dleOutputPre, 3);
    	if( numberOfBytesRead < 2){
    		return null;
    	}
    	if( dleOutputPre[0] == SiMessage.STX && (dleOutputPre[1] & 0xFF)  == 0x31 ){
    		readBytesDle(messageBuffer, allData, 128);
//    		msg += "Parse card\n";
//    		int i = 0;
//        	for( byte readbyte: allData){
//        		msg +=  i + "=0x" + byteToHex(readbyte) + ", ";
//        		i++;
//        	}
    		Card card = parseCard5Alt( allData, rawData, comp );
    		//Log.d("getCard5Data", "\n" + msg);
    		return card;
    		
    	}
		
		return null;
    	
    }
    
    public Card getSiacCardData( boolean verbose ) throws Exception{
    	int nrOfReadLoops = 2;
    	int series = -1;
    	int numberOfPunches = -1;
    	
    	List<Byte> allData = new ArrayList<Byte>();
    	BufferedWriter bw  = null;
    	File file = null;
    	try{
    		if( verbose ){
		    	File sdCard = Environment.getExternalStorageDirectory();
		    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro/siacdata");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				file = new File(dir, "cardDebugData_" + Calendar.getInstance().getTime().toString().replace(" ", "_").replace(":", "").replace("CEST", "") + ".card");
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write("#Testdata for SIAC card read\n");
    		}
    		
    		sendSiMessage(SiMessage.read_sicard_8_plus_b1.sequence());
    		
    		for( int currentReadLoop = 0; currentReadLoop < nrOfReadLoops; currentReadLoop++){
    			if( currentReadLoop == 0){
    				sendSiMessage(SiMessage.read_sicard_8_plus_b0.sequence());
    			}
    			else if( currentReadLoop == 1){
    				sendSiMessage(SiMessage.read_sicard_8_plus_b1.sequence());
    			}
    			else{
    				sendSiMessage(SiMessage.read_sicard_10_plus_b4.sequence());
    			}
    			
				byte[] rawData = readSiMessage(512, 1000, verbose, bw);
				MessageBuffer messageBuffer = new MessageBuffer(rawData);
				
				byte[] initialReadBytes = messageBuffer.readBytes(128+9);
				if( initialReadBytes.length > 128 + 6 ){ 
					if( initialReadBytes[0] == SiMessage.STX && (initialReadBytes[1]& 0xFF) == 0xEF ){
						//memcpy(b+k*128, bf+6, 128);
						for( int i = 0; i < 128; i++ ){
							allData.add( initialReadBytes[i+6]);
						}
						
						series = allData.get(24) & 15;
						if( series == 15 ){
							numberOfPunches = Math.min( (allData.get(22) ), 128);
							nrOfReadLoops = 1 + (numberOfPunches+31) / 32;
						}
						else{
							if(verbose){
								bw.write("Only SIAC cards currently supported for SICARD 8 and newer\n");
							}
							return new Card();
						}
						
					}
					else{
						if(verbose){
							bw.write("Tried to read SIAC card but failed. Expected STX(0x02) + 0xEF but got " + initialReadBytes[0] + " + " + initialReadBytes[1] + "\n");
						}
						return new Card();
					}
				}
				else{
					if(verbose){
						bw.write("Wanted to read 128+6 bytes, could only read " + initialReadBytes.length  + " byes\n");
					}
					return new Card();
				}
    		}
    	}	
    	finally{
			try {
				if( bw != null){
					bw.close();
	    		}
			} 
	    	catch (IOException e1) { return null; }	
    	}
    	
    	sendSiMessage(SiMessage.ack_sequence.sequence());
    	Card siacCard = parseSiacCard( allData, series, numberOfPunches );
    	if( verbose){
    		file.renameTo(new File(file.getAbsolutePath().replace(".card", siacCard.getCardNumber() + ".card")));
    	}
    	return siacCard;
    }
    
    public Card getCard6Data( boolean verbose){
    	sendSiMessage(SiMessage.request_si_card6.sequence());
    	
    	byte[] allData = new byte[128*3];
    	BufferedWriter bw  = null;
    	try{
    		if( verbose ){
		    	File sdCard = Environment.getExternalStorageDirectory();
		    	File dir = new File(sdCard.getAbsolutePath() + "/gscEnduro");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, "cardDebugData_" + Calendar.getInstance().getTime().toString() + ".card");
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write("#Testdata for readDleByte method. First line is raw data read at 128 bytes chunks from the card. The second line is the expected output after performing readBytesDle\n");
    		}
			
	//    	boolean compact = false;
			for(int blockNumber = 0; blockNumber < 3 ; blockNumber++){
				byte[] rawData = readSiMessage(256, 1000, verbose, bw);
				MessageBuffer messageBuffer = new MessageBuffer(rawData);
				byte[] dleOutputPre = new byte[10];
				readBytesDle(messageBuffer, dleOutputPre, 4);
				byte[] dleOutput = new byte[150];
				int bytesRead = readBytesDle(messageBuffer, dleOutput, 128);
				
				byte[] dleBytesCorrectSize = new byte[bytesRead];
				for( int i = 0; i < dleBytesCorrectSize.length; i++){
					dleBytesCorrectSize[i] = dleOutput[i];
				}
				
				if( verbose ){
					for(int i = 0; i < dleOutput.length; i++){
						bw.write("0x" + byteToHex(dleOutput[i]) + ",");
					}
					bw.write( "\n" );	
				}
	
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
			if( bw != null ){
				bw.close();
			}
    	}
    	catch( Exception e){
    		try {
    			if( bw != null){
    				bw.close();
    			}
			} 
    		catch (IOException e1) {
			}
    	}
    	
    	sendSiMessage(SiMessage.ack_sequence.sequence());
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
    
    private Punch analysePunch( List<Byte> data, int pos){
    	byte[] dataArray = new byte[ data.size() ];
    	for( int i = 0; i < data.size(); i++){
    		dataArray[i] = data.get(i);
    	}
    	return analysePunch(dataArray, pos);
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
				MainActivity.driverLayerErrorMsg += MainActivity.generateErrorMessage(e1);
				return false;
			}
			try {
				driver.setParameters(38400, UsbSerialDriver.DATABITS_8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			} catch (IOException e) {
				MainActivity.driverLayerErrorMsg += MainActivity.generateErrorMessage(e);
				return false;
			}

		} else {
			MainActivity.driverLayerErrorMsg += "\nacquire probe was unsuccesful";
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
	
    public byte[] readSiMessage( int size, int timeout, boolean verbose, BufferedWriter bw){
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
			try{
				for (int i = 0; i < numBytesRead; i++) {
					bw.write("0x" + byteToHex(buffer[i]) + ",");
				}
				bw.write( "\n" );
			}
			catch( Exception e ){
				
			}

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
     * Strip out all occurrences of 0x10 bytes (Except if 0x10 follow by one more 0x10, then keep one of the 0x10 bytes)
     * @param data Buffer with all the read data raw data. This is the buffer coming directly from the SI-master containing the 0x10 bytes that shall be stripped away
     * @param bytes This is the result buffer. The buffer need to be at least the size of len. This buffer will contain the data with all 0x10 bytes stripped away (except if there are two consecutive 0x10, then keep one)
     * @param len Number of bytes to read from the buffer, after 0x10 removal. I
     * @return number of bytes read. If the MessageBuffer data runs out of read bytes, then the number of read bytes until we ran out will be returned. Otherwise len will be returned.
     */
    public int readBytesDle( MessageBuffer data, byte[] bytes, int len ){
    	for( int i = 0; i < len; i++){
    		//Read one byte from the buffer
    		byte[] currentByte = data.readByte();
    		//If the buffer is empyt, it will return an array with size 0. Return the number of bytes we managed to read
    		if( currentByte.length == 0){
    			return i;
    		}
    		
    		//Strip away 0x10 bytes. If the byte is not 0x10 copy it to the out buffer
    		if( currentByte[0] != 0x10 ){
    			bytes[i] = currentByte[0];
    		}
    		//If the byte is ox10, then copy the next byte in the buffer. Do not check for 0x10 for the nextbyte, since if the next byte is 0x10 we need to copy it as real data
    		else{
    			byte[] nextByte = data.readByte();
    			//If the buffer is empty, it will return an array with size 0. Return the number of bytes we managed to read
        		if( nextByte.length == 0){
        			return i;
        		}
    			bytes[i] = nextByte[0];
    		}
    	}
    	
    	return len;
    }
    /**
     * Send in a preread data buffer and extract data between DLE
     * @param data preRead databuffer. 
     * @param bytes output data of all read bytes
     * @param pos where to start put the new data in the bytes area
     * @param len number of bytes to read from the databuffer
     * @return
     * @deprecated Use public int readBytesDle( MessageBuffer data, byte[] bytes, int len ) instead
     */
    public int readBytesDle( MessageBuffer data, byte[] bytes, int pos, int len ){
    	byte[] localBytes = data.readBytes( len );
	
//    	System.out.println("NEW CALL TO readBytesDle pos"+pos +" len "+len);
    	
    	if( localBytes.length > 0 ){
        	int ip = 0;  //Inpointer 
        	int op = 0;  //Outpointer
        	
//        	System.out.println("Before for lopp ip=" + ip + " localBytes.length-1=" + (localBytes.length-1));
        	for( ip = 0; ip  < localBytes.length-1; ip++ ){
        		//If byte is 0x10. Swap down the next coming byte to previous position.
        		if( localBytes[ip] == 0x10 ){
//        			System.out.println("0x10 found SKIP BYTE ip=" + ip + " op=" +op);
        			localBytes[op++] = localBytes[++ip];
        		}
        		else{
//        			System.out.println("normal byte, COPY BYTE ip=" + ip + " op=" +op);
        			localBytes[op++]=localBytes[ip];
        		}
        	}
    		
//        	System.out.println("After for lopp ip=" + ip + " localBytes.length=" + localBytes.length);
        	//If last byte read was 0x10 the inPointer will have been incremented after loop check -> dont execute the code in this if statement
    		if( ip < localBytes.length ){
//    			System.out.println("Last byte in for loop was normal byte. READ LAST BYTE: ip=" + ip + " op=" +op);
    			//If the last byte is 0x10, we need to read one more byte and discard the current 0x10 byte.
    			if( localBytes[ip] == 0x10 ){
//    				System.out.println("Last byte= 0x10 ip=" + ip + " op=" +op);
    				byte[] readByte = data.readByte();
    				localBytes[op++] = readByte[0];
//    				System.out.println("Read one EXTRA byte= " + readByte[0] + " at indata point " +( len+1) + " ip=" + ip + " op=" + op );
    			}
    			else{
//    				System.out.println("Last is normal byte, copy it  ip=" + ip + " op=" +op);
    				localBytes[op++] = localBytes[ip];
    			}
    		}
    		
    		//We have not read all the bytes yet: Call this method recursivly until all bytes are read.
//    		System.out.println("Check if we read all byte " + op + " < " + len );
    		if( op < len ){
    			//Copy the bytes we have managed to read to the result buffer (the bytes array is the return value of this function)
    			for(int i = 0; i < localBytes.length; i++ ){
//    				System.out.println("Copy byte [i+pos]="+ (i+pos) + " localBytes[i]="+localBytes[i]);
    				bytes[i+pos] = localBytes[i];
    			}
    			//Recursive call. Re-read the remaining bytes by starting to read at the position of the current outpointer. And remove the number of already read bytes from the new request
    			//TODO: Should it be op+pos instead?
//    			System.out.println("RECURSIVE READ readBytesDle(data, bytes, op, len-op)  readBytesDle(-," + op + "," + (len-op));
    			return op+readBytesDle(data, bytes, op, len-op);
    		}
    		//All bytes read. Copy the localcopy to the result buffer (the bytes array is the return value of this function)
    		else{
//    			System.out.println("All bytes read RETURN FINAL RESULT len=" + len);
    			for(int i = 0; i < localBytes.length; i++ ){
    				bytes[i+pos] = localBytes[i];
    			}
    			return len;
    		}
    	}

    	return 0;
    	 
    }
	
}
