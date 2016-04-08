package cs307.purdue.edu.autoawareapp;
import java.io.Serializable;


public class LightMessage extends Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//private float light_intensity;
	
	public LightMessage(String message, ClientConfig config) {
		super(message, config, MessageType.LIGHT);
		//
	}
	
}
