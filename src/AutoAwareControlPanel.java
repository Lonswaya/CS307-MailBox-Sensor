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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.*;



public class AutoAwareControlPanel extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private JPanel panelHolder;
	public ArrayList<ClientConfig> configs;
	public Hashtable<String, StreamBox> Streamers;
	private CentralServer server;
	private ConfigureMenu cf;
    private int controlIndex;
	public AutoAwareControlPanel() {
        initUI();
    }

    private void initUI() {
<<<<<<< HEAD
    	
	    System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
	    System.setProperty("javax.net.ssl.keyStorePassword", "sensor");

    	
    	server = new CentralServer();
    	server.addObserver(this);
=======
    	server = new CentralServer(this);
    	//server.addObserver(this);
>>>>>>> refs/remotes/origin/master
    	//GridLayout experimentLayout = new GridLayout(5,3);
    	Streamers = new Hashtable<String, StreamBox>();
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));

        //Timer t = new Timer(5000, new Refresher());
        setTitle("AutoAware Control Panel");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(CloseListener());
        controlIndex = 0;
        
        panelHolder = new JPanel(new GridLayout(0,2, 10, 10));
        panelHolder.setBackground(Color.black);
        InitConfigs();
        UpdateControlPanel(panelHolder);
        createMenuBar();
        JScrollPane scrollPane = new JScrollPane(panelHolder);
        this.add(scrollPane);
        //t.start();
        setVisible(true);
    }
    void UpdateControlPanel(JPanel panelHolder) {
        for (int i = 0; i < configs.size(); i++) {
    		//init panels
        	createNew(i, configs.get(i));
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
            	RemoveSensor(identifier);
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
		//System.out.println("added new sensor to panelholder");
	}
    
    public void createMenuBar() {
    	JMenuBar menubar = new JMenuBar();
        ImageIcon icon = new ImageIcon("exit.png");

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu actions = new JMenu("Actions");
        file.setMnemonic(KeyEvent.VK_A);
        JMenu help = new JMenu("Help");
        file.setMnemonic(KeyEvent.VK_H);

        JMenuItem eMenuItem = new JMenuItem("Exit", icon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                CloseOperation();
            }
        });
        JMenuItem SaveSensors = new JMenuItem("Save Sensor Information", icon);
        SaveSensors.setMnemonic(KeyEvent.VK_S);
        SaveSensors.setToolTipText("Add new sensor");
        SaveSensors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	SaveSensors();
                //System.out.println("Save sensors");
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
                dialog.setTitle("h̵͙̮̥̞̱̺̰͕̟̫̺͓͙̻̣̀ͧ͊̊͛ͥ̑͒̇̿̆͛ͫ̚͝ȇ̴̴̶̡̪̭̞̖̰̫̣̲̞͉̥̝̟̥͓͉͈ͯ̈́ͯͣ̉̓ͧ̒̽̆̾͘ͅl̹̟̺̼͖̪͖̠͎̳̻̯͙͈̯̦̞͌̊ͥ̃̓̾̂ͩ̒ͨ̾̚͜͜͜͡ͅp̢̨̗̞̻̗̗ͨ̔̔ͦ͐ͤͩ̾ͧ̏̽͊̆̆ͥ́͐ͤͅ ̵̜̲͓̲̜̼̦̹̠̬̻̂̅ͯ̆ͤ͆ͧ̿̃ͥ̚̕͢mͨͭ̉͂ͤ̌́͂̔͒̾̔͋͏̸̱̲̜͓͎̞͇̞͢eͫͩ̾͂ͨ̿͒ͩ͂̑͌̚͏̶҉̳͍̗͇͖̱͈͉͇");
                dialog.add(new JLabel(new ImageIcon("resources/help.png")));
                dialog.pack();
                dialog.setLocationByPlatform(true);
                dialog.setVisible(true);
            }
        });
        help.add(Helper);
        actions.add(TurnAllOn);
        actions.add(TurnAllOff);
        file.add(AddNewSensor);
        file.add(SaveSensors);
        file.add(eMenuItem);
        menubar.add(file);
        menubar.add(actions);
        menubar.add(help);
        setJMenuBar(menubar);
    }
    
    
    public void createConfigureMenu(String identifier) {
    	int sensorNumber = configs.indexOf(ConfigFind(identifier));
    	//System.out.println(configs.get(0));
    	if (cf == null || !cf.isEnabled()) {
    		cf = new ConfigureMenu(sensorNumber, this);
    	}
    }
    public void AddSensor() {
    	String address = JOptionPane.showInputDialog("Enter IP Address of sensor", "");
    	//System.out.println(ConfigFind(address));
    	ClientConfig finder = ConfigFind(address);
    	if (finder != null) {
    		//System.out.println("error");
    		JOptionPane.showMessageDialog(null,"Sensor already added, named \"" + finder.name + "\"", "error", JOptionPane.ERROR_MESSAGE, null);

    	} else {
	    	ClientConfig newSensor = new ClientConfig();
	    	newSensor.SetIP(address);
	    	Random r = new Random();
	    	newSensor.SetColor(new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
	    	newSensor.SetName("New Sensor");
	    	configs.add(newSensor);
	    	createNew(controlIndex, newSensor);
	    	refreshSensorList();
	    	//TODO add to database for sensor
    	}
    	
    }
    public void InitConfigs() {
    	//request to database, add sensors
    	//manually adding sensors for now
    	//TODO
    	configs = new ArrayList<ClientConfig>();
    	ClientConfig firstConfig = new ClientConfig();
    	firstConfig.ip = "1234";
    	firstConfig.SetName("First Config");
    	firstConfig.force_off = true;
    	configs.add(firstConfig);
    	
    }
    public void refreshSensorList() {
    	//System.out.println("updating");
    	for (int i = 0; i < configs.size(); i++) {
        	
    		//System.out.println(i);
    		//JPanel myPanel = controlPanels[i];
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
    		JLabel title = (JLabel) myPanel.getComponent(0);
    		title.setText("<html>" + configs.get(i).name + (configs.get(i).isSensorActive()?"":" <br />(Disabled)</html>"));
    	}	
		panelHolder.revalidate();
		panelHolder.repaint();

    
    }
    public void RemoveSensor(String identifier) {
    	int confirm = JOptionPane.showOptionDialog(
                null, "Remove sensor?", 
                "Remove Confirmation", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, null, null);
    	if (confirm == 0) {
    		controlIndex--;
    		ClientConfig cfg = ConfigFind(identifier);
    		String name = cfg.name;
    		int index = configs.indexOf(cfg);
    		panelHolder.remove(index);
    		configs.remove(index);
    		JOptionPane.showMessageDialog(null, "Deleted sensor " + name, "Deleted sensor " + name, JOptionPane.INFORMATION_MESSAGE, null);
    	
    	}
    	refreshSensorList();
    }
    public ClientConfig ConfigFind(String identifier) {
    	//System.out.println(configs);
    	for (int i = 0; i < configs.size(); i++) {
    		//System.out.println(configs.get(i).ip);
    		if ((configs.get(i).ip).equals(identifier)) {
    			return configs.get(i);
    		}
    	}
    	//was not found
    	return null;
    }
    public void SaveSensors() {
    	//save sensors to database
    	//TODO
    	System.out.println("Sensors saved");
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
    	int confirm = JOptionPane.showOptionDialog(
                null, "Save sensor information?", 
                "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, null, null);
           if (confirm == 1) {
           	//close menu, do not close application
              SaveSensors();
           } 
           dispose();
    	
    }
    public void OpenStream(SensorType s, String address) {
		ClientConfig cfg = ConfigFind(address);
    	if (cfg.isSensorActive()) {
    		StreamBox stream = new StreamBox(Streamers, address);
	    	if (s == SensorType.VIDEO) {
	    		stream.setTitle("Video stream on sensor " + address);
		    	stream.add(new VideoStreamBox(address));
		    	Streamers.put(address, stream);

	    	} else {
	    		ClientConfig c = ConfigFind(address);
	    		stream.setTitle(c.sensor_type + " stream on sensor " + address);
		    	stream.add(new ValueStreamBox(c.sensor_type, c.sensing_threshold, address));
		    	Streamers.put(address, stream);
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
	    				SendConfigToSensor(configs.indexOf(cfg));
    				}
    			} else {
    				if (!cfg.force_off){ 
	    				cfg.force_off = true;
	    				cfg.force_on = false;
	    				SendConfigToSensor(configs.indexOf(cfg));
    				}
    			}
    		}
    	} else {
    		ClientConfig cfg = ConfigFind(identifier);
    		if (how) {
				if (!cfg.force_on){ 
    				cfg.force_off = false;
    				cfg.force_on = true;
    	    		SendConfigToSensor(configs.indexOf(cfg));
				}
			} else {
				if (!cfg.force_off){ 
    				cfg.force_off = true;
    				cfg.force_on = false;
    	    		SendConfigToSensor(configs.indexOf(cfg));
				}
			}
    	}
    	SaveSensors();
    	refreshSensorList();
    }
    public void SendConfigToSensor(int index) {
    	//Send a message through the server to update a sensors config
    	//TODO
    	System.out.println("Updating info to a sensor with index "  + index);
    }
    public class Refresher implements ActionListener {
    	
        public void actionPerformed(ActionEvent e) {
        	//refreshSensorList();
        }
    }
    public static void main(String[] args) {
    	@SuppressWarnings("unused")
    	AutoAwareControlPanel ex = new AutoAwareControlPanel();
    	//ex.Notify("1234");
        
    }
    public void Notify(String ip) {
    	ClientConfig c = ConfigFind(ip);
    	//UIManager UI=new UIManager();
    	//Color oldColor = UI.getColor("OptionPane.background");
    	//UIManager.put("OptionPane.background",c.color);
    	//UIManager.put("OptionPane.foreground",c.color);
		JOptionPane.showMessageDialog(this, "Sensor " + c.name + " has reached threshold!");
		//UIManager.put("OptionPanel.background", oldColor );
		//UIManager.put("OptionPanel.foreground", oldColor );
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Message gotMessage = (Message)arg1;
		switch (gotMessage.type) {
			case VIDEO:
				break;
			case LIGHT:
				break;
			case READING:
				float f = ((ReadingMessage)gotMessage).getCurrentThreshold();
				((ValueStreamBox)(Streamers.get(gotMessage.from).getComponent(0))).value = f;
				if (ConfigFind(gotMessage.from).sensing_threshold <= f) Notify(gotMessage.from);
				break;
			case AUDIO:
				break;
			case PICTURE: 
				((VideoStreamBox)(Streamers.get(gotMessage.from).getComponent(0))).SetImage(((PictureMessage)gotMessage).getImage()); 

				break;
			default:
				break;
		}
	}
    
    
}