package Example;

import java.io.Serializable;

public class Message implements Serializable {
	private String message = "";
	
	public Message(String message) {
		if (message == null) return;
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String toSet) {
		if (toSet == null) return;
		this.message = toSet;
	}
}
