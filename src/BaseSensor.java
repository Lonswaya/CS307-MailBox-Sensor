
public abstract class BaseSensor{
	
	//config shit
	public BaseConfig config;
	public SensorType sType;
	public float upperBound; //some meaningful boundary values used to calculate percentage
	public float lowerBound;
	
	public BaseSensor(BaseConfig config){
		this.config = config;
		this.sType = config.sensor_type;
		
		switch(this.sType){
			case LIGHT:{
				upperBound = 255;
				lowerBound = 0;
				break;
			}
			default:{
				upperBound = 100;
				lowerBound = 0;
				break;
			}
		}
	}
	
	public void receive_message(){
		
	}
	
	public abstract void sense();
	
	public abstract boolean check_threshold();
	//public abstract MESSAGE_TYPE form_message();
	
}