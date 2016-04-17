package SeparateServer;
import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;

import com.twilio.sdk.TwilioRestException;

import Utilities.Connections;
import Utilities.SensorInfo;
import Utilities.ServerListener;
import Utilities.SocketWrapper;
import Utilities.StaticPorts;
import cs307.purdue.edu.autoawareapp.*;
public class SeparateServer {
	
	//TODO: polling shit
	
	static Hashtable<String, SocketWrapper> uiList = new Hashtable<String, SocketWrapper>();
	static Hashtable<String, SensorInfo> sensorList = new Hashtable<String, SensorInfo>();
	//SensorInfo contains a SocketWrapper for communicating with that Sensor.
	
	static ServerSocket serverSock = null;
	
	public static void main(String[] args) {
		//new sensor for basic tests
		/*Random r = new Random();
		ClientConfig cfg = new ClientConfig();
		cfg.r = r.nextFloat();
		cfg.g = r.nextFloat();
		cfg.b = r.nextFloat();
		SensorInfo randSensor = new SensorInfo(cfg, null);
		sensorList.put("1234", randSensor);*/
		
		
		serverSock = Connections.getServerSocket(StaticPorts.serverPort);
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Server started");
				while (true) {
					try {
						GetHandler(new SocketWrapper(serverSock.accept())).start();
						System.out.println("New connection");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	//COMMIT ALL OF THESE
	public static void sendAllConfigsToAllUis() {
		ArrayList<ClientConfig> configs = new ArrayList<ClientConfig>();
		for (String ip : sensorList.keySet()) {
			SensorInfo info = sensorList.get(ip);
			//lostConnection is set when the socket is killed in the ServerListener
			if (info.sock.lostConnection) { 
				sensorList.remove(ip);
			} else {
				configs.add(info.sensorInfo);
			}
		}
		for(String ip : uiList.keySet()) {
			sendAllConfigs(ip, configs);
		}
	}
	
	public static void sendAllConfigs(String ip) {
		SocketWrapper ui = uiList.get(ip);
		if (ui != null) {
			//get arraylist of all clientConfigs in SensorList
			ArrayList<ClientConfig> configs = new ArrayList<ClientConfig>();
			for (String key : sensorList.keySet()) {
				SensorInfo sensorInfo = sensorList.get(key);
				//lostConnection is set when the socket is killed in the ServerListener
				if (sensorInfo.sock.lostConnection) { 
					sensorList.remove(ip);
				} else {
					configs.add(sensorInfo.sensorInfo);
				}
			}
			SensorsMessage sm = new SensorsMessage("Here is your configs!", configs);
			Connections.send(ui.out, sm);
			System.out.println("Sent message " + sm.message + " to " + ui.sock);
		}
	}
	
	public static void sendAllConfigs(String ip, ArrayList<ClientConfig> configs) {
		SocketWrapper ui = uiList.get(ip);
		if (ui != null)
			Connections.send(ui.out, new SensorsMessage("Here is your configs!", configs));
	}
	
	public static void sendAllConfigs(SocketWrapper wrapper) {
		ArrayList<ClientConfig> configs = new ArrayList<ClientConfig>();
		for (String key : sensorList.keySet()) {
			SensorInfo info = sensorList.get(key);
			//lostConnection is set when the socket is killed in the ServerListener
			if (info.sock.lostConnection) { 
				sensorList.remove(key);
			} else {
				configs.add(info.sensorInfo);
			}
		}
		Connections.send(wrapper.out, new SensorsMessage("Here is your configs!", configs));
	}
	
	/*
	 * INIT comes from UIs. Resets the SockWrapper related to that UI in uiList HashMap
	 * 
	 * READING comes from Sensor. Notifies the users on the server.
	 * 
	 * CONFIG comes from UIs. Resets the config in the sensorList Hashtable's SensorInfo and re-sends all configs to all UIs
	 * 
	 * ADD_SENSOR comes from UIs. Checks if sensor exists. If it exists, sends the config to it, adds a new entry to the sensorList 
	 * HashMap and re-sends all configs to all UIs
	 * 
	 * GET_SENSORS comes from UIs. It re-sends all configs to all UIs.
	 */
	
	private static Thread GetHandler(final SocketWrapper socket) {
		return new Thread(new ServerListener(socket) {
			public void HandleMessage(Message msg) throws Exception  {
				if (msg == null) {
					throw(new Exception("Message was returned as null"));
				}
				System.out.println("New message: " + msg.message);
				switch (msg.type) {
					case INIT:
						//reset the SocketWrapper related to this UI in HashMap.
						//have UI re-send a GET_SENSORS message if they ever send this
						HandleInitMessage(msg, socket);
						break;
					case READING:
						//Notify users about the sensor going off.
						HandleReadingMessage((ReadingMessage)msg);
						break;
					case CONFIG:
						//Check that the sensor exists. If it does, reset the config in sensorList Hashtable, else remove it from Hashtable.
						//then re-send all configs to all UIs
						HandleConfigMessage((ConfigMessage)msg);
						break;
					case GET_SENSORS:
						//just send them all of the sensors configs in an arraylist.
						HandleGetSensors(msg, socket);
						break;
					default:
						break;
				}
			}
		});
	}
	public static void HandleReadingMessage(ReadingMessage msg) {
		HashMap<String, Boolean> tasks = new HashMap<String, Boolean>();
		
		ReadingMessage rmsg = (ReadingMessage) msg;
		//float reading = rmsg.getCurrentThreshold()/100;
		
		ClientConfig cc = SeparateServer.sensorList.get(msg.from).sensorInfo;
		//if threshold is exceeded and there is no notification yet, start one, else do nothing
		System.out.println(cc.emailNotification + " " + cc.textNotification);
		if (tasks.get(cc.ip) == null && cc.isSensorActive()) {
			tasks.put(cc.ip, true);
			if (cc.emailNotification == true) {
				try {
					Sender.send(cc.emailAddress, msg.getString());
				} catch (IOException e){
					e.printStackTrace();
					//handle the exception
				}
			}
			if (cc.textNotification == true) {
				try {
					System.out.println("sending text to " + cc.phoneNumber);
					TwilioSender.send(cc.phoneNumber, msg.getString());
				} catch (TwilioRestException tre) {
					tre.printStackTrace();
				//handle it
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(cc.interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tasks.remove(cc.ip);
		}
		System.out.println("Notifying client");
		for (String key: uiList.keySet()) {
			NotifyClient(msg,uiList.get(key));
		}
	}
	
	//wrote this part COMMIT
	public static void HandleConfigMessage(ConfigMessage msg) {		
		if (msg.delete) {
			System.out.println("Deleting sensor " + msg.config.name);
			SensorInfo info = sensorList.get(msg.config.ip);
			if (info != null)
				sensorList.remove(msg.config.ip);	
			Connections.closeSocket(info.sock.sock);
			SeparateServer.sendAllConfigsToAllUis();
			System.out.println("Updated all users with the lack of sensor");
			return;
		}
		
		boolean exists = true;
		boolean inList = false;
		SocketWrapper sock = null;
		//checking existance
		SensorInfo info = sensorList.get(msg.config.ip);
		if (info == null) {
			try {
				Socket newSocket = Connections.getSocket(msg.config.ip, StaticPorts.piPort);
				if (newSocket == null) {
					System.out.println("Socket connection was null for a new sensor");
					exists = false;
				} else {
					sock = new SocketWrapper(newSocket);
				}
			} catch (Exception e) {
				exists = false; //not in sensorList and couldn't connect to it.
				try {
					sock.sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			//has info
			inList = true;
			Message to_check = new Message("testing connection", null, null);
			try {
				Connections.send(info.sock.out, to_check);
			} catch(Exception e) {
				exists = false; //was in sensorList and couldn't connect to its socket
				try {
					info.sock.sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}				
		//if it does exists and is already in the list, update the SensorInfo's config in HashTable.
		//if it does exist and is not in the list, new SensorInfo using sock
		
		msg.SetFromThis(); //sets from to be from this server
		if (exists && !inList) {
			info = new SensorInfo(msg.config, sock);
			sensorList.put(msg.config.ip, info);
			GetHandler(sock).start();
			Connections.send(sock.out, msg);
			System.out.println("Sending a new config message to a new sensor");
		} else if (exists && inList) {
			info.sensorInfo = msg.config;
			sensorList.put(msg.config.ip, info);
			System.out.println("Sending a new config message to an existing sensor");
			Connections.send(info.sock.out, msg);
		} else {
			System.out.println("Nothing happened");
		}
		
		SeparateServer.sendAllConfigsToAllUis();
	}
	
	//wrote this part COMMIT
	public static void HandleInitMessage(Message msg, SocketWrapper sock) {
		uiList.put(msg.from, sock);
	}
	
	public static void HandleGetSensors(Message msg, SocketWrapper sock) {
		sendAllConfigs(sock);
	}
	
	public static void NotifyClient(Message msg, SocketWrapper sock) {
		Connections.send(sock.out, msg);
	}
}
		



	/*protected ServerSocket ss = null; 			     //SSL Server socket for accepting connections
	
	
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
				// 
				e.printStackTrace();
			} //get the local ip address
			myAddress = myAddress.substring(myAddress.indexOf('/') + 1);  //strip off the unnecessary bits
			msg.setFrom(myAddress);	
		}
		Socket sock = Connections.getSocket(address, port);
		Connections.send(sock, msg);
		
	}
	
	
	static synchronized void sendMessage(Socket sock, Message msg, boolean setFrom) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// 
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
				// 				e.printStackTrace();
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
	 *
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

	
}*/
