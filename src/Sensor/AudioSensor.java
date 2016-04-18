package Sensor;
import cs307.purdue.edu.autoawareapp.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


import Utilities.CircularBuffer;

public class AudioSensor extends BaseSensor
{
	//INSTANCE VARIABLES
	//AUDIO_CLIP_TYPE audioClip;
	private  float currentVolume;
	private float threshold;
	private boolean overThreshold = false;
	private byte[] audioBytes;
	
	protected boolean doneStreaming;
	protected boolean doneVoluming;
	static CircularBuffer cBuffer = new CircularBuffer();
	//private Capture listen = new Capture();
	
	String path = System.getProperty("user.home");
	static AudioFormat format;
	static TargetDataLine line = null;
	
	//constructor: takes the config as parameter and sets the threshold
	public AudioSensor(BaseConfig config)
	{
		
		super(config);
		doneStreaming = true;
		doneVoluming = true;
		format = new AudioFormat(44100.0f, 16, 2, true, true);
		threshold = config.sensing_threshold*100; //*100 because we convert decimal to percentage. FIX LATER
		
		//TODO if sensor cannot be made (lack of microphone) ready = false;
	}
	
	//set how long it should sense at a time
	/*
	public void setRecordDuration(double newDur)
	{	
		listen.setDuration(newDur);
	}
	*/

	
		
		//THIS FUNCTION CHECKS THE VOLUME AT THE CURRENT MOMENT
	public float checkVolume(){
			
			//return AudioCapture.record();
			
			//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, );
			//System.out.println("Please be able to read thxis");
			//TargetDataLine line = null;
			try{
				if (line == null) {
					line = AudioSystem.getTargetDataLine(format);
					line.open(format, 2048);
				}
			}catch(Exception e){
				System.out.println("Plug in the microphone you prick");
				e.printStackTrace();
			}
	
			//DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead = 0;
			int CHUNK_SIZE = 1024;
			//byte[] data = new byte[line.getBufferSize() / 5];
			
			line.start();
			
			byte[] buf = new byte[2048];
	        float [] samples = new float[1024];
	        
	        float rms = 0f;
	        
	        int b = line.read(buf, 0, buf.length);
	
	        // convert bytes to samples here
	        for(int i = 0, s = 0; i < b;) {
	        	int sample = 0;
	
	            sample |= buf[i++] & 0xFF; // (reverse these two lines
	            sample |= buf[i++] << 8;   //  if the format is big endian)
	
	            // normalize to range of +/-1.0f
	            samples[s++] = sample / 32768f;
	        }
	
	        //float rms = 0f;
	        float peak = 0f;
	        for(float sample : samples) {
	
	            float abs = Math.abs(sample);
	            if(abs > peak) {
	                 peak = abs;
	            }
	
	            rms += sample * sample;
	        }
	            
	        double blah = (double)(rms / samples.length)*100; //percentage
	        System.out.println(blah);
	        rms = (float)blah;
	        //rms = (float)Math.log((double)((blah/100)+1)); //voodo math shit
	        //rms*=2;
	        //System.out.println(rms);
	        if (rms > 100) rms = 100;
	        if (rms < 0) rms = 0;
	        line.stop();
	        audioBytes = buf;
	        //System.out.println("Amp: " + rms);	        
	        line.stop();
			return rms;
				
		
	}
	//Reads in audio through the Target Dataline, then converts it to a byte[]
	public synchronized void stream()
	{
		doneStreaming = false;
		//System.out.println("Start stream");
		//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f,true);
		//TargetDataLine microphone;	//Read sound into this
	    //SourceDataLine speakers;	//Play sound out of here
		
	    
		
		 
	
		int bytesRead = 0;
		//DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		//speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		//speakers.open(format);
		//speakers.start();
		 //int frameSizeInBytes = format.getFrameSize();
		 //int bufferLengthInFrames = line.getBufferSize() / 8;
		//int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		
		//long begin = System.currentTimeMillis();
		//double duration = 3.0f;
		//System.out.println("2");
		//while (bytesRead < 100000) {
		if(line != null)
		{
		  	line.read(audioBytes, 0, 1024);
		}
		else
		{
		   	System.out.println("line is null");
		}
		    //System.out.println("3 " + numBytesRead);
		    //bytesRead += numBytesRead;
		    // write the mic data to a stream for use later
		    //out.write(audioBytes, 0, numBytesRead); 
			
			//long current = System.currentTimeMillis();
		        
		    //long difference = (current - begin) / 1000;
		    
				
		    //out.write(data, 0, numBytesRead);
		    //Send this in the AudioMessage         
		//} 
		//System.out.println("4");
		//Close the line or nah?
		line.close();
		//System.out.println("End stream");
	
		doneStreaming = true;
	}
		
	
	
	
	@Override
	public boolean check_threshold()
	{
		float average = cBuffer.average();
		if(average > threshold) 
		{
			overThreshold = true;
		}
		
		overThreshold = false;
		
		return overThreshold;
	}
	
	public Message form_message(BufferedImage b)
	{
		//System.out.println("Forming Audio message");
		
		AudioMessage msg = new AudioMessage("Volume above threshold", null);
		boolean messageHasValues = false;
		if (this.currentVolume >= 0) {
			msg.setCurrentThreshold(currentVolume);
			messageHasValues = true;
			currentVolume = -1;
		}
		if (audioBytes != null && doneStreaming) {
			msg.setRecording(audioBytes);
			messageHasValues = true;
			//Probably have to empty audioBytes so it can be used again
			//audioBytes = null;
		}
		
		if (!messageHasValues) { //if there is nothing in here, why send it
			return null;
		}
		
		return msg;
		//return null;
	} 
	//public AUDIO_CLIP_TYPE record_audio() {}

	@Override
	public BufferedImage sense() {
		// TODO Auto-generated method stub
		//do nothing
		//System.out.println("Sensing audio stuff");
		if (doneVoluming) {
			doneVoluming = false;
			//Add to Circular buffer
			currentVolume = checkVolume();
			cBuffer.insert(currentVolume);
			
			doneVoluming = true;
		}
		return null;
		
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}


	

	
}
