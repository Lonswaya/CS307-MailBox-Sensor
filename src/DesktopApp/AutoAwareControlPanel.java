package DesktopApp;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import Utilities.MessageProcessor;
import Utilities.SocketWrapper;
import Utilities.StaticPorts;
import Utilities.UserBackend;
import cs307.purdue.edu.autoawareapp.*;


public class AutoAwareControlPanel extends JFrame implements MessageProcessor {//implements Observer {
	private static final long serialVersionUID = 1L;
	private JPanel panelHolder;
	public ArrayList<ClientConfig> configs, fullConfigsList;
	public Hashtable<String, StreamBox> Streamers;
	public Hashtable<String, SocketWrapper> streamersConnection;
	private ConfigureMenu cf;
    private int controlIndex;
    private Stack<String> notificationStack;
    private boolean notificationDialog;
	private static boolean t2bool;
	protected SocketWrapper serverConnection;
	private String username;
	
	//true: GUI test to build things 
	//false: full on, real connection
	private static boolean debug = false;
	
	
	public AutoAwareControlPanel() {
		 //this will create a new connection, and will call processMessage() on whatever has to be done
		username = JOptionPane.showInputDialog("Enter username", ""); 
		String password =  JOptionPane.showInputDialog("Enter password", ""); 
      	serverConnection = UserBackend.SetServerConnection("localhost", this, username, password);      
      	
        initUI();
        
       
      	
        
    }

