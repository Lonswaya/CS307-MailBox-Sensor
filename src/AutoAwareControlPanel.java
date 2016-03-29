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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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





public class AutoAwareControlPanel extends JFrame {//implements Observer {
	private static final long serialVersionUID = 1L;
	private JPanel panelHolder;
	public ArrayList<ClientConfig> configs;
	public Hashtable<String, StreamBox> Streamers;
	private CentralServer server;
	private ConfigureMenu cf;
    private int controlIndex;
    private boolean isNotifying;
    private Stack<String> notificationStack;
    private boolean t1bool;
	private static boolean t2bool;
	
	//true: GUI test to build things 
	//false: full on, real connection
	private static boolean debug = false;
	
	
	public AutoAwareControlPanel() {
        initUI();
        //TODO ask to start up server if the server does not exist
    }

    private void initUI() {
    	
	   
    	
    	//server.addObserver(this);
    	server = new CentralServer(this);
		//start server recieving thread
    	new Thread(server).start();
		
    	notificationStack = new Stack<String>();
    	//server.addObserver(this);
    	//GridLayout experimentLayout = new GridLayout(5,3);
    	Streamers = new Hashtable<String, StreamBox>();
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));

        Timer t = new Timer(5000, new Refresher());
        setTitle("AutoAware Control Panel");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(CloseListener());
        controlIndex = 0;
        isNotifying = false;
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
		myPanel.setBackground(configs.get(i).color);
		final String identifier = configs.get(i).ip;
		JButton configure = new JButton("Configure");
		configure.setPreferredSize(new Dimension(120, 30));
		configure.setVerticalTextPosition(AbstractButton.CENTER);
		configure.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		configure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               //System.out.println("Open configure menu for sensor " + identifier);
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
            	RemoveSensor(identifier, false);
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
		System.out.println("added new sensor to panelholder");
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
    		cf = new ConfigureMenu(sensorNumber, this);
    		System.out.println("Starting stream now");
        	ClientConfig cfg = ConfigFind(identifier);
        	System.out.println(cfg.isSensorActive());
    		if (cfg.isSensorActive())  {
    			t2bool = true;
    			server.sendMessage(new StreamingMessage("Telling to start streaming",cfg, true));
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

    	} else if (address != null) {
	    	ClientConfig newSensor = new ClientConfig();
	    	newSensor.force_off = true;
	    	newSensor.force_on = false;
	    	newSensor.SetIP(address);
	    	newSensor.serverPort = server.seperatePort;
	    	Random r = new Random();
	    	newSensor.SetColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
	    	newSensor.SetName("New Sensor");
	    	
	    	
	    	if (debug) {
	    		configs.add(newSensor);
		    	createNew(controlIndex, newSensor);
		    	refreshSensorList();
	    	} else {
	    		//should get a message back after trying to add a sensor
	    		Message m = server.AddSensor(new ConfigMessage("Updating config",newSensor));
		    	if (m.message.equals("Connection succeeded")) {
		    		configs.add(newSensor);
			    	createNew(controlIndex, newSensor);
			    	refreshSensorList();
		    	} else {
		    		JOptionPane.showMessageDialog(null,m.message, "error", JOptionPane.ERROR_MESSAGE, null);
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
    		new Thread(new GetConfigsListener()).start();
    	}
    	
    	/*ClientConfig firstConfig = new ClientConfig();
    	
    	firstConfig.ip = "128.211.255.32";
    	firstConfig.SetName("First Config");
    	firstConfig.force_off = true;
    	configs.add(firstConfig);
    	server.sendMessage(new ConfigMessage("Updating config",firstConfig), firstConfig.ip, StaticPorts.serverPort);*/
    	
    }
    public void refreshSensorList() {
    	//System.out.println("updating");
    	new Thread(new RefreshListener()).start();
    	
    
    }
    public void RemoveSensor(String identifier, boolean force) {
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
    		ClientConfig cfg = ConfigFind(identifier);
    		String name = cfg.name;
    		cfg.force_off = true;
    		cfg.force_on = false;
    		int index = configs.indexOf(cfg);
    		panelHolder.remove(index);
    		configs.remove(index);
    		ConfigMessage cfgMessage = new ConfigMessage("Updating config, turning off",cfg);
    		cfgMessage.delete = true;
    		server.sendMessage(cfgMessage);
    		JOptionPane.showMessageDialog(null, "Deleted sensor " + name, "Deleted sensor " + name, JOptionPane.INFORMATION_MESSAGE, null);
	    	
    	}
    	refreshSensorList();
    }
    public void StopStream(String address) {
    	StopStream(ConfigFind(address));
    }
    public void StopStream(ClientConfig cfg) {
    	System.out.println("Stopping stream now");
		server.sendMessage(new StreamingMessage("Telling to stop streaming",cfg, false));
		t2bool = false;

    }
    public ClientConfig ConfigFind(String identifier) {
    	//System.out.println(configs);
    	for (int i = 0; i < configs.size(); i++) {
    		//System.out.println(configs.get(i).ip);
    		if (configs.get(i) != null) {
	    		if ((configs.get(i).ip).equals(identifier)) {
	    			return configs.get(i);
	    		}
    		}
    	}
    	//was not found
    	return null;
    }
    public void SaveSensors() {
    	//save sensors to database
    	//
    	System.out.println("Sensors saved");
    }
    class GetConfigsListener implements Runnable {
    	public void run() {
    		while(!NewServer());    //keep asking for server, nothing can run without server	
    		refreshSensorList();
    	}
    }
    class RefreshListener implements Runnable { 
    	public void run() {
    		
    		if (configs == null) return;
    		//if we have not yet 

        	for (int i = 0; i < configs.size(); i++) {
            	
        		//System.out.println(i);
        		//JPanel myPanel = controlPanels[i];
        		try {
        			JPanel myPanel = (JPanel) panelHolder.getComponent(i);
            		//System.out.println(panelHolder.getComponent(i));
            		//myPanel.setBackground(Color.red);
            		myPanel.setBackground(configs.get(i).color);
            		JButton icon = (JButton) ((Container) myPanel.getComponent(3)).getComponent(0);
            		if (configs.get(i).sensor_type == SensorType.AUDIO) {
            			icon.setIcon(new ImageIcon("resources/mic.png"));
            		}
                    if (configs.get(i).sensor_type == SensorType.VIDEO) {
                    	icon.setIcon(new ImageIcon("resources/camera.png"));
                    }
                    if (configs.get(i).sensor_type == SensorType.LIGHT) {
                    	icon.setIcon(new ImageIcon("resources/light.png"));
                    }
                    if (configs.get(i).isSensorActive()) {
                    	if (icon.getBackground() == Color.gray) {
        	            	icon.setBackground(Color.white);
                    	}
                    } else {
                    	if (icon.getBackground() != Color.gray) {
        	            	icon.setBackground(Color.gray);
                    	}
                    }
                    JButton enabled = (JButton)((Container)myPanel.getComponent(2)).getComponent(1);
                    enabled.setText(configs.get(i).isSensorActive()?"Disable":"Enable");
                    //System.out.println("enabled : " + enabled.getText());
                    //System.out.println(configs.get(i).sensor_type);
                    //System.out.println("look here eugene you fucking prick " + configs.get(i).isSensorActive());
            		JLabel title = (JLabel) myPanel.getComponent(0);
            		title.setText("<html>" + configs.get(i).name + (configs.get(i).isSensorActive()?"":" <br />(Disabled)</html>"));
        		} catch (ArrayIndexOutOfBoundsException e) {
        			//Its okay, we just make a new sensor and add it in
			    	createNew(controlIndex, configs.get(i));
        		} catch (Exception e) {
        			//Okay, now something bad is happening
        		}
        		
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
            	server.Kill(); 
                CloseOperation();
            }
        };
        return exitListener;
	}
    private void CloseOperation() {
    	/*int confirm = JOptionPane.showOptionDialog(
                null, "Save sensor information?", 
                "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, null, null);
           if (confirm == 1) {
           	//close menu, do not close application
              SaveSensors();
           } 
           dispose();
    	*/
    	dispose();
    }
    public void OpenStream(SensorType s, String address) {
		ClientConfig cfg = ConfigFind(address);
    	if (cfg.isSensorActive()) {
    		StreamBox stream = new StreamBox(Streamers, address, this);
	    	if (s == SensorType.VIDEO) {
	    		ClientConfig c = ConfigFind(address);
	    		boolean connection = server.sendMessage(new StreamingMessage("Setting image/video streaming to true",c,true));
	    		if (connection) {
		    		stream.setTitle("Video stream on sensor " + address);
		    		VideoStreamBox newVid = new VideoStreamBox(address, server);
		    		stream.myPanel = newVid;
			    	stream.add(newVid);
			    	Streamers.put(address, stream);
			    	
	    		} else {
	    			JOptionPane.showMessageDialog(null, "Conneciton refused");
	    			System.out.println("Connection refused");
	    		}
		    	

	    	} else {
	    		ClientConfig c = ConfigFind(address);
	    		boolean connection = server.sendMessage(new StreamingMessage("Setting streaming to true",c,true));
	    		if (connection) {
		    		stream.setTitle(c.sensor_type + " stream on sensor " + address);
		    		ValueStreamBox newVal = new ValueStreamBox(c.sensor_type, c.sensing_threshold, address, server);
			    	stream.add(newVal);
			    	stream.myPanel = newVal;
			    	Streamers.put(address, stream);
			    	
			    	System.out.println("setting to stream now");
	    		} else {
	    			JOptionPane.showMessageDialog(null, "Conneciton refused");
	    			System.out.println("Connection refused");
	    		}
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
    	refreshSensorList();
    }
    public void SendConfigToSensor(ClientConfig cfg) {
    	//Send a message through the server to update a sensors config
    	
    	System.out.println("Updating info to a sensor with type "  + cfg.sensor_type);
    	server.sendMessage(new ConfigMessage("Updating config",cfg));
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
    	/*final Random r = new Random();
    	Thread t1 = new Thread(new Runnable(){
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
	    			
	    			ex.update(null, pm);
    			}
    		}
    	});
    	t2bool = false;
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
	    			if (t4bool) {
		    			ClientConfig cfg = new ClientConfig();
		    			cfg.SetColor(Color.cyan);
		    			cfg.ip = "1234";
		    			//cfg.name = r.n
		    			ConfigMessage rd = new ConfigMessage("", cfg);
		    			rd.setFrom("1234"); 
		    			
		    			ex.ProcessMessage(rd);
	    			}
    			}
	    	}
	    });*/
    	//t1.start();
    	///t4.start();
    }
    public void Notify() {
    	String names = "";
    	int number = 0;
    	while (!notificationStack.isEmpty()) {
    		names+=((number>0?", ":"") + notificationStack.pop());
    		number++;
    	}
    	if (number > 0)
    		JOptionPane.showMessageDialog(null, "Sensor" + (number>1?"s ":" ") + names + " ha" + (number>1?"ve":"s") + " reached threshold!");
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
    		String lastIP = server.seperateIP;
    		server.seperateIP = address;
    		configs = server.GetSensors();
    		if (configs == null) {
    			//System.out.println("server not found");
    			server.seperateIP = lastIP;
        		JOptionPane.showMessageDialog(null,"Server not found", "error", JOptionPane.ERROR_MESSAGE, null);
        		return false;
    		} else {
        		JOptionPane.showMessageDialog(null,"Server found, sensors updated", "success", JOptionPane.INFORMATION_MESSAGE, null);
        		return true;
    		}
    		
    		/*server.seperateIP = address;
    		System.out.println(address);
    		System.out.println("Correct Ip format!");*/
    		
    	}
    	return false;
        //System.out.println("Save sensors");

    }
  
	public void ProcessMessage(Message arg1) {
		Message gotMessage = arg1;
		ClientConfig myClient = ConfigFind(gotMessage.from);
		if (myClient == null) {
			System.out.println("Message recieved for incorrect sensor, please try again.\n " + gotMessage);
			return;
		}
		System.out.println(gotMessage);
		//System.out.println("Message recieved, says to: " + gotMessage.message + " with the type of" + gotMessage.type);
		
		
		switch (gotMessage.type) {
		
		
			case VIDEO:
				//actual video
				break;
			case LIGHT:
				//grayscale image?
				break;
			case READING:
				float f = ((ReadingMessage)gotMessage).getCurrentThreshold()/100; //hacky af
				System.out.println("reading message found with value " + f);

				//if the streaming page exists, then update it
				//include checking the sensor type, in case a message is sent and doesn't have the correct information (i.e. switching from light to video)
				
				if (Streamers.get(gotMessage.from) != null) ((ValueStreamBox)(Streamers.get(gotMessage.from).myPanel)).value = f;
				else if (cf != null) cf.currentThreshold.setValue(Math.round(f * 100));
				else {
					//close the stream, it does not exist
					//server.sendMessage(new StreamingMessage("Telling to stop streaming",gotMessage.config, false));

					//we probably shouldn't close the stream, as if we get a sensor that we aren't trying to stream but another is,
					//we will tell that one to stop streaming.
					
				}
				//if we have a higher threshold, and the item was not already added to the list
				if (myClient.sensing_threshold <= f && notificationStack.search(myClient.name) == -1) {
					//System.out.println(notificationStack.search(myClient.name));
					notificationStack.add(myClient.name);
				}
				break;
			case AUDIO:
				//System.out.println("Audio Clip got");
				//audio clip in .wav format
				AudioClip audio = ((AudioMessage)gotMessage).clip;
				if (Streamers.get(gotMessage.from) != null) ((ValueStreamBox)(Streamers.get(gotMessage.from).myPanel)).addClip(audio);
				else {
					//close the stream, it does not exist
					//server.sendMessage(new StreamingMessage("Telling to stop streaming",gotMessage.config, false));
					
					//we probably shouldn't close the stream, as if we get a sensor that we aren't trying to stream but another is,
					//we will tell that one to stop streaming.
				}
				break;
			case PICTURE: 
				//Actual picture, updated on a normal basis
				if (Streamers.get(gotMessage.from) != null) ((VideoStreamBox)(Streamers.get(gotMessage.from).myPanel)).SetImage(((PictureMessage)gotMessage).getImage()); 
				else {
					//close the stream, it does not exist
			    	//server.sendMessage(new StreamingMessage("Telling to stop streaming",gotMessage.config, false));
					
					//we probably shouldn't close the stream, as if we get a sensor that we aren't trying to stream but another is,
					//we will tell that one to stop streaming.
				}
				break;
			case CONFIG:
				//this code....  *shudder*
				//can't use gotMessage.from because it may be from another client interface
				if (((ConfigMessage)gotMessage).delete) {
					//remove sensor
					RemoveSensor(gotMessage.config.ip, true);
				} else if (ConfigFind(gotMessage.config.ip) != null) {
					//add new sensor
					configs.add(gotMessage.config);
			    	createNew(controlIndex, gotMessage.config);
				} else {
					//otherewise update sensor
					configs.set(configs.indexOf(ConfigFind((gotMessage.config.ip))), gotMessage.config);
				}
				refreshSensorList();
				break;
			default:
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