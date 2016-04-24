package cs307.purdue.edu.autoawareapp;
import java.util.ArrayList;


public class UsersMessage extends Message {
	public ArrayList<String> ar;
	private static final long serialVersionUID = 1L;
	public UsersMessage(String message, ArrayList<String> ar) {
		super(message, null, MessageType.GET_USERS);
		this.ar = ar;
	}

	
}
