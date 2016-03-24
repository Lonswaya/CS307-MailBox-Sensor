
public class SensorsMessage extends Message {
	public ClientConfig[] ar;

	public SensorsMessage(String message, ClientConfig[] ar) {
		super(message, null, MessageType.GET_SENSORS);
		this.ar = ar;
	}

	
}
