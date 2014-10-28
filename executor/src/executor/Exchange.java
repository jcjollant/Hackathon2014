package executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Exchange extends Thread {
	public Exchange( String name, int port, ExecutionReport er, NewOrder no) {
		this.name = name;
		this.port = port;
		this.executionReport = er;
		this.newOrder = no;
		this.serverSocket = null;
	}

	protected void finalize() throws Throwable {
		if( this.serverSocket != null) this.serverSocket.close();
	}
	
	@Override
	public void run() {
		
		try {
			this.serverSocket = new ServerSocket( this.port);
		} catch( IOException ioe) {
			System.out.println( "Could not create " + this.name + " server socket because " + ioe.getMessage());
			return;
		}
		
		// this loop if for all connections
		while( true) {
			Socket clientSocket = null;
			System.out.println( "Listenning for " + this.name + " incomming connections on " + this.serverSocket.getLocalPort() + ". Thread ID=" + Thread.currentThread().getId());
			try {
				clientSocket = this.serverSocket.accept();
			} catch( IOException ioe) {
				System.out.println( "Could not accept connection because " + ioe.getMessage());
				return;
			}
			
			// We have a hit
			System.out.println( this.name + " Incomming Connection from " + clientSocket);
			OutputStream os = null;
			InputStream is = null;
			try {
				os = clientSocket.getOutputStream();
				is = clientSocket.getInputStream();
			} catch( IOException ioe) {
				System.out.println( this.name + "Could get outputstream because " + ioe.getMessage());
				continue;
			}
			

			Message msg = null;
			Message response = null;
			
			while( true) { // this is the incomming orders loop
				try {
					byte inputBuffer[] = new byte[1024];
					int bufferSize = 0;
					while( ( bufferSize += is.read( inputBuffer)) != -1 ) {
						// do we have enough to read the full message?
						if( ( msg = this.getMessage( inputBuffer, bufferSize)) == null) continue;
						
						// copy remainder to the beginning of the buffer
						System.arraycopy( inputBuffer, msg.getSize(), inputBuffer, 0, bufferSize - msg.getSize());
						bufferSize -= msg.getSize();
						
						// send proper response
						if( msg.isNewOrder()) {
							
							System.out.println( this.name + " Received New Order (" + bufferSize + " bytes)");
							this.newOrder.decode( inputBuffer);
							System.out.println( this.name + " Order: " + this.newOrder.toString());
							System.out.println( Display.bytesToHex( inputBuffer, bufferSize));
							
							try {
								this.executionReport.populate( this.newOrder);
								this.executionReport.encode();
							} catch( Exception e) {
								System.out.println( "Could not encode response because " + e.getMessage());
							}
						} else if( msg.isLogon()) {
							response = msg;
						}
						
						if( response != null) {
							// send the response
							try {
								byte bb[] = response.getBytes();
								os.write( bb);
								System.out.println( this.name + " Sent Response (" + bb.length + " bytes)");
								System.out.println( this.name + " Message: " + this.executionReport.toString());
								System.out.println( Display.bytesToHex(bb, bb.length));
								Thread.sleep( 1000);
								clientSocket.close();
								response = null;
							} catch(IOException ioe) {
								System.out.println("Could not send response because " + ioe.getMessage());
								break;
							}
							
						}
					}
				} catch( Exception e) {
					System.out.println( "Could not process order because " + e.getMessage() );
					break;
				}
			}
			
			
		}
	}

	abstract Message getMessage(byte[] inputBuffer, int bufferSize);

	abstract int getHeaderSize();

	private String name;
	private int port;
	private ExecutionReport executionReport;
	protected NewOrder newOrder;
	private ServerSocket serverSocket;
}
