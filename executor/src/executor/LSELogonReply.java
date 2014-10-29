package executor;

public class LSELogonReply extends LSEMessage {
	static public int SIZE = 38;
	
	public LSELogonReply() {
		
		super( SIZE, 'B');

		// Reject code
		encode( (int)0, 4);
		// Password Expiry Day Count
		encode( "Password Expiry !Implemented", 8, 30);
	}
	
	@Override
	public String toString() {
		return "LSE Logon Reply";
	}

}
