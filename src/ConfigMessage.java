import java.io.Serializable;


public class ConfigMessage extends Message implements Serializable  {

	public ConfigMessage(String message, ClientConfig config) {
		super(message, config, MessageType.CONFIG);
		//
	}
	
}
