import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class SensorInfo {
	public SensorInfo(ClientConfig cfg, Socket sock) {
		sensorInfo = cfg;
		this.sock = new SocketWrapper(sock);
	}
	protected SocketWrapper sock;
	protected ClientConfig sensorInfo;
}
