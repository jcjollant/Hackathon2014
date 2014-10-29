package executor;

public class LSENewOrder extends LSEMessage {
	static final int SIZE = 97;
	
	public String clOrdID;
	public byte side;
	public int quantity;
	
	public LSENewOrder( byte[] bb) {
		super( bb, SIZE);
		
		this.clOrdID = decodeString( 4, 20);
		this.instrumentID = decodeInt32( 46);
		this.side = decodeInt8( 58);
		this.quantity = decodeInt32( 59);
		this.price = decodeInt64( 67);
	}
	
	@Override
	public String toString() {
		return "LSE New Order, instrumentID=" + this.instrumentID 
				+ ", side=" + this.side
				+ ", price=" + (this.price / 100000000)
				+ ", quantity=" + this.quantity
				;
	}
	
	public int instrumentID;
	public long price;
}
