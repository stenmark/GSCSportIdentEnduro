package se.gsc.stenmark.gscenduro.SporIdent.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.MessageBuffer;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriverDisconnectedException;
import se.gsc.stenmark.gscenduro.SporIdent.test.driverStubs.UsbDriverStub;

public class SiDriverTest {

	private SiDriver siDriver;

	/**
	 * Testdata for readDleByte method. 
	 * First line is raw data read at 128 bytes chunks from the card. 
	 * The second line is the expected output after performing readBytesDle
	 * @param fileName
	 * @param inData
	 * @param expectedData
	 */
	public static void readTestDataFromFile( String fileName, List<byte[]> inData, List<byte[]> expectedData, boolean isSiacData){
		System.out.println("Reading testdata from: " + fileName);
		BufferedReader fileBuffer = null;
		try{
			String workingDir = System.getProperty("user.dir");
			File file = new File( workingDir + File.separator + "testData" + File.separator + fileName);
			FileReader reader = new FileReader(file.getAbsoluteFile());
			fileBuffer = new BufferedReader(reader);
			fileBuffer.readLine();  //First line is comment line, read and discard
			String currentLine;
			while ((currentLine = fileBuffer.readLine()) != null) {
				List<Integer> parsedInts = new ArrayList<>();
				for( String hexString : currentLine.replaceAll("0x", "").split(",")){
					if(!hexString.isEmpty()){
						parsedInts.add( Integer.parseInt(hexString.replace(" ", ""), 16) );
					}
				}
				byte[] tmpIndataBytes = new byte[parsedInts.size()];
				for( int i = 0; i < parsedInts.size(); i++ ){
					tmpIndataBytes[i] = parsedInts.get(i).byteValue();
				}
				inData.add(tmpIndataBytes);
				
				if(!isSiacData){
					String currentLineExpectedData = fileBuffer.readLine();
					parsedInts = new ArrayList<>();
					for( String hexString : currentLineExpectedData.replaceAll("0x", "").split(",")){
						if(!hexString.isEmpty()){
							parsedInts.add( Integer.parseInt(hexString, 16) );
						}
					}
					byte[] tmpExpectedBytes = new byte[parsedInts.size()];
					for( int i = 0; i < parsedInts.size(); i++ ){
						tmpExpectedBytes[i] = parsedInts.get(i).byteValue();
					}
					expectedData.add(tmpExpectedBytes);
				}
				
			}
		}
		catch( Exception e){
			e.printStackTrace();
			fail();
		}
		finally{
			if(fileBuffer != null){
				try {
					fileBuffer.close();
				} catch (IOException e) {
					e.printStackTrace();
					fail();
				}
			}
		}
	}
	
	/**
	 * Wrapper method that uses the old method that reads indata and then expecteddata mixed in the same file.
	 * The SIAC data does not contain any expected data, only indata. 
	 * So this method just ueses the old method and then merges the two lists and returns it
	 */
	public static List<byte[]> readSiacTestDataFromFile( String fileName){
		List<byte[]> i1 = new ArrayList<>();
		List<byte[]> i2 = new ArrayList<>();
		readTestDataFromFile(fileName, i1, i2, true);
		List<byte[]> inData = new ArrayList<>();
		inData.addAll(i1);
		//inData.addAll(i2);
		
		return inData;
	}
	
	private void testCard6( String cardname ){		
		List<byte[]> inData = new ArrayList<>();
		List<byte[]> expectedData = new ArrayList<>();
		readTestDataFromFile(cardname, inData, expectedData, false);		
		
		assertEquals("Testdata read failuer, the testfile " + cardname + " Does not contain the same number of inData rows as ExpectedData rows",
					  inData.size(), 
					  expectedData.size());
		for( int i = 0; i < inData.size(); i++){
			MessageBuffer messageBuffer = new MessageBuffer(inData.get(i));
			
			byte[] dleOutputPre = new byte[10];
			siDriver.readBytesDle(messageBuffer, dleOutputPre, 4);
			
			byte[] dleOutput = new byte[150];
			int bytesRead = siDriver.readBytesDle(messageBuffer, dleOutput, 128);
			
			System.out.println("Validate that the correct number of bytes was read by readBytesDle");
			assertEquals("Incorrect number of bytes read",128, bytesRead);
			System.out.println("Validate content returned by readBytesDle");
			for( int j = 0; j < expectedData.get(2).length; j++ ){
				assertEquals("DLE output did not match on position " + j, expectedData.get(i)[j], dleOutput[j]);
			}
		}
	}
		
