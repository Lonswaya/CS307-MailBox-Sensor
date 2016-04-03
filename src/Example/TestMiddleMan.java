package Example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestMiddleMan {

	final static int DEFAULT_MIDDLEMAN_PORT = 6969;
	final static int DEFAULT_RECEIVER_PORT = 7000;
	
	private ServerSocket serverSock;
	public TestMiddleMan(ServerSocket ss) {
		this.serverSock = ss;
	}
	
	public TestMiddleMan() {
		this.serverSock = Connections.getServerSocket(DEFAULT_MIDDLEMAN_PORT);
	}
	
	public void run() {
		while(true) {
			
			try {
				Socket fromSender = this.serverSock.accept();
				Socket toReceiver = Connections.getSocket("localhost", DEFAULT_RECEIVER_PORT);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						
						Message msg = Connections.readObject(fromSender);
						System.out.println("Read msg");
						Connections.closeSocket(fromSender);
						Connections.send(toReceiver, msg);
						System.out.println("Sent msg");
						Connections.closeSocket(toReceiver);
					}
					
					
				}).start();
								
				System.out.println("Spawned thread");
			} catch (Exception e) {		e.printStackTrace();	}
		}
	}
	
	public static void main(String args[]) {
		ServerSocket ss = Connections.getServerSocket(DEFAULT_MIDDLEMAN_PORT);
		new TestMiddleMan(ss).run();
	}
	
}
