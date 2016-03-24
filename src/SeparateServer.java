
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SeparateServer {
			
	protected ServerSocket ss = null; 			     //SSL Server socket for accepting connections
	
	static SSLSocketFactory socketFactory = null; //factory for creating new SSL conections to other ServerSockets
	
	static boolean run = true; 					 	 //used to allow the ServerListener thread end the main server's execution by setting this to false
	
	static ArrayList<String> ui_ips = new ArrayList<String>();
	
	public SeparateServer() {
		
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");    //get ssl working
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");   //get ssl working
	
	    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
	    System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
	    
		SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault(); //initialize the factory for creating our server socket
		try {
			ss = f.createServerSocket(8888); //create our server socket
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Yeah something's gone super wrong");
			System.exit(1); //if we can't accept requests the server is useless so we should exit
		}
	}
	
	
	public void notifyUsers(Notification notification) {
		
		return;
	}	
	
	
	/*
	 * Purpose: Sends a message across ssl
	 * Returns: True if the message sent successfully and false otherwise
	 * Parameters:
	 * 				Message: an instances of the message class or any of its derivatives
	 * 				IP:		 the ip address to send the message to
	 * 				Port: 	 the port over which to send the message
	 */
	static boolean sendMessage(Message msg, String ip, int port) { 	// TODO: make it threaded
		
		Socket sock = null; 		   								//declared out here to maintain scope
		ObjectOutputStream out = null; 								//same
		
		try {
			String address = InetAddress.getLocalHost().toString(); //get the local ip address
			address = address.substring(address.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(address);									//set the sent from property of message
			
			sock = SeparateServer.socketFactory.createSocket(ip, port);			//create a socket to send the message over
			out = new ObjectOutputStream(sock.getOutputStream());	//get the output stream from the socket	
			out.writeObject(msg);									//write our message over the socket
			out.flush();											//flush to ensure the message is sent
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {													//cleaning up
			try {
				if (out != null) out.close();
				if (sock != null && !sock.isClosed()) sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;	
	}
	
	//receives message without closing socket
	static Message receiveMessage(ObjectInputStream in) {
		try {
			return (Message)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//sends message without closing socket
	static boolean sendMessage(ObjectOutputStream out, Message msg, String ip, int port) {
		try {
			String address = InetAddress.getLocalHost().toString(); //get the local ip address
			address = address.substring(address.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(address);									//set the sent from property of message
			
			out.writeObject(msg);									//write our message over the socket
			out.flush();											//flush to ensure the message is sent
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Purpose:		accepts a new connection from the server's serversocket
	 * Returns:		a Socket if it successfully received a new connection
	 * 				null if there was an error
	 */
	public Socket getNextConnection() {
		try {
			return ss.accept();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {

		SeparateServer server = new SeparateServer(); 			//initialize server
		do {
			Socket sock = server.getNextConnection();			//get a new connection from the server
			if(sock == null)									//if there was an error getting  a new connection
				continue;										//restart to wait for a new, valid connection
									
			new Thread(new ServerListener(sock)).start();		//creates a new thread that handles that socket
			
		} while (SeparateServer.run);							//as long as the server is running, loop
	}

	
}
