import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class CentralServer implements Runnable {
			
	//private Vector<BaseConfig> configVector;
	
	//private Vector<User> userVector;

	protected ServerSocket ss = null;
	
	public CentralServer() {
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
	    
	    
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
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			ServerSocket ss = f.createServerSocket(9999);
			
			CentralServer server = new CentralServer();
			do {
				
				Socket sock = ss.accept();
				
				//creates a new thread that handles that socket
				new Thread(new ServerListener(sock)).start();
				
				/*
				server.recieveMessage(sock);
				
				Message tmp = server.getMessage();
				
				System.out.println("Recieved: " + tmp.getString() + " from client");
				
				server.setMessage(new Message("F*#@ED UP THE MESSAGE", tmp.type));
				
				server.sendMessage();
				
				server.close();
				*/
				
			} while (!server.getMessage().getString().equalsIgnoreCase("quit"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public class ServerListener implements Runnable {

		protected Socket sock = null;
		protected ObjectOutputStream out = null;
		protected ObjectInputStream in = null;
		
		public ServerListener(Socket sock) {
			this.sock = sock;
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
		
		public void sendMessage(Message msg, Socket sock) throws IOException {
			out.writeObject(msg);
			out.flush();
		}
		
		public void close() throws IOException
		{
			in.close();
			out.close();
			sock.close();
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
		
			try {

				//do shit
				
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
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
					
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
		}
		
	}
	
	
	
	
}
