import java.awt.Color;
import java.io.Serializable;
import java.util.Calendar;


public class ClientConfig extends BaseConfig implements Serializable {
	protected String name;
	protected Color color;
	protected boolean desktopNotification, magicMirrorNotification, textNotification, emailNotification;
	protected String phoneNumber, emailAddress, ip;
	
	
	public ClientConfig(String ip, String start, String stop, boolean force_on, boolean force_off, SensorType type, float threshold, String name, Color color, boolean desktopNotification, boolean magicMirrorNotification, boolean textNotification, boolean emailNotification, String phoneNumber, String emailAddress) {
		super(start, stop, force_on, force_off , type, threshold);
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
				+	"FORCE ON=" + force_on + "\n"
				+	"FORCE OFF" + force_off + "\n"
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
	public boolean isSensorActive() {
		//Checks the time constraints on the client, and returns true if it should be on at the moment
		if(force_on)
			return true;
		if(force_off)
			return false;
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		int startH = this.start_hours;
		int startM = this.start_minutes;
		int stopH = this.stop_hours;
		int stopM = this.stop_minutes;

		if(hour > start_hours && hour < stop_hours || 
		  (hour == startH && minute >= startM) || 
		  (hour == stopH && minute < stopM) ||
		  (startH > stopH && (hour > startH || hour < stopH)) ||
		  hour > startH && hour < stopH) {
			return true;
		}
		return false;
	}
}
