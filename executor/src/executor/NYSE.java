package executor;


public class NYSE extends Exchange {
	public NYSE() {
		super( "NYSE", 10004, new NYSEExecutionReport(), new NYSENewOrder());
	}

	@Override
	int getHeaderSize() {
		return 8;
	}

	@Override
	Message getMessage(byte[] inputBuffer, int bufferSize) {
		if( bufferSize < getHeaderSize()) return null;
		
		return null;
	}

	@Override
	Message getResponse(Message msg) {
		// TODO Auto-generated method stub
		return null;
	}

}
