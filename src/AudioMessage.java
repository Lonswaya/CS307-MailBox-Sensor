import java.applet.AudioClip;
import java.io.Serializable;


public class AudioMessage extends Message implements Serializable {
	//as of now, we are assuming clip is going to be 3000 ms long
	protected AudioClip clip;
	
	public AudioMessage(String message, ClientConfig config) {
		super(message, config, MessageType.AUDIO);
	}

}
