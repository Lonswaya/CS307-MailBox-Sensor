import java.util.Calendar;

public class Notification {
    String name_pi;
    Sensor_type sensor_type;
    String time_created;
    
    public Notification(String name, Sensor_type type) {
        name_pi = name;
        sensor_type = type;
        Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
         
    }
}
