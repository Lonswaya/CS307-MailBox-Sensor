import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import Example.Connections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;
import java.util.Observer;

public class CentralServer extends Observable implements Runnable {

	protected ServerSocket ss = null;
	
	//protected SSLSocketFactory socketFactory = null;
	
	protected boolean run = true;
	
	private MessageProcessor ref;
	
	//private Observer obs = null;
	
	public String seperateIP = "localhost";
	public int seperatePort = StaticPorts.serverPort;
	public CentralServer(MessageProcessor obs) {
	    /*System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
	
	    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "sensor");
*/
	    ref = obs;
	    //addObserver(obs);
	    
	    //socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    
		/*SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		try {
			ss = f.createServerSocket(StaticPorts.clientPort);
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
	    ss = Connections.getServerSocket(StaticPorts.clientPort);
	}
	
	public void notifyUsers(Notification notification) {
		
	}
	
	public void sendMessageThreaded(Message msg) {
		
		new Thread(new MessageSender(msg)).start();
		
	}
	
	public void sendMessage(Message msg) {
		
		try {
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
			if (msg.type != null) System.out.println("Sending message: " + msg);

			/*Socket sock = socketFactory.createSocket(this.seperateIP, this.seperatePort);
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(msg);
			out.flush();
			
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
			//Wait for the connection to respond
			System.out.println(((Message)in.readObject()).message);
			
			
			out.close();*/
			Socket sock = Connections.getSocket(this.seperateIP, this.seperatePort);
			Connections.send(sock,msg);
			//Connections.closeSocket(sock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			
			do {
				
				if (!ss.isClosed()) {
					Socket sock = ss.accept();
					new Thread(new SensorListener(sock, this)).start();

				}
				
				//creates a new thread that handles that socket
				
			} while (run);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void set_run(boolean b) {
		this.run = b;
	}

	class SensorListener implements Runnable {

		protected Socket sock;
		protected CentralServer cs;
		
		public SensorListener(Socket sock, CentralServer cs) {
			this.sock = sock;
			this.cs = cs;
		}
		@Override
		public void run() {
			try {
				//in = new ObjectInputStream(sock.getInputStream());
				//out = new ObjectOutputStream(sock.getOutputStream());
				//Message msg = (Message)in.readObject();
				//System.out.println("MESSAGE RECIEVED, type " + msg.type);
				//if(msg.getString().equalsIgnoreCase("quit"))
				//	cs.set_run(false);
				
				//this.cs.notifyObservers(msg);
				Message msg = Connections.readObject(sock);
				ref.ProcessMessage(msg);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}
	
	public static void main(String[] args) {
		
	}
	
	
	public void Kill() {
		//Kills current server socket
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Message AddSensor(ConfigMessage cfg) {
		Message m = new Message("Server connection failed",null,null);
		try {
			
			//Message msg = new Message("Getting sensors", null, MessageType.GET_SENSORS);
			
			
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			cfg.setFrom(address);
			cfg.type = MessageType.ADD_SENSOR;
			
			//NICK I can't use your stuff here
			
			Socket sock = Connections.getSocket(seperateIP, seperatePort);
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(cfg);
			out.flush();
			
			ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

			m = (Message)in.readObject();
			
			in.close();
			out.close();
			sock.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ClientConfig> GetSensors() {
		ArrayList<ClientConfig> ar;
		try {
			Message msg = new Message("Getting sensors", null, MessageType.GET_SENSORS);
			
			
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			//NICK I can't use your stuff here, but it don't matter cause this doesn't happen often
			
			Socket sock = Connections.getSocket(seperateIP, seperatePort, 3000); //if nothing, socket returns null and we just catch 
			/*ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(msg);
			out.flush();*/
			Connections.send(sock, msg);
			
			//ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

			System.out.println("What the fuck");
			
			ar = (Connections.<SensorsMessage>readObject(sock)).ar;//((SensorsMessage)in.readObject()).ar;
			
			System.out.println("Got the fuck");
			
			//in.close();
			//out.close();
			sock.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Server not found");
			return null;
		}
		
		return ar;
		
	}
	class MessageSender implements Runnable { 
		Message msg;
		public MessageSender(Message msg) {
			this.msg = msg;
		}
		
    	public void run() {
    		sendMessage(msg);
    	}
	}
}
