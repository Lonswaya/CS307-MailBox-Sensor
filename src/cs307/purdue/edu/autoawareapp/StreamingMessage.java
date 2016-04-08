package cs307.purdue.edu.autoawareapp;
import java.io.Serializable;


public class StreamingMessage extends Message implements Serializable {
	private static final long serialVersionUID = 1L;
	//adding a config to this class is mandatory
	public StreamingMessage(String message, ClientConfig config, boolean on) {
		super(message, config, MessageType.STREAMING);
		streaming = on;
	}

	public boolean streaming;
}
