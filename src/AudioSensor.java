
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
	private Capture listen = new Capture();
	
	String path = System.getProperty("user.home");
	
	//constructor: takes the config as parameter and sets the threshold
	public AudioSensor(BaseConfig config)
	{
		super(config);
		threshold = config.sensing_threshold;
	}
	
	//set how long it should sense at a time
	public void setRecordDuration(double newDur)
	{	
		listen.setDuration(newDur);
	}
	
	//get how long it records for at a time
	public double getRecordDuration()
	{
		return listen.getDuration();
	}
	
	//Records sound and saves it as a WAV file. default 5 seconds at a time.
	@Override
	public void sense()
	{
		listen.recordSound(); 
	}
	
	
	@Override
	public boolean check_threshold()
	{
		//get the wav file to check the stuff
		 
		
		return false;
	}
	
	public Message form_message()
	{
		return null;
	} 
	//public AUDIO_CLIP_TYPE record_audio() {}
}
