import java.applet.AudioClip;
import java.io.Serializable;


public class AudioMessage extends Message implements Serializable {
	//as of now, we are assuming clip is going to be 3000 ms long
	protected AudioClip clip;
	protected byte[] recording;
	
	public AudioMessage(String message, ClientConfig config) {
		super(message, config, MessageType.AUDIO);
	}
	
	public void setRecording(byte[] b)
	{
		this.recording = b;
	}
	
	public byte[] getRecording()
	{
		return this.recording;
	}
}
