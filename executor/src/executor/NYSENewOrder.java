package executor;

public class NYSENewOrder extends NewOrder {

	public NYSENewOrder() {
		super( Endianness.BIG, 84);
	}
	
	@Override
	public void decode(byte[] bb) throws Exception {
		super.decode( bb);
		
		this.quantity = decodeInt32( 8);
		this.price = decodeInt32(16);
		this.symbol = decodeString( 21, 11);
		this.side = decodeInt8( 33);
		this.clOrdID = decodeString( 64, 17);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + ", symbol=" + this.symbol + ", price=" + this.price;
	}
	
	public String symbol;
	public int price;
}
