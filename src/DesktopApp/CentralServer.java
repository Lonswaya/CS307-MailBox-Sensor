package DesktopApp;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Scanner;
import java.util.Observer;

public class CentralServer extends Observable implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	/*protected Socket serverConnection;
	protected ObjectOutputStream objConnection;
	
	protected Hashtable<String, BooleanHolder> ThreadsToStop;
	
	
	protected boolean run = true;
	
	private MessageProcessor ref;
	
	//private Observer obs = null;
	
	public String seperateIP = "localhost";
	public int seperatePort = StaticPorts.serverPort;
	public CentralServer(MessageProcessor obs) {
		ThreadsToStop = new Hashtable<String, BooleanHolder>();
	   
	    ref = obs;
	   
	    //ss = Connections.getServerSocket(StaticPorts.clientPort);
	}
	class BooleanHolder {
		public boolean value;
	}
	
	public void notifyUsers(Notification notification) {
		
	}
	
	public void sendMessageThreaded(Message msg) {
		
		new Thread(new MessageSender(msg)).start();
		
	}
	public ArrayList<ClientConfig> GetSensors() {
		ArrayList<ClientConfig> ar = null;
		try {

		} catch (Exception e) {
			
		}
		
		
		return ar;
	}
	
	
	public boolean sendMessage(Message msg) {
		System.out.println("going to send");
		if (serverConnection == null) {
			System.out.println("server not found");
			return false;
		}
		try {
			String address = InetAddress.getLocalHost().toString();
			address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
			if (msg.type != null) System.out.println("Sending message: " + msg);
			
			Connections.send(objConnection,msg);
			System.out.println("sent a new message");
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
			
			//can come back and be null
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
			try {
				objConnection = new ObjectOutputStream(sock.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			BooleanHolder b = new BooleanHolder();
			b.value = false;
			ThreadsToStop.put(ip, b);
			new Thread(new SocketListener(serverConnection, true, null, ip)).start();
		}
	}

	
	public void SetStreaming(boolean startOrStopStreaming, ClientConfig cfg, StreamBox sref) {
		if (startOrStopStreaming) {
			BooleanHolder b = new BooleanHolder();
			b.value = false;
			ThreadsToStop.put(cfg.ip, b);
			//sendMessage(new StreamingMessage("Telling to start streaming", cfg, true));
			Socket sensorStreaming = Connections.getSocket(cfg.ip, StaticPorts.piPort, 5000);
			new Thread(new SocketListener(sensorStreaming, false, sref, cfg.ip)).start();
		} else {
			BooleanHolder b = new BooleanHolder();
			b.value = false;
			ThreadsToStop.put(cfg.ip, b);
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
		protected ObjectOutputStream out;
		protected ObjectInputStream in;
		protected StreamBox rref;
		protected boolean serverConnected; //if this is connected to a server (true) or to a sensor (false)
		protected String ip;
		
		
		public SocketListener(Socket ss, boolean serverConnected, StreamBox rref, String ip) {
			sock = ss;
			try {
				this.in = new ObjectInputStream(ss.getInputStream());
				//this.out = new ObjectOutputStream(ss.getOutputStream());
			} catch (Exception e) {
				System.err.println("Uh oh many errors");
			}
			this.rref = rref;
			this.serverConnected = serverConnected;
			this.ip = ip;
		}
		
		public void run() {
			boolean run = true;
			while(run) {
				Message m = null;
				try {
					m = Connections.readObject(in);
					if (m != null) {
						ref.ProcessMessage(m);
					}
				} catch (Exception e) { e.printStackTrace();}
					
				if (m == null || ThreadsToStop.get(ip).value) { //if the message is still null, if there is an exception, or if we force stop
					run = false;
					if (rref != null) { //close any streamboxes
						rref.Close();
					} 
					if (serverConnected) {
						// it has to be a server connection
						serverConnection = null;
					}
					try {
						sock.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
		}
	}*/
}
