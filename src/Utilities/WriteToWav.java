package Utilities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WriteToWav
{
	static AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
	
	//The method this whole class is for
	public static void writeToWav(String path, byte[] audioBytes)
	{
		InputStream in = new ByteArrayInputStream(audioBytes);
		AudioInputStream stream = new AudioInputStream(in, format, audioBytes.length);
		
		try
		{
			File file = new File(path);
			AudioSystem.write(stream, Type.WAVE, file);
		}
		catch(Exception e)
		{
			System.out.println("Problem writing to WAV file");
			e.printStackTrace();
		}
		
	}
	
}
