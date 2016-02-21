import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class RaspberryPi {
	private BaseSensor sensor = null;
	private ServerSocket receiveServer;
	private Socket receiveSocket;
	private Socket sendSocket;
	
	private String ip;
	private int port = 9999;
	
	private Thread receiveThread;
	
	public RaspberryPi(){
		
		//makes the thread that constantly receive message
		this.receiveThread = new Thread(new Runnable(){
			
			private ServerSocket receiveServer;
			private Socket receiveSocket;
			private ObjectInputStream objectIn;
			private Message msg;
			
			@Override
			public void run() {
				System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
			    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
			    
			    SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			    
				try {
					receiveServer = factory.createServerSocket(6666);
				
					while(true){
						receiveSocket = receiveServer.accept();
						objectIn = new ObjectInputStream(receiveSocket.getInputStream());
						msg = (Message) objectIn.readObject();
						
						//do shit with message here
						
						receiveSocket.close();
					}
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public Runnable init(ServerSocket serverSocket, Socket socket) {
				this.receiveServer = serverSocket;
				this.receiveSocket = socket;
				return this;
			}
		}.init(this.receiveServer, this.receiveSocket));
	}
	
	//get the thread receiving shit
	public Thread getReceiveThread(){
		return this.receiveThread;
	}
	
	//get the sensor specicically to this pi
	public BaseSensor getSensor(){
		return this.sensor;
	}
	
	public void send_message(Message msg){
		
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault(); 
		try {
			sendSocket = factory.createSocket(this.ip, this.port);
			ObjectOutputStream m_outputStream = new ObjectOutputStream(sendSocket.getOutputStream());
			m_outputStream.writeObject(msg);		
			m_outputStream.flush();
			this.sendSocket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	//run the pi?
	public void run() throws InterruptedException{
		
		this.receiveThread.run(); //constantly get message thread
		
		//main loop
		while(true){
			if(this.sensor == null){ //if pi doesnt have a sensor
				Thread.sleep(5000);
				continue;
			}else{
				if(isSensorActive()){
					this.sensor.sense(); //tell sensor to sense shit maybe a time interval in between
					if(this.sensor.check_threshold()){
						send_message(this.sensor.form_message());
					}
				}
			}
		}
	}
	
	//this function determins if sensor should be active given the time restriction in config
	public boolean isSensorActive(){
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
	
	public static void main(String[] args) throws InterruptedException{
		
		//basically run like this
		//RaspberryPi pi = new RaspberryPi();
		//pi.run(); 
		
		/*while(true){
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
		System.out.println(isActive(22, 12)); //false*/
	}
}
