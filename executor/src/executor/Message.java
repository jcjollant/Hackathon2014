package executor;

import java.util.Arrays;

public class Message {
	public Message( Endianness e, int size) {
		this.size = size;
		this.bytes = new byte[size];
		this.endiannes = e;
	}

	public int decodeInt32( int position) {
		int val = 0;
		if( this.endiannes == Endianness.BIG) {
			val += ( int)(this.bytes[  position] & 0xFF) << 24;
			val += ( int)(this.bytes[++position] & 0xFF) << 16;
			val += ( int)(this.bytes[++position] & 0xFF) << 8;
			val += ( int)(this.bytes[++position] & 0xFF);
		} else {
			val += ( int)(this.bytes[  position] & 0xFF) ;
			val += ( int)(this.bytes[++position] & 0xFF) << 8;
			val += ( int)(this.bytes[++position] & 0xFF) << 16;
			val += ( int)(this.bytes[++position] & 0xFF) << 24;
		}
		return val;
	}

	public long decodeInt64( int position) {
		long val = 0;
		if( this.endiannes == Endianness.BIG) {
			val += ( long)(this.bytes[  position] & 0xFF) << 56;
			val += ( long)(this.bytes[++position] & 0xFF) << 48;
			val += ( long)(this.bytes[++position] & 0xFF) << 40;
			val += ( long)(this.bytes[++position] & 0xFF) << 32;
			val += ( long)(this.bytes[++position] & 0xFF) << 24;
			val += ( long)(this.bytes[++position] & 0xFF) << 16;
			val += ( long)(this.bytes[++position] & 0xFF) << 8;
			val += ( long)(this.bytes[++position] & 0xFF);
		} else {
			val += ( long)(this.bytes[  position] & 0xFF);
			val += ( long)(this.bytes[++position] & 0xFF) << 8;
			val += ( long)(this.bytes[++position] & 0xFF) << 16;
			val += ( long)(this.bytes[++position] & 0xFF) << 24;
			val += ( long)(this.bytes[++position] & 0xFF) << 32;
			val += ( long)(this.bytes[++position] & 0xFF) << 40;
			val += ( long)(this.bytes[++position] & 0xFF) << 48;
			val += ( long)(this.bytes[++position] & 0xFF) << 56;
		}
		return val;
	}

	public byte decodeInt8( int position) {
		return (byte)(this.bytes[position] & 0xFF);
	}

	public String decodeString( int position, int length) {
		// look for the first 0 before length
		int index = position;
		for( ;index < position + length && this.bytes[index] != 0; index++){}
		int shortLength = index - position;
		
		byte bytes[] = new byte[shortLength];
		System.arraycopy( this.bytes, position, bytes, 0, shortLength);
		return new String( bytes);
	}
	
	public int encode( byte val, int position) { 
		this.bytes[position] = val;
		return ++position;
	}
	
	public int encode( short val, int position) { 
		if( this.endiannes == Endianness.BIG) {
			this.bytes[position] = (byte)( val & 0xFF);
			this.bytes[++position] = (byte)((val & 0xFF00) >> 8);
		} else {
			this.bytes[position] = (byte)((val & 0xFF00) >> 8);
			this.bytes[++position] = (byte)( val & 0xFF);
		}
		return position + 2;
	}
	
	public int encode( int val, int position) { 
		if( this.endiannes == Endianness.BIG) {
			this.bytes[  position] = (byte)((val & 0xFF000000) >> 24);
			this.bytes[++position] = (byte)((val & 0x00FF0000) >> 16);
			this.bytes[++position] = (byte)((val & 0x0000FF00) >> 8);
			this.bytes[++position] = (byte)( val & 0x000000FF);
		} else {
			this.bytes[  position] = (byte)( val & 0x000000FF);
			this.bytes[++position] = (byte)((val & 0x0000FF00) >> 8);
			this.bytes[++position] = (byte)((val & 0x00FF0000) >> 16);
			this.bytes[++position] = (byte)((val & 0xFF000000) >> 24);
		}
		return position + 4;
	}
	
	public int encode( long val, int position) { 
		if( this.endiannes == Endianness.BIG) {
			this.bytes[  position] = (byte)((val & 0xFF00000000000000L) >> 56);
			this.bytes[++position] = (byte)((val & 0x00FF000000000000L) >> 48);
			this.bytes[++position] = (byte)((val & 0x0000FF0000000000L) >> 40);
			this.bytes[++position] = (byte)((val & 0x000000FF00000000L) >> 32);
			this.bytes[++position] = (byte)((val & 0x00000000FF000000L) >> 24);
			this.bytes[++position] = (byte)((val & 0x0000000000FF0000L) >> 16);
			this.bytes[++position] = (byte)((val & 0x000000000000FF00L) >> 8);
			this.bytes[++position] = (byte)( val & 0x00000000000000FFL);
		} else {
			this.bytes[  position] = (byte)( val & 0x00000000000000FFL);
			this.bytes[++position] = (byte)((val & 0x000000000000FF00L) >> 8);
			this.bytes[++position] = (byte)((val & 0x0000000000FF0000L) >> 16);
			this.bytes[++position] = (byte)((val & 0x00000000FF000000L) >> 24);
			this.bytes[++position] = (byte)((val & 0x000000FF00000000L) >> 32);
			this.bytes[++position] = (byte)((val & 0x0000FF0000000000L) >> 40);
			this.bytes[++position] = (byte)((val & 0x00FF000000000000L) >> 48);
			this.bytes[++position] = (byte)((val & 0xFF00000000000000L) >> 56);
		}
		return position + 8;
	}
	
	public int encode( String val, int position, int size) { 
		String subString;
		if( val.length() > size) {
			subString = val.substring( 0, size - 1);
		} else {
			// Right Padding
			subString = String.format("%-" + size + "s", val);
		}
		System.arraycopy( subString.getBytes(), 0, this.bytes, position, subString.length());

		return position + size;
	}
//	public int encode( String val, int position, int size) { 
//		String subString;
//		if( val.length() > size) {
//			subString = val.substring( 0, size - 1);
//		} else {
//			subString = val;
//		}
//		System.arraycopy( subString.getBytes(), 0, this.bytes, position, subString.length());
//
//		return position + size;
//	}
	
	public byte getByte( int position) { return bytes[position];}
	public byte[] getBytes() { return bytes;}
	public int getSize() { return size;}
	
	public void reset() {
		Arrays.fill( this.bytes, (byte)0);
	}
	
	protected byte[] bytes;
	
	private Endianness endiannes;
	private int size;
}
