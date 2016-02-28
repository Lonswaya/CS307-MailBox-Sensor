
public class ReadingMessage extends Message{
	
	private float threshold;
	
	public ReadingMessage(String message, BaseConfig config) {
		super(message, config, MessageType.READING);
		
		//
	}
	public float getCurrentThreshold() {
		return threshold;
	}
}
