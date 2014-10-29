package se.gsc.stenmark.gscenduro.SporIdent;

import java.util.Arrays;

public class MessageBuffer {
	public MessageBuffer( byte[] buffer){
		position = 0;
		this.buffer = buffer;
	}
	private int position;
	private byte[] buffer;
	
	
	public byte[] readByte(){
		if( position < buffer.length ){
			byte currentByte = buffer[position];
			position++;
			byte[] result = new byte[1];
			result[0] = currentByte;
			return result;
		}
		
		return new byte[0];
	}
	
	public byte[] readBytes(int len){
		byte bytes[] = new byte[len];
		for( int i = 0; i < len; i++){
			byte[] newByte = readByte();
			if( newByte.length == 1){
				bytes[i] = newByte[0];
			}
			else{
				byte[] result = Arrays.copyOfRange(bytes, 0, i);
				return result;
			}
		}
		return bytes;
	}
}
