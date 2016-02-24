import javax.swing.JPanel;


public class VideoStreamBox extends JPanel {
	/* Screen that will show the video stream of the sensor
	 * Updates over time
	 */
	private String address;
	public VideoStreamBox(String address) {
		 //setTitle("Video stream on sensor " + address);
		 this.address = address;
	     

	}
}
