package Example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat.Type;
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
	
	static ArrayList<Byte> total_bytes = new ArrayList<Byte>();

	static TargetDataLine microphone;
	
	//Method that record chunks of audio and saves it to a wav file
	public static void recordChunks()
	{
		//TargetDataLine microphone;	//Read sound into this

		try{
		
			int numBytesRead = 0;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[microphone.getBufferSize()/5];
			
			//int bytesRead = 0;
			
				
				System.out.println("Going to start reading bytes");
				numBytesRead = microphone.read(data,  0,  CHUNK_SIZE);
				//bytesRead += numBytesRead;
				//write the microphone data to a stream for use later
				System.out.println("writing outputstream");
				out.write(data, 0, numBytesRead);
				//Add the bytes that were read to the massive byte array
				//massive = (byte[])ArrayUtils.addAll(massive, data);
			
			//microphone.close();
		    
			for (byte b : data)
				total_bytes.add(b);
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
		//byte[] audioBytes = new byte[total_bytes.size()];
		//for (int i = 0; i < total_bytes.size(); i++)
		//	audioBytes[i] = total_bytes.get(i).byteValue();
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
	
	public static void main(String [] args) throws LineUnavailableException
	{
		double duration = 6.0;
		long begin = System.currentTimeMillis();
		
		total_bytes.clear();
		
		microphone = AudioSystem.getTargetDataLine(format);
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		microphone = (TargetDataLine) AudioSystem.getLine(info);
		microphone.open(format);
		
		microphone.start();
		
		while(true)
		{
			long current = System.currentTimeMillis();
			long difference = (current-begin)/1000;
			
			
			
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
		
		microphone.stop();
		
		System.out.println("done recording, write to wav");
		writeToWAV();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
