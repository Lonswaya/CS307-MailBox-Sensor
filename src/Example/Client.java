package Example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.net.ssl.SSLSocketFactory;

public class Client {
	
	private ObjectOutputStream m_outputStream = null;
	private ObjectInputStream m_inputStream = null;
	
	private Socket m_socket;
	
	private ServerSocket m_serverSocket;
	
	private Message m_message;
	
	public Client(String ip, int port) throws UnknownHostException, IOException {
		
		SSLSocketFactory f = (SSLSocketFactory)SSLSocketFactory.getDefault(); 
		m_socket = f.createSocket(ip, port);
		//m_socket = new Socket(ip, port);
	}
	
	public void sendMessage() throws IOException {
		
		m_outputStream = new ObjectOutputStream(m_socket.getOutputStream());
		
		m_outputStream.writeObject(m_message);		
		m_outputStream.flush();
	}
	
	public void recieveMessage() throws IOException, ClassNotFoundException {

		m_inputStream = new ObjectInputStream(m_socket.getInputStream());
		
		m_message = (Message)m_inputStream.readObject();
		
		System.out.println(m_message.getMessage());
	}
	
	public Message getMessage() {
		return m_message;
	}
	
	public void setMessage(Message m) {
		m_message = m;
	}
	
	public void close() throws IOException {
		m_outputStream.close();
		m_inputStream.close();
		m_socket.close();
	}
	
	public static void main(String[] args) {
		try {
			
		    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
		    System.setProperty("javax.net.ssl.trustStorePassword", "sensor");
			
		    
		    
			Client client = new Client("localhost", 9999);
			
			Message m = new Message("DEAR SERVER, HI. LOVE CLIENT");
			
			client.setMessage(m);
			
			client.sendMessage();
			
			client.recieveMessage();
			
			Message recieved = client.getMessage();
			
			System.out.println("Recieved: " + recieved.getMessage() + " from server");			
			
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}