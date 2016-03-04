
public class WebcamSensorFactory implements SensorFactory {

	@Override
	public BaseSensor get_sensor(BaseConfig config) {
		if(config.sensor_type == SensorType.AUDIO)
			return new AudioSensor(config);
		else if(config.sensor_type == SensorType.LIGHT)
			return new LightSensor(config);
		//currently set to picture for simplicity
		else if(config.sensor_type == SensorType.VIDEO)
			return new PictureSensor(config);
		return null;
	}

}
