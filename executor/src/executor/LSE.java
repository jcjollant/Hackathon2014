package executor;


public class LSE extends Exchange {
	public LSE() {
		super( "LSE", 10003, new LSEExecutionReport(), new LSENewOrder());
	}

}
