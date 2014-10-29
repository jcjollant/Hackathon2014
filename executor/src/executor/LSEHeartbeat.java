package executor;

public class LSEHeartbeat extends LSEMessage {
	static public int SIZE = 4;

	public LSEHeartbeat() {
		super( SIZE, '0');
	}

	@Override
	public String toString() {
		return "LSE Heartbeat";
	}
}
