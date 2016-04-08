import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import Example.Connections;

public class RaspberryPi {
	public BaseSensor sensor;
	private ServerSocket receiveServer;
	public int sleepAmt;
	//this is the port and IP for the separate server 

	boolean streaming = false;
	
	private Socket serverConnection;
	
	public RaspberryPi() throws IOException {
		
		streaming = false;

		serverConnection = null;
		
		//TODO: pull config from text file and create sensor or if no config, leave sensor null
		BaseConfig config = new BaseConfig();
		sensor = (!config.Load()) ? null : new WebcamSensorFactory().get_sensor(config);
		
		
		
		// makes the thread that constantly receive message
		new Thread(new Runnable() {

			private ServerSocket receiveServer;
			private RaspberryPi ref;
			
			@Override
			public void run() {

				try {
					receiveServer = Connections.getServerSocket(StaticPorts.piPort);
					
					while (true) {
						
						Socket sock = receiveServer.accept();
						Message msg = Connections.readObject(sock);
						
						switch(msg.type) {
							case CONFIG:
								ConfigMessage conf = (ConfigMessage)msg;
								if(ref.sensor == null || ref.sensor.sType != conf.getConfig().sensor_type) {
									//create sensor
									System.out.println("sensor created");
									if (ref.sensor != null)ref.sensor.close();
									ref.sensor = new WebcamSensorFactory().get_sensor(conf.getConfig());
									if (!ref.sensor.ready) {
										System.out.println("Oh, we couldn't make that sensor work right. \nSorry.\nSend a better config next time");
										ref.sensor = null;
										conf.delete = true;
										send_message(conf); //send it back, delete us
									}
									System.out.println("new sensor: " + ref.sensor);
									serverConnection = sock;
								} 
								if (ref.sensor != null) ref.sensor.setConfig(conf.getConfig());
								break;
							case STREAMING:
								
								streaming = ((StreamingMessage)msg).streaming;
								if (streaming) {
									//refresh the thread, so we aren't sleeping as soon as we get a streaming message
									//the delay really builds up
									sleepAmt = 0;
									
									//start new thread to socket
									new Thread(new Runnable() {
										final private Socket socket = sock;
										final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
										public void run() {
											
											while(true) {
												try {
													out.writeObject(sensor.form_message());																										
												} catch (Exception e) {
													break;
												}
											}
											
											try {
												socket.close();
											} catch (Exception e) { }
											
										}
									}).start();
								}
								//System.out.println("Pi streaming set to " + streaming);
								//set Pi to constantly send values back to the server, send a new ReadingMessage
								//note: it will still send ReadingMessage if the threshold is greater than normal.
							default:
								break;
								
						}
						
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

	// get the sensor specifically to this pi
	public BaseSensor getSensor() {
		return this.sensor;
	}

	public void send_message(Message msg) {
		new Thread(new Runnable(){
    		public void run(){
				long tempTime = System.currentTimeMillis();
				System.out.println("start sending msg");
				
				try {
					Connections.send(serverConnection, msg);
					
				} catch (Exception e) {
					e.printStackTrace();
					serverConnection = null;
					streaming = false;
					
					
				}
				System.out.println("Debug: End compression, Time:" + (int) (System.currentTimeMillis() - tempTime) + "\n");
				System.out.println("msg sent");
    		}
		}).start();
	}
	
}