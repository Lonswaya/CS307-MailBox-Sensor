import java.net.Socket;
import java.util.ArrayList;


public class SensorInfo {
	public SensorInfo(ClientConfig cfg, Socket sock) {
		sensorInfo = cfg;
		streaming = false;
		socket = sock;
	}
	protected boolean streaming;
	protected Socket socket;
	protected ClientConfig sensorInfo;
}
