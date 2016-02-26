import java.awt.Color;


public class ClientConfig extends BaseConfig {
	protected String name;
	protected Color color;
	protected boolean desktopNotification, magicMirrorNotification, textNotification, emailNotification;
	protected String phoneNumber, emailAddress, ip;
	
	
	public ClientConfig(String ip, String start, String stop, boolean sensing, SensorType type, float threshold, String name, Color color, boolean desktopNotification, boolean magicMirrorNotification, boolean textNotification, boolean emailNotification, String phoneNumber, String emailAddress) {
		super(start, stop, sensing, type, threshold);
		this.ip = ip;
		this.color = color;
		this.name = name;
		this.desktopNotification = desktopNotification;
		this.magicMirrorNotification = magicMirrorNotification;
		this.textNotification=textNotification; 
		this.emailNotification = emailNotification;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}
	public ClientConfig() {
		super();
		this.color = Color.white;
		this.name = "";
		this.phoneNumber = "";
		this.emailAddress = "";
	}
	@Override 
	public String toString() {
		return		"[TIME SETTINGS]\n"
				+	"START TIME=" + start_hours + ":" + start_minutes + "\n"
				+	"STOP TIME=" + stop_hours + ":" + stop_minutes + "\n"
				+	"[SENSOR SETTINGS]\n"
				+	"SENSOR TYPE=" + sensor_type + "\n"
				+	"CURRENTLY SENSING=" + is_sensing + "\n"
				+	"THRESHOLD=" + sensing_threshold + "%\n"
				+   "[USER SETTINGS]\n"
				+   "NAME=\"" + name + "\"\n"
				+   "COLOR=" + color + "\n"
				+   "DESKTOP NOTIFICATIONS=" + desktopNotification + "\n"
				+   "MAGIC MIRROR NOTIFICATION=" + magicMirrorNotification + "\n"
				+   "TEXT NOTIFICATION=" + textNotification + ", NUMBER=" + phoneNumber + "\n"
				+   "EMAIL NOTIFICATION=" + emailNotification + ", EMAIL ADDRESS=" + emailAddress + "\n";
	}
	public void SetIP(String s) {
		ip = s;
	}
	public void SetColor(Color c) {
		color = c;
	}
	public void SetName(String s) {
		name = s;
	}
	
}
