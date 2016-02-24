import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.JFrame;



public class StreamBox extends JFrame {
	protected String address;
	protected Timer t;

	public StreamBox() {
		 
		 setTitle("You shouldn't be seeing this");
	     setSize(600, 600);
	     setVisible(true);
	     setLocationRelativeTo(null);
	     setResizable(false);
	}
	
	
        
}
