package test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MiddleManHandler implements Runnable {
	private Socket fromSock, toSock;
	private ObjectInputStream fromIn, toIn;
	private ObjectOutputStream fromOut, toOut;
	
	public MiddleManHandler(Socket from, Socket to) {
		this.fromSock = from;
		this.toSock = to;
		try {
			this.fromIn = new ObjectInputStream(this.fromSock.getInputStream());
			this.fromOut = new ObjectOutputStream(this.fromSock.getOutputStream());	
			this.toIn = new ObjectInputStream(this.toSock.getInputStream());
			this.toOut = new ObjectOutputStream(this.toSock.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		//starting to read a new connection
		
		if (fromIn == null || fromOut == null || toIn == null || toOut == null) return;
		
		Message message = Connections.<Message>readObjectWithAck(fromIn, fromOut, new Acknowledge());
		if (message == null) return;
		
		
		
	}
	
	
}
