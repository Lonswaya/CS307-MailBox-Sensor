import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import com.twilio.sdk.TwilioRestException;

import Example.Connections;

public class SeparateServer {
	static Hashtable<String, SocketWrapper> uiList = new Hashtable<String, SocketWrapper>();
	static Hashtable<String, SensorInfo> sensorList = new Hashtable<String, SensorInfo>();
	
	
	public static void main(String[] args) {
		ServerSocket serverSock = Connections.getServerSocket(StaticPorts.serverPort);
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						GetHandler(new SocketWrapper(serverSock.accept())).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}).start();
		
		
		
	}
	private static Thread GetHandler(SocketWrapper socket) {
		return new Thread(new ServerListener(socket) {
			public void HandleMessage(Message msg) throws Exception  {
				if (msg == null) {
					throw(new Exception("Message was returned as null"));
				}
				
				switch (msg.type) {
					case INIT:
						HandleInitMessage(msg);
						break;
					case READING:
						HandleReadingMessage((ReadingMessage)msg);
						break;
					case CONFIG:
						HandleConfigMessage((ConfigMessage)msg);
						break;
					case ADD_SENSOR:
						HandleAddSensor((ConfigMessage)msg);
						break;
					case GET_SENSORS:
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
		float reading = rmsg.getCurrentThreshold()/100;
		
		ClientConfig cc = SeparateServer.sensorList.get(msg.from).sensorInfo;
		//if threshold is exceeded and there is no notification yet, start one, else do nothing
		if (cc.sensing_threshold >= reading && tasks.get(cc.ip) == null) {
			tasks.put(cc.ip, true);
			if (cc.emailNotification == true) {
				try {
					Sender.send(cc.emailAddress, msg.getString());
				} catch (IOException e){
				//handle the exception
				}
			}
			if (cc.textNotification == true) {
				try {
					TwilioSender.send(cc.phoneNumber, msg.getString());
				} catch (TwilioRestException tre) {
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
		for (String key: uiList.keySet()) {
			NotifyClient(msg,uiList.get(key));
		}
	}
	public static void HandleConfigMessage(ConfigMessage msg) {
		//TODO check if sensor exists within the system
		//TODO send config to sensor
		//TODO update sensor list
		//TODO notify clients with an entire arraylist
	}
	public static void HandleInitMessage(Message msg) {
		//TODO add new client to uiList
	}
	public static void HandleAddSensor(ConfigMessage msg) {
		HandleConfigMessage(msg);
		//TODO return to client 
		
		//Get ip of sensor from msg
		//check if sensor is in list
		//check if can connect to sensor
		//if yes, handle it if no, remove it from list
	}
	public static void NotifyClient(Message msg, SocketWrapper sock) {
		Connections.send(sock.out, msg);
	}
	public static void HandleGetSensors(Message msg, SocketWrapper sock) {
		//TODO respond to the socket marked Sock with the arraylist of sensors
		//
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
