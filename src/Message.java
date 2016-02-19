import java.io.*;
import java.net.*;

public class Message implements Serializable {
	
	private String message;
	
	public Message(String message) {
		setString(message);
	}
	
	public String getString() {
		return this.message;
	}
	
	public void setString(String message) {
		this.message = message;
	}
	
	
}