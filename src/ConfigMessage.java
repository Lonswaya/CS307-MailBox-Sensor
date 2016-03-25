import java.io.Serializable;


public class ConfigMessage extends Message implements Serializable  {
	protected boolean delete;
	
	public ConfigMessage(String message, ClientConfig config) {
		super(message, config, MessageType.CONFIG);
		delete = false;
		//
	}
	
	
}
