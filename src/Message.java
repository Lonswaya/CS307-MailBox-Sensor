import java.io.*;
import java.net.*;

public class Message implements Serializable {
	
	protected String message;
	protected BaseConfig config;
	
	public Message(String message, BaseConfig config) {
		setString(message);
		setConfig(config);
	}
	
	public String getString() {
		return this.message;
	}
	
	public void setConfig(BaseConfig config) {
		this.config = config;
	}
	
	public void setString(String message) {
		this.message = message;
	}
	
	public BaseConfig getConfig() {
		return this.config;
	}
	
	
	
	
	
}