    private void initUI() {
    	
    	notificationStack = new Stack<String>();
    	//server.addObserver(this);
    	//GridLayout experimentLayout = new GridLayout(5,3);
    	Streamers = new Hashtable<String, StreamBox>();
    	streamersConnection = new Hashtable<String, SocketWrapper>();
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));
        Timer t = new Timer(10000, new Refresher());
        setTitle("AutoAware Control Panel");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(CloseListener());
        controlIndex = 0;
        panelHolder = new JPanel(new GridLayout(0,2, 10, 10));
        panelHolder.setBackground(Color.black);
        InitConfigs();
        CreateControlPanel(panelHolder);
        createMenuBar();
        JScrollPane scrollPane = new JScrollPane(panelHolder);
        this.add(scrollPane);
        t.start();
        setVisible(true);
    }
    void CreateControlPanel(JPanel panelHolder) {
    	if (configs != null) {
	        for (int i = 0; i < configs.size(); i++) {
	    		//init panels
	        	createNew(i, configs.get(i));
	    	}
    	}
    }
    public void createNew(int i, ClientConfig c) {
		controlIndex++;
    	JPanel myPanel;
		myPanel = new JPanel(new GridLayout(2,2,10,10));
		//System.out.println(c.r + " " + c.g + " " + c.b);
		myPanel.setBackground(new Color(c.r, c.g, c.b));
		final String identifier = configs.get(i).ip;
		JButton configure = new JButton("Configure");
		configure.setPreferredSize(new Dimension(120, 30));
		configure.setVerticalTextPosition(AbstractButton.CENTER);
		configure.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		configure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               System.out.println("Open configure menu for sensor " + identifier);
               createConfigureMenu(identifier);
               //Notify(identifier);
            }
        });
		configure.setOpaque( true );
		configure.setBackground(Color.green);
		configure.setForeground( Color.black );
		configure.setFont(new Font(configure.getName(), Font.PLAIN, 20));

		
		JButton remove = new JButton("Remove");
		remove.setPreferredSize(new Dimension(120, 30));
		remove.setVerticalTextPosition(AbstractButton.CENTER);
		remove.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println(ConfigFind(identifier) + " " + identifier);
            	RemoveSensor(ConfigFind(identifier), configs.indexOf(ConfigFind(identifier)), false, true);
                //System.out.println("Remove sensor " + identifier);
            }
        });
		remove.setOpaque( true );
		remove.setBackground(Color.red);
		remove.setForeground( Color.WHITE );
		remove.setFont(new Font(remove.getName(), Font.BOLD, 20));
		
		JButton toggle = new JButton(configs.get(i).isSensorActive()?"Disable":"Enable");
		toggle.setPreferredSize(new Dimension(120, 30));
		toggle.setVerticalTextPosition(AbstractButton.CENTER);
		toggle.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		toggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	ClientConfig cfg = ConfigFind(identifier);
                boolean var = !(cfg.isSensorActive());
            	System.out.println(var?"Enable":"Disable" + " sensor " + identifier);
                ChangeSensors(var, false, identifier);
                
            }
        });
		toggle.setOpaque( true );
		toggle.setBackground(Color.blue);
		toggle.setForeground( Color.black );
		toggle.setFont(new Font(toggle.getName(), Font.PLAIN, 20));

		JButton icon = new JButton(new ImageIcon("resources/mic.png"));
		if (configs.get(i).sensor_type == SensorType.AUDIO)
        	icon = new JButton(new ImageIcon("resources/mic.png"));
        if (configs.get(i).sensor_type == SensorType.VIDEO)
        	icon = new JButton(new ImageIcon("resources/camera.png"));
        if (configs.get(i).sensor_type == SensorType.LIGHT)
        	icon = new JButton(new ImageIcon("resources/light.png"));
        if (configs.get(i).sensor_type == SensorType.MOTION)
        	icon = new JButton(new ImageIcon("resources/motion.png"));
		icon.setVerticalTextPosition(AbstractButton.CENTER);
		icon.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		icon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	//have to get it every time, since it may change
                if (ConfigFind(identifier).sensor_type == SensorType.AUDIO)
                	OpenStream(SensorType.AUDIO, identifier);
                	//System.out.println("Open audio stream for sensor " + identifier);
                if (ConfigFind(identifier).sensor_type == SensorType.VIDEO)
                	OpenStream(SensorType.VIDEO, identifier);
                	//System.out.println("Open video stream for sensor " + identifier);
                if (ConfigFind(identifier).sensor_type == SensorType.LIGHT)
                	OpenStream(SensorType.LIGHT, identifier);
                /*if (ConfigFind(identifier).sensor_type == SensorType.MOTION)
                	OpenStream(SensorType.MOTION, identifier);*/ //we dont want to open a stream here
                	//System.out.println("Open light stream for sensor " + identifier);

            }
        });
        icon.setOpaque( true );
        
        icon.setBackground(configs.get(i).isSensorActive()?Color.white:Color.gray);
        
		JLabel title = new JLabel("<html>" + configs.get(i).name + (configs.get(i).isSensorActive()?"":" <br />(Disabled)</html>"), JLabel.CENTER);
		title.setFont(new Font(title.getName(), Font.BOLD, 30));
		myPanel.add(title);
		
		JPanel filler = new JPanel();
		filler.setOpaque(false);
		//filler.setBackground(configs.get(i).color);
		JPanel buttonBorderPanel = new JPanel(); //something they can all use
		buttonBorderPanel.setLayout(new GridLayout(3,1, 10, 10));
		//uttonBorderPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		buttonBorderPanel.setOpaque(false);
		//buttonBorderPanel.setBackground(configs.get(i).color);
		buttonBorderPanel.add(configure);
		buttonBorderPanel.add(toggle);
		buttonBorderPanel.add(remove);
		//buttonBorderPanel
		JPanel iconBorderPanel = new JPanel();
		iconBorderPanel.setLayout(new FlowLayout());
		iconBorderPanel.setOpaque(false);
		//iconBorderPanel.setBackground(configs.get(i).color);
		iconBorderPanel.add(icon);
		myPanel.add(filler);
		myPanel.add(buttonBorderPanel);
		myPanel.add(iconBorderPanel);
		panelHolder.add(myPanel);
		//System.out.println("added new sensor to panelholder");
	}
    
    public void createMenuBar() {
    	JMenuBar menubar = new JMenuBar();
        ImageIcon icon = new ImageIcon("exit.png");

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu options = new JMenu("Options");
        options.setMnemonic(KeyEvent.VK_O);
        JMenu actions = new JMenu("Actions");
        actions.setMnemonic(KeyEvent.VK_A);
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        

        JMenuItem eMenuItem = new JMenuItem("Exit", icon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                CloseOperation();
            }
        });
        /*MenuItem SaveSensors = new JMenuItem("Save Sensor Information", icon);
        SaveSensors.setMnemonic(KeyEvent.VK_S);
        SaveSensors.setToolTipText("Add new sensor");
        SaveSensors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	SaveSensors();
                //System.out.println("Save sensors");
            }
        });*/
        
        JMenuItem ChangeServer = new JMenuItem("Change Server Information", icon);
        ChangeServer.setMnemonic(KeyEvent.VK_S);
        ChangeServer.setToolTipText("Add new sensor");
        ChangeServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	//aveSensors();
            	NewServer();
            }
        });
        
        JMenuItem AddNewSensor = new JMenuItem("Add New Sensor", icon);
        AddNewSensor.setMnemonic(KeyEvent.VK_A);
        AddNewSensor.setToolTipText("Add new sensor");
        AddNewSensor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	AddSensor();
                //System.out.println("Open sensor dialog");
            }
        });
        JMenuItem TurnAllOff = new JMenuItem("Disable All Sensors", icon);
        TurnAllOff.setMnemonic(KeyEvent.VK_D);
        TurnAllOff.setToolTipText("Disable all sensors");
        TurnAllOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Turn all off");
                ChangeSensors(false, true, null);
            }
        });
        JMenuItem TurnAllOn = new JMenuItem("Enable All Sensors", icon);
        TurnAllOn.setMnemonic(KeyEvent.VK_E);
        TurnAllOn.setToolTipText("Enable all sensors");
        TurnAllOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Turn all on");
                ChangeSensors(true, true, null);
                
            }
        });
        JMenuItem RefreshSensors = new JMenuItem("Refresh Sensors", icon);
        RefreshSensors.setMnemonic(KeyEvent.VK_R);
        RefreshSensors.setToolTipText("Refresh Sensors");
        RefreshSensors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("RefreshSensors");
                UserBackend.GetSensors(serverConnection);
                refreshSensorList();
            }
        });
        JMenuItem Helper = new JMenuItem("Help Menu", icon);
        Helper.setMnemonic(KeyEvent.VK_H);
        Helper.setToolTipText("Open Help");
        Helper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	JDialog dialog = new JDialog();     
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setTitle("help me");
                dialog.add(new JLabel(new ImageIcon("resources/help.png")));
                dialog.pack();
                dialog.setLocationByPlatform(true);
                dialog.setVisible(true);
            }
        });
        help.add(Helper);
        actions.add(TurnAllOn);
        actions.add(TurnAllOff);
        actions.add(RefreshSensors);
        options.add(AddNewSensor);
        options.add(ChangeServer);
