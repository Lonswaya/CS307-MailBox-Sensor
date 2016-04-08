package cs307.purdue.edu.autoawareapp;
import java.applet.AudioClip;
import java.io.Serializable;


public class AudioMessage extends Message implements Serializable {
	//as of now, we are assuming clip is going to be 3000 ms long
	private static final long serialVersionUID = 1L;
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
