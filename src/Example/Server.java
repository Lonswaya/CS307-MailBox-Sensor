package Example;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Server {

	
	private Message m_message;
	
	private ServerSocket m_serverSocket;
	
	private Socket m_socket;
	
	private ObjectOutputStream m_outputStream;
	
	private ObjectInputStream m_inputStream;
	
	public Server(ServerSocket ss) {
		this.m_serverSocket = ss;
	}
	
	public Message getMessage() {
		return this.m_message;
	}
	
	public void recieveMessage() throws ClassNotFoundException, IOException {
		
		m_socket = m_serverSocket.accept();
		
		m_inputStream = new ObjectInputStream(m_socket.getInputStream());
	
		m_message = (Message)m_inputStream.readObject();
		
		System.out.println(m_message.getMessage());
	}
	
	public void sendMessage() throws IOException {

		m_outputStream = new ObjectOutputStream(m_socket.getOutputStream());

		m_outputStream.writeObject(m_message);
		
		m_outputStream.flush();

	}
	
	
	public void setMessage(Message m) {
		this.m_message = m;
	}
	
	public void close() throws IOException
	{
		m_inputStream.close();
		m_outputStream.close();
		m_socket.close();
	}
	
	
	
	public static void main(String[] args) {
		
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");

	    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "sensor");

	    
		try {
			
			SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			ServerSocket ss = f.createServerSocket(9999);
			
			Server server = new Server(ss);
			do {
				
				//server.recieveMessage();
				
				//Message tmp = server.getMessage();
				
				//System.out.println("Recieved: " + tmp.getString() + " from client");

				SSLSocketFactory fac = (SSLSocketFactory)SSLSocketFactory.getDefault(); 
				Socket sockk = fac.createSocket("localhost", 9999);

				ObjectOutputStream out = new ObjectOutputStream(sockk.getOutputStream());

				out.writeObject(new Message("SHIT"));
				
				out.flush();

				
				
				//server.setMessage(new Message("IM SENDING DA MESSAGE"));
				
				//server.sendMessage();
				
				server.close();
				
			} while (!server.getMessage().getMessage().equalsIgnoreCase("quit"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}