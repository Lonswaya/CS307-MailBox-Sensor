import java.awt.image.BufferedImage;

public class LightSensor extends BaseSensor {

	private float light_intensity;
	
	public LightSensor(BaseConfig config){
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
	
	//public MESSAGE_TYPE form_message(){};

	public void set_webcam_state(boolean state){
		
	}
	
	public void take_picture(){
		
	}
	
	public BufferedImage greyscale_picture(BufferedImage image){
		return null;
	}
	
	public float get_reading(BufferedImage image){
		return 0;
	}
}
