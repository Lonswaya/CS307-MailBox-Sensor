
public class AudioSensor extends BaseSensor{
	//AUDIO_CLIP_TYPE audioClip;
	private float last_decibel;
	
	public AudioSensor(BaseConfig config){
		super(config);
	}
	
	@Override
	public void sense(){
		
	}
	@Override
	public boolean check_threshold(){
		return false;
	}
	
	//public MESSAGE_TYPE form_message() {} 
	//public AUDIO_CLIP_TYPE record_audio() {}
}
