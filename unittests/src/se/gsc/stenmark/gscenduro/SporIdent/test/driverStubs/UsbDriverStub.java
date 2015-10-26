package se.gsc.stenmark.gscenduro.SporIdent.test.driverStubs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import static org.junit.Assert.*;

public class UsbDriverStub extends UsbSerialDriver {
	private List<byte[]> stubUsbData;
	
	public UsbDriverStub() {
		super(null, null);
		stubUsbData = new ArrayList<>();
	}

	
	@Override
	public int read(byte[] dest, int timeoutMillis) throws IOException {
		System.out.println("Reading up to " + dest.length + " bytes from stub driver");
		byte[] tmpReadData = stubUsbData.remove(0);
		System.arraycopy(tmpReadData, 0, dest, 0, tmpReadData.length);
		System.out.println("Read " + tmpReadData.length + " bytes from stubDriver" );
		return tmpReadData.length;
	}
	
	public void setStubUsbData(List<byte[]> stubUsbData){
		this.stubUsbData = stubUsbData;
	}
	
	
	/*
	 * NOT IMPLEMENTED METHODS IN STUB
	 */
	@Override
	public void open() throws IOException {
		fail("Not implemented in UsbDriverStub");
	}

	@Override
	public void close() throws IOException {
		fail("Not implemented in UsbDriverStub");

	}



	@Override
	public int write(byte[] src, int timeoutMillis) throws IOException {
		fail("Not implemented in UsbDriverStub");
		return 0;
	}

	@Override
	public int setBaudRate(int baudRate) throws IOException {
		fail("Not implemented in UsbDriverStub");
		return 0;
	}

	@Override
	public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
		fail("Not implemented in UsbDriverStub");

	}

	@Override
	public boolean getCD() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public boolean getCTS() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public boolean getDSR() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public boolean getDTR() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public void setDTR(boolean value) throws IOException {
		fail("Not implemented in UsbDriverStub");

	}

	@Override
	public boolean getRI() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public boolean getRTS() throws IOException {
		fail("Not implemented in UsbDriverStub");
		return false;
	}

	@Override
	public void setRTS(boolean value) throws IOException {
		fail("Not implemented in UsbDriverStub");

	}

}
