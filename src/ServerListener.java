
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

import com.twilio.sdk.TwilioRestException;

/*
 * Handles individual requests from clients
 */

public class ServerListener implements Runnable {

	private Socket sock = null;													//used for sending data
	private ObjectOutputStream out = null;										//same
	private ObjectInputStream in = null;											//same
	
	/*
	 * Generates a new object to handle individual requests from clients
	 * Parameters:
	 * 				sock: a socket to handle the request from
	 */
	public ServerListener(Socket sock) {
		this.sock = sock;															
		try {
			out = new ObjectOutputStream(sock.getOutputStream());					//initialize the input and output streams
			in = new ObjectInputStream(sock.getInputStream());
			if(in == null) {
				System.out.println("Error getting ObjectInputStream");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Receives a message from the socket
	 * Returns: 
	 * 			Message if successful
	 * 			Null if unsuccessful
	 */
	public Message receiveMessage() {
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
	 */
	public boolean sendMessage(Message msg) {
		try {
			out.writeObject(msg);							//write the message over the socket
			out.flush();									//flush to ensure it is sent in its entirity
			return true;									//return true since the message was sent
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;										//failed sending so return false
	}
	
	/*
	 * Purpose: 	Closes all sockets/socket related things
	 */
	private void close()
	{
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Purpose:		adds the ip address that it 
	 */
	private void addUI(String from) {
		
		if(from == null || !from.equals("") || SeparateServer.ui_ips.contains(from)) {
			return;
		} else {
			SeparateServer.ui_ips.add(from);
		}
	}
	
	
	// TODO: documentation
	
	private void notify_uis(ClientConfig config) {
		for (String ip : SeparateServer.ui_ips) {
			ConfigMessage msg = new ConfigMessage(ip, config);
			Socket sock;
			try {
				sock = SeparateServer.socketFactory.createSocket(ip, StaticPorts.clientPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			ObjectOutputStream out;
			try {
				out = (ObjectOutputStream)sock.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			SeparateServer.sendMessage(out, msg, true);
		}
	}
	
	
	@Override
	public void run() {

	
		
		Message msg = receiveMessage();											//receive message from socket
		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return;
		}
		
		System.out.println("New message recieved:+\n" + msg);
		
		if(msg.type == MessageType.CONFIG || 
				msg.type == MessageType.STREAMING || 
				msg.type == MessageType.GET_SENSORS ||
				msg.type == MessageType.ADD_SENSOR) 
			addUI(msg.from);													//add ip to list of gui ips
		
		if (msg.getString().equalsIgnoreCase("quit")) {							//if the message told the server to quit
			SeparateServer.run = false;											//make the server quit running
			return;
		}
		
		
		
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
					//TODO notification parsing for UI
					
					ReadingMessage rmsg = (ReadingMessage) msg;
					float threshold = rmsg.getCurrentThreshold()/100;
					ClientConfig cc = SeparateServer.sendingList.get(msg.from).sensorInfo;
					if (cc.sensing_threshold <= threshold) {
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
					
					if (!SeparateServer.sendingList.get(msg.from).streaming) {
						//if it is not streaming and still got one,
						//send one to every client, we reached a threshold
						for (String client : SeparateServer.ui_ips) {
							System.out.println("Sending message to client " + client + " , reading message");
							Socket sock = SeparateServer.socketFactory.createSocket(client, StaticPorts.clientPort);
							
							ObjectOutputStream out = (ObjectOutputStream)sock.getOutputStream();
							
							if (!SeparateServer.sendMessage(out, msg, true)) {
								//remove
								
								SeparateServer.ui_ips.remove(client);
							}
						}
					}
					//also send it to everyone who is streaming
					for (String client : SeparateServer.sendingList.get(msg.from).readingList) {
						System.out.println("Sending message to client " + client + " , reading message");
						Socket sock = SeparateServer.socketFactory.createSocket(client, StaticPorts.clientPort);
						ObjectOutputStream out = (ObjectOutputStream)sock.getOutputStream();
						if (!SeparateServer.sendMessage(out, msg, false)) {
							//remove
							SeparateServer.sendingList.get(msg.from).readingList.remove(client);
						}

					}
					//Go through this sensor's list for reading clients to send to, send each 
					break;
				case AUDIO:
					for (String client : SeparateServer.sendingList.get(msg.from).audioList) {
						Socket sock = SeparateServer.socketFactory.createSocket(client, StaticPorts.clientPort);
						if (!SeparateServer.sendMessage(out, msg, false)) {
							//remove
							SeparateServer.sendingList.get(msg.from).audioList.remove(client);
						}					
					}
					//Go through this sensor's list for audio clients to send to, send each 

					break;
				case PICTURE:
					for (String client : SeparateServer.sendingList.get(msg.from).pictureList) {
						Socket sock = SeparateServer.socketFactory.createSocket(client, StaticPorts.clientPort);
						if (!SeparateServer.sendMessage(out, msg,false)) {
							//remove
							SeparateServer.sendingList.get(msg.from).pictureList.remove(client);
						}				
					}
					//Go through this sensor's list for picture clients to send to, send each 

					break;
				case CONFIG:
					// TODO: update database?
					ConfigMessage cm3 = (ConfigMessage)msg;
					//System.out.println(cm3);
					notify_uis(cm3.config);
					if (cm3.delete) {
						//remove sensor
						SeparateServer.sendingList.remove(cm3.config.ip); //TODO remove once database established
						
					}
					//send to sensor
					String address = "";
					try {
						address = InetAddress.getLocalHost().toString();
					} catch (UnknownHostException e) {
						e.printStackTrace();
						return;
					}
					address = address.substring(address.indexOf('/') + 1);
					msg.setFrom(address);
					//set the from to this address, so the sensor knows to reply to the server
					Socket sock = SeparateServer.socketFactory.createSocket(msg.config.ip, StaticPorts.piPort);
					ObjectOutputStream out = (ObjectOutputStream)sock.getOutputStream();
					SeparateServer.sendMessage(out, msg, false);
					
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
	
						case PICTURE:
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
					Socket sock = SeparateServer.socketFactory.createSocket(msg.config.ip, StaticPorts.piPort);
					SeparateServer.sendMessage(out, msg, false);
					
					break;
				case ADD_SENSOR:
					
					
					if (SeparateServer.sendingList.get(msg.config.ip) != null) {// TODO: Add database support
						SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null, null), StaticPorts.clientPort);
						return;			
					} 
					
					//add to the streaming list
					
					
					
					//System.out.println(msg);
					
					msg.type = MessageType.CONFIG;
					ConfigMessage cm = (ConfigMessage)msg;
					
					//if proper connection, use the outputstream and send back a new successful message
					
					if(cm.config.ip.equals("1234")/*TODO remove debug*/ || SeparateServer.sendMessage(msg, cm.config.ip, StaticPorts.piPort, true)) {
						System.out.println("adding new sensor");
						SeparateServer.sendMessage(out, new Message("Connection succeeded", null, null), StaticPorts.clientPort); // msg type can be null, no biggie
						SeparateServer.sendingList.put(msg.config.ip, new SensorSendingList(cm.config));
						notify_uis(cm.config); 
					} else {
						SeparateServer.sendMessage(out, new Message("Sensor connection failed", null, null), StaticPorts.clientPort);    //
					}
					break;
				case GET_SENSORS:
					//get all configs from database, store in a regular array for maximum compression at the moment
					ArrayList<ClientConfig> cf = new ArrayList<ClientConfig>();
					//SeparateServer.sendingList.forEach(action);
					
					for (String s : SeparateServer.sendingList.keySet()) {
						cf.add(SeparateServer.sendingList.get(s).sensorInfo);
					}
					ArrayList<ClientConfig> ar = cf; //TODO remove once database established
					SeparateServer.sendMessage(out, new SensorsMessage("Here's your sensors, yo", ar), StaticPorts.clientPort); 
					break;
				default:
					break;
					
			}
		
		}
			close();		
		 
	}
}