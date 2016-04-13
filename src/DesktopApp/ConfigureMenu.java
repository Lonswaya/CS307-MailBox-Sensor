package DesktopApp;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.*;

import Utilities.UserBackend;
import cs307.purdue.edu.autoawareapp.*;
public class ConfigureMenu extends JFrame {
	/* The menu which will take the users information, and change the settings on one of the sensors.
	 * Values: Name(s), Color(c), Scheduled Times (two times), Sensor type (enum), Threshold (integer),
	 * 		DesktopNot(bool), MagicMirror(bool), Text(bool), Email(bool), Phone#(string), EmailAddress(s)
	 */
	private static final long serialVersionUID = 2L;
	private JTextField name;
	private JButton colorChooser;
	private JCheckBox scheduledTimes;
	private JTextField startH, startM, endH, endM;
	private JLabel startTH, endTH, startTM, endTM;
	private JRadioButton light, sound, motion;
	private JLabel leftThreshold, rightThreshold;
	private JSlider threshold;
	public JSlider currentThreshold;
	private JCheckBox desktop, magicMirror, text, email;
	private JTextField phoneNum, emailAddress;
	

	
	private SensorType type, firstType;
	private boolean desktopBool, magicMirrorBool, textBool, emailBool;
	private boolean usesScheduledTimes;

	
	
	
	private static String leftThresholdLight = "Ember";
	private static String rightThresholdLight = "Laser Pointer";
	
	private static String leftThresholdMotion = "Ant Crawling";
	private static String rightThresholdMotion = "Person Running";
	
	private static String leftThresholdSound = "Falling Leaf";
	private static String rightThresholdSound = "Gunshot";
	
	private Color c;
	
	private AutoAwareControlPanel parent;
	public String address; //address is used to verify to update the configure menu, in case you get a streaming message for it
	public int inputNum;
	
	public ConfigureMenu(int inputNum, AutoAwareControlPanel parent, String address) {
		this.address = address;
		//I just pass the whole damn parent through the constructor, saves any back-references we need. Unless java already has that built in. 
		this.inputNum = inputNum;
		this.parent = parent;
		InitUI();
		
	}
	
