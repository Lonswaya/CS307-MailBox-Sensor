
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

import Example.AudioCapture;

public class AudioSensor extends BaseSensor
{
	//INSTANCE VARIABLES
	//AUDIO_CLIP_TYPE audioClip;
	private  float currentVolume;
	private float threshold;
	private boolean overThreshold = false;
	//private Capture listen = new Capture();
	
	String path = System.getProperty("user.home");
	static AudioFormat format;
	
	//constructor: takes the config as parameter and sets the threshold
	public AudioSensor(BaseConfig config)
	{
		super(config);
		format = new AudioFormat(44100.0f, 16, 2, true, false);
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

	static TargetDataLine line = null;
		
		//THIS FUNCTION CHECKS THE VOLUME AT THE CURRENT MOMENT
	public static float checkVolume(){
			
			//return AudioCapture.record();
			
			//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, );
			System.out.println("Please be able to read thxis");
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
			
			byte[] buf = new byte[2048];
	        float [] samples = new float[1024];
	        
	        float rms = 0f;
	        
	        line.start();
	        for(int b; (b = line.read(buf, 0, buf.length)) > -1;) {
	
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
	
	            rms = (float)Math.sqrt(rms / samples.length)*100; //percentage
	            line.stop();
	
	            //System.out.println("Amp: " + rms);
	            return rms;
	        }
	        line.stop();
			return rms;
				
		
	}
	//Check the volume && send sound while streaming
	public Message stream()
	{
		System.out.println("Start stream");
		//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f,true);
		//TargetDataLine microphone;	//Read sound into this
	    //SourceDataLine speakers;	//Play sound out of here
	    AudioMessage aMessage = new AudioMessage("Streaming audio Message", null);
		
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
		byte[] data = new byte[line.getBufferSize() / 5];
		line.start();
	
		int bytesRead = 0;
		//DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		//speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		//speakers.open(format);
		//speakers.start();
		 int frameSizeInBytes = format.getFrameSize();
		 int bufferLengthInFrames = line.getBufferSize() / 8;
		 int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		
		long begin = System.currentTimeMillis();
		double duration = 3.0f;
		
		while (true) {
		    //numBytesRead = line.read(data, 0, CHUNK_SIZE);
		    //bytesRead += numBytesRead;
		    // write the mic data to a stream for use later
		    //out.write(data, 0, numBytesRead); 
			
			long current = System.currentTimeMillis();
		        
		    long difference = (current - begin) / 1000;
		    try {
		    	numBytesRead = line.read(data, 0, bufferLengthInBytes);
		    	if (difference > duration) throw new Exception();
		    } catch (Exception e) {
		    	break;
		    }
				
		    out.write(data, 0, numBytesRead);
		    //Send this in the AudioMessage
		    byte[] soundRecorded = data;         
		    aMessage.setRecording(soundRecorded);
		} 
		System.out.println("End stream");
	
		return aMessage;
	}
		
	
	
	
	@Override
	public boolean check_threshold()
	{
		if(currentVolume > threshold) 
		{
			overThreshold = true;
		}
		
		overThreshold = false;
		
		return overThreshold;
	}
	
	public Message form_message()
	{
		System.out.println("Forming Volume message");
		
		ReadingMessage msg = new ReadingMessage("Volume above threshold", null);
		msg.setFrom(getIP());
		msg.setCurrentThreshold(this.currentVolume);
		return msg;
		//return null;
	} 
	//public AUDIO_CLIP_TYPE record_audio() {}

	@Override
	public void sense() {
		// TODO Auto-generated method stub
		//do nothing
		//System.out.println("Sensing audio stuff");
		currentVolume = checkVolume();
		
		
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	

	
}
