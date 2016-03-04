import java.io.IOException;

public class dicks {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		RaspberryPi pi = new RaspberryPi();
		
		// main loop
		while (true) {
			//System.out.println("from sense thread");
			int sleepAmt = 3000;
			if (pi.sensor == null) { // if pi doesnt have a sensor
				System.out.println("no sensor found");
				sleepAmt = 10000;
			} else {
				
				if (pi.sensor.isSensorActive()) {
					//System.out.println("sensor is active, " + pi.sensor.check_threshold() + " " + pi.streaming);
					pi.sensor.sense(); // tell sensor to sense shit maybe a
						// time interval in between, currently one second
					if (pi.sensor.check_threshold() || pi.streaming) { //if the threshold is above, or if we are supposed to stream constantly
						System.out.println("currently sending a message");
						
						pi.send_message(pi.sensor.form_message());
						if (pi.sensor.sType == SensorType.PICTURE || pi.sensor.sType == SensorType.LIGHT) sleepAmt = 0; //for lower latency
					} else {
						pi.sensor.close();
					}
				} else {
					System.out.println("sensor is not active");
				}
			}
			System.out.println("sleeping for " + sleepAmt);
			if (sleepAmt > 0) Thread.sleep(sleepAmt);

		}
		
	}
	
}
