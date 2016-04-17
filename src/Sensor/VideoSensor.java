package Sensor;
import java.awt.image.BufferedImage;

import cs307.purdue.edu.autoawareapp.*;
public class VideoSensor extends BaseSensor {
	
	public boolean picOrVid; //true if pictures only, false if whole videos are sent, will be affecting messagetype (picture or video)
	
	//private VIDEO_CLIP_TYPE videoClip;

	public VideoSensor(BaseConfig config) {
		super(config);
		
	}

	@Override
	public BufferedImage sense() {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean check_threshold() {
		// TODO Auto-generated method stub
		return false;
	}

	public Message form_message(){
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Message form_message(BufferedImage sensedObject) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//public VIDEO_CLIP_TYPE record_video(){}
	
	//public float detect_motion(VIDEO_CLIP_TYPE clip){}
}
