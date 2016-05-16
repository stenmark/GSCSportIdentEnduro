package se.gsc.stenmark.gscenduro.SporIdent;

public class SiMessage {

	private final byte[] sequence;
	
	public SiMessage(byte[] sequence) {
		this.sequence = sequence;
	}

	public byte[] sequence() {
		return sequence;
	}

	/*
	 * Basic protocol instructions
	 */
	public static final byte WAKEUP = (byte) 0xFF;
	public static final byte STX = 0x02;
	public static final byte ETX = 0x03;
	public static final byte ACK = 0x06;
	public static final byte NAK = 0x15;
	public static final byte DLE = 0x10;

	/*
	 * Command instructions
	 */
	public static final byte GET_SYSTEM_VALUE = (byte) 0x83;
	public static final byte SET_MASTER_MODE = (byte) 0xF0;
	public static final byte DIRECT_MODE = 0x4d;
	public static final byte BEEP = (byte) 0xF9;

	/*
	 * Card detected/removed
	 */
	public static final byte SI_CARD_5_DETECTED = (byte) 0xE5;
	public static final byte SI_CARD_6_PLUS_DETECTED = (byte) 0xE6;
	public static final byte SI_CARD_8_PLUS_DETECTED = (byte) 0xE8;
	public static final byte SI_CARD_REMOVED = (byte) 0xE7;
	
	/*
	 * Card Readout instructions
	 */
	public static final byte GET_SI_CARD_5 = (byte) 0xB1;
	public static final byte GET_SI_CARD_6_BN = (byte) 0xE1;
	public static final byte GET_SI_CARD_8_PLUS_BN = (byte) 0xEF;

	/*
	 * SiCard special data
	 */
	public static final int SI3_NUMBER_INDEX = 5;
	public static final byte SI_CARD_10_PLUS_SERIES = 0x0F;
	
	/*
	 * Command messages
	 */
	public static final SiMessage startup_sequence = new SiMessage(new byte[] {
		WAKEUP, STX, STX, SET_MASTER_MODE, 0x01, DIRECT_MODE, 0x6D, 0x0A, ETX
	});

	public static final SiMessage get_protocol_configuration = new SiMessage(new byte[] {
		STX, GET_SYSTEM_VALUE, 0x02, 0x74, 0x01, 0x04, 0x14, ETX
	});
	
	//Get from address 70 and 6 bytes
	public static final SiMessage read_system_data = new SiMessage(new byte[] {
			GET_SYSTEM_VALUE, 0x02, 0x70, 0x06
		});
	
	public static final SiMessage get_cardblocks_configuration = new SiMessage(new byte[] {
		STX, GET_SYSTEM_VALUE, 0x02, 0x33 , 0x01, 0x16, 0x11, ETX
	});
	
	public static final SiMessage ack_sequence = new SiMessage(new byte[] {
		ACK
	});

	public static final SiMessage read_sicard_5 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_5, 0x00, GET_SI_CARD_5, 0x00, ETX	
	});

	public static final SiMessage read_sicard_6_b0 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x00, 0x46, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_plus_b2 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x02, 0x44, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_plus_b3 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x03, 0x45, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_plus_b4 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x04, 0x42, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_plus_b5 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x05, 0x43, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_b6 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x06, 0x40, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_b7 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x07, 0x41, 0x0A, ETX
	});

	public static final SiMessage read_sicard_6_b8 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_6_BN, 0x01, 0x08, 0x4E, 0x0A, ETX
	});
	
	public static final SiMessage read_sicard_8_plus_b0 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x00, (byte) 0xE2, 0x09, ETX	
	});

	public static final SiMessage read_sicard_8_plus_b1 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x01, (byte) 0xE3, 0x09, ETX	
	});

	public static final SiMessage read_sicard_10_plus_b0 = read_sicard_8_plus_b0;

	public static final SiMessage read_sicard_10_plus_b1 = new SiMessage(new byte[] {
			STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x01, (byte) 0xE3, 0x09, ETX
		});
	
	public static final SiMessage read_sicard_10_plus_b4 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x04, (byte) 0xE6, 0x09, ETX
	});

	public static final SiMessage read_sicard_10_plus_b5 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x05, (byte) 0xE7, 0x09, ETX
	});

	public static final SiMessage read_sicard_10_plus_b6 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x06, (byte) 0xE4, 0x09, ETX
	});

	public static final SiMessage read_sicard_10_plus_b7 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x07, (byte) 0xE5, 0x09, ETX
	});

	public static final SiMessage read_sicard_10_plus_b8 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_8_PLUS_BN, 0x01, 0x08, (byte) 0xEA, 0x09, ETX	
	});

	public static final SiMessage beep_twice = new SiMessage(new byte[] {
		STX, BEEP, 0x01, 0x02, 0x14, 0x0A, ETX
	});
	
	public static final SiMessage request_si_card5 = new SiMessage( new byte[] {
			STX, 0x31, ETX
		});
	public static final SiMessage request_si_card6 = new SiMessage( new byte[] {
			STX, 0x61, 0x08, ETX
		});

	//Station mode
	public static final int STATION_MODE_CTRL = 2;
	public static final int STATION_MODE_FINISH = 4;
	public static final int STATION_MODE_START = 3;
	public static final int STATION_MODE_READ_CARD = 5;
	public static final int STATION_MODE_CLEAR = 7;
	public static final int STATION_MODE_CHECK = 10;
	
	public static String getStationMode( int mode){
		switch( mode ){
			case STATION_MODE_CTRL: return "STATION_MODE_CTRL";
			case STATION_MODE_FINISH: return "STATION_MODE_FINISH";
			case STATION_MODE_START: return "STATION_MODE_START";
			case STATION_MODE_READ_CARD: return "STATION_READ_CARD";
			case STATION_MODE_CLEAR: return "STATION_MODE_CLEAR";
			case STATION_MODE_CHECK: return "STATION_MODE_CHECK";
			default: return "UNKOWN STASTION MODE";
		}
	}
	
}