	@Test
	public void testReadBytesDle() {
		siDriver = new SiDriver();
		List<String> cardsToTest = new ArrayList<>();
		cardsToTest.add("testCard6_2065381_12punches.card");
		cardsToTest.add("testCard6_2065396_12punches.card");
		cardsToTest.add("testCard6_2079768_12punches.card");
		cardsToTest.add("testCard6_2078064_12punches.card");
		cardsToTest.add("testCard6_2078082_12punches.card");
		cardsToTest.add("testCard6_2079749_12punches.card");
		cardsToTest.add("testCard6_2078056_12punches.card");
		cardsToTest.add("testCard6_2078040_12punches.card");
		cardsToTest.add("testCard6_2079752_12punches.card");
		cardsToTest.add("testCard6_2079747_12punches.card");
		cardsToTest.add("testCard6_2065349_12punches.card");
		cardsToTest.add("testCard6_2065339_12punches.card");

		for(String cardToTest : cardsToTest){
			testCard6(cardToTest);
		}
			
	}
	
	@Test
	public void testPollForNewCard() throws InterruptedException, SiDriverDisconnectedException{
		System.out.println("Starting testPollForNewCard");
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
		
		System.out.println("Trying to read a card that was pulled out in the middle of the read sequence, and was not put back in again");
		List<byte[]> inData = SiDriverTest.readSiacTestDataFromFile("failedCardRead" + File.separator + "failedSiacCardRead_CardRemoved.card");
		stubUsbDriver.setStubUsbData(inData);
		Card emptyCard = siDriver.pollForNewCard(false);
		assertEquals(0, emptyCard.getCardNumber());
		
		System.out.println("Trying to read a card that was pulled out in the middle of the read sequence, and was not put back in again");
		inData = SiDriverTest.readSiacTestDataFromFile("failedCardRead" + File.separator + "failedSiacCardRead_CardRemoved2.card");
		stubUsbDriver.setStubUsbData(inData);
		emptyCard = siDriver.pollForNewCard(false);
		assertEquals(0, emptyCard.getCardNumber());
		
		System.out.println("Trying to read a card that was pulled out in the middle of the read sequence, and was then put back in again");
		inData = SiDriverTest.readSiacTestDataFromFile("failedCardRead" + File.separator + "failedSiacCardRead_CardRemoved_andReinserted.card");
		stubUsbDriver.setStubUsbData(inData);
		Card correctCard = siDriver.pollForNewCard(false);
		assertEquals(8633672, correctCard.getCardNumber());
	}
	
	@Test
	public void testGetCard6Data() {
		System.out.println("Testing Card6 parsing");
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
		
		List<byte[]> inData = new ArrayList<>();
		List<byte[]> expectedData = new ArrayList<>();
		readTestDataFromFile("testCard6_2065381_12punches.card", inData, expectedData, false);
		stubUsbDriver.setStubUsbData(inData);
		
		Card card6Data = siDriver.getCard6Data(false);
		
		assertNotNull("Card6 data was null", card6Data);
		System.out.println("Card6 data: " + card6Data.toString());
		assertEquals(12, card6Data.getNumberOfPunches());
		assertEquals(card6Data.getNumberOfPunches(), card6Data.getPunches().size());
		assertEquals(2065381, card6Data.getCardNumber());
		
		assertEquals(71, card6Data.getPunches().get(0).getControl());
		assertEquals(72, card6Data.getPunches().get(1).getControl());
		assertEquals(71, card6Data.getPunches().get(2).getControl());
		assertEquals(72, card6Data.getPunches().get(3).getControl());
		assertEquals(71, card6Data.getPunches().get(4).getControl());
		assertEquals(72, card6Data.getPunches().get(5).getControl());
		assertEquals(71, card6Data.getPunches().get(6).getControl());
		assertEquals(72, card6Data.getPunches().get(7).getControl());
		assertEquals(71, card6Data.getPunches().get(8).getControl());
		assertEquals(72, card6Data.getPunches().get(9).getControl());
		assertEquals(71, card6Data.getPunches().get(10).getControl());
		assertEquals(72, card6Data.getPunches().get(11).getControl());

		assertEquals(39152000, card6Data.getPunches().get(0).getTime());
		assertEquals(39257000, card6Data.getPunches().get(1).getTime());
		assertEquals(41200000, card6Data.getPunches().get(2).getTime());
		assertEquals(41351000, card6Data.getPunches().get(3).getTime());
		assertEquals(42727000, card6Data.getPunches().get(4).getTime());
		assertEquals(42819000, card6Data.getPunches().get(5).getTime());
		assertEquals(47462000, card6Data.getPunches().get(6).getTime());
		assertEquals(47581000, card6Data.getPunches().get(7).getTime());
		assertEquals(48957000, card6Data.getPunches().get(8).getTime());
		assertEquals(49064000, card6Data.getPunches().get(9).getTime());
		assertEquals(52272000, card6Data.getPunches().get(10).getTime());
		assertEquals(52394000, card6Data.getPunches().get(11).getTime());
	}
	
