
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

import java.util.Scanner;

public class Capture 
{
    private double recordDuration;
    
    TargetDataLine line;
    
    public Capture(double dur)
    {
        this.recordDuration = dur;
    }
    
    public Capture()
    {
    	this.recordDuration = 5;
    }
    
    public void setDuration(double newDur)
    {
    	this.recordDuration = newDur;
    }
    
    public double getDuration()
    {
    	return this.recordDuration;
    }
    
    public void recordSound() 
    {
        
        //double duration = 0;
        //AudioInputStream audioInputStream = null;
        
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        //int frameSize = 4;
        int sampleSize = 16;
        boolean bigEndian = true;
        
        AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize/8)*channels,rate,bigEndian);
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
        if(!AudioSystem.isLineSupported(info))
        {
            System.out.println("I'm sorry but " + info + " isn't supported.");
            return;
        }
        System.out.println(info + " is in fact supported.");
        
        try 
        {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        }
        catch (LineUnavailableException ex) 
        {
            System.out.println(ex + " wasn't able to open.");
            return;
        } 
        catch (Exception e) 
        {
            System.out.println(e + " happened. Don't let it happen again.");
            return;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;
        
        line.start();
        
        long begin = System.currentTimeMillis();
        
        while(true) {
            
            long current = System.currentTimeMillis();
            
            long difference = (current - begin) / 1000;
            
            
            if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1 || difference > this.recordDuration)
            {
                break;
            }
            out.write(data, 0, numBytesRead);
        }
        
        line.stop();
        line.close();
        line = null;
        
        try {
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        System.out.println(recordDuration +"s of audio was recorded.");
        
        
        
        byte[] audioBytes = out.toByteArray();
        
        InputStream in = new ByteArrayInputStream(audioBytes);
        try {
            //DataOutputStream dos = new DataOutputStream(new FileOutputStream("C:\\dicks.bin"));
            //dos.write(audioBytes);
            AudioInputStream stream = new AudioInputStream(in, format, audioBytes.length);
            
            String path = System.getProperty("user.home");
            
            
            
            System.out.println(path);
            
            File file = new File(path + File.separator + "output.wav");
            AudioSystem.write(stream, Type.WAVE, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        /*
         ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
         audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
         
         long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
         .getFrameRate());
         duration = milliseconds / 1000.0;
         
         try {
         audioInputStream.reset();
         } catch (Exception ex) {
         ex.printStackTrace();
         return;
         }
         */
        
        
    }
    
    public static void main(String[] args) {
        
        Scanner in = new Scanner(System.in);
        
        double duration = in.nextDouble();
        in.close();
        Capture cap = new Capture(duration);
        cap.recordSound();
    }
    
    
}
