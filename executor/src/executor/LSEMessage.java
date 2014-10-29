package executor;

public class LSEMessage extends Message {

	/**
	 * Construct from scratch
	 * @param size
	 * @param type
	 */
	public LSEMessage( int size, char type) {
		super( Endianness.LITTLE, size);
		
		// Encode Header
		encode( (byte)2, 0);
		encode( (short)size, 1);
		encode( (byte)type, 3);
	}

	/**
	 * Construct from bytes
	 * @param bb
	 * @param size
	 */
	public LSEMessage(byte[] bb, int size) {
		super( bb, Endianness.LITTLE, size);
	}

}
