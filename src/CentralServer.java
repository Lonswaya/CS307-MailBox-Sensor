import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.util.Observable;
import java.util.Scanner;
import java.util.Observer;

public class CentralServer extends Observable implements Runnable {

	protected ServerSocket ss = null;
	
	protected SSLSocketFactory socketFactory = null;
	
	protected boolean run = true;
	
	private AutoAwareControlPanel ref;
	
	private Observer obs = null;
	
	public String seperateIP;
	public int seperatePort = 9999;
	public CentralServer(AutoAwareControlPanel obs) {
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
	
	    System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "sensor");

	    ref = obs;
	    //addObserver(obs);
	    
	    socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    
		SSLServerSocketFactory f = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		try {
			ss = f.createServerSocket(9999);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyUsers(Notification notification) {
		
	}
	
	public boolean sendMessage(Message msg, String ip, int port) {
		
		try {
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
			Socket sock = socketFactory.createSocket(ip, port);
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(msg);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
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
				
			} while (run);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void set_run(boolean b) {
		this.run = b;
	}

	class ServerListener implements Runnable {

		protected Socket sock;
		protected ObjectOutputStream out;
		protected ObjectInputStream in;
		protected CentralServer cs;
		
		public ServerListener(Socket sock, CentralServer cs) {
			this.sock = sock;
			this.cs = cs;
		}
		@Override
		public void run() {
			try {
				in = new ObjectInputStream(sock.getInputStream());
				Message msg = (Message)in.readObject();
				//System.out.println("MESSAGE RECIEVED, type " + msg.type);
				if(msg.getString().equalsIgnoreCase("quit"))
					cs.set_run(false);
				
				//this.cs.notifyObservers(msg);
				ref.ProcessMessage(msg);
				
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
				in.close();
				sock.close();
				
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}
	
	public static void main(String[] args) {
		//CentralServer c = new CentralServer();
		
		//Scanner s = new Scanner(System.in);
		//String string = s.nextLine();
	}
	
	
	public void Kill() {
		//Kills current server socket
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<ClientConfig> GetSensors() {
		try {
			Message msg = new Message("Getting sensors", null, MessageType.GET_SENSORS);
			
			
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
			Socket sock = socketFactory.createSocket(seperateIP, seperatePort);
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(msg);
			out.flush();
			
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
		
	}
	
	
}
