import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamDriverUtils;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class LightSensor extends BaseSensor {

	private float light_intensity;
	private BufferedImage image;
	private Webcam webcam = null;
	
	public LightSensor(BaseConfig config){
		super(config);
		Webcam.setDriver(new V4l4jDriver());
		webcam = Webcam.getDefault();	
		if (webcam != null) {
			if(isSensorActive())
				webcam.open();
		} else {
			System.out.println("Errors opening da camera");
			System.exit(1);
		}	
	}
	
	@Override
	public void sense() {
		take_picture();
		this.light_intensity = get_reading(this.image);
	}
	
	public Message form_message(){		
		ReadingMessage msg = new ReadingMessage("Light above threshold", null);
		try {
			msg.setFrom(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		msg.setCurrentThreshold(this.light_intensity);
		return msg;
	}
	
	public void take_picture(){
		
		if(isSensorActive()) {
			if(!webcam.isOpen())
				webcam.open();
			this.image = webcam.getImage();
		} else {
			if(webcam.isOpen())
				webcam.close();
		}
	}
	
	//turn image into greyscale
	public BufferedImage greyscale_picture(BufferedImage image){
		//BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Color c;
		int avg;
		
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				c = new Color(image.getRGB(x, y));
				avg = (c.getBlue() + c.getGreen() + c.getRed()) / 3;
				//System.out.println(avg);
				c = new Color(avg, avg, avg);
				image.setRGB(x, y, c.getRGB());
			}
		}
		return image;
	}
	
	public float get_reading(BufferedImage image){
		Color color = new Color(image.getRGB(image.getWidth() / 2, image.getHeight() / 2));
		//System.out.println("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
		return color.getBlue() / (this.upperBound - this.lowerBound);
	}

	@Override
	public boolean check_threshold() {
		if(this.light_intensity > config.sensing_threshold)
			return true;
		return false;
	}
}
