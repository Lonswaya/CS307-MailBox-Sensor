package cs307.purdue.edu.autoawareapp;
import java.util.ArrayList;


public class SensorsMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<ClientConfig> ar;

	public SensorsMessage(String message, ArrayList<ClientConfig> ar) {
		super(message, null, MessageType.GET_SENSORS);
		this.ar = ar;
	}

	
}
