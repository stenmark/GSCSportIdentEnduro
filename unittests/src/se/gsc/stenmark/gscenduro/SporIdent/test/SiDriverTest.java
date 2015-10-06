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
			String currentLine;
			while ((currentLine = fileBuffer.readLine()) != null) {
				List<Integer> parsedInts = new ArrayList<>();
				for( String hexString : currentLine.replaceAll("0x", "").split(",")){
					if(!hexString.replace(" ", "").isEmpty()){
						parsedInts.add( Integer.parseInt(hexString.replace(" ", ""), 16) );
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
					if(!hexString.replaceAll(" ", "").isEmpty()){
						parsedInts.add( Integer.parseInt(hexString.replaceAll(" ", ""), 16) );
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
	
	@Test
	public void testReadBytesDle() {
		final String TEST_CARD_1 = "testCard_2065381_12punches.card";
		
		SiDriver siDriver = new SiDriver();
		
		List<byte[]> inData = new ArrayList<>();
		List<byte[]> expectedData = new ArrayList<>();
		readTestDataFromFile(TEST_CARD_1, inData, expectedData);		
		
		assertEquals("Testdata read failuer, the testfile " + TEST_CARD_1 + " Does not contain the same number of inData rows as ExpectedData rows",
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

}
