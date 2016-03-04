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
	BaseSensor sensor = null;
	private ServerSocket receiveServer;

	private String ip = "localhost";
	private int port = 9999;

	boolean streaming = false;
	
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
						
						System.out.println(msg.config.toString());
						ip = msg.config.serverIP;
						port = msg.config.serverPort;
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

}