import java.util.ArrayList;


public class SensorSendingList {
	public SensorSendingList(ClientConfig cfg) {
		audioList = new ArrayList<String>();
		pictureList = new ArrayList<String>();
		readingList = new ArrayList<String>();
		sensorInfo = cfg;
	}
	protected ClientConfig sensorInfo;
	protected ArrayList<String> audioList;
	protected ArrayList<String> pictureList;
	protected ArrayList<String> readingList;
}