	@Test 
	public void testSiacMilliSecondTiming() throws Exception{
		System.out.println("Testing SIAC card parsing for milliscond timestamps");
		
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
		
		checkMilliSecondCard("cardDebugData_Mon_May_16_214703__20168633672.card", stubUsbDriver, Arrays.asList(37728601L,38494000L,38617382L,40099000L,40144304L,41613000L,41684703L,43458000L,43593656L,45042000L,45130476L,46834000L,46916687L));
		checkMilliSecondCard("cardDebugData_Wed_May_18_213950__20168633694.card", stubUsbDriver, Arrays.asList(77582000L,77604984L,77625785L,77618000L,77626000L,77721253L,77734000L,77742937L));
		checkMilliSecondCard("cardDebugData_Sat_May_21_212726__20168633676.card", stubUsbDriver, Arrays.asList(38429000L,38567976L,39985000L,40067921L,41464000L,41547000L,43318000L,43544296L,46849265L,47301097L));	
	}
	
	private void checkMilliSecondCard( String cardDatFile,UsbDriverStub stubUsbDriver,List<Long> expectedTimeStamps) throws Exception{
		System.out.println("Testing SIAC card with millisecond timing: " + "cardDebugData_Mon_May_16_214703__20168633672.card");
		List<byte[]> inData = SiDriverTest.readSiacTestDataFromFile(cardDatFile);
		stubUsbDriver.setStubUsbData(inData);
		
		Card siacCard = siDriver.getSiacCardData(false);
		int i  = 0;
		for( Punch punch : siacCard.getPunches() ){
			assertEquals(expectedTimeStamps.get(i), (Long)punch.getTime());
			i++;
			System.out.println(i + " - " + siacCard.getCardNumber() + " - " + punch.getTime() );
			
		}
	}
	
	@Test
	public void testGetSiacCardData() throws Exception{
		System.out.println("Testing SIAC card parsing");
		
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
		
		testSpecificCard("test_SIAC_8633680_4punches.card", 8633680, Arrays.asList(70045546L,70052816L,70067429L,70073359L), stubUsbDriver);
		testSpecificCard("test_SIAC_8633676_6punches.card", 8633676, Arrays.asList(83485546L,83504816L,83530429L,83583359L,83609750L,83664703L), stubUsbDriver);
		testSpecificCard("test_SIAC_8633672_8punches.card", 8633672, Arrays.asList(83483546L,83509816L,83529429L,83584359L,83606750L,83667703L,83700523L,83856000L), stubUsbDriver);
		testSpecificCard("test_SIAC_8633683_10punches.card", 8633683, Arrays.asList(83479546L,83507816L,83528429L,83581359L,83604750L,83670703L,83698523L,83849000L,83870210L,83934000L), stubUsbDriver);
		testSpecificCard("test_SIAC_8633698_12punches.card", 8633698, Arrays.asList(83480546L,83510816L,83526429L,83589359L,83603750L,83666703L,83689523L,83852000L,83874210L,83938000L,83957000L,83999000L), stubUsbDriver);
//		testSpecificCard("test_SIAC_8633691_14punches.card", 8633691, Arrays.asList(83482L,83503L,83523L,83586L,83607L,83671L,83692L,83851L,83872L,83936L,83955L,83997L,84019L,84048L,84076L), stubUsbDriver);
	}
	
	private void testSpecificCard( String cardIndataFile, int cardNumber, List<Long> punchTimes, UsbDriverStub stubUsbDriver) throws Exception{
		System.out.println("Testing SIAC card: " + cardIndataFile);
		List<byte[]> inData = SiDriverTest.readSiacTestDataFromFile(cardIndataFile);
		stubUsbDriver.setStubUsbData(inData);
		
		Card siacCard = siDriver.getSiacCardData(false);
		
		assertNotNull("SIAC card data was null", siacCard);
		System.out.println("SIAC data: " + siacCard.toString());
		
		assertEquals(punchTimes.size(), siacCard.getNumberOfPunches());
		assertEquals(siacCard.getNumberOfPunches(), siacCard.getPunches().size());
		assertEquals(cardNumber, siacCard.getCardNumber());
				
		for(int i = 0; i < punchTimes.size(); i++){
			//Every other punch should be either 71 or 72. I.e. punches are 71,72,71,72 etc.
			if( i%2 == 0){
				assertEquals(71, siacCard.getPunches().get(i).getControl());
			}
			else{
				assertEquals(72, siacCard.getPunches().get(i).getControl());	
			}
			assertEquals((Long)punchTimes.get(i), (Long)siacCard.getPunches().get(i).getTime());
		}
		
	}

}
