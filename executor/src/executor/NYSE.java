package executor;


public class NYSE extends Exchange {
	public NYSE() {
		super( "NYSE", 10004, new NYSEExecutionReport(), new NYSENewOrder());
	}

}
