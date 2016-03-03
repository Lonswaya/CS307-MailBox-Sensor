import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



public class VideoStreamBox extends JPanel {
	/* Screen that will show the video stream of the sensor
	 * Updates over time
	 */
	private String address;
	private BufferedImage image;
	private Timer t;
	private boolean found;

	public VideoStreamBox(String address) {
		found = false;
		this.address = address;
		t  = new Timer(1000, new repainter());
		t.start();
		try {
		    image = ImageIO.read(new File("resources/none.png"));
		} catch (IOException e) {
			//System.out.println("Image not found");
		}
		JButton exit = new JButton("Exit");
		final JPanel tempThis = this;
		exit.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
		    	SwingUtilities.getWindowAncestor(tempThis).dispose();
		    }
	    });
		exit.setBounds(260, 520, 80, 30);
		//setTitle("Video stream on sensor " + address);
		this.address = address;
		repaint();
	}
	public void SetImage(BufferedImage image) {
		this.image = image;
		found = true;
	}
	private class repainter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	repaint();
        }
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (found) {
		} else {
			//System.out.println("image not found");
		}
		g.drawImage(image, getWidth()/2-image.getWidth()/2, getHeight()/2-image.getHeight()/2, null);

	}
}
