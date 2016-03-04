import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Enumeration;

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
		System.out.println("light camrea created");
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
		System.out.println("forming light message");
		ReadingMessage msg = new ReadingMessage("Light above threshold", null);
		try {
			NetworkInterface ni = NetworkInterface.getByName("eth0");
	        Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
	        String address = "";
	        while(inetAddresses.hasMoreElements()) {
	            InetAddress ia = inetAddresses.nextElement();
	            if(!ia.isLinkLocalAddress()) {
	                address = ia.getHostAddress();
	            }
	        }
	        
			//String address = InetAddress.getLocalHost().toString();
			//address = address.substring(address.indexOf('/') + 1);
			msg.setFrom(address);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		msg.setCurrentThreshold(this.light_intensity);
		return msg;
	}
	
	public void take_picture(){
		System.out.println("picture taken");
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
		double intensity = 0;
		Color color;
		
		if(image.getHeight() < 5 || image.getWidth() < 5){
			color = new Color(image.getRGB(image.getWidth() / 2, image.getHeight() / 2));
			intensity = color.getBlue();
		}else{
			/*int wDistance = image.getWidth() / 5;
			int hDistance = image.getHeight() / 5;
			
			int lastH = 0, lastW = 0;
			
			for(int i = hDistance; times != 0; i += hDistance, times--){
				for(int j = wDistance; times != 0; j += wDistance, times--){
					
				}
			}*/
			
			for(int x = 0; x < image.getWidth(); x++){
				for(int y = 0; y < image.getHeight(); y++){
					color = new Color(image.getRGB(x, y));
					intensity += color.getBlue();
				}
			}
			intensity /= image.getHeight() * image.getWidth();
		}
		return (float) (intensity / (this.upperBound - this.lowerBound) * 100);
	}

	@Override
	public boolean check_threshold() {
		if(this.light_intensity > config.sensing_threshold)
			return true;
		return false;
	}
	
	/*public static void main (String[] args) throws Exception{
		
		LightSensor s = new LightSensor(new BaseConfig());
		BufferedImage img = ImageIO.read(new File("C:/Users/Zixuan/Desktop/CS307/winter.png"));
		img = s.greyscale_picture(img);
		Color c = new Color(img.getRGB(img.getHeight() / 2, img.getWidth() / 2));
		if(c.getRed() == c.getBlue() && c.getGreen() == c.getBlue()){
			System.out.println("Image greyscaled");
		}else{
			System.out.println("Image didn't get greyscaled");
		}
		
		ImageIO.write(img, "jpg", new File("newpic.jpg"));
		
		System.out.println(s.get_reading(img));
	}*/
}
