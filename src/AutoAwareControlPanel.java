import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class AutoAwareControlPanel extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel[] controlPanels;
	private int maxPanels = 10;
	private ConfigureMenu cf;
    public AutoAwareControlPanel() {
        initUI();
    }

    private void initUI() {
    	//GridLayout experimentLayout = new GridLayout(5,3);
    	
        setTitle("AutoAware Control Panel");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panelHolder = new JPanel(new GridLayout(5,3, 10, 10));
        panelHolder.setBackground(Color.black);
       // panelHolder.setLayout(new WrapLayout());
       // JPanel panelHolder = new JPanel(new BoxLayout(panelHolder, BoxLayout.PAGE_AXIS));
       //scrollPane.setLayout(new ScrollPaneLayout());
        UpdateControlPanel(panelHolder);
        createMenuBar();
        JScrollPane scrollPane = new JScrollPane(panelHolder);
        this.add(scrollPane);
    }
    void UpdateControlPanel(JPanel panelHolder) {
    	controlPanels = new JPanel[maxPanels];
        for (int i = 0; i < maxPanels; i++) {
    		//init panels
        	if (i > 6) { //pretend 6 is the number of active sensors
        		JPanel myPanel = controlPanels[i];
        		myPanel = new JPanel();
        		myPanel.setBackground(Color.gray);
        		myPanel.add(new JLabel("Empty", JLabel.CENTER));
        		//myPanel.setSize(300, 200);
        		panelHolder.add(myPanel);
        	} else {
        		JPanel myPanel = controlPanels[i];
        		myPanel = new JPanel(new GridLayout(2,2,10,10));
        		myPanel.setBackground(Color.magenta);
        		final int num = i;
        		
        		
        		JButton configure = new JButton("Configure");
        		configure.setPreferredSize(new Dimension(120, 30));
        		configure.setVerticalTextPosition(AbstractButton.CENTER);
        		configure.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        		configure.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                       System.out.println("Open configure menu for sensor " + num);
                       createConfigureMenu(num);
                    }
                });
        		
        		JButton remove = new JButton("Remove");
        		remove.setPreferredSize(new Dimension(120, 30));
        		remove.setVerticalTextPosition(AbstractButton.CENTER);
        		remove.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        		remove.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                       System.out.println("Remove sensor " + num);
                    }
                });
        		remove.setOpaque( true );
        		remove.setBackground(Color.red);
        		remove.setForeground( Color.WHITE );
        		
        		JButton toggle = new JButton("Enable");
        		toggle.setPreferredSize(new Dimension(120, 30));
        		toggle.setVerticalTextPosition(AbstractButton.CENTER);
        		toggle.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        		toggle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        System.out.println("Enable/disable sensor " + num);
                        
                    }
                });
        		
        		JButton icon = new JButton(new ImageIcon("resources/mic.png"));
        		icon.setVerticalTextPosition(AbstractButton.CENTER);
        		icon.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        		icon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        System.out.println("Open audio stream for mic " + num);
                    }
                });
        		JLabel title = new JLabel("   Sensor " + i, JLabel.CENTER);
        		title.setFont(new Font(title.getName(), Font.BOLD, 30));
        		myPanel.add(title);
        		JPanel filler = new JPanel();
        		filler.setBackground(Color.magenta);
        		JPanel buttonBorderPanel = new JPanel(); //something they can all use
        		buttonBorderPanel.setLayout(new GridLayout(3,1, 10, 10));
        		//uttonBorderPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        		buttonBorderPanel.setBackground(Color.magenta);
        		buttonBorderPanel.add(configure);
        		buttonBorderPanel.add(toggle);
        		buttonBorderPanel.add(remove);
        		//buttonBorderPanel
        		JPanel iconBorderPanel = new JPanel();
        		iconBorderPanel.setLayout(new FlowLayout());
        		iconBorderPanel.setBackground(Color.magenta);
        		iconBorderPanel.add(icon);
        		myPanel.add(filler);
        		myPanel.add(buttonBorderPanel);
        		myPanel.add(iconBorderPanel);
        		panelHolder.add(myPanel);
        	}
    	}
    }
    public void createMenuBar() {
    	JMenuBar menubar = new JMenuBar();
        ImageIcon icon = new ImageIcon("exit.png");

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu actions = new JMenu("Actions");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu help = new JMenu("Help");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Exit", icon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        JMenuItem AddNewSensor = new JMenuItem("Add New Sensor", icon);
        AddNewSensor.setMnemonic(KeyEvent.VK_E);
        AddNewSensor.setToolTipText("Add new sensor");
        AddNewSensor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Open sensor dialog");
            }
        });
        JMenuItem TurnAllOff = new JMenuItem("Disable All Sensors", icon);
        TurnAllOff.setMnemonic(KeyEvent.VK_E);
        TurnAllOff.setToolTipText("Disable all sensors");
        TurnAllOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Turn all off");
            }
        });
        JMenuItem TurnAllOn = new JMenuItem("Enable All Sensors", icon);
        TurnAllOn.setMnemonic(KeyEvent.VK_E);
        TurnAllOn.setToolTipText("Enable all sensors");
        TurnAllOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Turn all on");
            }
        });
        JMenuItem Helper = new JMenuItem("Help Menu", icon);
        Helper.setMnemonic(KeyEvent.VK_E);
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
        file.add(eMenuItem);
        menubar.add(file);
        menubar.add(actions);
        menubar.add(help);
        setJMenuBar(menubar);
    }
    
    
    public void createConfigureMenu(int sensorNumber) {
    	if (cf == null || !cf.inUse)
    		cf = new ConfigureMenu(sensorNumber);
    
    }

    public static void main(String[] args) {
    	AutoAwareControlPanel ex = new AutoAwareControlPanel();
        ex.setVisible(true);
        
    }
}