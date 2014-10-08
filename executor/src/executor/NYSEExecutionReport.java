package executor;

public class NYSEExecutionReport extends ExecutionReport {

	public NYSEExecutionReport() {
		super(Endianness.BIG, 116);
	}
	
	void encode() {
		super.encode();
		
		// Message Type 2@0
		encode( (short)0x0081, 0);
		
		// MsgLength 2@2
		encode( (short)getSize(), 2);
		
		// SeqNum 4@4
		encode( (int)++seqNum, 4);
		
		// 4@8 ignored
		
		// Transactime 4@12
		encode( (int)System.currentTimeMillis(), 12);
		
		// LeavesQty 4@16
		int leavesQty = 16;
		encode( leavesQty, 16);
		
		// LastShares 4@20
		int lastShares = this.quantity;
		encode( lastShares, 20);
		
		// Last Price 4@24
		int lastPrice = this.lastPrice;
		encode( lastPrice, 24);
		
		// Price Scale 1@28
		byte priceScale = 0;
		encode( priceScale, 28);
		
		// Side 1@29
		byte side = this.side;
		encode( side, 29);
		
		// BillingIndicator 1@30
		byte billingIndicator = '1';
		encode( billingIndicator, 30);
		
		// LastMarket 1 @31
		
		// DeliverToCompID 5 @ 32
		
		// TargetSubID 5@37
		
		// ExecBroker 5@42
		
		// ContraBroker 5@47
		
		// ContraTrader 5@52
		
		// ExecAway Mkt ID 6@57
		
		// BillingRate 6@63
		
		// ExecID 10@69
		this.execID = Display.randomString(10);
		encode( this.execID, 69, 10);
		
		// Account 10@79
		
		// DBExecID 10@89
		
		// ClientOrderID 17@99
		encode( this.clOrdID, 99, 17);
	}
	
	public void populate(NewOrder no) {
		super.populate(no);
		if( no instanceof NYSENewOrder) {
			NYSENewOrder nno = (NYSENewOrder)no;
			this.symbol = nno.symbol;
			this.lastPrice = nno.price;
		}
	}
	
	
	@Override
	public String toString() {
		return super.toString() + ", lastPrice=" + this.lastPrice + ", symbol=" + this.symbol;
	}
	
	protected int lastPrice;
	protected String symbol;

}
