import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class RaspberryPi {
	private BaseSensor sensor = null;
	private ServerSocket receiveServer;

	private String ip = "localhost";
	private int port = 9999;

	private boolean streaming;

	public RaspberryPi() throws IOException {

		Scanner in = new Scanner(System.in);
		System.out.println("Enter the server's ip");
		ip = in.nextLine();
		System.out.println("Enter server's port");
		port = Integer.parseInt(in.nextLine());
		
		System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "sensor");
		System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
		System.setProperty("javax.net.ssl.trustStorePassword", "sensor");
		
		streaming = false;
		
		//TODO: pull config from text file and create sensor or if no config, leave sensor null
		BaseConfig config = new BaseConfig();
		sensor = (!config.Load()) ? null : new WebcamSensorFactory().get_sensor(config);
		
		
		
		// makes the thread that constantly receive message
		new Thread(new Runnable() {

			private ServerSocket receiveServer;
			private BaseSensor sensor;
			private ObjectInputStream in;
			private Socket sock;
			
			@Override
			public void run() {

				SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

				try {
					receiveServer = factory.createServerSocket(9999);

					while (true) {
						//System.out.println("from receive thread");
						sock = receiveServer.accept();
						in = new ObjectInputStream(sock.getInputStream());
						Message msg = (Message) in.readObject();
						
						switch(msg.type) {
							case CONFIG:
								ConfigMessage conf = (ConfigMessage)msg;
								if(sensor == null) {
									//create sensor
									sensor = new WebcamSensorFactory().get_sensor(conf.getConfig());
								}
								sensor.setConfig(conf.getConfig());
								break;
							case STREAMING:
								streaming = ((StreamingMessage)msg).streaming;
								//set Pi to constantly send values back to the server, send a new ReadingMessage
								//note: it will still send ReadingMessage if the threshold is greater than normal.
							default:
								break;
								
						}
						in.close();
						sock.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} 
			}

			public Runnable init(ServerSocket serverSocket, BaseSensor sensor) {
				this.receiveServer = serverSocket;
				this.sensor = sensor;
				return this;
			}
		}.init(this.receiveServer, sensor)).start();
	}

	// get the sensor specicically to this pi
	public BaseSensor getSensor() {
		return this.sensor;
	}

	public void send_message(Message msg) {

		Socket sendSocket = null;
		
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			sendSocket = factory.createSocket(this.ip, this.port);
			ObjectOutputStream outputStream = new ObjectOutputStream(sendSocket.getOutputStream());
			outputStream.writeObject(msg);
			outputStream.flush();
			outputStream.close();
			sendSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// run the pi?
	public void run() throws InterruptedException {

		
		// main loop
		while (true) {
			//System.out.println("from sense thread");
			if (this.sensor == null) { // if pi doesnt have a sensor
				Thread.sleep(5000);
				continue;
			} else {
				if (isSensorActive()) {
					this.sensor.sense(); // tell sensor to sense shit maybe a
					Thread.sleep(1000);		// time interval in between, currently one second
					if (this.sensor.check_threshold() || streaming) { //if the threshold is above, or if we are supposed to stream constantly
						send_message(this.sensor.form_message());
					}
				}
			}
		}
	}

	// this function determins if sensor should be active given the time
	// restriction in config
	public boolean isSensorActive() {
		
		if(sensor.config.force_on)
			return true;
		if(sensor.config.force_off)
			return false;
		
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		int startH = this.sensor.config.start_hours;
		int startM = this.sensor.config.start_minutes;
		int stopH = this.sensor.config.stop_hours;
		int stopM = this.sensor.config.stop_minutes;

		if(hour > sensor.config.start_hours && hour < sensor.config.stop_hours || 
		  (hour == startH && minute >= startM) || 
		  (hour == stopH && minute < stopM) ||
		  (startH > stopH && (hour > startH || hour < stopH)) ||
		  hour > startH && hour < stopH) {
			return true;
		}

		
		return false;
		
			/*
				legacy. keep 
			if (hour > startH && hour < stopH)
				return true;
			if (startH > stopH) { // 22:00 - 2:00
				if (hour > startH)
					return true;
				if (hour < stopH)
					return true;
			}
			if ((hour == startH && hour == stopH)
					&& (minute >= startM && minute < stopM))
				return true;
			if (hour == startH && minute >= startM)
				return true;
			if (hour == stopH && minute < stopM)
				return true;
		}
		return false;
		*/
	}
}