package Example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import javax.sound.sampled.AudioFileFormat.Type;

import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class RecordChunks
{
	//static InputStream in = null;
	//static AudioInputStream stream = null;
	//static int maxArraySize = 2147483647;
	//static byte[] massive = new byte[999999999];
	
	static ByteArrayOutputStream out = new ByteArrayOutputStream();
	static AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
	
	//Method that record chunks of audio and saves it to a wav file
	public static void recordChunks()
	{
		TargetDataLine microphone;	//Read sound into this
		
		try{
			microphone = AudioSystem.getTargetDataLine(format);
			
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			
			
			int numBytesRead = 0;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[microphone.getBufferSize()/5];
			microphone.start();
			
			int bytesRead = 0;
			
			while(bytesRead < 10000)
			{	
				System.out.println("Going to start reading bytes");
				numBytesRead = microphone.read(data,  0,  CHUNK_SIZE);
				bytesRead += numBytesRead;
				//write the microphone data to a stream for use later
				System.out.println("writing outputstream");
				out.write(data, 0, numBytesRead);
				//Add the bytes that were read to the massive byte array
				//massive = (byte[])ArrayUtils.addAll(massive, data);
			}
			microphone.close();
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}
	//The method that will write the massive byte array to a wav file.
	public static void writeToWAV()
	{
		//Convert the output stream to a byte array and make audiostream
		byte[] audioBytes = out.toByteArray();
		InputStream in = new ByteArrayInputStream(audioBytes);
	    AudioInputStream stream = new AudioInputStream(in, format, audioBytes.length);
	    
	    //Save to a wav file here
	    String path = System.getProperty("user.home");
	    System.out.println(path);
	    try{
	    	File file = new File (path + File.separator + "ChunkRecording.wav");
	    	AudioSystem.write(stream,  Type.WAVE, file);
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
		
	    
	}
	
	public static void main(String [] args)
	{
		long begin = System.currentTimeMillis();
		
		
		while(true)
		{
			long current = System.currentTimeMillis();
			long difference = (current-begin)/100;
			
			double duration = 6;
			
			if(difference > duration)
			{
				break;
			}
			else
			{	
				System.out.println("recording a chunk of audio");
				recordChunks();
			}
		}
		
		System.out.println("done recording, write to wav");
		writeToWAV();
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
