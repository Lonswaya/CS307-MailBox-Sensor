package cs307.purdue.edu.autoawareapp;
import java.io.*;
import java.net.*;
import java.util.Calendar;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public String message;
	public ClientConfig config;
	public MessageType type;
	public String from;
	public String creationTime;
	
	public Message(String message, ClientConfig config, MessageType type) {
		setString(message);
		setConfig(config);
		generateCreateTime();
		this.type = type;
		SetFromThis();
		System.out.println("Creating new message with ip " + this.from);
	}
	public void SetFromThis() {
		String myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			
		} //get the local ip address
		setFrom(myAddress.substring(myAddress.indexOf('/') + 1));  //strip off the unnecessary bits
	}
	public String getString() {
		return this.message;
	}
	
	public void setConfig(ClientConfig config) {
		this.config = config;
	}
	
	public void setString(String message) {
		this.message = message;
	}
	
	public ClientConfig getConfig() {
		return this.config;
	}
	
	public void setFrom(String from){
		this.from = from;
	}
	
	public String getFrom(){
		return this.from;
	}
	
	public String getCreateTime(){
		return this.creationTime;
	}
	
	public void generateCreateTime(){
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		this.creationTime = hour + ":" + minute;
	}
	
	public String toString(){
		String s;
		s = "Message: " + this.message +
			"\nFrom: " + this.from +
			"\nCreated at: " + this.creationTime + 
			"\nType: " + this.type;
		return s;
	}
	
	
	
	
}