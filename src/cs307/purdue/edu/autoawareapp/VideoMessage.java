package cs307.purdue.edu.autoawareapp;
import java.io.Serializable;


public class VideoMessage extends Message implements Serializable {
	//date type for video
	
	private float video_length;
	private float motion_level;
	
	public VideoMessage(String message, ClientConfig config) {
		super(message, config, MessageType.VIDEO);
		//
	}
}