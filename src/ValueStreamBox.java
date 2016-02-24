import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;



public class ValueStreamBox extends JPanel {
	private static final long serialVersionUID = -1L;
	/* Screen that will show the value of the sensor, and its threshold
	 * Updates over time
	 * Shown in a linear fashion
	 * 
	 */
	private String address;
	private float threshold;
	private SensorType sensorType;
	private Timer t;
	private int[] pointsX1, pointsX2, pointsY1, pointsY2, thresholdPointX, thresholdPointY;
	public ValueStreamBox(String address, SensorType sensorType, float threshold) {
		super();
		t  = new Timer(1000, new repainter());
		t.start();
		JButton exit = new JButton("Exit");
		final JPanel tempThis = this;
		exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	SwingUtilities.getWindowAncestor(tempThis).dispose();
            }
        });
		exit.setBounds(260, 520, 80, 30);
		JLabel title = new JLabel("Current " + sensorType + " value");
		title.setFont(new Font(title.getName(), Font.BOLD, 20));
		System.out.println(title.getBounds());
		title.setBounds(200, 30, 500, 300);
		this.add(exit);
		this.add(title);
		this.setLayout(null);
		this.threshold = threshold;
		this.sensorType = sensorType;
		this.address = address;
		
		repaint();

	}
	private class repainter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	repaint();
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
	public float GetSensorValue() {
		Random r = new Random();
		//currently filler value
		return r.nextFloat();
	}
	
}
