package DesktopApp;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;
import cs307.purdue.edu.autoawareapp.*;


public class ValueStreamBox extends JPanel {
	private static final long serialVersionUID = -1L;
	/* Screen that will show the value of the sensor, and its threshold
	 * Updates over time
	 * Shown in a linear fashion
	 * 
	 */
	public String address;
	private boolean playingSound;
	private Queue<byte[]> audioQueue;
	
	public float value;
	private float threshold;
	private SensorType sensorType;
	private Thread playerThread;
	private JButton audioToggle;
	private Timer t;
	//private CentralServer server;
	
	SourceDataLine speakers;	//Play sound out of here
		
	private int[] pointsX1, pointsX2, pointsY1, pointsY2, thresholdPointX, thresholdPointY;
	public ValueStreamBox(SensorType sensorType, float threshold, String address) {
		super();
		//this.server = server;
		this.address = address;
		t  = new Timer(1000, new repainter());
		//t.start();
		JButton exit = new JButton("Exit");
		final JPanel tempThis = this;
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	SwingUtilities.getWindowAncestor(tempThis).dispose();
            }
        });
		exit.setBounds(260, 520, 80, 30);
		JLabel title = new JLabel("Current " + sensorType + " value");
		title.setFont(new Font(title.getName(), Font.BOLD, 20));
		System.out.println(title.getBounds());
		title.setBounds(200, 30, 500, 300);
		
		if (sensorType == SensorType.AUDIO) {
			audioToggle = new JButton(new ImageIcon("resources/soundOff.png"));
			audioToggle.setBounds(550,520,40,40);
			playingSound = false;
			audioToggle.addActionListener(new muter());
			this.add(audioToggle);
			audioQueue = new LinkedList<byte[]>();
			new Thread(new AudioPlayer()).start();
			//System.out.println("thread started");
	    	AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);
	    	try {
	    		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
	            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	            speakers.open(format);
	            speakers.start();
	    	} catch (Exception e) {
	    		System.err.println("uh oh, sensor audio stuff");
	    		e.printStackTrace();
	    		
	    	}
		}
		
		this.add(exit);
		this.add(title);
		this.setLayout(null);
		this.threshold = threshold;
		this.sensorType = sensorType;
		this.address = address;
		value = 0;
		
		repaint();

	}
	private class repainter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	repaint();
        	/*value = server.getValue(address);
        	if (sensorType == SensorType.AUDIO) {
        		AudioClip clip = server.getClip(address);
        		if (clip != null) {
        			audioQueue.add(clip);
        		}
        	}*/
        }
	}
	private class muter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	System.out.println(playingSound);
        	playingSound = !playingSound;
        	if (playingSound) {
        		//audioToggle.set
        		audioToggle.setIcon(new ImageIcon("resources/soundOn.png"));
        	} else {
        		audioToggle.setIcon(new ImageIcon("resources/soundOff.png"));
        	}
        	//
        	
        }
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (pointsX1 == null) {
			//for some reason wont work in constructor
			pointsX1 = new int[3];
			pointsY1 = new int[3];
			//left triangle
			pointsX1[0] = (int)(getWidth()/5);
			pointsX1[1] = (int)(getWidth()/5) - 10;
			pointsX1[2] = (int)(getWidth()/5) - 10;
			pointsY1[0] = (int)(getHeight()/2);
			pointsY1[1] = (int)(getHeight()/2) + 5;
			pointsY1[2] = (int)(getHeight()/2) - 5;
			
			pointsX2 = new int[3];
			pointsY2 = new int[3];
			//right triangle
			pointsX2[0] = (int)(getWidth() * 4/5);
			pointsX2[1] = (int)(getWidth() * 4/5) + 10;
			pointsX2[2] = (int)(getWidth() * 4/5) + 10;
			pointsY2[0] = (int)(getHeight()/2);
			pointsY2[1] = (int)(getHeight()/2) + 5;
			pointsY2[2] = (int)(getHeight()/2) - 5;
			
			thresholdPointX = new int[3];
			thresholdPointY = new int[3];
			thresholdPointX[0] = (int)(getWidth() * 3/5 * threshold) + (int)(getWidth()/5);
			thresholdPointX[1] = (int)(getWidth() * 3/5 * threshold) + (int)(getWidth()/5) + 10;
			thresholdPointX[2] = (int)(getWidth() * 3/5 * threshold) + (int)(getWidth()/5) - 10;
			thresholdPointY[0] = (int)(getHeight()/2);
			thresholdPointY[1] = (int)(getHeight()/2) + 10;
			thresholdPointY[2] = (int)(getHeight()/2) + 10;
		}
		int[] currentThresholdX = new int[3];
		int[] currentThresholdY = new int[3];
		float f = GetSensorValue();
		currentThresholdX[0] = (int)(f * getWidth() * 3/5) + (int)(getWidth()/5);
		currentThresholdX[1] = (int)(f * getWidth() * 3/5) + (int)(getWidth()/5) + 10;
		currentThresholdX[2] = (int)(f * getWidth() * 3/5) + (int)(getWidth()/5) - 10;
		currentThresholdY[0] = (int)(getHeight()/2);
		currentThresholdY[1] = (int)(getHeight()/2) - 10;
		currentThresholdY[2] = (int)(getHeight()/2) - 10;
		g.setColor(Color.green);
		g.fillPolygon(currentThresholdX,currentThresholdY,3);
		g.setColor(Color.black);
		g.drawPolygon(pointsX1, pointsY1, 3);
		g.drawPolygon(pointsX2, pointsY2, 3);
		g.setColor(Color.red);
		g.fillPolygon(thresholdPointX, thresholdPointY, 3);
		g.setColor(Color.black);
		g.drawLine(getWidth() / 5, getHeight()/2, getWidth() * 4/5, getHeight()/2);
	}
	public synchronized void addClip(byte[] recording) {
		audioQueue.add(recording);
		System.out.println("Clip added, queue size is " + audioQueue.size());
	}
	public synchronized byte[] getRecording() {
		//System.out.println("Getting one " + audioQueue.size());
		return audioQueue.poll();
	}
	public float GetSensorValue() {
		//Random r = new Random();
		//currently filler value
		//return r.nextFloat();
		return value;
	}
	public void SetSensorValue(float f) {
		this.value = f;
		repaint();
	}
	public class AudioPlayer implements Runnable {

		public void run() {
			while(true) {
				//System.out.println("looping thread" + audioQueue.size());
				
					//System.out.println("playing clip");
					byte[] nextClip = getRecording();
					if (playingSound && nextClip != null) {
						System.out.println(nextClip[0] + " " + nextClip[1] + " " + nextClip[2]);
						System.out.println("Playing sound");
						speakers.write(nextClip, 0, nextClip.length);
						//speakers.drain();
						
					}
					
				
				
			}
			
		}
		
	}
	
}
