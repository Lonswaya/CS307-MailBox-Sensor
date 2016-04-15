package Sensor;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.WebcamDriverUtils;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import cs307.purdue.edu.autoawareapp.*;
public class PictureSensor extends BaseSensor{

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
			ready = false;
		}
	}
	@Override
	public synchronized BufferedImage sense() {
		//System.out.println("Debug: Start forming picture");
		try {	
			if(!webcam.isOpen())
				webcam.open();
			
			//System.out.println("Debug: Taking a picture");
			//long tempTime = System.currentTimeMillis();
			//BufferedImage tempImg = this.image != null ? this.image : null;
			//this.image = webcam.getImage();
			/*if (tempImg != null) {
				tempImg.flush();
				tempImg = null;
			}*/
			
			BufferedImage bf = webcam.getImage();
			
			return bf;
			//System.out.println("Debug: End taking a picture, Time:" + (int) (System.currentTimeMillis() - tempTime) + "\n");
			//webcam.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}

	
	@Override
	public boolean check_threshold() {
		return false;
	}

	@Override
	public Message form_message(BufferedImage o) {
		PictureMessage msg = new PictureMessage("Picture Message", null); //message will be set from setImage()
		msg.setImage((BufferedImage)o, false);
		System.out.println("Debug: End taking a picture");
		return msg;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (this.webcam.isOpen()) this.webcam.close();

	}
	
	/*
	 * If the camera is locked (in use), return true
	 * Otherwise, if the camera is ready to go, return false
	 */
	public boolean IsLocked() {
		return (!this.ready || this.webcam.getLock().isLocked());
		
	}
	
	

}
