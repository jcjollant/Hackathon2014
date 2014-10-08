package executor;


public class Executor {
	
	final static int portLSE  = 10003;
	final static int portNYSE = 10004;

	public static void main(String[] args) {
		// start on thread per exchange
		LSE lse = new LSE();
		NYSE nyse = new NYSE();
		
		lse.start();
		nyse.start();
		
		while( lse.isAlive() || nyse.isAlive()) {
			if( !lse.isAlive()) System.out.println( "Warning LSE is down");
			if( !nyse.isAlive()) System.out.println( "Warning NYSE is down");
			try {
				Thread.sleep( 5000);
			} catch( InterruptedException ie) {
				break;
			}
		}
		
		System.out.println( "Graceful termination");
	}

}
