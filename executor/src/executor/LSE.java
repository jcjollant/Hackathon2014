package executor;


public class LSE extends Exchange {
	public LSE() {
		super( "LSE", 10003);
	}

	@Override
	int getHeaderSize() {
		return 4;
	}

	@Override
	Message getMessage(byte[] inputBuffer, int bufferSize) {
		if( bufferSize < getHeaderSize()) return null;
		
		// create proper message base on type
		switch( inputBuffer[3]) {
		case '0': // heartbeat
			if( bufferSize < LSEHeartbeat.SIZE) return null;
			return new LSEHeartbeat();
			
		case 'D': // New Order
			if( bufferSize < LSENewOrder.SIZE) return null;
			try {
				return new LSENewOrder(inputBuffer);
			} catch( Exception e) {
				return null;
			}
			
		case 'A': // Logon
			if( bufferSize < LSELogonMessage.SIZE) return null;
			return new LSELogonMessage( inputBuffer);
			
		default:
			return null;
		}

	}

	@Override
	Message getResponse(Message msg) {
		if( msg instanceof LSENewOrder) {
			LSEExecutionReport er = new LSEExecutionReport();
			er.populate( ( LSENewOrder)msg);
			er.encode();
			return er;
		} else if ( msg instanceof LSELogonMessage) {
			LSELogonReply lr = new LSELogonReply();
			return lr;
		} else if ( msg instanceof LSEHeartbeat) {
			// hearbeats are identical just resend inbound msg
			return (LSEHeartbeat)msg;
		}
		
		return null;
	}

}
