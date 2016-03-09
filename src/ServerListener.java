package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Handles individual requests from clients
 */

public class ServerListener implements Runnable {

	protected Socket sock = null;													//used for sending data
	protected ObjectOutputStream out = null;										//same
	protected ObjectInputStream in = null;											//same
	
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
			SeparateServer.sendMessage(msg, ip, 9999);
		}
	}
	
	
	@Override
	public void run() {

		Message msg = receiveMessage();											//receive message from socket
		if (msg == null) {														//if there was an error receiving the message
			System.err.println("Error, failed receiving message");
			return;
		}
		
		if(msg.type == MessageType.CONFIG || msg.type == MessageType.STREAMING) // TODO: add other sensor type shit
			addUI(msg.from);													//add ip to list of gui ips
		
		if (msg.getString().equalsIgnoreCase("quit")) {							//if the message told the server to quit
			SeparateServer.run = false;											//make the server quit running
			return;
		}
		
		//parse the type of message
		switch(msg.type) {
			
			case VIDEO:
				break;
			case AUDIO:
				break;
			case LIGHT:
				break;
			case ADD_SENSOR:
				
				if (msg.from already in database) {// TODO: Add database support
					return;			
				} else {
					SeparateServer.sendMessage(out, new Message("Adding sensor already in db",  null), msg.from, 9999);
					return;
				}
				
				msg.type = MessageType.CONFIG;
				ConfigMessage cm = (ConfigMessage)msg;
				
				if(SeparateServer.sendMessage(msg, cm.getConfig().serverIP, 9999)) {
					SeparateServer.sendMessage(out, new Message("Shit succeeded", null), msg.from, 9999); // TODO: create msg type for this
					notify_uis(cm.getConfig());
				} else {
					SeparateServer.sendMessage(out, new Message("Shit failed", null), msg.from, 9999);    // TODO: create msg type for this
				}
				break;
			case CONFIG:
				// TODO: update database?
				ConfigMessage cm = (ConfigMessage)msg;
				notify_uis(cm.getConfig());
				break;
			default:
				break;
				
		}
		
		close();
			
	} 
		
}