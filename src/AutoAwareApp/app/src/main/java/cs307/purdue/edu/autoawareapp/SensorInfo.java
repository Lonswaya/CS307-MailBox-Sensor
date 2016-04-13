package Utilities;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import cs307.purdue.edu.autoawareapp.*;

public class SensorInfo {
	public SensorInfo(ClientConfig cfg, Socket sock) {
		sensorInfo = cfg;
		this.sock = new SocketWrapper(sock);
	}
	public SocketWrapper sock;
	public ClientConfig sensorInfo;
}
