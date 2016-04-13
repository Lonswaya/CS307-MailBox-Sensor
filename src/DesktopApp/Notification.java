package DesktopApp;
import java.util.Calendar;
import cs307.purdue.edu.autoawareapp.*;

public class Notification {
    String name_pi;
    SensorType sensor_type;
    String time_created;
    
    public Notification(String name, SensorType type) {
        name_pi = name;
        sensor_type = type;
        Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
         
    }
}
