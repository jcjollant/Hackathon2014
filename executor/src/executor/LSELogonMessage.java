package executor;

public class LSELogonMessage extends LSEMessage {
	static final int SIZE = 80;

	public LSELogonMessage( byte[] inputBuffer) {
		super( SIZE, 'A');
		System.arraycopy( inputBuffer, 0, this.bytes, 0, SIZE);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "LSE Logon";
	}
}
