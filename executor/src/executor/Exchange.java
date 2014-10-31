package executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Exchange extends Thread {
	public Exchange( String name, int port) {
		this.name = name;
		this.port = port;
		this.serverSocket = null;
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
						System.out.println( "[" + System.currentTimeMillis() + "] " + this.name + " Message received : " + msg.toString());
						System.out.println( Display.bytesToHex( msg.getBytes(), msg.getSize()));
						
						// copy remainder to the beginning of the buffer
						System.arraycopy( inputBuffer, msg.getSize(), inputBuffer, 0, bufferSize - msg.getSize());
						bufferSize -= msg.getSize();
						
						// Do we have a response to this incoming message?
						if( ( response = getResponse( msg)) == null ) continue;
						
						// send the response
						try {
							byte bb[] = response.getBytes();
							os.write( bb);
							System.out.println( "[" + System.currentTimeMillis() + "] " + this.name + " Response Sent : " + response.toString());
							System.out.println( Display.bytesToHex(bb, response.getSize()));
						} catch(IOException ioe) {
							System.out.println("Could not send response because " + ioe.getMessage());
							break;
						}
							
					}
				} catch( Exception e) {
					System.out.println( this.name + " Could not process message because " + e.getMessage() );
					break;
				}
			}
			
			try {
				System.out.println( "Closing server socket");
				this.serverSocket.close();
			} catch( Exception e) {
				
			}
			
		}
	}

	abstract int getHeaderSize();
	abstract Message getMessage(byte[] inputBuffer, int bufferSize);
	abstract Message getResponse( Message msg);


	private String name;
	private int port;
	private ServerSocket serverSocket;
}
