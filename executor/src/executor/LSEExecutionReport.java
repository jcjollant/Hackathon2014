package executor;

public class LSEExecutionReport extends ExecutionReport {
	public LSEExecutionReport() {
		super( Endianness.LITTLE, 151);
	}
	
	public void encode() {
		super.encode();
		
		encode( (byte)2, 0);
		encode( (short)151, 1);
		encode( (byte)'8', 3);
		// Client Order ID 20@21
		encode( this.clOrdID, 21, 20);
		
		// Order ID 12@41
		this.orderID = Display.randomString(12);
		encode( this.orderID, 41, 12);
		
		// Exec Type 1@53 Forced to "Trade"
		encode( (byte)'F', 53);
		
		// Execution Report RefID 12@54
		this.execID = Display.randomString(12);
		encode( this.execID, 54, 12);
		
		// OrdStatus 1@66 always filled=2
		if( this.quantity == 666) {
			this.status = 8;
		} else {
			this.status = 2;
		}
		encode( (byte)this.status, 66);
		
		// Order Reject Code 4@67 ignored
		
		// ExecutedPrice 8@71 TODO read from order
		long executedPrice = this.executedPrice;
		encode( executedPrice, 71);
		
		// ExecutedQty 4@79 
		int executedQty = this.quantity;
		encode( executedQty, 79);
		
		// Leves QTy 4@83
		int leavesQty = 0;
		encode( leavesQty, 83);
		
		// Container 1@87 ignored
		
		// 4@88 DisplayQty set to 0;
		int displayQty = 0;
		encode( displayQty, 88);
		
		// instrumentID 4@92 
		encode( this.instrumentID, 92);
		
		// 1@96 ignored
		
		// Reserved field 2  1@97 ignored
		// Side 1@98 
		encode( this.side, 98);
		
		// 8@99
		
		// 11@107
		// 1@118
		// 8@119
		// TransactTime 8@127
		
		// 1@135
		// 1@136
		// 1@137
		// 1@138
		// 12@139
	}
	
	@Override
	public void populate( NewOrder no) {
		super.populate(no);
		if( no instanceof LSENewOrder) {
			LSENewOrder lno = (LSENewOrder)no;
			this.instrumentID = lno.instrumentID;
			this.executedPrice = lno.price;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ", instrumentID=" + this.instrumentID + ", executedPrice=" + this.executedPrice + ", status=" + this.status;
	}
	
	protected int instrumentID;
	protected long executedPrice;
	protected byte status;
}
