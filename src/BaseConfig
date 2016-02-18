import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

public class BaseConfig {

	protected int start_hours; //in 24H format
	protected int start_minutes; 
	protected int stop_hours; //in 24H format
	protected int stop_minutes;
	protected boolean is_sensing; 
	protected SensorType sensor_type;
	protected float sensing_threshold; //percentage?
	
	//creates a new config based on the parameters. Start and stop are the times to start and stop sensing in 24H format "HH:MM"
	public BaseConfig(String start, String stop, boolean sensing, SensorType type, float threshold) {
		
		String[] starting = start.split(":");
		if(starting.length < 2) {
			//ERROR
		}
		this.start_hours = Integer.parseInt(starting[0]);
		this.start_minutes = Integer.parseInt(starting[1]);
		
		String[] stoping = stop.split(":");
		if(stoping.length < 2) {
			//ERROR
		}
		this.stop_hours = Integer.parseInt(stoping[0]);
		this.stop_minutes = Integer.parseInt(stoping[1]);
		this.is_sensing = sensing;
		this.sensor_type = type;
		this.sensing_threshold = threshold;
	}
	
	//creates a new, garbage config
	public BaseConfig() {
		long current = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(current);
		this.start_hours = cal.get(Calendar.HOUR_OF_DAY);
		this.start_minutes = cal.get(Calendar.MINUTE);
		this.stop_hours = this.start_hours;
		this.stop_minutes = this.start_minutes;
		this.is_sensing = false;
		this.sensor_type = null;
		this.sensing_threshold = 101.0f;
	}
	
	//loads the config file from a specified path
	public BaseConfig(Path to_load) throws IOException {
		Load(to_load);
	}
	
	//writes the BaseConfig to disk in ~/config.txt
	public void Save() throws IOException {
		
		String home_directory = System.getProperty("user.home");
		Path file = Paths.get(home_directory, "config.txt"); //generate the path to the file
		Save(file);
	}
	
	//writes the BaseConfig to disk at the path specified
	public void Save(Path to_save) throws IOException {
				
		//format our file
		List<String> to_write = Arrays.asList("[TIME SETTINGS]","START TIME=" + start_hours + ":" + start_minutes, 
				"STOP TIME=" + stop_hours + ":" + stop_minutes, "[SENSOR SETTINGS]", "SENSOR TYPE=" + sensor_type,
				"CURRENTLY SENSING=" + is_sensing, "THRESHOLD=" + sensing_threshold);
		
		//if the directory to store it in doesn't exist make it
		if(!Files.exists(to_save.getParent()))
			Files.createDirectories(to_save.getParent());

		Files.write(to_save, to_write, Charset.forName("UTF-8")); //could throw IOException
	}
	
	//loads the config assuming it is stored in ~/config.txt
	public void Load() throws IOException {
		String home_directory = System.getProperty("user.home");
		Path file = Paths.get(home_directory, "config.txt"); //generate the path to the file
		Load(file);
	}
	
	//loads the config from the specified path
	public void Load(Path to_load) throws IOException {

		List<String> lines = Files.readAllLines(to_load, Charset.forName("UTF-8"));
		for(String line : lines)
			handle_line(line);		
	}
	
	//parses the line to populate protected fields
	private void handle_line(String line) {
		String[] split = line.split("="); //only get lines with variables
		if(split.length < 2)
			return;
							
		//switch onto what is being read
		switch(split[0]) {
			case "START TIME":
				String[] start_times = split[1].split(":");
				if(start_times.length < 2)
					break; //error
				this.start_hours = Integer.parseInt(start_times[0]);
				this.start_minutes = Integer.parseInt(start_times[1]);
				break;
			case "STOP TIME":
				String[] stop_times = split[1].split(":");
				if(stop_times.length < 2)
					break; //error
				this.stop_hours = Integer.parseInt(stop_times[0]);
				this.stop_minutes = Integer.parseInt(stop_times[1]);
				break;
			case "SENSOR TYPE":
				handle_sensor_type(split[1]); //minimize nested switches/ifs with private methods
				break;
			case "CURRENTLY SENSING":
				if(split[1].equalsIgnoreCase("yes"))
					this.is_sensing = true;
				else
					this.is_sensing = false;
				break;
			case "THRESHOLD":
				this.sensing_threshold = Float.parseFloat(split[1]);
				break;
			default:
				break;
		}
	}
	
	//helper function to parse the type of sensor in config
	private void handle_sensor_type(String text) {
		
		switch(text.toLowerCase()) {
			case "video":
				this.sensor_type = SensorType.VIDEO;
				break;
			case "audio":
				this.sensor_type = SensorType.AUDIO;
				break;
			case "light":
				this.sensor_type = SensorType.LIGHT;
				break;
			default:
				break;
		}
	}
	
	
	//great for debugging
	@Override 
	public String toString() {
		return		"[TIME SETTINGS]\n"
				+	"START TIME=" + start_hours + ":" + start_minutes + "\n"
				+	"STOP TIME=" + stop_hours + ":" + stop_minutes + "\n"
				+	"[SENSOR SETTINGS]\n"
				+	"SENSOR TYPE=" + sensor_type + "\n"
				+	"CURRENTLY SENSING=" + is_sensing + "\n"
				+	"THRESHOLD=" + sensing_threshold;
	}
	
	
	public static void main(String[] args) {
		
		
		
		/*
		 * 
		 * Example of reading/writing config files with base path
		 * 
		BaseConfig saving = new BaseConfig("10:30", "23:45", false, SensorType.VIDEO, 50.0f);
		
		try {
			saving.Save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BaseConfig loading = new BaseConfig();
		try {
			loading.Load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Saving: " + saving);
		System.out.println("Loading: " + loading);
		*/
		
		
		
		/*
		 * Example writing config files with a specific path
		 * 
		String home_dir = System.getProperty("user.home");
		Path path = Paths.get(home_dir, "config", "config.txt");
	
		
		
		BaseConfig saving = new BaseConfig("10:00", "23:00", false, SensorType.VIDEO, 50.0f);
		try {
			saving.Save(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BaseConfig loading = null;
		
		try {
			loading = new BaseConfig(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Saving: " + saving);
		System.out.println("Loading: " + loading);
		*/
	}
}
