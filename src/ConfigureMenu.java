import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
public class ConfigureMenu extends JFrame {
	/* The menu which will take the users information, and change the settings on one of the sensors.
	 * Values: Name(s), Color(c), Scheduled Times (two times), Sensor type (enum), Threshold (integer),
	 * 		DesktopNot(bool), MagicMirror(bool), Text(bool), Email(bool), Phone#(string), EmailAddress(s)
	 */
	private static final long serialVersionUID = 2L;
	public boolean inUse; //because none of the vars I am trying to use are working
	public ConfigureMenu(int sensorID) {
		InitUI();
		inUse = true;
	}
	private void InitUI() {
	    	
	        setTitle("AutoAware Configure Panel");
	        setSize(600, 600);
	       // setAlwaysOnTop(true);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	        
	        
	        addWindowListener(CloseListener());
	       
	        
	        setVisible(true);
	}
	private WindowListener CloseListener() {
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	//setAlwaysOnTop(false);
                int confirm = JOptionPane.showOptionDialog(
                     null, "Close without saving?", 
                     "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                     JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                	//close menu, do not close application
                   dispose();
                   inUse = false;
                } else {
                	
                	//setAlwaysOnTop(true);
                }
            }
        };
        return exitListener;
		
	
	}
	private void SubmitValues() {
    	inUse = false;
    	dispose();
    }
}
