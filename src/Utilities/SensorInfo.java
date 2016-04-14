package Utilities;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import cs307.purdue.edu.autoawareapp.*;

public class SensorInfo {
	public SensorInfo(ClientConfig cfg, SocketWrapper sock) {
		sensorInfo = cfg;
		this.sock = sock;
	}
	public SocketWrapper sock;
	public ClientConfig sensorInfo;
}
