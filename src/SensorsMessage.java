import java.util.ArrayList;


public class SensorsMessage extends Message {
	public ArrayList<ClientConfig> ar;

	public SensorsMessage(String message, ArrayList<ClientConfig> ar) {
		super(message, null, MessageType.GET_SENSORS);
		this.ar = ar;
	}

	
}
