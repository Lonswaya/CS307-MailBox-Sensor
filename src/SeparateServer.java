
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

import Example.Connections;

public class SeparateServer {
			
	protected ServerSocket ss = null; 			     //SSL Server socket for accepting connections
	
	
	//static SSLSocketFactory socketFactory = null; //factory for creating new SSL conections to other ServerSockets
	
	static boolean run = true; 					 	 //used to allow the ServerListener thread end the main server's execution by setting this to false
	
	
	
	static Hashtable<String, SocketInfo> uiSockets = new Hashtable<String, SocketInfo>();
	
	static Hashtable<String, SensorInfo> sendingList = new Hashtable<String, SensorInfo>();
	
	public SeparateServer() {
		ss = Connections.getServerSocket(StaticPorts.serverPort);//f.createServerSocket(StaticPorts.serverPort, 6969); //create our server socket
		
	}
	
	
	
	
	/*static synchronized void sendMessage(Message msg, String address, int port, boolean setFrom) {
		if (setFrom) {
			String myAddress = null;
			try {
				myAddress = InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //get the local ip address
			myAddress = myAddress.substring(myAddress.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(myAddress);	
		}
		Socket sock = Connections.getSocket(address, port);
		Connections.send(sock, msg);
		
	}*/
	
	
	static synchronized void sendMessage(Socket sock, Message msg, boolean setFrom) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMessage(out, msg, setFrom);
	}
	
	//sends message without closing socket
	static synchronized void sendMessage(ObjectOutputStream out, Message msg, boolean setFrom) {
		if (setFrom) {
			String myAddress = null;
			try {
				myAddress = InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //get the local ip address
			myAddress = myAddress.substring(myAddress.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(myAddress);	
		}
		Connections.send(out, msg);
		System.out.println("Message sent of type " + msg.type);
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
	
	public static void main(String[] args) throws IOException {
		SeparateServer server = new SeparateServer(); 			//initialize server
		System.out.println("Booting up server");

		do {
			Socket sock = server.getNextConnection();			//get a new connection from the server
			System.out.println("Next Connection " + sock);
			if(sock == null)									//if there was an error getting  a new connection
				continue;										//restart to wait for a new, valid connection
			
			new Thread(new ServerListener(sock)).start();		//creates a new thread that handles that socket
			
		} while (SeparateServer.run);							//as long as the server is running, loop
	}

	
}
