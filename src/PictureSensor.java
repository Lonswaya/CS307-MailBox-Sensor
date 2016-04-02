import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamDriverUtils;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class PictureSensor extends BaseSensor{

	private BufferedImage image;
	private Webcam webcam = null;
	
	public PictureSensor(BaseConfig config) {
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
		//Probably not needed since picture sensor purely send pictures
	}

	
	@Override
	public boolean check_threshold() {
		return false;
	}

	@Override
	public Message form_message() {
		System.out.println("Debug: Start forming picture");

		if(!webcam.isOpen())
			webcam.open();
		
		System.out.println("Debug: Taking a picture");
		long tempTime = System.currentTimeMillis();
		this.image = webcam.getImage();
		System.out.println("Debug: End taking a picture, Time:" + (int) (System.currentTimeMillis() - tempTime) + "\n");
		//webcam.close();
		
		PictureMessage msg = new PictureMessage("", null); //message will be set from setImage()
		msg.setImage(this.image, false);						   //in picture message class
		msg.setFrom(this.ip);
		System.out.println("Debug: End taking a picture");
		return msg;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (this.webcam.isOpen()) this.webcam.close();

	}

}
