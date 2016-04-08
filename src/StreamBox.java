import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class StreamBox extends JFrame {
	protected String address;
	protected Timer t;
	public Hashtable<String, StreamBox> hRef;
	public JPanel myPanel;
	private AutoAwareControlPanel aRef;
	public StreamBox(Hashtable<String, StreamBox> h, String address, AutoAwareControlPanel aRef) {
		 this.address = address;
		 hRef = h;
		 this.aRef = aRef;
		 setTitle("You shouldn't be seeing this");
	     setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));
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
            public void windowClosed(WindowEvent e) {
            	//System.out.println("STOP THE FUCK NOW");
            	Close();
            }
            public void windowClosing(WindowEvent e) {
            	Close();
            	System.out.println("STOP THE FUCK NOW");
            }
		};
		return exitListener;
	}
	
	public void Close() {
		hRef.remove(address);
    	setEnabled(false);
        dispose();
        aRef.StopStream(address);
	}
	
	
        
}