	private void InitUI() {
    		this.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));
	        setSize(600, 450);
	        setResizable(false);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	        setLayout(new GridLayout(4,1));
	        JPanel nameAndSchedule = new JPanel(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        JTextArea nameText = new JTextArea("Name: ");
		    nameText.setBackground(nameAndSchedule.getBackground());
	        nameText.setEditable(false);
	        
	        ClientConfig input = parent.configs.get(inputNum);
	        name = new JTextField(input.name);
		    name.setPreferredSize(new Dimension(100, 25));
		    c = new Color(input.r, input.g, input.b);
	        colorChooser = new JButton("Color");
	        colorChooser.setBackground(c);
	        colorChooser.setPreferredSize(new Dimension(90, 30));
	        colorChooser.addActionListener(new ButtonListener());
	       
	        usesScheduledTimes = !(input.force_off || input.force_on); 

	        scheduledTimes = new JCheckBox();
	        scheduledTimes.setText("Scheduled Times");
	        scheduledTimes.setSelected(usesScheduledTimes);
	        scheduledTimes.addActionListener(new ScheduledSwitch());
	        
	        JPanel timeTextH = new JPanel(new GridLayout(2, 1, 5, 5));
	        JPanel timeFieldsH = new JPanel(new GridLayout(2,3,5,5));
	        
	       // JPanel timeTextM = new JPanel(new GridLayout(2, 1, 5, 5));
	       // JPanel timeFieldsM = new JPanel(new GridLayout(2,6,5,5));
	        
	       
	        
	        startTH = new JLabel();
	        startTH.setText("Turn on at     ");
	        startTH.setBackground(nameAndSchedule.getBackground());
	        
	        startTM = new JLabel();
	        startTM.setText("   :");
	        startTM.setBackground(nameAndSchedule.getBackground());
		    
	        endTH = new JLabel();
		    endTH.setText("Turn off at     ");
		    endTH.setBackground(nameAndSchedule.getBackground());
		    //startT
		    endTM = new JLabel();
		    endTM.setText("   :");
		    endTM.setBackground(nameAndSchedule.getBackground());
	        
	        startH = new JTextField(usesScheduledTimes ? Integer.toString(input.start_hours) : "");
	        endH = new JTextField(usesScheduledTimes ? Integer.toString(input.stop_hours) : "");
	        startM = new JTextField(usesScheduledTimes ? (input.start_minutes < 10 ? "0" : "") + Integer.toString(input.start_minutes) : "");
	        endM = new JTextField(usesScheduledTimes ? (input.start_minutes < 10 ? "0" : "") + Integer.toString(input.stop_minutes) : "");
	        
	        
	        startH.setPreferredSize(new Dimension(25, 20));
	        endH.setPreferredSize(new Dimension(25, 20));
	        startM.setPreferredSize(new Dimension(25, 20));
	        endM.setPreferredSize(new Dimension(25, 20));
	        
	        
		    startH.setEnabled(usesScheduledTimes);
		    endH.setEnabled(usesScheduledTimes);
		    startM.setEnabled(usesScheduledTimes);
		    endM.setEnabled(usesScheduledTimes);
		    startTH.setEnabled(usesScheduledTimes);
		    endTH.setEnabled(usesScheduledTimes);
		    startTM.setEnabled(usesScheduledTimes);
		    endTM.setEnabled(usesScheduledTimes);
		    
	        timeTextH.add(startTH);
	        timeTextH.add(endTH);
		    timeFieldsH.add(startH);
	        timeFieldsH.add(startTM);
	        timeFieldsH.add(startM);
	        timeFieldsH.add(endH);
	        timeFieldsH.add(endTM);
	        timeFieldsH.add(endM);
	        
	        nameAndSchedule.add(nameText, gbc);
	        nameAndSchedule.add(name, gbc);
	        nameAndSchedule.add(new JPanel(), gbc);
	        nameAndSchedule.add(colorChooser, gbc);
	        nameAndSchedule.add(new JPanel(), gbc);
	        nameAndSchedule.add(scheduledTimes, gbc);
	        nameAndSchedule.add(timeTextH, gbc);
	        nameAndSchedule.add(timeFieldsH, gbc);
	        //nameAndSchedule.add(timeTextM);
	        //nameAndSchedule.add(timeFieldsM);
	        
	        JPanel sensorFields = new JPanel();
	        
	        JPanel gridRadio = new JPanel(new GridLayout(4,1));
	        JPanel thresholds = new JPanel();
		    
		    type = input.sensor_type;
		    firstType = type;
		    light = new JRadioButton("Light Sensor");
		    light.setActionCommand("light");
		    if (input.sensor_type == SensorType.LIGHT) light.setSelected(true);
		    sound = new JRadioButton("Sound Sensor");
		    sound.setActionCommand("sound");
		    if (input.sensor_type == SensorType.AUDIO) sound.setSelected(true);
		    motion = new JRadioButton("Video/motion Sensor");
		    motion.setActionCommand("motion");
		    if (input.sensor_type == SensorType.VIDEO) motion.setSelected(true);

		    
		    light.addActionListener(new RadioListener());
		    sound.addActionListener(new RadioListener());
		    motion.addActionListener(new RadioListener());
		    
		    ButtonGroup group = new ButtonGroup();
		    group.add(light);
		    group.add(sound);
		    group.add(motion);
		    
		    JLabel radioName = new JLabel("Sensor Type");
		    gridRadio.add(radioName);
		    gridRadio.add(light);
		    gridRadio.add(sound);
		    gridRadio.add(motion);
		    
		    
	        sensorFields.add(gridRadio);
	        
	       // System.out.println((input.sensing_threshold));
		    threshold = new JSlider(JSlider.HORIZONTAL, 0, 100, Math.round(input.sensing_threshold * 100));
		    
		    
		    /*JTextPane currentLabel = new JTextPane();
		    currentLabel.setText("Current:");
		    currentLabel.setEnabled(false);
		    currentLabel.setBackground(sensorFields.getBackground());*/
		    
		    JLabel leftBuffer = new JLabel("Current: " + (input.isSensorActive()?"":"(Disabled)"));


		    currentThreshold = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); //the updating slider 
		    
		    
		    currentThreshold.setEnabled(false);

		    leftThreshold = new JLabel("Threshold:");
		    //leftThreshold.setText(leftThresholdLight);
//		    leftThreshold.setBackground(sensorFields.getBackground());
		    
//		    rightThreshold = new JLabel();
		    //rightThreshold.setText(rightThresholdLight);
