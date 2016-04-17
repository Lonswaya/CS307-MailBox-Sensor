package Example;

import java.awt.Dimension;
import java.awt.FlowLayout;
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


/**
 * Detect motion.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MotionSensorExample implements WebcamMotionListener {

	private static final long serialVersionUID = -585739158170333370L;

	private static final int INTERVAL = 100; // ms


	public Webcam webcam;

	public MotionSensorExample() {
		//Webcam.setDriver(new V4l4jDriver());
		webcam = Webcam.getDefault();
		webcam.setViewSize(new Dimension(320, 240));
	}

	public static void main(String[] args) throws InterruptedException {
		MotionSensorExample ms = new MotionSensorExample();

		int threshold = 25;
		int inertia = 1000; // how long motion is valid

		WebcamMotionDetector detector = new WebcamMotionDetector(ms.webcam, 35);
		detector.setInterval(INTERVAL * 2);
		detector.addMotionListener(ms);
		detector.start();

		new Scanner(System.in).next();		
	}
	
	static int i = 0;

	@Override
	public void motionDetected(WebcamMotionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println(i++);
	}

}