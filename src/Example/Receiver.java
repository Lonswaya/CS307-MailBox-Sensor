package Example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {

	ServerSocket ss;
	final static int DEFAULT_RECEIVER_PORT = 7000;
	
	public Receiver(ServerSocket ss) {
		this.ss = ss;
	}
	
	public void run() {
		while(true) {
			
			try {
				Socket fromMiddleman = this.ss.accept();
				new Thread(new Runnable() {
					@Override
					public void run() {				
						Message msg = Connections.readObject(fromMiddleman);
						System.out.println(msg.getMessage());
						Connections.closeSocket(fromMiddleman);
					}
				}).start();
				
				System.out.println("Spawned thread");
			} catch (Exception e) {		e.printStackTrace();	}	
		}
	}
	
	public static void main(String[] args) {
		ServerSocket ss = Connections.getServerSocket(DEFAULT_RECEIVER_PORT);	
		new Receiver(ss).run();
	}
	
}
