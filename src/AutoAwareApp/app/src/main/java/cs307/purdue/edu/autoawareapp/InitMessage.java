package cs307.purdue.edu.autoawareapp;

public class InitMessage extends Message {

	public String username;
	
	public String password;
	//this is sent to the server to create a new user, or log in to the user
	public InitMessage(String name, String password) {
		super("Init for user " + name, null, MessageType.INIT);
		this.username = name;
		this.password = password;
	}

}
