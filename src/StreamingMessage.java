import java.io.Serializable;


public class StreamingMessage extends Message implements Serializable {
	public StreamingMessage(String message, ClientConfig config, boolean on) {
		super(message, config, MessageType.STREAMING);
		streaming = on;
	}

	public boolean streaming;
}
