package Example;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class RecordChunks
{

	public static void recordChunks()
	{
		AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
		
		TargetDataLine microphone = null;	//Read sound into this
		byte[] out = new byte[1024];
		
		//Bunch of buffers and stuff copied from audiorecorder
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
	    int frameSizeInBytes = format.getFrameSize();
	    int bufferLengthInFrames = microphone.getBufferSize() / 8;
	    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
	    byte[] data = new byte[bufferLengthInBytes];
	    int numBytesRead;
		
		List<Byte> massive = new ArrayList<Byte>(99999999);
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	    
	    //Check to see if the format is supported
	    if(!AudioSystem.isLineSupported(info))
	    {
	        System.out.println("I'm sorry but " + info + " isn't supported. Bitch.");
	        System.exit(-1);
	    }
	    System.out.println(info + " is in fact supported.");
		
	    //Try to open the microphone line
		try
		{
			microphone = (TargetDataLine) AudioSystem.getLine(info);
		}
		catch(LineUnavailableException ex)
		{
			System.out.println(ex +"Couldn't open the Target data line");
			System.exit(-1);
		}
		catch(Exception e)
		{
			System.out.println(e + "happened for some reason");
			System.exit(-1);
		}
		
		long begin = System.currentTimeMillis();
		
		 while(true) {
		        
		        long current = System.currentTimeMillis();
		        long difference = (current - begin) / 1000;
		        
		        microphone.read(out, 0, 1024);
		        double duration = 10;
		        
		        if(difference > duration)
		        {
		            break;
		        }
		       Collections.addAll(massive,  out);
		        
		    }
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
