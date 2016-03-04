import java.io.IOException;

public class dicks {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		RaspberryPi pi = new RaspberryPi();
		
		// main loop
		while (true) {
			//System.out.println("from sense thread");
			if (pi.sensor == null) { // if pi doesnt have a sensor
				Thread.sleep(5000);
				continue;
			} else {
				if (pi.sensor.isSensorActive()) {
					pi.sensor.sense(); // tell sensor to sense shit maybe a
					Thread.sleep(1000);		// time interval in between, currently one second
					if (pi.sensor.check_threshold() || pi.streaming) { //if the threshold is above, or if we are supposed to stream constantly
						pi.send_message(pi.sensor.form_message());
					}
				}
			}
		}
		
	}
	
}
