import java.io.Serializable;


public class GetReadingMessage extends Message implements Serializable {
	protected MessageType type;
	protected String ip;
	
	public GetReadingMessage(String message, MessageType type, String ip) {
		super(message, null, MessageType.GET_READING);
		this.type = type;
		this.ip = ip;
	}
}	
