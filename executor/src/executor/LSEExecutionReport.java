package executor;

public class LSEExecutionReport extends LSEMessage {
	public static int SIZE = 151;
	
	protected int seqNum;
	
	protected byte side;
	protected String clOrdID;
	protected int quantity;
	protected String orderID;
	protected String execID;
	
	protected int instrumentID;
	protected long executedPrice;
	protected byte status;
	
	public LSEExecutionReport() {
		super( SIZE, '8');
	}
	
	/**
	 * Encode the rest of the message
	 */
	public void encode() {
		// Execution ID
		this.execID = Display.randomString(12);
		encode( this.execID, 9, 12);
		
		// Client Order ID 20@21
		encode( this.clOrdID, 21, 20);
		
		// Order ID 12@41
		this.orderID = Display.randomString(12);
		encode( this.orderID, 41, 12);
		
		// Exec Type 1@53 Forced to "Trade"
		encode( (byte)'F', 53);
		
		// Execution Report RefID 12@54
		
		// OrdStatus 1@66 always filled=2
		if( this.quantity == 666) {
			this.status = 8;
		} else {
			this.status = 2;
		}
		encode( (byte)this.status, 66);
		
		// Order Reject Code 4@67 ignored
		
		// ExecutedPrice 8@71
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
	
	/**
	 * Retrieve information from the order
	 * @param no
	 */
	public void populate( LSENewOrder no) {
		this.clOrdID = no.clOrdID;
		this.instrumentID = no.instrumentID;
		this.executedPrice = no.price;
		this.side = no.side;
	}
	
	@Override
	public String toString() {
		return "LSE Execution Report, instrumentID=" + this.instrumentID 
				+ ", clOrdID=" + this.clOrdID 
				+ ", ordID=" + this.orderID 
				+ ", execID=" + this.execID 
				+ ", executedPrice=" + (this.executedPrice / 100000000) 
				+ ", side=" + this.side 
				+ ", status=" + this.status;
	}
}
