package cs307.purdue.edu.autoawareapp;
import java.io.Serializable;


public class ConfigMessage extends Message implements Serializable  {
	private static final long serialVersionUID = 1L;
	protected boolean delete;
	
	public ConfigMessage(String message, ClientConfig config) {
		super(message, config, MessageType.CONFIG);
		delete = false;
		//
	}
	
	
}
