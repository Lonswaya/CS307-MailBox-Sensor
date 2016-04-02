import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
//Records the audio. #Exactlywhatitsaysonthetin
public class AudioRecorder {
	
	public static float recordAudio (double duration)
	{
		//Initialize a bunch of variables
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 44100.0f;
		int channels = 2;
		//int frameSize = 4;
		int sampleSize = 16;
		boolean bigEndian = true;
    
    TargetDataLine line;
    //Set the format for recording the audio
    AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize/8)*channels,rate,bigEndian);
    
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    
    //Check to see if the format is supported
    if(!AudioSystem.isLineSupported(info))
    {
        System.out.println("I'm sorry but " + info + " isn't supported. Bitch.");
        return -2.0f;
    }
    System.out.println(info + " is in fact supported.");
	
    //Open up the Line and shit
    try 
    {
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format, line.getBufferSize());
    }
    catch (LineUnavailableException ex) 
    {
        System.out.println(ex + " wasn't able to open.");
        return -2.0f;
    } 
    catch (Exception e) 
    {
        System.out.println(e + " happened. Don't let it happen again.");
        return -2.0f;
    }
    
    //Create a bunch of buffers and stuff
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int frameSizeInBytes = format.getFrameSize();
    int bufferLengthInFrames = line.getBufferSize() / 8;
    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
    byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead;
    
    line.start();
    
    //Get the current system time
    long begin = System.currentTimeMillis();
  
    //The Loop where the actual Recording happens
    while(true) {
        
        long current = System.currentTimeMillis();
        
        long difference = (current - begin) / 1000;
        
        
        if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1 || difference > duration)
        {
            break;
        }
        out.write(data, 0, numBytesRead);
    }
    
    //Close everything
    line.stop();
    line.close();
    line = null;
    
    try {
        out.flush();
        out.close();
    } catch (Exception e) {
        e.printStackTrace();
        return -2.0f;
    }
    
    System.out.println(duration +"s of audio was recorded.");
    
    //Some random shit
    byte[] audioBytes = out.toByteArray();
    
    InputStream in = new ByteArrayInputStream(audioBytes);
    AudioInputStream stream = new AudioInputStream(in, format, audioBytes.length);
    //DataOutputStream dos = new DataOutputStream(new FileOutputStream("C:\\dicks.bin"));
     //dos.write(audioBytes);
    
    //Save to a wav file here
    String path = System.getProperty("user.home");
    System.out.println(path);
    try{
    File file = new File(path + File.separator + "output.wav");
    AudioSystem.write(stream, Type.WAVE, file);
    }
    catch (Exception e){
    	e.printStackTrace();
    }
    
    
    return 0;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AudioRecorder.recordAudio(10);
	}

}
