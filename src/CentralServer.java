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
import java.util.Hashtable;
import java.util.Observable;
import java.util.Scanner;
import java.util.Observer;

public class CentralServer extends Observable implements Runnable {

	protected ServerSocket ss = null;
	
	protected Socket serverConnection;
	
	protected Hashtable<String, BooleanHolder> ThreadsToStop;
	
	//protected SSLSocketFactory socketFactory = null;
	
	protected boolean run = true;
	
	private MessageProcessor ref;
	
	//private Observer obs = null;
	
	public String seperateIP = "localhost";
	public int seperatePort = StaticPorts.serverPort;
	public CentralServer(MessageProcessor obs) {
		ThreadsToStop = new Hashtable<String, BooleanHolder>();
	   
	    ref = obs;
	   
	    ss = Connections.getServerSocket(StaticPorts.clientPort);
	}
	class BooleanHolder {
		public boolean value;
	}
	
	public void notifyUsers(Notification notification) {
		
	}
	
	public void sendMessageThreaded(Message msg) {
		
		new Thread(new MessageSender(msg)).start();
		
	}
	
	public boolean sendMessage(Message msg) {
		if (serverConnection == null) {
			return false;
		}
		try {
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
			if (msg.type != null) System.out.println("Sending message: " + msg);
			
			Connections.send(serverConnection,msg);
			return true;
			//Connections.closeSocket(sock);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				serverConnection.close();
			} catch (IOException e1) {
				
			}
			serverConnection = null;
			return false;
		}
		
	}
	
	
	@Override
	public void run() {
		
		try {
			
			//can be null
			setServerConnection(Connections.getSocket(seperateIP, StaticPorts.serverPort, 5000), seperateIP);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void set_run(boolean b) {
		this.run = b;
	}
	
	public void setServerConnection(Socket sock, String ip) {
		if (sock != null) {
			serverConnection = sock;
			new Thread(new SocketListener(serverConnection, true, null, ip)).start();
		}
	}
	
	
	public void Kill() {
		//Kills current server socket
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* Purpose: To tell a sensor to start streaming directly to you
	 * startOrStopStreaming: true to start streaming, false to stop streaming
	 * ip: ip of the sensor you want to connect to
	 * serverConnection: if it is a server you are connecting to or not
	 * ref: (can be null) if you are creating a streambox, it will call the Close() function to stop the box
	 */
	
	public void SetStreaming(boolean startOrStopStreaming, String ip, boolean serverConnection, StreamBox ref) {
		if (startOrStopStreaming) {
			BooleanHolder b = new BooleanHolder();
			b.value = true;
			ThreadsToStop.put(ip, b);
			Socket sensorStreaming = Connections.getSocket(ip, StaticPorts.piPort);
			new Thread(new SocketListener(sensorStreaming, serverConnection, ref, ip)).start();
		} else {
			BooleanHolder b = new BooleanHolder();
			b.value = false;
			ThreadsToStop.put(ip, b);
		}
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
	class SocketListener implements Runnable {
		protected Socket sock;
		protected StreamBox ref;
		protected boolean serverConnected; //if this is connected to a server (true) or to a sensor (false)
		protected String ip;
		
		
		public SocketListener(Socket ss, boolean serverConnected, StreamBox ref, String ip) {
			sock = ss;
			this.ref = ref;
			this.serverConnected = serverConnected;
			this.ip = ip;
		}
		
		public void run() {
			boolean run = true;
			while(run) {
				Message m = null;
				try {
					m = Connections.readObject(sock);
				} catch (Exception e) {}
				
				if (m == null || ThreadsToStop.get(ip).value) { //if the message is still null, if there is an exception, or if we force stop
					run = false;
					if (ref != null) { //close any streamboxes
						ref.Close();
						
					} 
					if (serverConnected) {
						// it has to be a server connection
						serverConnection = null;
					}
				}
			}	
		}
	}
}