//		    rightThreshold.setBackground(sensorFields.getBackground());
		    
		   /* if (input.sensor_type == SensorType.LIGHT) {
				leftThreshold.setText(leftThresholdLight);
				rightThreshold.setText(rightThresholdLight);
				type = SensorType.LIGHT;
			} else if (input.sensor_type == SensorType.AUDIO) {
				leftThreshold.setText(leftThresholdSound);
				rightThreshold.setText(rightThresholdSound);
				type = SensorType.AUDIO;
			} else if (input.sensor_type == SensorType.VIDEO) {
				leftThreshold.setText(leftThresholdMotion);
				rightThreshold.setText(rightThresholdMotion);
				type = SensorType.VIDEO;
			}*/
		    
		    JPanel labelThresholds = new JPanel(new GridLayout(2,1)); 
		    JPanel sliderThresholds = new JPanel(new GridLayout(2,1));
		    
		    
		    labelThresholds.add(leftThreshold);
		    sliderThresholds.add(threshold);
		    //setThreshold.add(rightThreshold);
		    
		    
		    labelThresholds.add(leftBuffer); 
		    sliderThresholds.add(currentThreshold);
		    
		    
		    thresholds.add(labelThresholds);
		    thresholds.add(sliderThresholds);
		    sensorFields.add(thresholds);
		    
		    
		    
		    JPanel notificationLevel = new JPanel(new GridBagLayout());
	        GridBagConstraints gbcNotification = new GridBagConstraints();
		    
		    JPanel notificationMethod = new JPanel(new GridLayout(3,2,5,5));
		    JLabel notificationLabel = new JLabel("Notification Method");
		    
		    desktop = new JCheckBox("Desktop notification");
		    desktop.addActionListener(new CheckboxListener());
		    desktop.setActionCommand("desktop");
		    desktop.setSelected(input.desktopNotification);
		    magicMirror = new JCheckBox("Magic Mirror");
		    magicMirror.addActionListener(new CheckboxListener());
		    magicMirror.setActionCommand("magicMirror");
		    magicMirror.setSelected(input.magicMirrorNotification);
		    text = new JCheckBox("Text");
		    text.addActionListener(new CheckboxListener());
		    text.setActionCommand("text");
		    text.setSelected(input.textNotification);
		    email = new JCheckBox("Email");
		    email.addActionListener(new CheckboxListener());
		    email.setActionCommand("email");
		    email.setSelected(input.emailNotification);
		    
		  
		    
		    
		    
		    phoneNum = new JTextField(input.phoneNumber);
		    emailAddress = new JTextField(input.emailAddress);
		    
		    textBool = input.textNotification;
		    emailBool = input.emailNotification;
		    
			phoneNum.setEnabled(textBool);
			emailAddress.setEnabled(emailBool);
			
			notificationMethod.add(notificationLabel);
			notificationMethod.add(new JPanel());
			notificationMethod.add(new JPanel());
			notificationMethod.add(desktop);
			notificationMethod.add(text);
			notificationMethod.add(phoneNum);
			notificationMethod.add(magicMirror);
			notificationMethod.add(email);
			notificationMethod.add(emailAddress);
			
		   
		    notificationLevel.add(notificationMethod, gbcNotification);
		    
		    
		    
		    JPanel applyLevel = new JPanel(new GridBagLayout());
	        GridBagConstraints gbcApply = new GridBagConstraints();
	        
	        JButton applyButton = new JButton("Apply");
	        applyButton.setPreferredSize(new Dimension(100, 40));
	        applyButton.addActionListener(new ApplyListener());
	        applyLevel.add(applyButton, gbcApply);
	        
	        add(nameAndSchedule);
	        add(sensorFields);
	        add(notificationLevel);
	        add(applyLevel);
	        

	        setTitle("Configuration Menu");
	        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
            	   setEnabled(false);
            	   UpdateTypeOnly(firstType);
           		   parent.StopStream(parent.configs.get(inputNum)); //stop the stream, pointless
                   dispose();
                } else {
                	setVisible(true);
                	//setAlwaysOnTop(true);
                }
            }
        };
        return exitListener;
		
	
	}
	private class ButtonListener implements ActionListener {
		@SuppressWarnings("unused")
	    public void actionPerformed(ActionEvent e) {
	      Color newC = JColorChooser.showDialog(null, "Choose a Color", c);
	      if (newC!= null)
	        c = newC;
	      colorChooser.setBackground(c);
	    }
	}
	private class ApplyListener implements ActionListener {
		@SuppressWarnings("unused")
	    public void actionPerformed(ActionEvent e) {
	      SubmitValues();
	    }
	}
	private class ScheduledSwitch implements ActionListener {
		@SuppressWarnings("unused")
	    public void actionPerformed(ActionEvent e) {
		  usesScheduledTimes = !usesScheduledTimes;
	      startH.setEnabled(usesScheduledTimes);
	      endH.setEnabled(usesScheduledTimes);
	      startM.setEnabled(usesScheduledTimes);
	      endM.setEnabled(usesScheduledTimes);
	      startTH.setEnabled(usesScheduledTimes);
		  endTH.setEnabled(usesScheduledTimes);
		  startTM.setEnabled(usesScheduledTimes);
		  endTM.setEnabled(usesScheduledTimes);
	    }
	}
	private class RadioListener implements ActionListener {
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			currentThreshold.setValue(0); //indicate buffering
			if (e.getActionCommand().equals("light")) {
				if (type != SensorType.LIGHT) {
	            	UpdateTypeOnly(SensorType.LIGHT);
					//send cfg to change into light sensor
				}
//				leftThreshold.setText(leftThresholdLight);
//				rightThreshold.setText(rightThresholdLight);
				type = SensorType.LIGHT;
			} else if (e.getActionCommand().equals("sound")) {
				if (type != SensorType.AUDIO) {
	            	UpdateTypeOnly(SensorType.AUDIO);
					//send cfg to change into audio sensor
				}
//				leftThreshold.setText(leftThresholdSound);
//				rightThreshold.setText(rightThresholdSound);
				type = SensorType.AUDIO;
			} else if (e.getActionCommand().equals("motion")) {
				if (type != SensorType.VIDEO) {
	            	UpdateTypeOnly(SensorType.VIDEO);
	            	//send cfg to change into video sensor
				}
//				leftThreshold.setText(leftThresholdMotion);
//				rightThreshold.setText(rightThresholdMotion);
				type = SensorType.VIDEO;
			}
		}
	}
	private class CheckboxListener implements ActionListener {
		@SuppressWarnings("unused")
	    public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("text")) {
				textBool = !textBool;
				phoneNum.setEnabled(textBool);
			} else if (e.getActionCommand().equals("email")) {
				emailBool = !emailBool;
				emailAddress.setEnabled(emailBool);
			} else if (e.getActionCommand().equals("desktop")) {
				desktopBool = !desktopBool;
			} else if (e.getActionCommand().equals("magicMirror")) {
				magicMirrorBool = !magicMirrorBool;
			}
	    }
	}
	private void SubmitValues() {
		System.out.println(phoneNum.getText() + " " + emailAddress.getText() + " " + startH.getText() + "|");
		//do your time parsing here boi
		
		if (!usesScheduledTimes || startH.getText().compareTo("") == 0 || startM.getText().compareTo("") == 0 || endH.getText().compareTo("") == 0 || endM.getText().compareTo("") == 0) {
			//It's all or nothing, baby.
			startH.setText("-1");
			startM.setText("-1");
			endH.setText("-1");
			endM.setText("-1");
		}
		//ClientConfig toSubmit = parent.configs.get(inputNum);
		ClientConfig lastCfg = parent.configs.get(inputNum);
		boolean fOff, fOn;
		if (this.usesScheduledTimes) {
			fOff = false;
			fOn = false;
		} else {
			fOff = lastCfg.force_off;
			fOn = lastCfg.force_on;
		}
		
		String nameString = name.getText();
		if (nameString.length() > 20)
			nameString = nameString.substring(0, 20);
		ClientConfig toSubmit = new ClientConfig( 
				lastCfg.ip,
				startH.getText() + ":" + startM.getText(), 
				endH.getText() + ":" + endM.getText(), 
				fOn, 
				fOff,
				type, 
				threshold.getValue() * .01f, 
				nameString,
				c.getRed()/255, c.getGreen()/255, c.getBlue()/255, //cause otherwise I would have to get a float array and eaugh
				desktopBool,
				magicMirrorBool,
				textBool,
				emailBool, 
				phoneNum.getText(), emailAddress.getText(),
				10000 //TODO get, in ms
		);
		this.setEnabled(false);
		parent.configs.set(inputNum, toSubmit);
		System.out.println(toSubmit);
		//stop the stream to the pi
		parent.StopStream(lastCfg);
		//stop the stream
		UserBackend.SendStreaming(toSubmit.ip, parent);

		//parent.SendConfigToSensor(toSubmit);
		parent.refreshSensorList();
    	dispose();
    }
	private void UpdateTypeOnly(final SensorType t) {
		//send a config to a sensor to update only the sensortype, since streaming
		Thread thr = new Thread(new Runnable(){
			public void run(){
				ClientConfig cfg = new ClientConfig();
				cfg.ip = parent.configs.get(inputNum).ip;
				cfg.sensor_type = t;
				parent.SendConfigToSensor(cfg);
			}
		});
		thr.start();
		
		
	}
	
}
