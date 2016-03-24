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
	public BaseSensor sensor;
	private ServerSocket receiveServer;

	private String ip;
	//this is the port and IP for the separate server 
	private int port = 8888;

	boolean streaming = false;
	
	public RaspberryPi() throws IOException {

		/*Scanner in = new Scanner(System.in);
		System.out.println("Enter the server's ip");
		ip = in.nextLine();
		System.out.println("Enter server's port");
		port = Integer.parseInt(in.nextLine());*/
		
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
			private ObjectInputStream in;
			private Socket sock;
			private RaspberryPi ref;
			
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
						if (ip == null) ip = msg.getFrom();
						//System.out.println(msg.config.toString() + " " +  msg);
						
						switch(msg.type) {
							case CONFIG:
								ConfigMessage conf = (ConfigMessage)msg;
								if(ref.sensor == null || ref.sensor.sType != conf.getConfig().sensor_type) {
									//create sensor
									System.out.println("sensor created");
									if (ref.sensor != null)ref.sensor.close();
									ref.sensor = new WebcamSensorFactory().get_sensor(conf.getConfig());
									System.out.println("new sensor: " + ref.sensor);
								}
								ref.sensor.setConfig(conf.getConfig());
								break;
							case STREAMING:
								streaming = ((StreamingMessage)msg).streaming;
								//System.out.println("Pi streaming set to " + streaming);
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

			public Runnable init(ServerSocket serverSocket, RaspberryPi ref) {
				this.receiveServer = serverSocket;
				this.ref = ref;
				return this;
			}
		}.init(this.receiveServer, this)).start();
	}

	// get the sensor specicically to this pi
	public BaseSensor getSensor() {
		return this.sensor;
	}

	public void send_message(Message msg) {
		System.out.println("start sending msg");
		Socket sendSocket = null;
		//System.out.println("sending message " + msg.toString() + " with config " + msg.config);
		final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			//System.out.println("ip: " + this.ip + " port: " + this.port);
			sendSocket = factory.createSocket(this.ip, this.port);
			//System.out.println("1");
			ObjectOutputStream outputStream = new ObjectOutputStream(sendSocket.getOutputStream());
			//System.out.println("2");
			outputStream.writeObject(msg);
			//System.out.println("3");
			outputStream.flush();
			//System.out.println("4");
			Thread.sleep(50); //pause to let the central server get info
			
			outputStream.close();
			sendSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("msg sent");
	}

}