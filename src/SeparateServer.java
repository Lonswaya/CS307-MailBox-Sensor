
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Queue;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SeparateServer {
			
	protected ServerSocket ss = null; 			     //SSL Server socket for accepting connections
	
	
	static SSLSocketFactory socketFactory = null; //factory for creating new SSL conections to other ServerSockets
	
	static boolean run = true; 					 	 //used to allow the ServerListener thread end the main server's execution by setting this to false
	
	static ArrayList<String> ui_ips = new ArrayList<String>();
	
	static Hashtable<String, SensorSendingList> sendingList = new Hashtable<String, SensorSendingList>();
	
	//static ArrayList<String> valueSendingUI = new ArrayList<String>();
	//static ArrayList<String> pictureSendingUI = new ArrayList<String>();
	//static ArrayList<String> audioSendingUI = new ArrayList<String>();
	
	//static Hashtable<String, Float> readingTable = new Hashtable<String, Float>();
	//static Hashtable<String, Queue<AudioClip>> audioTable = new Hashtable<String, Queue<AudioClip>>();
	//static Hashtable<String, BufferedImage> pictureTable = new Hashtable<String, BufferedImage>();
	
	//static ArrayList<ClientConfig> sensorConfigs = new ArrayList<ClientConfig>(); //TODO remove once database is established
	
	public SeparateServer() {
		
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");    //get ssl working
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");   //get ssl working
	
	    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
	    System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
	    
		SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault(); //initialize the factory for creating our server socket
	
		socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		try {
			ss = f.createServerSocket(StaticPorts.serverPort, 6969); //create our server socket
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
	/*
	static boolean sendMessage(Message msg, String ip, int port, boolean setFrom) { 	// TODO: make it threaded
		
		Socket sock = null; 		   								//declared out here to maintain scope
		ObjectOutputStream out = null; 								//same
		
		try {
			String address = InetAddress.getLocalHost().toString(); //get the local ip address
			address = address.substring(address.indexOf('/') + 1);  //strip off the unnecessary bits
			if (setFrom) msg.setFrom(address);									//set the sent from property of message
			System.out.println("Sending message to ip: " + ip + " and port: " + port);
			sock = SeparateServer.socketFactory.createSocket(ip, port);			//create a socket to send the message over
			out = new ObjectOutputStream(sock.getOutputStream());	//get the output stream from the socket	
			out.writeObject(msg);									//write our message over the socket
			out.flush();											//flush to ensure the message is sent
		} catch (Exception e) {
			System.out.println("Message sending failed");
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
	*/
	
	//receives message without closing socket
	static synchronized Message receiveMessage(ObjectInputStream in) {
		try {
			return (Message)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static synchronized Socket getSocket(String host, int port) {
		try {
			return SeparateServer.socketFactory.createSocket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static synchronized boolean sendMessage(Message msg, String address, int port, boolean setFrom) {
		try {
			Socket ss = SeparateServer.getSocket(address, port);
			ObjectOutputStream out = new ObjectOutputStream(ss.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(ss.getInputStream());
			sendMessage(out, in, msg, setFrom);
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	//sends message without closing socket
	static synchronized boolean sendMessage(ObjectOutputStream out, ObjectInputStream in, Message msg, boolean setFrom) {
	
		try {
			String address = InetAddress.getLocalHost().toString(); //get the local ip address
			address = address.substring(address.indexOf('/') + 1);  //strip off the unnecessary bits
			if (setFrom)
				msg.setFrom(address);									//set the sent from property of message
			System.out.println("Sending message " + msg.message);
			out.writeObject(msg);									//write our message over the socket
			out.flush();									//flush to ensure the message is sent
			System.out.println("Message sent successfully");
			//Wait for the connection to respond
			System.out.println(((Message)in.readObject()).message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Message not correctly sent");
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
	/*public static ClientConfig ConfigFind(String identifier) { //TODO remove when adding database
    	//System.out.println(configs);
    	for (int i = 0; i < sensorConfigs.size(); i++) {
    		//System.out.println(configs.get(i).ip);
    		if (sensorConfigs.get(i) != null) {
	    		if ((sensorConfigs.get(i).ip).equals(identifier)) {
	    			return sensorConfigs.get(i);
	    		}
    		}
    	}
    	//was not found
    	return null;
    }*/
	
	public static void main(String[] args) throws IOException {
		SeparateServer server = new SeparateServer(); 			//initialize server
		System.out.println("Booting up server");

		do {
			Socket sock = server.getNextConnection();			//get a new connection from the server
			System.out.println("Next Connection");
			if(sock == null)									//if there was an error getting  a new connection
				continue;										//restart to wait for a new, valid connection
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			new Thread(new ServerListener(in, out)).start();		//creates a new thread that handles that socket
			
		} while (SeparateServer.run);							//as long as the server is running, loop
	}

	
}
