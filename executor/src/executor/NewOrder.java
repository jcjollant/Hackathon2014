package executor;

public abstract class NewOrder extends Message {
	public NewOrder( Endianness e, int size) {
		super( e, size);
	}
	
	public void decode( byte bb[]) throws Exception {
		this.bytes = bb;
	}
	
	@Override
	public String toString() {
		return "clOrdID=" + this.clOrdID + ", side=" + this.side + ", quantity=" + this.quantity;
	}
	
	public String clOrdID;
	public byte side;
	public int quantity;
}
