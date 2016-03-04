
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.*;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioSensor extends BaseSensor
{
	//INSTANCE VARIABLES
	//AUDIO_CLIP_TYPE audioClip;
	private float last_decibel;
	private float threshold;
	private boolean overThreshold = false;
	//private Capture listen = new Capture();
	
	String path = System.getProperty("user.home");
	
	//constructor: takes the config as parameter and sets the threshold
	public AudioSensor(BaseConfig config)
	{
		super(config);
		threshold = config.sensing_threshold;
	}
	
	//set how long it should sense at a time
	/*
	public void setRecordDuration(double newDur)
	{	
		listen.setDuration(newDur);
	}
	*/
	
	public void senseAudio(double duration)
	{
		float max =Capture.recordSound(duration); 
		
		if (max > threshold)
		{
			overThreshold = true;
		}
		else
		{
			overThreshold = false;
		}
			
		
	}
		
	
	
	
	@Override
	public boolean check_threshold()
	{
		return overThreshold;
	
	}
	
	public Message form_message()
	{
		
		return null;
	} 
	//public AUDIO_CLIP_TYPE record_audio() {}

	@Override
	public void sense() {
		// TODO Auto-generated method stub
		//do nothing
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	

	
}
