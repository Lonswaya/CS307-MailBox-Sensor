import java.util.Calendar;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class RasberryPi {
	BaseSensor sensor;
	SSLServerSocket receiveSocket;
	SSLSocket sendSocket;
	
	String ip;
	int port;
	
	public void send_message(){
		
	}
	
	public void run(){
		while(!isActive()){
			this.sensor.sense();
		}
	}
	
	//this function determins if sensor should be active given the time restriction in config
	public boolean isActive(){
		if(this.sensor.config.is_sensing){
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			int startH = this.sensor.config.start_hours;
			int startM = this.sensor.config.start_minutes;
			int stopH = this.sensor.config.stop_hours;
			int stopM = this.sensor.config.stop_minutes;
			
			if(hour > startH && hour < stopH)
				return true;
			if(startH > stopH){ //22:00 - 2:00
				if(hour > stopH && hour > startH)
					return true;
				if(hour < startH && hour < stopH)
					return true;
			}
			if( (hour == startH && hour == stopH) && (minute >= startM && minute < stopM))
				return true;
			if(hour == startH && minute >= startM)
				return true;
			if(hour == stopH && minute < stopM)
				return true;
		}
		return false;
	}
	
	/*public static void main(String[] args){
		while(true){
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			System.out.println(hour + ": " + minute);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		sensor = new LightSensor(new BaseConfig("9:25", "22:10", true, SensorType.LIGHT, 0));
		
		System.out.println(isActive(9, 30)); // true
		System.out.println(isActive(12, 30)); //true
		System.out.println(isActive(22, 2)); //true
		System.out.println(isActive(2, 2)); //false
		System.out.println(isActive(9, 24)); //false
		System.out.println(isActive(22, 12)); //false
		
		sensor = new LightSensor(new BaseConfig("9:01", "9:25", true, SensorType.LIGHT, 0));
		
		System.out.println(isActive(9, 24)); // true
		System.out.println(isActive(12, 30)); //false
		System.out.println(isActive(12, 30)); //false
		
		sensor = new LightSensor(new BaseConfig("23:00", "9:25", true, SensorType.LIGHT, 0));
	
		System.out.println(isActive(9, 30)); // false
		System.out.println(isActive(23, 01)); //true
		System.out.println(isActive(0, 0)); //true
		System.out.println(isActive(2, 2)); //true
		System.out.println(isActive(9, 24)); //true
		System.out.println(isActive(22, 12)); //false
	}*/
}
