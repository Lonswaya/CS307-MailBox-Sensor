
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

	//private Socket sock;
	private ObjectOutputStream out = null;										//same
	private ObjectInputStream in = null;											//same
	
	/*
	 * Generates a new object to handle individual requests from clients
	 * Parameters:
	 * 				sock: a socket to handle the request from
	 */
	public ServerListener(ObjectInputStream in, ObjectOutputStream out) {
		/*if (sock == null || sock.isClosed() || !sock.isConnected()) {
			System.err.println("shit's fucked" + sock);
			System.exit(69696969);
		}*/
		try {
			this.out = out;
			this.in = in;
			//out = new ObjectOutputStream(sock.getOutputStream());					//initialize the input and output streams
			//in = new ObjectInputStream(sock.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Receives a message from the socket
	 * Returns: 
	 * 			Message if successful
	 * 			Null if unsuccessful
	 */
	/*public synchronized Message receiveMessage() {
		try {
			return (Message)in.readObject();				//try to read the message
		} catch (ClassNotFoundException e) {				//if the class wasn't found something is seriously wrong
			e.printStackTrace();
			System.exit(1);									//so exit
		} catch (IOException e) {							//IOExceptions aren't as big of a deal so don't exit
			e.printStackTrace();
		}
		return null;										//return null if there was an IOException
	}
	
	/*
	 * Purpose:		Sends message over socket
	 * Parameters:	message to send
	 * Returns:		true if the message was successfully sent
	 * 				false if the message was unsuccessfully sent
	 
	public boolean sendMessage(Message msg) {
		try {
			out.writeObject(msg);							//write the message over the socket
			out.flush();									//flush to ensure it is sent in its entirity
			return true;									//return true since the message was sent
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;										//failed sending so return false
	}*/
	
	/*
	 * Purpose: 	Closes all sockets/socket related things
	 */
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
	private void addUI(String from) {
		
		if(from == null || from.equals("") || SeparateServer.ui_ips.contains(from)) {
			return;
		} else {
			System.out.println("Added user " + from + " to list");
			SeparateServer.ui_ips.add(from);
		}
	}
	
	
	// TODO: documentation
	
	private void notify_uis(ClientConfig config) {
		/*
		for (String ip : SeparateServer.ui_ips) {
			System.out.println("sending config to user " + ip);
			ConfigMessage msg = new ConfigMessage(ip, config);
			new Thread(new ClientNotifier(msg, ip, MessageType.READING)).start();
		} we are just going to have the users individually request for the sensors, manually*/
	}
	
	
	@Override
	public void run() {

		HashMap<String, Timer> timers = new HashMap<String, Timer>();
	
		
		Message msg = Connections.readObject(in, 5000);									//receive message from socket
		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return;
		}
		
		if (msg.type != null) System.out.println("New message recieved:+\n" + msg);
		
		if(msg.type == MessageType.CONFIG || 
				msg.type == MessageType.STREAMING || 
				msg.type == MessageType.GET_SENSORS ||
				msg.type == MessageType.ADD_SENSOR) 
			addUI(msg.from);													//add ip to list of gui ips
		
		if (msg.getString().equalsIgnoreCase("quit")) {							//if the message told the server to quit
			SeparateServer.run = false;											//make the server quit running
			return;
		}
		boolean closing = true;
		
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
					//TODO multiple sensors
					
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
					
					if (!SeparateServer.sendingList.get(msg.from).streaming) {
						//if it is not streaming and still got one,
						//send one to every client, we reached a threshold
						for (String client : SeparateServer.ui_ips) {
							System.out.println("Sending message to client " + client + " , reading message");
							
							new Thread(new ClientNotifier(msg, client, MessageType.READING)).start();
						}
					}
					//also send it to everyone who is streaming
					for (String client : SeparateServer.sendingList.get(msg.from).readingList) {
						System.out.println("Sending message to client " + client + " , reading message");
						new Thread(new ClientNotifier(msg, client, MessageType.STREAMING)).start();
						

					}
					//Go through this sensor's list for reading clients to send to, send each 
					break;
				case AUDIO:
					for (String client : SeparateServer.sendingList.get(msg.from).audioList) {
						new Thread(new ClientNotifier(msg, client, MessageType.AUDIO)).start();				
					}
					//Go through this sensor's list for audio clients to send to, send each 

					break;
				case PICTURE:
					for (String client : SeparateServer.sendingList.get(msg.from).pictureList) {
						System.out.println("SENDING PICTURE MESSAGE TO:  " + client);
						new Thread(new ClientNotifier(msg, client, MessageType.PICTURE)).start();				
					}
					//Go through this sensor's list for picture clients to send to, send each 

					break;
				case CONFIG:
					// TODO: update database?
					ConfigMessage cm3 = (ConfigMessage)msg;
					//System.out.println(cm3);
					//notify_uis(cm3.config);
					if (cm3.delete) {
						if (SeparateServer.sendingList.get(msg.from) != null) {
							//this is from a sensor, we sure done fucked something up
							//TODO send error message to clients
						}
						
						//remove sensor
						System.out.println("removing sensor from sendinglist");
						SeparateServer.sendingList.remove(cm3.config.ip); //TODO remove once database established
						
						
						
					} else {
						SeparateServer.sendMessage(msg, msg.config.ip, StaticPorts.piPort, true);
					}
					
					break;
					
					//send to sensor
				case STREAMING:
					//System.out.println(msg);
					StreamingMessage smsg = (StreamingMessage)msg;
					SeparateServer.sendingList.get(msg.config.ip).streaming = smsg.streaming;
					switch (msg.config.sensor_type) {
						case LIGHT:
							// add or remove to value streaming list for this sensor
							if (smsg.streaming) {
								SeparateServer.sendingList.get(msg.config.ip).readingList.add(msg.from);
								
							} else {
								if (SeparateServer.sendingList.get(msg.config.ip).readingList.indexOf(msg.from) >= 0) SeparateServer.sendingList.get(msg.config.ip).readingList.remove(msg.from);
							}
	
							//SeparateServer.audioSendingUI.add(msg.from);
							break;
						case AUDIO:
							// add or remove to value streaming list for this sensor
							// add or remove audio streaming list for this sensor
							if (smsg.streaming) {
								SeparateServer.sendingList.get(msg.config.ip).audioList.add(msg.from);
								SeparateServer.sendingList.get(msg.config.ip).readingList.add(msg.from);
							} else {
								if (SeparateServer.sendingList.get(msg.config.ip).audioList.indexOf(msg.from) >= 0) SeparateServer.sendingList.get(msg.config.ip).audioList.remove(msg.from);
								if (SeparateServer.sendingList.get(msg.config.ip).readingList.indexOf(msg.from) >= 0) SeparateServer.sendingList.get(msg.config.ip).readingList.remove(msg.from);
							}
	
							break;
							//SeparateServer.videoSendingUI.add(msg.from);
	
						case VIDEO:
							// add to picture streaming list for this sensor
							if (smsg.streaming) {
								SeparateServer.sendingList.get(msg.config.ip).pictureList.add(msg.from);
							} else {
								if (SeparateServer.sendingList.get(msg.config.ip).pictureList.indexOf(msg.from) >= 0) SeparateServer.sendingList.get(msg.config.ip).pictureList.remove(msg.from);
							}
							//SeparateServer.lightSendingUI.add(msg.from);
	
							break;
						default:
							break;
					
					}
					//send to sensor
					String address1 = "";
					try {
						address1 = InetAddress.getLocalHost().toString();
					} catch (UnknownHostException e) {
						e.printStackTrace();
						return;
					}
					address1 = address1.substring(address1.indexOf('/') + 1);
					msg.setFrom(address1);
					//set the from to this address, so the sensor knows to reply to the server
					SeparateServer.sendMessage(msg, msg.config.ip, StaticPorts.piPort, false);
					
					break;
				case ADD_SENSOR:
					
					
					if (SeparateServer.sendingList.get(msg.config.ip) != null) {// TODO: Add database support
						
						SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null, null), true);
						return;			
					} 
					
					//add to the streaming list
					
					
					
					//System.out.println(msg);
					
					msg.type = MessageType.CONFIG;
					ConfigMessage cm = (ConfigMessage)msg;
					
					//if proper connection, use the outputstream and send back a new successful message
					boolean success = false;
					if (cm.config.ip.equals("1234")) success = true; //TODO remove debug
					else {
						if (Connections.sendAndCheck(cm.config.ip, StaticPorts.piPort, cm, 1000)) {
							
							success = true;
						}
					}
					if(success) {
						System.out.println("adding new sensor");
						SeparateServer.sendMessage(out,new Message("Connection succeeded", null, null), true); // msg type can be null, no biggie
						SeparateServer.sendingList.put(msg.config.ip, new SensorSendingList(cm.config));
						notify_uis(cm.config); 
					} else {
						SeparateServer.sendMessage(out, new Message("Sensor connection failed", null, null), true);    //
					}
					closing=false;
					break;
				case GET_SENSORS:
					//get all configs from database, store in a regular array for maximum compression at the moment
					ArrayList<ClientConfig> cf = new ArrayList<ClientConfig>();
					//SeparateServer.sendingList.forEach(action);
					for (String s : SeparateServer.sendingList.keySet()) {
						System.out.println("Included in your package is sensor " + s);
						cf.add(SeparateServer.sendingList.get(s).sensorInfo);
					}
					ArrayList<ClientConfig> ar = cf; //TODO remove once database established
					SeparateServer.sendMessage(out, new SensorsMessage("Here's your sensors, yo", ar), true); 
					closing = false;
					break;
				default:
					break;
			}
		
		}
		/*try {
			//Send confirmation that we are done
		//System.out.println("Confirm connection");
			out.writeObject(new Message("Confirmed connection",null,null));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		if (closing) close();		
		 
	}
	class ClientNotifier implements Runnable { 
		Message msg;
		String client;
		MessageType type;
		public ClientNotifier(Message msg, String client, MessageType type) {
			this.msg = msg;
			this.client = client;
			this.type = type;
		}
		
    	public void run() {
    		//System.out.println("sending message " + msg.message + " to client " + client);
    		Socket sock = null;
    		if ((sock = Connections.getSocket(client, StaticPorts.clientPort, 1000)) != null) {
    			if (!Connections.sendAndCheck(sock, msg, 1000)) {
    				
    			} else {
    				System.out.println("Message sent successfully to " + client);
    			}
    		} else {
    		
    			System.err.printf("User %s not found, removing from list\n", client);
				//remove
				switch(type) {
				case READING:
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
			} 
    		
    	

		}
    	
	}
    	
}