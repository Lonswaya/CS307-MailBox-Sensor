import java.io.Serializable;


public class LightMessage extends Message implements Serializable {
	
	//private float light_intensity;
	
	public LightMessage(String message, ClientConfig config) {
		super(message, config, MessageType.LIGHT);
		//
	}
	
}
