package Example;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {
	
	final static int DEFAULT_MIDDLEMAN_PORT = 6969;	
	
	public void run() {
		while(true) {
			Message msg = new Message("Made message");
			Socket sock = Connections.getSocket("localhost", DEFAULT_MIDDLEMAN_PORT);
			Connections.send(sock, msg);
			System.out.println("Sent message");
			Connections.closeSocket(sock);
		}
	}
	
	
	public static void main(String[] args) {
		new Sender().run();
	}
	

}
