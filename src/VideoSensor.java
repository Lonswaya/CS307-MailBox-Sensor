
public class VideoSensor extends BaseSensor {
	
	public boolean picOrVid; //true if pictures only, false if whole videos are sent, will be affecting messagetype (picture or video)
	
	//private VIDEO_CLIP_TYPE videoClip;

	public VideoSensor(BaseConfig config) {
		super(config);
		
	}

	@Override
	public void sense() {
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
	
	//public VIDEO_CLIP_TYPE record_video(){}
	
	//public float detect_motion(VIDEO_CLIP_TYPE clip){}
}
