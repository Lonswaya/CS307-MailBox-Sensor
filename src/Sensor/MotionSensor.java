package Sensor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

import Utilities.Connections;
import cs307.purdue.edu.autoawareapp.BaseConfig;
import cs307.purdue.edu.autoawareapp.BaseSensor;
import cs307.purdue.edu.autoawareapp.Message;
import cs307.purdue.edu.autoawareapp.ReadingMessage;


/**
 * Detect motion.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MotionSensor extends BaseSensor implements WebcamMotionListener {

	private static final long serialVersionUID = -585739158170333370L;

	private static final int INTERVAL = 200; // ms
	static int threshold = 35;
	public Webcam webcam;
	WebcamMotionDetector detector;
	private RaspberryPi pi;

	public MotionSensor(BaseConfig config, RaspberryPi pi) {
		super(config);
		this.pi = pi;
		Webcam.setDriver(new V4l4jDriver());
		webcam = Webcam.getDefault();
		ready = true;
		if (webcam == null)
			ready = false;
		webcam.setViewSize(new Dimension(320, 240));
		detector = new WebcamMotionDetector(webcam, MotionSensor.threshold);
		if (detector == null)
			ready = false;
		detector.setInterval(MotionSensor.INTERVAL);
		detector.addMotionListener(this);
		detector.start();		//sets it to start detecting
		System.out.println("Done building motion sensor");
	}

	@Override
	public void motionDetected(WebcamMotionEvent arg0) {
		System.out.println("Oh boy, motion detected");

		// called when motion is detected
		if (this.isSensorActive()) {
			Message msg = form_message(null);
			msg.setFrom(pi.assignedIPAddress);
			pi.send_message(msg);
		}
	}

	@Override
	public BufferedImage sense() {
		System.out.println("Motion sense");

		return null;
	}

	@Override
	public boolean check_threshold() {
		// no need
		return false;
	}

	@Override
	public Message form_message(BufferedImage sensedObject) {
		// no need4
		ReadingMessage rm = new ReadingMessage("above", null);
		rm.setCurrentThreshold(100.0f);
		return rm;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		detector.stop();
		webcam.close();
	}

}