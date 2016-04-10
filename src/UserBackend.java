import java.net.Socket;
import java.util.ArrayList;

import Example.Connections;

public class UserBackend {
	static String serverIP = "localhost";
	
	/* GetSensors: Takes a server connection, and reads/writes from it to get an array
	 * Returns an arraylist of sensors
	 */
	static ArrayList<ClientConfig> GetSensors(SocketWrapper serverConnection) {
		ArrayList<ClientConfig> ar = null;
		Message msg = new Message("Gib sensors plz", null, MessageType.GET_SENSORS);
		Connections.send(serverConnection.out, msg);
		ar = Connections.readObject(serverConnection.in);
		return ar;
	}
	
	/* SendStreaming: Sends a streaming message to a sensor determined by the IP
	 * 
	 */
	static void SendStreaming(String ip, MessageProcessor msgProccesser) {
		SocketWrapper sWrapper = new SocketWrapper(Connections.getSocket(ip, StaticPorts.piPort));
		new Thread(new ServerListener(sWrapper) {
			public void HandleMessage(Message msg) throws Exception  {
				msgProccesser.ProcessMessage(msg);
			}
		});
	}
	/* Sends a config to a server connection
	 */
	static void SendConfig(ClientConfig cfg, SocketWrapper serverConnection) {
		ConfigMessage confM = new ConfigMessage("To update", cfg);
		Connections.send(serverConnection.out, confM);
	}
	/* Adds a new sensors
	 * Returns true if the sensor can be connected to, false if they could not
	 * if true, sends a new sensor info to the server
	 */
	static boolean AddSensor(ClientConfig cfg, SocketWrapper serverConnection) {
		Socket newSensorSocket = Connections.getSocket(cfg.ip, StaticPorts.piPort, 5000);
		if (newSensorSocket != null) {
			Connections.closeSocket(newSensorSocket);
			ConfigMessage confM = new ConfigMessage("To add", cfg);
			Connections.sendAndCheck(serverConnection.out, confM, 5000);		
			return true;
		} else {
			return false;
		}
	}
}
