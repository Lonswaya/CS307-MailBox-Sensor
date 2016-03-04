import java.util.Calendar;

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
	
	public void setConfig(BaseConfig config) {
		this.config = config;
	}
	
	public void receive_message(){
		
	}
	
	// this function determins if sensor should be active given the time
	// restriction in config
	public boolean isSensorActive() {
		
		if(config.force_on)
			return true;
		if(config.force_off)
			return false;
		
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		int startH = this.config.start_hours;
		int startM = this.config.start_minutes;
		int stopH = this.config.stop_hours;
		int stopM = this.config.stop_minutes;

		if(hour > config.start_hours && hour < config.stop_hours || 
		  (hour == startH && minute >= startM) || 
		  (hour == stopH && minute < stopM) ||
		  (startH > stopH && (hour > startH || hour < stopH)) ||
		  hour > startH && hour < stopH) {
			return true;
		}

		
		return false;
	}
	
	public abstract void sense();
	
	public abstract boolean check_threshold();
	public abstract Message form_message();

	
}