import java.io.Serializable;


public class ReadingMessage extends Message implements Serializable {
	
	private float currentThreshold;
	
	public ReadingMessage(String message, ClientConfig config) {
		super(message, config, MessageType.READING);
	}
	public void setCurrentThreshold(float threshold){
		this.currentThreshold = threshold;
	}
	public float getCurrentThreshold() {
		return currentThreshold;
	}
}
