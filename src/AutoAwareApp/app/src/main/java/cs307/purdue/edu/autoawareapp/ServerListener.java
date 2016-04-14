package cs307.purdue.edu.autoawareapp;
import java.net.ServerSocket;

import cs307.purdue.edu.autoawareapp.*;
/*
 * Handles individual requests from clients
 * 
 * You will have to create a new server listener and implement HandleMessage
 * 
 * All that has to be provided is the SocketWrapper which should contain a new socket
 */

public abstract class ServerListener implements Runnable {
	
	public SocketWrapper sock;
	
	public boolean run;
	
	public ServerListener(SocketWrapper sock) {
		this.sock = sock;
	}
	
	
	public void HandleMessage(Message msg) throws Exception {
		/* HandleMessage is where you parse your own message received by a connection
		 */
	}

	public void run() {
		run = true;
		while (run) {
			try {
				if (sock == null) {
					System.err.println("ServerListener: Ending connection because the socket was null");
					run = false;
					break;
				}
				if (sock.in == null) continue; //continue because it isnt ready yet
				Message msg = Connections.readObject(sock.in);
				if (msg == null) {
					System.err.println("ServerListener: Ending connection because the message returned null");
					run = false;
				} else
					HandleMessage(msg);
			} catch (Exception e) {
				System.err.println("ServerListener: Connection lost due to exception " + e.toString());
				e.printStackTrace();
				run = false;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

	/*private Socket sock;
	private ObjectOutputStream out = null;										//same
	private ObjectInputStream in = null;											//same
	
	/*
	 * Generates a new object to handle individual requests from clients
	 * Parameters:
	 * 				sock: a socket to handle the request from
	 */
	/*public ServerListener(Socket sock) {
		
		try {
			this.sock = sock;
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ServerListener(Socket sock, ObjectInputStream in, ObjectOutputStream out) {
		try {
			this.sock = sock;
			if (in != null) this.in = in;
			else in = new ObjectInputStream(sock.getInputStream());
			if (out != null) this.out = out;
			else out = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
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
	/*private void addUI(String from, Socket socker) {
		
		if(from == null || from.equals("")) {
			return;
		} else {
			if (SeparateServer.uiSockets.containsKey(from)) {
				try {
					SocketInfo socks = SeparateServer.uiSockets.get(from);
					if (socks.out != null) socks.out.close();
					if (socks.in != null) socks.in.close();
					if (socks.sock != null) socks.sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Added user " + from + " to list with socket " + socker);
			SeparateServer.uiSockets.put(from, new SocketInfo(socker, in, out));
		}
	}
	
	
	private void notify_uis() {
		
		for (String ip : SeparateServer.uiSockets.keySet()) {
			System.out.println("sending config to user " + ip);
			ArrayList<ClientConfig> cf = new ArrayList<ClientConfig>();
			//SeparateServer.sendingList.forEach(action);
			for (String s : SeparateServer.sendingList.keySet()) {
				System.out.println("Included in your package is sensor " + s);
				cf.add(SeparateServer.sendingList.get(s).sensorInfo);
			}
			ArrayList<ClientConfig> ar = cf; 
			new Thread(new ClientNotifier(new SensorsMessage("here's your sensors, yo", ar), SeparateServer.uiSockets.get(ip).getOut(), ip)).start();;
			//new Thread(new ClientNotifier(new SensorsMessage("Here's your sensors, yo", ar), ip, MessageType.GET_SENSORS)); 
			
		}
	}
	
	
	@Override
	public void run() {

		
		boolean run = true;
		while (run) {
		
			try {
				//System.out.println("Next connection from socket " + sock);
				boolean canContinue = processMessage();
				if (!canContinue) break;
			} catch (Exception e) {
				e.printStackTrace();
				run = false;
			}
		}
		close();
		  
	}
	boolean processMessage() { //this is what processes things
		HashMap<String, Boolean> tasks = new HashMap<String, Boolean>();
		/*boolean avaliable = false;
		try {
			avaliable = in.available() > 0; //if we are able to read 
			if (in.available() != 0) System.out.println(in.available());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (!avaliable) {
			return true; //wait and continue looping until we can read some more bytes
		}*/
		/*Message msg = null;
		try {
			msg = (Message)in.readObject();
		} catch (Exception e1) {
			//e1.printStackTrace();
			try {
				in.reset();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			return true; 
			//could not read and stuff, keep looping
		} 							//receive message from socket
			
		System.out.println("Got read object of something");

		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return false;
		}
		
		if(msg.type == MessageType.CONFIG || 
				msg.type == MessageType.STREAMING || 
				msg.type == MessageType.GET_SENSORS ||
				msg.type == MessageType.ADD_SENSOR) 
			addUI(msg.from, sock);

		if (msg.type != null) System.out.println("New message recieved:+\n" + msg);
		
															//add ip to list of gui ips
		
		if (msg.getString().equalsIgnoreCase("quit")) {							//if the message told the server to quit
			return false;
		}
		//boolean closing = true;
		
		if(msg.type == null) {
			//System.out.println(msg);
		} else {
			float reading = 0;
			//parse the type of message
			switch(msg.type) {
				case VIDEO:
					//unused
					break;
				case LIGHT:
					//unused
					break;
				case AUDIO:
					
					reading = ((AudioMessage)msg).currentThreshold;
					
					break;
				
				case READING:
					if (reading == 0) {
						ReadingMessage rmsg = (ReadingMessage) msg;
						reading = rmsg.getCurrentThreshold()/100;

					}
					ClientConfig cc = SeparateServer.sendingList.get(msg.from).sensorInfo;
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						tasks.remove(cc.ip);
					}
					
					for (String ip : SeparateServer.uiSockets.keySet()) {
						//notify all clients that the threshold was reached
						new Thread(new ClientNotifier(msg, SeparateServer.uiSockets.get(ip).getOut(), ip)).start();;
						
						
						
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
						SeparateServer.sendMessage(SeparateServer.sendingList.get(cm3.config.ip).obj, msg, true);
					}
					notify_uis();
					break;
					
					//send to sensor
				case STREAMING:
					
					break;
					
				case ADD_SENSOR:
					
					
					if (SeparateServer.sendingList.get(msg.config.ip) != null) {
						
						//SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null, null), true);
						break;			
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
						
						out = Connections.getOutputStream(newSocket, 1000);
						
						SeparateServer.sendMessage(out, cm, true);
						//Create a new thread that will be able to process a request from a sensor
						new Thread(new ServerListener(newSocket, null, out)).start(); 
						SeparateServer.sendingList.put(msg.config.ip, new SensorInfo(cm.config, out));
						notify_uis(); 
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
					System.out.println("Sensors together, sending back");
					ArrayList<ClientConfig> ar = cf;
					SeparateServer.sendMessage(out, new SensorsMessage("Here's your sensors, yo", ar), true); 
					//closing = false;
					break;
				default:
					break;
			}
		
		}
		return true;
		
	}
	class ClientNotifier implements Runnable { 
		Message msg;
		ObjectOutputStream client;
		String ip;
		public ClientNotifier(Message msg, ObjectOutputStream client, String ip) {
			this.msg = msg;
			this.client = client;
			this.ip = ip;
		}
		
    	public void run() {
    		
    			if (!Connections.sendAndCheck(client, msg, 1000)) {
    				switch(msg.type) {
    				case READING:
    				case GET_SENSORS:
    					SeparateServer.uiSockets.remove(ip);
    					break;
    				/*case STREAMING:
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
		}
    	
	}
    	
}*/