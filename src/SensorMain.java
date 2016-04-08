import java.io.IOException;

public class SensorMain {
	
	protected static RaspberryPi pi;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		pi = new RaspberryPi();
		
		
		// main loop
		while (true) {
			//System.out.println("from sense thread");
			pi.sleepAmt = 3000;
			if (pi.sensor == null) { // if pi doesnt have a sensor
				System.out.println("no sensor found");
				pi.sleepAmt = 10000;
			} else {
				
				if (pi.sensor.isSensorActive()) {
					new Thread(new SenseThread(pi)).run(); // tell sensor to sense shit maybe a
					
					if (pi.sensor.sType == SensorType.AUDIO && ((AudioSensor)pi.sensor).doneStreaming) {
						new Thread(new StreamThread(pi)).run();
					}
						// time interval in between, currently one second
					if (pi.sensor.check_threshold() || pi.streaming) { //if the threshold is above, or if we are supposed to stream constantly
						System.out.println("currently sending a message");
						new Thread(new SendThread(pi)).run();
						pi.sleepAmt = 0; //for lower latency
					} else {
						pi.sensor.close();
					}
				} else {
					System.out.println("sensor is not active");
				}
			}
			System.out.println("sleeping for " + pi.sleepAmt);
			if (pi.sleepAmt > 0) Thread.sleep(pi.sleepAmt);

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
		pi.send_message(pi.sensor.form_message());
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
