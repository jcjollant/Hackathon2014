package executor;


public class LSE extends Exchange {
	public LSE() {
		super( "LSE", 10003, new LSEExecutionReport(), new LSENewOrder());
	}

	@Override
	int getHeaderSize() {
		return 4;
	}

	@Override
	Message getMessage(byte[] inputBuffer, int bufferSize) {
		if( bufferSize < getHeaderSize()) return null;
		
		if( inputBuffer[3] == 'D') {
			if( bufferSize < this.newOrder.getSize()) return null;
			try {
				this.newOrder.decode( inputBuffer);
			} catch( Exception e) {
				
			}
			return this.newOrder;
		} else if( inputBuffer[3] == 'A') {
			Message output = new LSELogonMessage( inputBuffer);
			if( bufferSize < output.getSize()) return null;
			return output;
		}

		return null;
	}

}
