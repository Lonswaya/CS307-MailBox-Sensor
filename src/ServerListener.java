
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
		
		if(from == null || from != "" || SeparateServer.ui_ips.contains(from)) {
			return;
		} else {
			SeparateServer.ui_ips.add(from);
		}
	}
	
	
	// TODO: documentation
	
	private void notify_uis(ClientConfig config) {
		for (String ip : SeparateServer.ui_ips) {
			ConfigMessage msg = new ConfigMessage(ip, config);
			SeparateServer.sendMessage(msg, ip, StaticPorts.clientPort);
		}
	}
	
	
	@Override
	public void run() {

	
		
		Message msg = receiveMessage();											//receive message from socket
		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return;
		}
		
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
			System.out.println(msg);
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
					//TODO notification parsing
					//forward to client
				case AUDIO:
					//forward to client
				case PICTURE:
					//forward to client
					//as of now, we just forward every single message to the client. May cause desync issues otherwise.
					for (String s : SeparateServer.ui_ips) {
						try {
							//TODO assume this is threaded
							SeparateServer.sendMessage(msg, s, StaticPorts.clientPort);
						} catch (Exception e) {
							System.out.println("Unable to send to user with ip " + s);
							SeparateServer.ui_ips.remove(s);
						}
					}
					
					break;
				case CONFIG:
					// TODO: update database?
					ConfigMessage cm3 = (ConfigMessage)msg;
					System.out.println(cm3);
					notify_uis(cm3.config);
					//send to sensor
				case STREAMING:
					//send to sensor
					String address = "";
					try {
						address = InetAddress.getLocalHost().toString();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					address = address.substring(address.indexOf('/') + 1);
					msg.setFrom(address);
					//set the from to this address, so the sensor knows to reply to the server
					SeparateServer.sendMessage(msg, msg.config.ip, StaticPorts.piPort);
					break;
				case ADD_SENSOR:
					
					//if (msg.from already in database) {// TODO: Add database support
					//	return;			
					//} else {
					//	SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null, null), msg.from, StaticPorts.clientPort);
					//	return;
					//}
					
					System.out.println(msg);
					
					msg.type = MessageType.CONFIG;
					ConfigMessage cm = (ConfigMessage)msg;
					
					//if proper connection, use the outputstream and send back a new successful message
					
					if(SeparateServer.sendMessage(msg, cm.config.ip, StaticPorts.piPort)) {
						SeparateServer.sendMessage(out, new Message("Connection succeeded", null, null), msg.from, StaticPorts.clientPort); // msg type can be null, no biggie
						notify_uis(cm.config);
					} else {
						SeparateServer.sendMessage(out, new Message("Sensor connection failed", null, null), msg.from, StaticPorts.clientPort);    //
					}
					break;
				case GET_SENSORS:
					//TODO: get all configs from database, store in a regular array for maximum compression at the moment
					ClientConfig[] ar = null;
					SeparateServer.sendMessage(out, new SensorsMessage("Here's your sensors, yo", ar), msg.from, StaticPorts.clientPort); 
					break;
				default:
					break;
					
			}
		
		}
			close();		
		 
	}
}