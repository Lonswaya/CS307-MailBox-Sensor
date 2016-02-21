import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamDriverUtils;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class DetectWebcamExample {

	
	public static void main(String[] args) {

		Webcam.setDriver(new V4l4jDriver());

		
		Webcam webcam = Webcam.getDefault();
		if (webcam != null) {
			System.out.println("Webcam: " + webcam.getName());
				
			webcam.open();
			
			System.out.println("Opened webcam");
						
			BufferedImage image = webcam.getImage();
			
			String path = System.getProperty("user.home");
			path += File.separator + "image.png";
			
			System.out.println("Took picture");
			
			try {
				ImageIO.write(image, "PNG", new File(path));		
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			System.out.println("No webcam detected");
		}
	}
}
