package cs307.purdue.edu.autoawareapp;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Purpose: Static methods and a static string that are used by both the AutoAwareControlPanel
 * 		and phone app to communicate and respond to a server.
 * 
 */

public class UserBackend {
	/* GetSensors: Takes a server connection, and sends a get sensors message
	 * You should promptly get back a message through ProcessMessage that contains the servers
	 */
	public static void GetSensors(SocketWrapper serverConnection) {
		if (serverConnection != null) {
			Message msg = new Message("Gib sensors plz", null, MessageType.GET_SENSORS);
			Connections.send(serverConnection.out, msg);
		} else {
			System.err.println("get sensors: server not found");
		}
	}
	
	/* SendStreaming: Sends a streaming message to a sensor determined by the IP
	 * onOrOff: On to start streaming, Off to stop
	 */
	public static SocketWrapper SendStreaming(String ip, final MessageProcessor msgProcessor) {
		SocketWrapper sWrapper = new SocketWrapper(Connections.getSocket(ip, StaticPorts.piPort));
		if (sWrapper.sock == null) {
			System.out.println("Sensor connection came back as null, could not connect");
			return null;
		}
		new Thread(new ServerListener(sWrapper) {
			public void HandleMessage(Message msg) throws Exception  {
				msgProcessor.ProcessMessage(msg);
			}
		}).start();
		//sets streaming on
		Connections.send(sWrapper.out, new StreamingMessage("Setting Streaming to True", null, true));
		return sWrapper;
	}
	/*
	 * Stops the current streaming method for the connection
	 * 
	 */
	public static void StopStreaming(SocketWrapper sWrapper) {
		Connections.send(sWrapper.out, new StreamingMessage("Setting Streaming to False", null, false));
	}
	/*
	 * Creates a socketwrapper that will connect to the server
	 * Also instances a new thread that will handle messages from that socket
	 * If the conneciton is interrupted, then 
	 * Returns null if the connection can not be made
	 * 
	 */
	public static SocketWrapper SetServerConnection(String ip, final MessageProcessor msgProcessor) {
		SocketWrapper sWrapper = new SocketWrapper(Connections.getSocket(ip, StaticPorts.serverPort));
		if (sWrapper.sock == null) {
			System.out.println("Server not found");
			return null;
		} else {
			System.out.println("Created server connection " + sWrapper.sock);
		}
		new Thread(new ServerListener(sWrapper) {
			public void HandleMessage(Message msg) throws Exception  {
				msgProcessor.ProcessMessage(msg);
			}
		}).start();
		
		Connections.send(sWrapper.out, new Message("Hi there I am a new dude", null, MessageType.INIT));
		return sWrapper;
	}
	/* Sends a config to a server connection
	 */
	public static void SendConfig(ClientConfig cfg, boolean delete, SocketWrapper serverConnection) {
		ConfigMessage confM = new ConfigMessage("To update", cfg);
		confM.delete = delete;
		Connections.send(serverConnection.out, confM);
	}
	/* Adds a new sensors
	 * Returns true if the sensor can be connected to, false if they could not
	 * if true, sends a new sensor info to the server
	 */
	public static boolean AddSensor(ClientConfig cfg, SocketWrapper serverConnection) {
		System.out.println("In Add Sensor Backend");
        //Socket newSensorSocket = Connections.getSocket(cfg.ip, StaticPorts.piPort);
        //System.out.println("Socket = " + newSensorSocket);
		// TODO: Uncomment next two lines
		//if (newSensorSocket != null && Connections.send(newSensorSocket, new Message("Hi do you exist plz respond", null, null))) {
            //Connections.closeSocket(newSensorSocket);
			ConfigMessage confM = new ConfigMessage("To add", cfg);
			return Connections.send(serverConnection.out, confM);		
		//} else {
		//	return false;
		//}
	}
}
