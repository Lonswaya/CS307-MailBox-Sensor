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
		
	}

	
	@Override
	public boolean check_threshold() {
		return false;
	}

	@Override
	public Message form_message() {
		
		if(!webcam.isOpen())
			webcam.open();
		this.image = webcam.getImage();
		webcam.close();
		
		PictureMessage msg = new PictureMessage("Sending a picture from picture sensor", null);
		msg.setImage(this.image);
		try{
			NetworkInterface ni = NetworkInterface.getByName("eth0");
	        Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
	        String address = "";
	        while(inetAddresses.hasMoreElements()) {
	            InetAddress ia = inetAddresses.nextElement();
	            if(!ia.isLinkLocalAddress()) {
	                address = ia.getHostAddress();
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
        
		//String address = InetAddress.getLocalHost().toString();
		//address = address.substring(address.indexOf('/') + 1);
		msg.setFrom(address);
		
		return null;
	}

}
