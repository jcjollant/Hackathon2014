package executor;

public class LSELogonMessage extends Message {
	static final int SIZE = 80;

	public LSELogonMessage( byte[] inputBuffer) {
		super( Endianness.LITTLE, SIZE);
	}
}
