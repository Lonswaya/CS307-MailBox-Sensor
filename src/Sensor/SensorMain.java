package Sensor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import cs307.purdue.edu.autoawareapp.*;
public class SensorMain {
	
	protected static RaspberryPi pi;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, InterruptedException {
		
		pi = new RaspberryPi();
		
		
		// main loop, this loop only connects to the server and notifies if the threshold is above.
		while (true) {
			//System.out.println("from sense thread");
			pi.sleepAmt = 3000;
			if (pi.sensor == null) { // if pi doesnt have a sensor
				System.out.println("no sensor found");
				pi.sleepAmt = 10000;
			} else {
				//if (RaspberryPi.streaming) pi.sleepAmt = 0;
				if ((pi.sensor.isSensorActive() || RaspberryPi.streaming) && pi.sensor.sType != SensorType.VIDEO) {
					pi.sensor.sense();
					//pi.dataReady = true;
					//new Thread(new SenseThread(pi)).run(); // tell sensor to sense shit maybe a
					
					/*if (pi.sensor.sType == SensorType.AUDIO && ((AudioSensor)pi.sensor).doneStreaming) {
						
						new Thread(new StreamThread(pi)).run();
					}*/ //we are not going to be wanting to stream audio if this is only for connecting to the server
						// time interval in between, currently one second
					if (pi.sensor.check_threshold()) { //if the threshold is above, or if we are supposed to stream constantly
						System.out.println("sending a message to a server, above notification");
						new Thread(new SendThread(pi)).run();
					} else {
						//pi.sensor.close();
					}
				} else {
					System.out.println("sensor is not active");
				}
			}
			if (pi.sleepAmt > 0) {
				System.out.println("sleeping for " + pi.sleepAmt);
				Thread.currentThread().sleep(pi.sleepAmt);
			}

		}
		
	}
	
	
}
class StreamThread implements Runnable {
	RaspberryPi pi;
	public StreamThread(RaspberryPi pi) {
		this.pi = pi;
	}
	public void run() {
		((AudioSensor)pi.sensor).stream();
	}
}
class SendThread implements Runnable {
	RaspberryPi pi;
	public SendThread(RaspberryPi pi) {
		this.pi = pi;
	}
	public void run() {
		Message msg = pi.sensor.form_message(null);
		msg.setFrom(pi.assignedIPAddress);
		pi.send_message(msg);
	}
}
class SenseThread implements Runnable {
	RaspberryPi pi;
	public SenseThread(RaspberryPi pi) {
		this.pi = pi;
	}
	public void run() {
		pi.sensor.sense();
	}
}
