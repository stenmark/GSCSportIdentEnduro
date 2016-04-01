package se.gsc.stenmark.gscenduro.SporIdent.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.MessageBuffer;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
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
	private void readTestDataFromFile( String fileName, List<byte[]> inData, List<byte[]> expectedData){
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
						parsedInts.add( Integer.parseInt(hexString, 16) );
					}
				}
				byte[] tmpIndataBytes = new byte[parsedInts.size()];
				for( int i = 0; i < parsedInts.size(); i++ ){
					tmpIndataBytes[i] = parsedInts.get(i).byteValue();
				}
				inData.add(tmpIndataBytes);
				
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
	private List<byte[]> readSiacTestDataFromFile( String fileName){
		List<byte[]> i1 = new ArrayList<>();
		List<byte[]> i2 = new ArrayList<>();
		readTestDataFromFile(fileName, i1, i2);
		List<byte[]> inData = new ArrayList<>();
		inData.addAll(i1);
		inData.addAll(i2);
		
		return inData;
	}
	
	private void testCard6( String cardname ){		
		List<byte[]> inData = new ArrayList<>();
		List<byte[]> expectedData = new ArrayList<>();
		readTestDataFromFile(cardname, inData, expectedData);		
		
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
	
//	@Test
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
	
//	@Test
	public void testGetCard6Data() {
		System.out.println("Testing Card6 parsing");
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
		
		List<byte[]> inData = new ArrayList<>();
		List<byte[]> expectedData = new ArrayList<>();
		readTestDataFromFile("testCard6_2065381_12punches.card", inData, expectedData);
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

		assertEquals(39152, card6Data.getPunches().get(0).getTime());
		assertEquals(39257, card6Data.getPunches().get(1).getTime());
		assertEquals(41200, card6Data.getPunches().get(2).getTime());
		assertEquals(41351, card6Data.getPunches().get(3).getTime());
		assertEquals(42727, card6Data.getPunches().get(4).getTime());
		assertEquals(42819, card6Data.getPunches().get(5).getTime());
		assertEquals(47462, card6Data.getPunches().get(6).getTime());
		assertEquals(47581, card6Data.getPunches().get(7).getTime());
		assertEquals(48957, card6Data.getPunches().get(8).getTime());
		assertEquals(49064, card6Data.getPunches().get(9).getTime());
		assertEquals(52272, card6Data.getPunches().get(10).getTime());
		assertEquals(52394, card6Data.getPunches().get(11).getTime());
	}
	
	@Test
	public void testGetSiacCardData() throws Exception{
		System.out.println("Testing SIAC card parsing");
		
		siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);
	
		List<byte[]> inData = this.readSiacTestDataFromFile("test_SIAC.card");
		stubUsbDriver.setStubUsbData(inData);
		
		Card siacCard = siDriver.getSiacCardData(false);
		
		assertNotNull("SIAC card data was null", siacCard);
		System.out.println("SIAC data: " + siacCard.toString());
		
		assertEquals(4, siacCard.getNumberOfPunches());
		assertEquals(siacCard.getNumberOfPunches(), siacCard.getPunches().size());
		assertEquals(8633680, siacCard.getCardNumber());
		
		assertEquals(71, siacCard.getPunches().get(0).getControl());
		assertEquals(72, siacCard.getPunches().get(1).getControl());
		assertEquals(71, siacCard.getPunches().get(2).getControl());
		assertEquals(72, siacCard.getPunches().get(3).getControl());
		
		assertEquals(70045, siacCard.getPunches().get(0).getTime());
		assertEquals(70052, siacCard.getPunches().get(1).getTime());
		assertEquals(70067, siacCard.getPunches().get(2).getTime());
		assertEquals(70073, siacCard.getPunches().get(3).getTime());
	}

}
