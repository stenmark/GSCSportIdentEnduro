package se.gsc.stenmark.gscenduro.SporIdent.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.MessageBuffer;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;

public class SiDriverTest {

	private SiDriver siDriver;

	/**
	 * 
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
			siDriver.readBytesDle(messageBuffer, dleOutputPre, 0, 4);
			
			byte[] dleOutput = new byte[150];
			int bytesRead = siDriver.readBytesDle(messageBuffer, dleOutput, 0, 128);
			
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
		
		final String TEST_CARD_1 = "testCard6_2065381_12punches.card";
		final String TEST_CARD_2 = "testCard6_2065396_12punches.card";
		final String TEST_CARD_3 = "testCard6_2079768_12punches.card";
		final String TEST_CARD_4 = "testCard6_2078064_12punches.card";
		final String TEST_CARD_5 = "testCard6_2078082_12punches.card";
		final String TEST_CARD_6 = "testCard6_2079749_12punches.card";
		final String TEST_CARD_7 = "testCard6_2078056_12punches.card";
		final String TEST_CARD_8 = "testCard6_2078040_12punches.card";
		final String TEST_CARD_9 = "testCard6_2079752_12punches.card";
		final String TEST_CARD_10 = "testCard6_2079747_12punches.card";
		final String TEST_CARD_11 = "testCard6_2065349_12punches.card";
		final String TEST_CARD_12 = "testCard6_2065339_12punches.card";
		
		testCard6(TEST_CARD_1);
		testCard6(TEST_CARD_2);
		testCard6(TEST_CARD_3);
		testCard6(TEST_CARD_4);
		testCard6(TEST_CARD_5);
		testCard6(TEST_CARD_6);
		testCard6(TEST_CARD_7);
		testCard6(TEST_CARD_8);
		testCard6(TEST_CARD_9);
		testCard6(TEST_CARD_10);
		testCard6(TEST_CARD_11);
		testCard6(TEST_CARD_12);
			
	}

}
