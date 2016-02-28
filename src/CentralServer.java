import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.util.Observable;

public class CentralServer extends Observable implements Runnable {
			
	//private Vector<BaseConfig> configVector;
	
	//private Vector<User> userVector;

	protected ServerSocket ss = null;
	
	private SSLSocketFactory socketFactory = null;
	
	public CentralServer() {
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
	    
	    
	    socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    
		SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		try {
			ss = f.createServerSocket(9999);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void notifyUsers(Notification notification) {
		
	}
	
	public boolean sendMessage(Message msg, String ip, int port) {
		
		try {
			Socket sock = socketFactory.createSocket(ip, port);
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			return false;
		}
		
		return true;	
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			do {
				
				Socket sock = ss.accept();
				
				//creates a new thread that handles that socket
				new Thread(new ServerListener(sock, this)).start();
				
			} while (!server.getMessage().getString().equalsIgnoreCase("quit"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public class ServerListener implements Runnable {

		protected Socket sock = null;
		protected ObjectOutputStream out = null;
		protected ObjectInputStream in = null;
		protected CentralServer cs = null;
		
		public ServerListener(Socket sock, CentralServer cs) {
			this.sock = sock;
			this.cs = cs;
			try {
				out = new ObjectOutputStream(sock.getOutputStream());
				in = new ObjectInputStream(sock.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Message receiveMessage() throws ClassNotFoundException, IOException {
			return (Message)in.readObject();
		}
		
		public void sendMessage(Message msg) throws IOException {
			out.writeObject(msg);
			out.flush();
		}
		
		public void close() throws IOException
		{
			in.close();
			out.close();
			sock.close();
		}
		
		//sends notification to observers from central server
		public void notifyObservers(Message msg) {
			cs.notifyObservers(msg);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
		
			try {

					Message msg = receiveMessage();
					
					switch(msg.type) {
					
					case VIDEO:
						break;
					case AUDIO:
						break;
					case LIGHT:
						break;
					case CONFIG:
						break;
					default:
						break;
						
					}
					
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			
			
			
			
		}
		
	}
	
	
	
	
}
