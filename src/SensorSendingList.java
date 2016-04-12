import java.net.Socket;
import java.util.ArrayList;
import cs307.purdue.edu.autoawareapp.*;

public class SensorSendingList {
	public SensorSendingList(ClientConfig cfg, Socket sock) {
		audioList = new ArrayList<String>();
		pictureList = new ArrayList<String>();
		readingList = new ArrayList<String>();
		sensorInfo = cfg;
		streaming = false;
		socket = sock;
	}
	protected boolean streaming;
	protected Socket socket;
	protected ClientConfig sensorInfo;
	protected ArrayList<String> audioList;
	protected ArrayList<String> pictureList;
	protected ArrayList<String> readingList;
}
