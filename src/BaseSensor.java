import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseSensor{
	
	//config shit
	public BaseConfig config;
	public SensorType sensorType;
	
	//networking
	public String ip;
	public int port;
	public ServerSocket receiveSocket;
	public Socket sendSocket;
	
	public BaseSensor(BaseConfig config){
		this.config = config;
		this.sensorType = config.sensor_type;
	}
	
	public void receive_message(){
		
	}
	
	public abstract void sense();
	public abstract boolean check_threshold();
	//public abstract MESSAGE_TYPE form_message();
}