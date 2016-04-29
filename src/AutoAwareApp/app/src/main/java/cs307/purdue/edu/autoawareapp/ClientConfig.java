package cs307.purdue.edu.autoawareapp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


public class ClientConfig extends BaseConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	public String name;
	//public Color color;
	public float r,g,b;
	public boolean desktopNotification, magicMirrorNotification, textNotification, emailNotification;
	public String phoneNumber, emailAddress, ip;
	public int notificationInterval;
	public ArrayList<String> users; //for all the users that are able to view this sensor
	
	
	public ClientConfig(String ip, String start, String stop, boolean force_on, boolean force_off, SensorType type, float threshold, String name, float r, float g, float b, boolean desktopNotification, boolean magicMirrorNotification, boolean textNotification, boolean emailNotification, String phoneNumber, String emailAddress, int notificationInterval, int sensorInterval) {
		super(start, stop, force_on, force_off , type, threshold, sensorInterval);
		this.ip = ip;
		//this.color = color;
		this.r = r;
		this.g = g;
		this.b = b;
		this.name = name;
		this.desktopNotification = desktopNotification;
		this.magicMirrorNotification = magicMirrorNotification;
		this.textNotification=textNotification; 
		this.emailNotification = emailNotification;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.notificationInterval = notificationInterval;
		this.sensorInterval = sensorInterval;
	}
	public ClientConfig() {
		super();
		r = g = b = 1;
		this.name = "";
		this.phoneNumber = "";
		this.emailAddress = "";
		this.desktopNotification = magicMirrorNotification = textNotification = emailNotification = false;
		this.sensorInterval = 1000;
		this.notificationInterval = 1000;
	}
	@Override 
	public String toString() {
		return		"[TIME SETTINGS]\n"
				+	"START TIME=" + start_hours + ":" + start_minutes + "\n"
				+	"STOP TIME=" + stop_hours + ":" + stop_minutes + "\n"
				+	"[SENSOR SETTINGS]\n"
				+	"SENSOR TYPE=" + sensor_type + "\n"
				+	"FORCE ON=" + force_on + "\n"
				+	"FORCE OFF" + force_off + "\n"
				+	"THRESHOLD=" + sensing_threshold + "%\n"
				+   "[USER SETTINGS]\n"
				+   "NAME=\"" + name + "\"\n"
				+   "COLOR=" + r + " " + g + " " + b + "\n"
				+   "DESKTOP NOTIFICATIONS=" + desktopNotification + "\n"
				+   "MAGIC MIRROR NOTIFICATION=" + magicMirrorNotification + "\n"
				+   "TEXT NOTIFICATION=" + textNotification + ", NUMBER=" + phoneNumber + "\n"
				+   "EMAIL NOTIFICATION=" + emailNotification + ", EMAIL ADDRESS=" + emailAddress + "\n"
				+	"NOTIFICATION NTERVAL=" + notificationInterval + " ms\n"
				+   "SENSOR INTERVAL=" + sensorInterval + " ms\n";
	}
	public void SetIP(String s) {
		ip = s;
	}
	
	public void SetName(String s) {
		name = s;
	}
	public void AddUser(String s) {
		users.add(s);
	}
}
