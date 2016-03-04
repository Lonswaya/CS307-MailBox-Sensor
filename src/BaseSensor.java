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
		if (this.config == null) return false;
		return this.config.isSensorActive();
		/*if (hour == startH)
			if (minute >= startM)
				return true;
		if (hour == stopH)
			if (minute < stopM)
				return true;*/
		/*  (hour == startH && minute >= startM) return true;
		  (hour == stopH && minute < stopM) ||
		  (startH > stopH && (hour > startH || hour < stopH)) ||
		  hour > startH && hour < stopH) {
			return true;
		}*/

		
	}
	
	public abstract void sense();
	
	public abstract boolean check_threshold();
	public abstract Message form_message();
	
	public String toString() {
		return "FUCK you " + this.sType;
	}
	public abstract void close();

	
}