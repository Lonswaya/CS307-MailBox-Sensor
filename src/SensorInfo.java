import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class SensorInfo {
	public SensorInfo(ClientConfig cfg, ObjectOutputStream obj) {
		sensorInfo = cfg;
		streaming = false;
		this.obj = obj;
	}
	protected boolean streaming;
	protected ObjectOutputStream obj;
	protected ClientConfig sensorInfo;
}
