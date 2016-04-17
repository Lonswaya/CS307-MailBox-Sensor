package Example;

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
		
		TargetDataLine microphone;	//Read sound into this
		
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
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