//        file.add(SaveSensors);
        file.add(eMenuItem);
        menubar.add(file);
        menubar.add(options);
        menubar.add(actions);
        menubar.add(help);
        setJMenuBar(menubar);
    }
    
    
    public void createConfigureMenu(String identifier) {
    	int sensorNumber = configs.indexOf(ConfigFind(identifier));
    	//System.out.println(configs.get(0));
    	if (cf == null || !cf.isEnabled()) {
    		cf = new ConfigureMenu(sensorNumber, this, identifier, username);
        	ClientConfig cfg = ConfigFind(identifier);
        	System.out.println(cfg.isSensorActive());
    		if (cfg.isSensorActive() && cfg.sensor_type != SensorType.VIDEO && cfg.sensor_type != SensorType.MOTION)  { //no reason to be streaming here
    			t2bool = true;
    			System.out.println("Starting stream now");

    			//this will tell the sensor to start streaming, and handles the connections with the processMessage
    			SocketWrapper newSocketWrapper = UserBackend.SendStreaming(cfg.ip, this);
    			if (newSocketWrapper != null) {
    				streamersConnection.put(identifier, newSocketWrapper);
    			} else {
    				System.err.println("Sensor connection was not made to start streaming");
    			}
    		} else {
    			t2bool = false;
    		}

    	}
    }
    public void AddSensor() {
    	
    	String address = null;
    	do
    	{
    		if (address != null) JOptionPane.showMessageDialog(null,"Invalid address", "error", JOptionPane.ERROR_MESSAGE, null);

    		address = JOptionPane.showInputDialog("Enter IP Address of sensor", ""); //Do ip parsing here.
    		if (address == null) break;
    	}
    	while(!checkIPFormat(address));

    	//System.out.println(ConfigFind(address));
    	
    	ClientConfig finder = ConfigFind(address);
    	if (finder != null) {
    		//System.out.println("error");
    		JOptionPane.showMessageDialog(null,"Sensor already added, named \"" + finder.name + "\"", "error", JOptionPane.ERROR_MESSAGE, null);

    	} else if ((finder = FullConfigFind(address)) != null) {
    		//just use the config, add the user in the list, send
    		finder.users.add(username);
    		if (UserBackend.AddSensor(finder, serverConnection)) {
	    		//configs.add(newSensor);
		    	//createNew(controlIndex, newSensor);
		    	refreshSensorList();
	    	} else {
	    		JOptionPane.showMessageDialog(null,"Sensor not found", "error", JOptionPane.ERROR_MESSAGE, null);
	    	}
    	} else if (address != null) {
    	
    		ClientConfig newSensor = new ClientConfig();
    		
    		String[] values = {"Audio", "Video", "Light", "Motion"};

    		Object selected = JOptionPane.showInputDialog(null, "What type of sensor do you want?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
    		if ( selected != null ){//null if the user cancels. 
    		    String selectedString = selected.toString();
    		    switch (selectedString) {
	    		    case "Audio":
	    		    	newSensor.sensor_type = SensorType.AUDIO;
	    		    	break;
	    		    case "Video": //i know its not video/picture blah blah blah
	    		    	newSensor.sensor_type = SensorType.VIDEO;
	    		    	break;
	    		    case "Light":
	    		    	newSensor.sensor_type = SensorType.LIGHT;
	    		    	break;
	    		    case "Motion":
	    		    	newSensor.sensor_type = SensorType.MOTION;
	    		    default:
	    		    	break;
    		    }
    		}else{
    		    System.out.println("User cancelled");
    		    return;
    		}
    		
	    	
	    	newSensor.force_off = true;
	    	newSensor.force_on = false;
	    	newSensor.SetIP(address);
	    	newSensor.serverPort = StaticPorts.serverPort;
	    	Random r = new Random();
	    	newSensor.r = r.nextFloat();
	    	newSensor.g = r.nextFloat();
	    	newSensor.b = r.nextFloat();
	    	newSensor.SetName("New Sensor");
	    	newSensor.users = new ArrayList<String>();
	    	newSensor.users.add(username);
	    	
	    	
	    	if (debug) {
	    		configs.add(newSensor);
		    	createNew(controlIndex, newSensor);
		    	refreshSensorList();
	    	} else {
	    		if (UserBackend.AddSensor(newSensor, serverConnection)) {
		    		//configs.add(newSensor);
			    	//createNew(controlIndex, newSensor);
			    	refreshSensorList();
		    	} else {
		    		JOptionPane.showMessageDialog(null,"Sensor not found", "error", JOptionPane.ERROR_MESSAGE, null);
		    	}
		    	
	    	}
	    	
	    	//get a return message from the server, if sensor type is null then blah
	    	

    	}
    }
    public void InitConfigs() {
    	//request to database, add sensors
    	//manually adding sensors for now
    	
    	if (debug) {
        	configs = new ArrayList<ClientConfig>();
    	} else {
    		
    		if (serverConnection == null) {
    			new Thread(new GetConfigsListener()).start(); //probe for the sensors
    		} else {
        		JOptionPane.showMessageDialog(getContentPane(), "Server connection found at localhost", "success", JOptionPane.DEFAULT_OPTION, null);
        		UserBackend.GetSensors(serverConnection);
    		}
    	}
   
    }
    public void refreshSensorList() {
    	//System.out.println("updating");
    	new Thread(new RefreshListener()).start();
    	
    	
    
    }
    //you probably shouldn't be calling this unless you are about to refresh them
    private void RemoveSensorPanels() {
    	panelHolder.removeAll();
    	controlIndex = 0;
    }
    public void RemoveSensor(ClientConfig cfg, int index, boolean force, boolean removeFromList) {
    	int confirm;
    	if (!force) {
    		confirm = JOptionPane.showOptionDialog(
                null, "Remove sensor?", 
                "Remove Confirmation", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, null, null);
    	} else {
    		confirm = 0;
    	}
    	if (confirm == 0) {
    		controlIndex--;
    		String name = cfg.name; 
    		cfg.force_off = true;
    		cfg.force_on = false;
    		
    		if (removeFromList) configs.remove(index);
    		
    		UserBackend.SendConfig(cfg, true, serverConnection);
    		System.out.println("Deleted sensor " + name);
    		//JOptionPane.showMessageDialog(null, "Deleted sensor " + name, "Deleted sensor " + name, JOptionPane.INFORMATION_MESSAGE, null);
	    	
    	}
    	//refreshSensorList();
    }
    public void StopStream(String address) {
    	SocketWrapper sWrapper = streamersConnection.get(address);
    	if (sWrapper != null) {
    		UserBackend.StopStreaming(sWrapper);
        	streamersConnection.remove(address);
    	}
    	//StopStream(ConfigFind(address));
    }
    public void StopStream(ClientConfig cfg) {
    	//System.out.println("Stopping stream now");
    	//stopping stream
    	//UserBackend.StopStreaming(streamersConnection.remove(cfg.ip));
    	t2bool = false;
    	StopStream(cfg.ip);
    }
    
    public ClientConfig ConfigFind(String identifier) {
    	//System.out.println(configs);
    	if (configs != null) {
	    	for (int i = 0; i < configs.size(); i++) {
	    		//System.out.println(configs.get(i).ip);
	    		if (configs.get(i) != null) {
		    		if ((configs.get(i).ip).equals(identifier)) {
		    			return configs.get(i);
		    		}
	    		}
	    	}
	    	//was not found
    	}
    	return null;
    }
    public ClientConfig FullConfigFind(String identifier) {
    	//System.out.println(configs);
    	if (this.fullConfigsList != null) {
	    	for (int i = 0; i < fullConfigsList.size(); i++) {
	    		//System.out.println(configs.get(i).ip);
	    		if (fullConfigsList.get(i) != null) {
		    		if ((fullConfigsList.get(i).ip).equals(identifier)) {
		    			return fullConfigsList.get(i);
		    		}
	    		}
	    	}
	    	//was not found
    	}
    	return null;
    }
    public void SaveSensors() {
    	//save sensors to database
    	//
    	System.out.println("Sensors saved");
    }
    class GetConfigsListener implements Runnable {
    	public void run() {
    		System.out.println("server not found, probing for it");
    		while(!NewServer());    //keep asking for server, nothing can run without server	
    		refreshSensorList();
    	}
    }
    class RefreshListener implements Runnable { 
    	public void run() {
    		if (configs == null) {
    			panelHolder.revalidate();
        		panelHolder.repaint();
    			return;
    		}
    		
    		//if we have not yet 
    		
    		if (serverConnection == null || serverConnection.lostConnection) {
    			System.out.println("server not found");
    			configs.removeAll(configs);
    			if (!notificationDialog) {
	    			notificationDialog = true;
	        		JOptionPane.showMessageDialog(getContentPane(), "Lost server connection, please change server information", "error", JOptionPane.ERROR_MESSAGE, null);
    			}
    		} else {
    			//Is this necessary? We only get the sensors when needed
    			//UserBackend.GetSensors(serverConnection);
    		}
    		RemoveSensorPanels();
    		for (int i = 0; i < configs.size(); i++) {
    			createNew(controlIndex, configs.get(i));
    		}
    		panelHolder.revalidate();
    		panelHolder.repaint();

    	}
    	
    }
    
    private WindowListener CloseListener() {
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	//setAlwaysOnTop(false);
            	//server.Kill(); 
                CloseOperation();
            }
        };
        return exitListener;
	}
    private void CloseOperation() {
    	dispose();
    }
    public void OpenStream(SensorType s, String address) {
		ClientConfig cfg = ConfigFind(address);
    	if (cfg.isSensorActive()) {
    		StreamBox stream = new StreamBox(Streamers, address, this);
	    	if (s == SensorType.VIDEO) {
	    		//ClientConfig c = ConfigFind(address);
	    		
	    		
	    		stream.setTitle("Video stream on sensor " + address);
	    		SocketWrapper newSocketWrapper = UserBackend.SendStreaming(cfg.ip, this);
    			if (newSocketWrapper != null) {
    				streamersConnection.put(cfg.ip, newSocketWrapper);
    				VideoStreamBox newVid = new VideoStreamBox(address);
    	    		stream.myPanel = newVid;
    		    	stream.add(newVid);
    		    	Streamers.put(address, stream);
    			} else {
    				System.err.println("Sensor connection was not made to start streaming");
    				stream.Close();
    			}
	    		
		    	
		    	

	    	} else if (s != SensorType.MOTION) { //no streambox for motion
	    		ClientConfig c = ConfigFind(address);
	    		
	    		
	    		
		    	stream.setTitle(c.sensor_type + " stream on sensor " + address);
		    	SocketWrapper newSocketWrapper = UserBackend.SendStreaming(cfg.ip, this);
    			if (newSocketWrapper != null) {
    				streamersConnection.put(cfg.ip, newSocketWrapper);
    				ValueStreamBox newVal = new ValueStreamBox(c.sensor_type, c.sensing_threshold, address);
    			    stream.add(newVal);
    			    stream.myPanel = newVal;
    			    Streamers.put(address, stream);
    			} else {
    				System.err.println("Sensor connection was not made to start streaming");
    				stream.Close();
    			}
		    	
			    	
			    System.out.println("setting to stream now");

	    	}
    	}
    }
    public void ChangeSensors(boolean how, boolean all, String identifier) {
    	if (all) {
    		for (ClientConfig cfg : configs) {
    			if (how) {
    				if (!cfg.force_on){ 
	    				cfg.force_off = false;
	    				cfg.force_on = true;
	    				SendConfigToSensor(cfg);
    				}
    			} else {
    				if (!cfg.force_off){ 
	    				cfg.force_off = true;
	    				cfg.force_on = false;
	    				SendConfigToSensor(cfg);
    				}
    			}
    		}
    	} else {
    		ClientConfig cfg = ConfigFind(identifier);
    		if (how) {
				if (!cfg.force_on){ 
    				cfg.force_off = false;
    				cfg.force_on = true;
    	    		SendConfigToSensor(cfg);
				}
			} else {
				if (!cfg.force_off){ 
    				cfg.force_off = true;
    				cfg.force_on = false;
    	    		SendConfigToSensor(cfg);
				}
			}
    	}
//   	SaveSensors();
    	//refreshSensorList();
    }
    public void SendConfigToSensor(ClientConfig cfg) {
    	//Send a message through the server to update a sensors config
    	
    	System.out.println("Updating info to a sensor with type "  + cfg.sensor_type);
    	UserBackend.SendConfig(cfg, false, serverConnection);
    }
    public class Refresher implements ActionListener {
    	
        public void actionPerformed(ActionEvent e) {
        	refreshSensorList();
        	Notify();
        }
    }
    public static void main(String[] args) {
    	@SuppressWarnings("unused")
		final
    	AutoAwareControlPanel ex = new AutoAwareControlPanel();
    	final Random r = new Random();
    	/*Thread t1 = new Thread(new Runnable(){
    		public void run(){
    			int num = 8;
    			while(true) {
	    			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    			BaseConfig cfg = new BaseConfig();
	    			cfg.sensor_type = SensorType.VIDEO;
	    			PictureMessage pm = new PictureMessage("", cfg);
	    			pm.setFrom("1234"); 
	    			try {
	    				//some temp images from my game project
	    				pm.setImage(ImageIO.read(new File("resources/tmp-" + num + ".gif")));
	    				num++;
	    				if (num == 25) num = 8;
	    				//pm.setImage(ImageIO.read(new File("tmp-" + (8+r.nextInt(16) + ".gif"))));
					} catch (IOException e) {
						e.printStackTrace();
					}
	    			
	    			ex.ProcessMessage( pm);
    			}
    		}
    	});
    	/*t2bool = false;
    	Thread t2 = new Thread(new Runnable(){
    		public void run(){
    			while(true) {
	    			try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    			if (t2bool) {
		    			ClientConfig cfg = new ClientConfig();
		    			ReadingMessage rd = new ReadingMessage("", cfg);
		    			rd.setFrom("1234"); 
		    			
		    			rd.setCurrentThreshold(r.nextFloat() * 100);
		    			ex.ProcessMessage(rd);
	    			}
    			}
	    	}
	    });
    	//t1.start();
    	t2.start();
    	//ex.Notify("1234");
    	Thread t3 = new Thread(new Runnable(){
    		public void run(){
    			while(true) {
	    			try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    			ClientConfig cfg = new ClientConfig();
		    			AudioMessage am = new AudioMessage("", cfg);
		    			am.setFrom("1234"); 
		    			try {
							am.clip = Applet.newAudioClip(new URL("file:///C:/Users/Lonswaya/workspace/CS307-MailBox-Sensor/resources/pink.wav"));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
		    			ex.ProcessMessage(am);
	    			
    			}
	    	}
	    });
        t3.start();
    	final boolean t4bool = true;
    	Thread t4 = new Thread(new Runnable(){
    		public void run(){
    			while(true) {
    				Random r = new Random();
	    			try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    			if (true) {
		    			ClientConfig cfg = new ClientConfig();
		    			cfg.SetColor(Color.cyan);
		    			cfg.ip = "1234";
		    			//cfg.name = r.n
		    			ReadingMessage rd = new ReadingMessage("reading", cfg);
		    			rd.setFrom("1234"); 
		    			
		    			ex.ProcessMessage(rd);
	    			}
    			}
	    	}
	    });
    	//t1.start();
    	t4.start();*/
    }
    public void Notify() {
    	String names = "";
    	int number = 0;
    	while (!notificationStack.isEmpty()) {
    		names+=((number>0?", ":"") + notificationStack.pop());
    		number++;
    	}
    	if (number > 0)
    		JOptionPane.showMessageDialog(null, "Sensor" + (number>1?"s ":" ") + names + " ha" + (number>1?"ve":"s") + " an alert!");
    	//ClientConfig c = ConfigFind(ip);
    	//UIManager UI=new UIManager();
    	//Color oldColor = UI.getColor("OptionPane.background");
    	//UIManager.put("OptionPane.background",c.color);
    	//UIManager.put("OptionPane.foreground",c.color);
    	/*if (!isNotifying) {
    		isNotifying = true;
    		JOptionPane.showMessageDialog(this, "Sensor " + c.name + " has reached threshold!");
    		isNotifying = false;
    	}*/
		//UIManager.put("OptionPanel.background", oldColor );
		//UIManager.put("OptionPanel.foreground", oldColor );
	}
    public boolean NewServer() {
    	String address = null;
    	do
    	{
    		if (address != null) JOptionPane.showMessageDialog(null,"Invalid address", "error", JOptionPane.ERROR_MESSAGE, null);

    		address = JOptionPane.showInputDialog("Enter IP Address of server", ""); //Do ip parsing here.
    		if (address == null) break;
    	}
    	while(!checkIPFormat(address));
    	//System.out.println(address);
		//do your ip parsing here boi

    	if(address != null && !address.isEmpty())
    	{
    		//String lastIP = UserBackend.serverIP;
    		username = JOptionPane.showInputDialog("Enter username", ""); 
    		String password =  JOptionPane.showInputDialog("Enter password", ""); 
          	serverConnection = UserBackend.SetServerConnection(address, this, username, password);
    		if (serverConnection == null) {
    			//System.out.println("server not found");
        		JOptionPane.showMessageDialog(null,"Server not found", "error", JOptionPane.ERROR_MESSAGE, null);
        		return false;
    		} else {
        		JOptionPane.showMessageDialog(null,"Server found", "Success", JOptionPane.INFORMATION_MESSAGE, null);
        		UserBackend.GetSensors(serverConnection);
        		return true;
    		}
    		
    		
    	}
    	return false;
        //System.out.println("Save sensors");

    }
  
	public void ProcessMessage(Message arg1) {
		Message gotMessage = arg1;
		ClientConfig myClient = new ClientConfig();
		if (gotMessage.type != MessageType.GET_SENSORS) {
			myClient = ConfigFind(gotMessage.from);
			if (myClient == null && gotMessage.type != MessageType.CONFIG) {
				System.out.printf("Message recieved for incorrect sensor, please try again.\n %s",gotMessage);
				return;
			} //if its a new sensor, we dont want this
		}
		//System.out.println(gotMessage.message + " " + gotMessage.type);
		//System.out.println("Message recieved, says to: " + gotMessage.message + " with the type of" + gotMessage.type);
		
		
		switch (gotMessage.type) {
		
		
			case VIDEO:
				//actual video
				break;
			case LIGHT:
				//grayscale image?
				break;
			case READING:
				float f = ((ReadingMessage)gotMessage).getCurrentThreshold(); //hacky af
				if (f > 100) f = 100;
				if (f < 0) f = 0;
				
				System.out.println("reading message found with value " + f);

				//if the streaming page exists, then update it
				//include checking the sensor type, in case a message is sent and doesn't have the correct information (i.e. switching from light to video)
				//System.out.println(cf);
				if (cf != null) System.out.println(cf.address + " " + gotMessage.from);
				
				StreamBox temp = Streamers.get(gotMessage.from);
				if (temp != null) ((ValueStreamBox)temp.myPanel).SetSensorValue(f/100);
				else if (cf != null && cf.address.equals(gotMessage.from)) {
					System.out.println("Updating slider");
					cf.currentThreshold.setValue(Math.round(f));
				}
				
				//if we have a higher threshold, and the item was not already added to the list
				if ((myClient.sensing_threshold * 100 <= f || myClient.sensor_type == SensorType.MOTION) && 
						notificationStack.search(myClient.name) == -1 && 
						myClient.isSensorActive() && 
						myClient.desktopNotification) {
					//System.out.println(notificationStack.search(myClient.name));
					notificationStack.add(myClient.name);
				}
				break;
			case AUDIO:
				//System.out.println("Audio Clip got");
				//audio clip in .wav format
				byte[] recording = ((AudioMessage)gotMessage).recording;
				/*if (recording != null) {
					System.out.println("Recording: " + recording.length);
					//JOptionPane.showInputDialog("Hi");
				} else {
					System.out.println("No recording");
				}*/
				float currentAmount = ((AudioMessage)gotMessage).currentThreshold;
				
				//System.out.println("reading message found with value " + currentAmount);

				
				StreamBox audioBox = Streamers.get(gotMessage.from);
				if (audioBox != null) {
					if (recording != null) ((ValueStreamBox)audioBox.myPanel).addClip(recording);
					 ((ValueStreamBox)audioBox.myPanel).SetSensorValue(currentAmount/100);
				}
				else if (cf != null && cf.address.equals(gotMessage.from)) {
					System.out.println("Updating slider");
					cf.currentThreshold.setValue(Math.round(currentAmount));
				}
				else {
					
				}
				break;
			case PICTURE: 
				//Actual picture, updated on a normal basis
				if (Streamers.get(gotMessage.from) != null) ((VideoStreamBox)(Streamers.get(gotMessage.from).myPanel)).SetImage(((PictureMessage)gotMessage).getImage()); 
				else {
					
				}
				break;
			case CONFIG:
				//this code....  *shudder*
				//this kind of stuff will not be happening, we should never get a config message from the server
				/*System.out.println("Got config, updating/adding info now");
				//can't use gotMessage.from because it may be from another client interface
				if (((ConfigMessage)gotMessage).delete) {
					//remove sensor
					RemoveSensor(gotMessage.config, configs.indexOf(gotMessage.config), true, true);
				} else if (ConfigFind(gotMessage.config.ip) == null) {
					//add new sensor
					configs.add(gotMessage.config);
			    	New(controlIndex, gotMessage.config);
				} else {
					//otherewise update sensor
					configs.set(configs.indexOf(ConfigFind((gotMessage.config.ip))), gotMessage.config);
				}*/ 	
				refreshSensorList();
				break;
			case STREAMING:
				//
				
				
				break;
			case GET_SENSORS:
				fullConfigsList = ((SensorsMessage)gotMessage).ar;
				configs = new ArrayList<ClientConfig>();
				for (ClientConfig cfg : ((SensorsMessage)gotMessage).ar) {
					System.out.println("Config");
					for (String user : cfg.users) {
						System.out.println(user);
						if (user.equals(username)) {
							configs.add(cfg);
							//break;
						}
					}
				}
				//configs = ((SensorsMessage)gotMessage).ar;
				refreshSensorList();
				break;
			default:
        		JOptionPane.showMessageDialog(null,gotMessage.message, "Error", JOptionPane.ERROR_MESSAGE, null);

				//null, error message
				System.out.println(gotMessage.message);
				break;
		}
		
	}
	public boolean checkIPFormat(String ip)
	{
		//debug, I like using 1234
		if (ip.equals("1234") || ip.equals("localhost")) return true;
		
		try {
	        if ( ip == null || ip.isEmpty() ) {
	            return true;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if ( ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
		
	}
    
    
}
