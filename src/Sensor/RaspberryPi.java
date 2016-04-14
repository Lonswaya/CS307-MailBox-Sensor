package Sensor;
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

import Utilities.Connections;
import Utilities.ServerListener;
import Utilities.SocketWrapper;
import Utilities.StaticPorts;
import cs307.purdue.edu.autoawareapp.*;
public class RaspberryPi {
	public BaseSensor sensor;
	private ServerSocket receiveServer;
	public int sleepAmt;
	//this is the port and IP for the separate server 

	static public boolean streaming = false;
	
	private SocketWrapper serverConnection;
	
	ServerSocket serverSock;
	
	public RaspberryPi() throws IOException {
		
		streaming = false;

		serverConnection = null;
		BaseConfig config = new BaseConfig();
		sensor = (!config.Load()) ? null : new WebcamSensorFactory().get_sensor(config);
		// makes the thread that constantly receive message
		
		//create new receiving thing
		serverSock = Connections.getServerSocket(StaticPorts.piPort);
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						SocketWrapper newSock = new SocketWrapper(serverSock.accept());
						System.out.println("new connection");
						new Thread(new ServerListener(newSock) {
							public void HandleMessage(Message msg) throws Exception  {
								run = ProcessMessage(msg, sock);
							}
						}).start();
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	
	/*
	 * Process message processes the message. It can either be from a client (streaming) or from a server (config)
	 * If it is from a server, it updates the server connection socket wrapper so we can send messages back
	 * If it is from a client, it will keep the same thread and write objects until the connection fails.
	 * Throws an exception if we fail to write another object. 
	 */
	
	public boolean ProcessMessage(Message msg, SocketWrapper connectionSocket) throws Exception {
		if (msg.type == null) {
			System.out.println("Debug message, " + msg.message);
			return true;
		}
		System.out.println("New message: " + msg.message);
		switch(msg.type) {
		case CONFIG:
			serverConnection = connectionSocket; //update so we can send messages
			ConfigMessage conf = (ConfigMessage)msg;
			if(sensor == null || sensor.sType != conf.getConfig().sensor_type) {
				//create sensor
				System.out.println("sensor created");
				if (sensor != null) sensor.close();
				sensor = new WebcamSensorFactory().get_sensor(conf.getConfig());
				if (!sensor.ready) {
					System.out.println("Oh, we couldn't make that sensor work right. \nSorry.\nSend a better config next time");
					sensor = null;
					conf.delete = true;
					send_message(conf); //send it back, delete us
				}
				System.out.println("new sensor: " + sensor);
				
			} 
			if (sensor != null) sensor.setConfig(conf.getConfig());
			break;
		case STREAMING:
			
			streaming = ((StreamingMessage)msg).streaming;
			if (streaming && sensor != null && sensor.ready) {
				//open a new connection, start streaming as fast as humanly possible until the connection quits
				boolean run = true;
				System.out.println("Hello user. We are going to start streaming now.");
				while (run) {
					//System.out.println("Streaming loop");
					if (sensor.sType == SensorType.VIDEO && ((PictureSensor)sensor).IsLocked()) continue; //if we are occupied, continue
					if (sensor.sType == SensorType.LIGHT && ((LightSensor)sensor).IsLocked()) continue; //if we are occupied, continue
					System.out.println("Able to take picture, as no cameras are open");
					//sensor.sense();
					if (sensor.sType == SensorType.AUDIO && ((AudioSensor)sensor).doneStreaming) {
						((AudioSensor)sensor).stream();
					}
					Message toSend = sensor.form_message();
					if (toSend != null) {
						if (Connections.send(connectionSocket.out, toSend)) {
							System.out.println("Yay, we were able to send a message. Let's do this again.");
						} else {
							System.out.println("Connection lost, stopping thread");
							streaming = false;
							return false; //stop the thread
						}
						
					}
				}				
			}
		default:
			break;
			
		}
		return true; //continue the thread
	}
	
	// get the sensor specifically to this pi
	public BaseSensor getSensor() {
		return this.sensor;
	}

	public void send_message(Message msg) {
		new Thread(new Runnable(){

			public void run() {
				if (serverConnection == null) {
					System.err.println("we can't send a message bruh, no server");
					return; 
				}
				Connections.send(serverConnection.out, msg);
			}
    		
		}).start();
	}
	
}
