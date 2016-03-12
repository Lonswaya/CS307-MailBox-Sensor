
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AudioSensor extends BaseSensor
{
	//INSTANCE VARIABLES
	//AUDIO_CLIP_TYPE audioClip;
	private  float currentVolume;
	private float threshold;
	private boolean overThreshold = false;
	//private Capture listen = new Capture();
	
	String path = System.getProperty("user.home");
	
	//constructor: takes the config as parameter and sets the threshold
	public AudioSensor(BaseConfig config)
	{
		super(config);
		threshold = config.sensing_threshold*100; //*100 because we convert decimal to percentage. FIX LATER
	}
	
	//set how long it should sense at a time
	/*
	public void setRecordDuration(double newDur)
	{	
		listen.setDuration(newDur);
	}
	*/

	//THIS FUNCTION CHECKS THE VOLUME AT THE CURRENT MOMENT
public static float checkVolume(){
		
		//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, );
		AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
		TargetDataLine line = null;
		
		try{
			line = AudioSystem.getTargetDataLine(format);
			line.open(format, 2048);
		}catch(Exception e){
			System.out.println("Pllug in the microphone you prick");
			e.printStackTrace();
		}
		
		byte[] buf = new byte[2048];
        float[] samples = new float[1024];
        
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
            //System.out.println("Amp: " + rms);
            return rms;
        }
		return rms;
		
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
		msg.setFrom(this.getIP());
		msg.setCurrentThreshold(this.currentVolume);
		return msg;
		//return null;
	} 
	//public AUDIO_CLIP_TYPE record_audio() {}

	@Override
	public void sense() {
		// TODO Auto-generated method stub
		//do nothing
		currentVolume = AudioSensor.checkVolume();
		
		
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	

	
}
