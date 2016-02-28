import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Timer;

import javax.swing.JFrame;



public class StreamBox extends JFrame {
	protected String address;
	protected Timer t;
	public Hashtable<String, StreamBox> hRef;
	public StreamBox(Hashtable<String, StreamBox> h, String address) {
		 this.address = address;
		 hRef = h;
		 setTitle("You shouldn't be seeing this");
	     setSize(600, 600);
	     setVisible(true);
	     setLocationRelativeTo(null);
	     setResizable(false);
	     setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	     addWindowListener(CloseListener());

	}
	private WindowListener CloseListener() {
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	hRef.remove(address);
            	setEnabled(false);
                dispose();
            }
		};
		return exitListener;
	}
	
	
        
}
