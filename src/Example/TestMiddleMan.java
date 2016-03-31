package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestMiddleMan {

	final static int DEFAULT_PORT = 6969;
	
	private ServerSocket serverSock;
	public TestMiddleMan(ServerSocket ss) {
		this.serverSock = ss;
	}
	
	public TestMiddleMan() {
		this.serverSock = Connections.getServerSocket(DEFAULT_PORT);
	}
	
	public void run() {
		while(true) {
			
			try {
				Socket slaveSock = this.serverSock.accept();
				new Thread(new )
			} catch (Exception e) {		e.printStackTrace();	}
			
			
			
			
		}
	}
	
	public static void main(String args[]) {
		
		TestMiddleMan test = new TestMiddleMan();
		test.run();
		
	}
	
}
