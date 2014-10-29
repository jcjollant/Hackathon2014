package executor;

public class LSEMessage extends Message {

	public LSEMessage( int size, char type) {
		super( Endianness.LITTLE, size);
		
		// Encode Header
		encode( (byte)2, 0);
		encode( (short)size, 1);
		encode( (byte)type, 3);
	}

}
