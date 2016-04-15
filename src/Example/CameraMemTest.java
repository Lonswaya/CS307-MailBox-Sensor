package Example;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class CameraMemTest {
	public static void main(String[] args) {
		Webcam.setDriver(new V4l4jDriver());
		Webcam webcam = Webcam.getDefault();	
		if (webcam != null) 
				webcam.open();
		while(true) {
			BufferedImage myImg = webcam.getImage();
		}
	}
}
