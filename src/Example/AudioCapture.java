package Example;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioCapture {
	public static void record(){
		
		//AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, );
		AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
		TargetDataLine line = null;
		
		try{
			line = AudioSystem.getTargetDataLine(format);
			line.open(format, 2048);
		}catch(Exception e){
			System.out.println("line error");
			e.printStackTrace();
		}
		
		byte[] buf = new byte[2048];
        float[] samples = new float[1024];

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

            float rms = 0f;
            float peak = 0f;
            for(float sample : samples) {

                float abs = Math.abs(sample);
                if(abs > peak) {
                    peak = abs;
                }

                rms += sample * sample;
            }

            rms = (float)Math.sqrt(rms / samples.length)*100; //percentage
            if(rms > 40)
            {
            System.out.println("Amp: " + rms);
            }
            else
            {
            	System.out.println("low sound");
            }
        }
	}
	
	public static void main(String[] args){
		System.out.println("recording");
		//while(true) record(); //no exit
		record();
	}
}