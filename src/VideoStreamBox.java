import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
	private String path;
	private CentralServer server;

	public VideoStreamBox(String address, CentralServer server) {
		this.setLayout(null);
		found = false;
		this.server = server;
		this.address = address;
		t  = new Timer(10, new repainter());
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
		
		JButton pathFinder = new JButton("<html>Save<br />Path</html>");
		pathFinder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PromptPath();
			}
			
		});
		pathFinder.setBounds(250,10,60,50);
		
		JButton saveButton = new JButton("<html>Save<br />Image</html>");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (path == null) {
					PromptPath();
				}
				Calendar cal = Calendar.getInstance();
		        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
				File newFile = new File(path +  "/AutoAware-" + sdf.format(cal.getTime()) + ".png");
				try {
				    ImageIO.write(image, "png", newFile);

				} catch (Exception e) {
					System.err.print("Unable to save image.... fuck");
				}
			}
			
		});
		saveButton.setBounds(310,10,60,50);
		
		//setTitle("Video stream on sensor " + address);
		add(exit);
		add(pathFinder);
		add(saveButton);
		this.address = address;
		repaint();
	}
	public void SetImage(BufferedImage image) {
		System.out.println("new image set");
		this.image = image;
		found = true;
	}
	private void PromptPath() {
		JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new File("."));
	    chooser.setDialogTitle("Open location");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	   
	    chooser.setAcceptAllFileFilterUsed(false);
	    
	    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) { 
	    	path = chooser.getSelectedFile().toString();	
	    	System.out.println(path);
	    }
	    else {
	    	System.out.println("No Selection ");
	    }
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
