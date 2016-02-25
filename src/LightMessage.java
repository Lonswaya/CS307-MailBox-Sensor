
public class LightMessage extends Message{
	
	private float light_intensity;
	
	public LightMessage(String message, BaseConfig config) {
		super(message, config, MessageType.LIGHT);
		//
	}
}
