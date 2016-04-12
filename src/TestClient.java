package src;
import cs307.purdue.edu.autoawareapp.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.sun.org.apache.bcel.internal.generic.GOTO;

public class TestClient {

	
	public Thread receiving = null;
	
	
	public ServerSocket ss = null; 			     //SSL Server socket for accepting connections
	
	static SSLSocketFactory socketFactory = null; //factory for creating new SSL conections to other ServerSockets
	
	public TestClient() {
		System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");    //get ssl working
		System.setProperty("javax.net.ssl.keyStorePassword", "sensor");   //get ssl working
		
		System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
		System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
		
		SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault(); //initialize the factory for creating our server socket
		try {
			ss = f.createServerSocket(9999); //create our server socket
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Yeah something's gone super wrong");
			System.exit(1); //if we can't accept requests the server is useless so we should exit
		}
		
		socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		
		
	    receiving = new Thread(new Runnable() {
	    	
	    	private ServerSocket ss = null;

			@Override
	    	public void run() {
				
				while(true) {
					
		    		try {
		    			Socket sock = ss.accept();
		    			
		    			System.out.println("Got connection");
		    			
		    			new Thread(new TestClientReceiver(sock)).start();
		    			
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
					
				}	
	    	}
	    	
			public Runnable init(ServerSocket s) {
				this.ss = s;
				return this;
			}
	    	
	    }.init(ss));
		
	    receiving.start();
	    
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
	
	static boolean sendMessage(Message msg, String ip, int port) { 	// TODO: make it threaded
		
		Socket sock = null; 		   								//declared out here to maintain scope
		ObjectOutputStream out = null; 								//same
		
		try {
			String address = InetAddress.getLocalHost().toString(); //get the local ip address
			address = address.substring(address.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(address);									//set the sent from property of message
			
			sock = TestClient.socketFactory.createSocket(ip, port);			//create a socket to send the message over
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
	
	
	
	public static void main(String[] args) {
	
		TestClient client = new TestClient();
		Scanner scan = new Scanner(System.in);
		while(true) {
			
			System.out.println("What do you want to send?");
			
			String[] options = {
		            "0: Send config", 
		            "1: Send base message", 
		            "2: Send add sensor"
		            };
			
			for (String s : options)
				System.out.println(s);				

			
			int option = scan.nextInt();
			switch(option) {
			case 0:				//config
				
				
				BaseConfig base = new BaseConfig();
				ConfigMessage cm = new ConfigMessage("Config", base);
				TestClient.sendMessage(cm, "localhost", 8888);
				
				
				break;
			case 1:				//base message
				
				
				
				Message msg = new Message("Yo", null);
				TestClient.sendMessage(msg, "localhost", 8888);
				
				
				
				break;
			case 2:				//add_sensor
				
				
				
				ConfigMessage conMes = new ConfigMessage("con", new BaseConfig());
				Message mes = (Message)conMes;
				mes.type = MessageType.ADD_SENSOR;
				Socket socket = null;
				try {
					socket = TestClient.socketFactory.createSocket("localhost", 8888);
					if (socket == null) {
						System.out.println("Error creating socket");
						continue;
					}
				} catch (Exception e) {
					System.out.println("Exception creating socket");
					continue;
				}
				//socket is gucci
				ObjectOutputStream out = null;
				try {
					out = new ObjectOutputStream(socket.getOutputStream());
				} catch (Exception e) {
					System.out.println("Exception opening ObjectOutputStream");
					continue;
				}
				//out is gucci
				if(!TestClient.sendMessage(out, mes, "localhost", 8888)) {
					System.out.println("Error sending message");
					continue;
				}
				
				ObjectInputStream in = null;
				try {
					in = new ObjectInputStream(socket.getInputStream());
				} catch (Exception e) {
					System.out.println("Exception opening ObjectInputStream");
					continue;
				}
				//in in gucci
				Message received = TestClient.receiveMessage(in);
				if(received == null) {
					System.out.println("Error receiving message");
					continue;
				}
				
				System.out.println("Received message: " + received);
				
				break;	
			default:			//bad
				System.out.println("Bad input. Bad user.");
				continue;
			}
			
			
			
			
		}
		
		
	}
	
}
