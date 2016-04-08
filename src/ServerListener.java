
import java.applet.AudioClip;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.HashMap;

import com.twilio.sdk.TwilioRestException;

import Example.Connections;

/*
 * Handles individual requests from clients
 */

public class ServerListener implements Runnable {

	private Socket sock;
	private ObjectOutputStream out = null;										//same
	private ObjectInputStream in = null;											//same
	
	/*
	 * Generates a new object to handle individual requests from clients
	 * Parameters:
	 * 				sock: a socket to handle the request from
	 */
	public ServerListener(Socket sock) {
		
		try {
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void close()
	{
		try {
			in.close();
			out.close();
			//sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Purpose:		adds the ip address that it 
	 */
	private void addUI(String from, Socket sock) {
		
		if(from == null || from.equals("") || SeparateServer.uiSockets.containsKey(from)) {
			return;
		} else {
			System.out.println("Added user " + from + " to list");
			SeparateServer.uiSockets.put(from, sock);
		}
	}
	
	
	private void notify_uis(ClientConfig config) {
		
		for (String ip : SeparateServer.uiSockets.keySet()) {
			System.out.println("sending config to user " + ip);
			ArrayList<ClientConfig> cf = new ArrayList<ClientConfig>();
			//SeparateServer.sendingList.forEach(action);
			for (String s : SeparateServer.sendingList.keySet()) {
				System.out.println("Included in your package is sensor " + s);
				cf.add(SeparateServer.sendingList.get(s).sensorInfo);
			}
			ArrayList<ClientConfig> ar = cf; 
			new Thread(new ClientNotifier(new SensorsMessage("here's your sensors, yo", ar), SeparateServer.uiSockets.get(ip), ip, MessageType.GET_SENSORS)).start();;
			//new Thread(new ClientNotifier(new SensorsMessage("Here's your sensors, yo", ar), ip, MessageType.GET_SENSORS)); 
			
		}
	}
	
	
	@Override
	public void run() {

		
		boolean run = true;
		
		while (run) {
		
			try {
				processMessage();
			} catch (Exception e) {
				e.printStackTrace();
				run = false;
			}
		}
		 
	}
	void processMessage() { //this is what processes things
		HashMap<String, Timer> timers = new HashMap<String, Timer>();

		
		Message msg = Connections.<Message>readObject(in);									//receive message from socket
		
		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return;
		}
		
		if(msg.type == MessageType.CONFIG || 
				msg.type == MessageType.STREAMING || 
				msg.type == MessageType.GET_SENSORS ||
				msg.type == MessageType.ADD_SENSOR) 
			addUI(msg.from, sock);
		
		if (msg.type != null) System.out.println("New message recieved:+\n" + msg);
		
															//add ip to list of gui ips
		
		if (msg.getString().equalsIgnoreCase("quit")) {							//if the message told the server to quit
			SeparateServer.run = false;											//make the server quit running
			return;
		}
		//boolean closing = true;
		
		if(msg.type == null) {
			//System.out.println(msg);
		} else {
			
			//parse the type of message
			switch(msg.type) {
				case VIDEO:
					//unused
					break;
				case LIGHT:
					//unused
					break;
				
				case READING:
					
					ReadingMessage rmsg = (ReadingMessage) msg;
					float threshold = rmsg.getCurrentThreshold()/100;
					ClientConfig cc = SeparateServer.sendingList.get(msg.from).sensorInfo;
					//if threshold is exceeded and there is no timertask yet, start one
					if (cc.sensing_threshold >= threshold && timers.get(cc.ip) == null) {
						Timer t = new Timer(cc.ip);
							TimerTask tt = new TimerTask() {
								@Override
								public void run() {
									if (cc.emailNotification == true) {
										try {
											Sender.send(cc.emailAddress, rmsg.getString());
										} catch (IOException e){
										//handle the exception
										}
									}
									if (cc.textNotification == true) {
										try {
											TwilioSender.send(cc.phoneNumber, rmsg.getString());
										} catch (TwilioRestException tre) {
										//handle it
										}
									}
								}
							};
					
						t.scheduleAtFixedRate(tt, new Date(System.currentTimeMillis() + 1000), cc.interval); //wait an extra second so the first one happens
						timers.put(cc.ip, t);
					}
					//else if threshold is not exceeded but a timertask is running, stop it and remove it
					else if (cc.sensing_threshold < threshold && timers.get(cc.ip) != null) {
						Timer t = timers.get(cc.ip);
						t.cancel();
						timers.remove(cc.ip);
					}
					
					break;
				case CONFIG:
					ConfigMessage cm3 = (ConfigMessage)msg;
					//System.out.println(cm3);
					//notify_uis(cm3.config);
					if (cm3.delete) {
						if (SeparateServer.sendingList.get(msg.from) != null) {
							//this is from a sensor, we sure done fucked something up
							
						}
						
						//remove sensor
						System.out.println("removing sensor from sendinglist");
						SeparateServer.sendingList.remove(cm3.config.ip); 
						
						
						
					} else {
						SeparateServer.sendMessage(SeparateServer.sendingList.get(cm3.config.ip).socket, msg, true);
					}
					
					break;
					
					//send to sensor
				case STREAMING:
					
					break;
					
				case ADD_SENSOR:
					
					
					if (SeparateServer.sendingList.get(msg.config.ip) != null) {// 
						
						SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null, null), true);
						return;			
					} 
					
					
					msg.type = MessageType.CONFIG;
					ConfigMessage cm = (ConfigMessage)msg;
					
					//if proper connection, use the outputstream and send back a new successful message
					boolean success = false;
					Socket newSocket = null;
					if (cm.config.ip.equals("1234")) success = true; 
					else {
						
						newSocket = Connections.getSocket(cm.config.ip, StaticPorts.piPort, 1000);
						success = (newSocket != null);
					}
					if(success) { //I create success to allow a fake config 1234 in  
						System.out.println("adding new sensor");
						SeparateServer.sendMessage(newSocket, cm, true);
						//Create a new thread that will be able to process a request from a sensor
						new Thread(new ServerListener(newSocket)).start(); 
						SeparateServer.sendingList.put(msg.config.ip, new SensorSendingList(cm.config, newSocket));
						notify_uis(cm.config); 
					} else {
						//sensor connection failed
						//SeparateServer.sendMessage(out, new Message("Sensor connection failed", null, null), true);    //
					}
					//closing=false;
					break;
				case GET_SENSORS:
					//get all configs from database, store in a regular array for maximum compression at the moment
					ArrayList<ClientConfig> cf = new ArrayList<ClientConfig>();

					for (String s : SeparateServer.sendingList.keySet()) {
						System.out.println("Included in your package is sensor " + s);
						cf.add(SeparateServer.sendingList.get(s).sensorInfo);
					}
					ArrayList<ClientConfig> ar = cf;
					SeparateServer.sendMessage(sock, new SensorsMessage("Here's your sensors, yo", ar), true); 
					//closing = false;
					break;
				default:
					break;
			}
		
		}
	}
	class ClientNotifier implements Runnable { 
		Message msg;
		Socket client;
		MessageType type;
		String ip;
		public ClientNotifier(Message msg, Socket client, String ip, MessageType type) {
			this.msg = msg;
			this.client = client;
			this.type = type;
			this.ip = ip;
		}
		
    	public void run() {
    		
    			if (!Connections.sendAndCheck(sock, msg, 1000)) {
    				switch(type) {
    				case READING:
    				case GET_SENSORS:
    					SeparateServer.uiSockets.remove(ip);
    					break;
    				case STREAMING:
    					SeparateServer.sendingList.get(msg.from).readingList.remove(client);
    					break;
    				case PICTURE:
    					SeparateServer.sendingList.get(msg.from).pictureList.remove(client);
    					break;
    				case AUDIO:
    					SeparateServer.sendingList.get(msg.from).audioList.remove(client);
    					break;
    				default:
    					break;
    				
    				}
    			} else {
    				System.out.println("Message sent successfully to " + client);
    			}
    		/*} else {
    		
    			System.err.printf("User %s not found, removing from list\n", client);
				//remove
				switch(type) {
				case READING:
				case GET_SENSORS:
					SeparateServer.ui_ips.remove(client);
					break;
				case STREAMING:
					SeparateServer.sendingList.get(msg.from).readingList.remove(client);
					break;
				case PICTURE:
					SeparateServer.sendingList.get(msg.from).pictureList.remove(client);
					break;
				case AUDIO:
					SeparateServer.sendingList.get(msg.from).audioList.remove(client);
					break;
				default:
					break;
				
				}
				SeparateServer.ui_ips.remove(client);
			} */
    		
    	

		}
    	
	}
    	
}