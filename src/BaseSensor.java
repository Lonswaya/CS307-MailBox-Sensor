
public abstract class BaseSensor{
	
	//config shit
	protected BaseConfig config;
	protected SensorType sType;
	protected float upperBound; //some meaningful boundary values used to calculate percentage
	protected float lowerBound;
	
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
	public abstract Message form_message();
	
}