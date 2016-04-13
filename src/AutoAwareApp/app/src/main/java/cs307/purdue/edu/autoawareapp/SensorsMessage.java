package cs307.purdue.edu.autoawareapp;
import java.util.ArrayList;


public class SensorsMessage extends Message {
	public ArrayList<ClientConfig> ar;
	private static final long serialVersionUID = 1L;
	public SensorsMessage(String message, ArrayList<ClientConfig> ar) {
		super(message, null, MessageType.GET_SENSORS);
		this.ar = ar;
	}

	
}
