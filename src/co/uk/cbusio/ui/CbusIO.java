/*
 * (c) Ian Hogg 2017
 */
/* This work is licensed under the:
      Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
   To view a copy of this license, visit:
      http://creativecommons.org/licenses/by-nc-sa/4.0/
   or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

   License summary:
    You are free to:
      Share, copy and redistribute the material in any medium or format
      Adapt, remix, transform, and build upon the material

    The licensor cannot revoke these freedoms as long as you follow the license terms.

    Attribution : You must give appropriate credit, provide a link to the license,
                   and indicate if changes were made. You may do so in any reasonable manner,
                   but not in any way that suggests the licensor endorses you or your use.

    NonCommercial : You may not use the material for commercial purposes. **(see note below)

    ShareAlike : If you remix, transform, or build upon the material, you must distribute
                  your contributions under the same license as the original.

    No additional restrictions : You may not apply legal terms or technological measures that
                                  legally restrict others from doing anything the license permits.

   ** For commercial use, please contact the original copyright holder(s) to agree licensing terms

    This software is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE

**************************************************************************************************************
  Note:   This source code has been written using a tab stop and indentation setting
          of 4 characters. To see everything lined up correctly, please set your
          IDE or text editor to the same settings.
*******************************************************************************************************/
package co.uk.cbusio.ui;

/**
 * Provides the UI for the CBUS sniffer and sender.
 * 
 * Allows a user to capture CBUS events and also to transmit events onto the CBUS.
 * When the CbusIO program is started it is necessary to connect to the CBUS using the serial port
 * connected to the CAN_USB or CAN_RS module. The list of available serial ports is listed under the connect menu.
 * 
 * At the top of the window is a toolbar which allows events to be constructed by specifying:
 * <ul>
 * <li>MjPri</li>
 * <li>MinPri</li>
 * <li>CAN_ID</li>
 * <li>OPC</li>
 * </ul>
 * Other parameters can be set dependent upon the OPC selected. To transmit the event press the "Send" button.
 * <p>
 * The main part of the window shows a log of transmitted events in black, received events in green and serial port conditions
 * in red.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.driver.CbusDriverException;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.sniffer.CbusEvent.MinPri;
import co.uk.ccmr.cbus.sniffer.CbusEvent.MjPri;
import co.uk.ccmr.cbus.sniffer.Opc;
import co.uk.ccmr.cbus.sniffer.ParamNameAndLen;
import co.uk.ccmr.cbus.sniffer.Util;
import co.uk.ccmr.cbus.tcpserver.TcpServer;
import co.uk.ccmr.cbus.util.OptionsImpl;
import co.uk.ccmr.cbusio.script.MacroChangeListener;
import co.uk.ccmr.cbusio.script.ScriptHandler;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMacro;

public class CbusIO implements ActionListener, ItemListener, CbusReceiveListener, MacroChangeListener {
	protected static final String VERSION = "2.1b";
	private static final String DEFAULT_SCRIPT = "/defaultScript.sct";
	private static final int MAX_LINES = 100;
	private static int port = 5550;
	private JPanel panel;
	private JComboBox<Opc> opcCombo;
	private JComboBox<MjPri> mjPriCombo;
	private JComboBox<MinPri> minPriCombo; 
	private JFrame frame;
	private List<JTextField> textFields;
	private JTextPane textPane;
	private StyledDocument log;
	private JTextField canid;
	private JPanel params;
	private CbusDriver theDriver;
	private TcpServer tcpServer;
	private JComboBox<ASTMacro> macroCombo;
	private ScriptHandler scriptHandler;
	private String portText = null; //"/dev/ttyACM0";		// e.g. /dev/ttyACM0
	private ButtonGroup connectGroup;
	private CbusIO self = null;
	private Container connectMenu;
	private AttributeSet redAset;
	
	/**
	 * Basic constructor.
	 */
	@SuppressWarnings("unchecked")
	public CbusIO(String [] args) {
		self = this;
		
		textPane = new JTextPane();
		log = textPane.getStyledDocument();
		log.addDocumentListener(new LimitLinesDocumentListener(MAX_LINES));
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	redAset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, Color.RED);
		// try to load the driver
		String clazz = OptionsImpl.getOptions(log, args).getDriver();
		Class<CbusDriver> clz = null;
		try {
			clz = (Class<CbusDriver>) Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("No class:"+clazz);
			System.exit(1);
		}
		if (clz == null) {
			System.err.println("No class:"+clazz);
			System.exit(1);
		}
		try {
			theDriver = clz.newInstance();
			theDriver.init(0, log, OptionsImpl.getOptions(log, args));
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.err.println("Can't instantiate class:"+clazz);
			System.exit(1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.println("Can't instantiate class:"+clazz);
			System.exit(1);
		}
		if (theDriver == null) {
			System.err.println("Can't instantiate class:"+clazz);
			System.exit(1);
		} else {
			System.out.println("Using driver "+clazz);
			try {
				log.insertString(log.getLength(), "Using driver "+clazz+"\n", redAset);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		String portName = OptionsImpl.getOptions(log, args).getAutoConnect();
		if (portName != null) {
			try {
				log.insertString(log.getLength(), "Auto Opening port "+portName+"\n", redAset);
			} catch (BadLocationException e2) {
				e2.printStackTrace();
			}
			
			
			try {
				theDriver.connect(portName);
			} catch (CbusDriverException e2) {
				e2.printStackTrace();
				try {
					log.insertString(log.getLength(), e2.getMessage()+"\n", null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Construct the UI.
	 */
	private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("CBUS I/O");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create the menu bar.  Make it have a green background.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(200, 20));
        
        //File menu
        JMenu menu = new JMenu("File");
        JMenuItem mi;
        mi = new JMenuItem("Load...");
        mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 JFileChooser chooser = new JFileChooser();
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "Script files", "sct");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(frame);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	// parse the script file
				        scriptHandler.load(chooser.getSelectedFile(), log);
				    }
			}
        	
        });
        menu.add(mi);
        menu.addSeparator();
        JMenu submenu = new JMenu("Options");
        
        
        
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Decimal");
        if (OptionsImpl.getOptions(log, null).getBase() == 10) {
        	rbMenuItem.setSelected(true);
        }
        rbMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 OptionsImpl.getOptions(log, null).setBase(10);
			}
        });
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Hexadecimal");
        if (OptionsImpl.getOptions(log, null).getBase() == 16) {
        	rbMenuItem.setSelected(true);
        }
        rbMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 OptionsImpl.getOptions(log, null).setBase(16);
			}
        });

        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        menu.add(submenu);
        
        mi = new JMenuItem("Define Port");
        mi.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		portText = JOptionPane.showInputDialog("Enter port device name");
        		if ((portText != null) && (portText.length() > 0)) {
                	JRadioButtonMenuItem item = new JRadioButtonMenuItem(portText);
                	item.addItemListener(self);
                	connectGroup.add(item);;
                	connectMenu.add(item);
                }
        	}
        });
        menu.add(mi);
        
        
        menu.addSeparator();
        mi = new JMenuItem("Exit");
        mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tcpServer.close();
				theDriver.close();
				System.exit(0);
			}
        	
        });
        menu.add(mi);
        menuBar.add(menu);
        
        
      //Edit menu
        menu = new JMenu("Edit");
        mi = new JMenuItem("Clear log");
        mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 try {
					log.remove(0, log.getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
        	
        });
        menu.add(mi);
        menuBar.add(menu);
        
        // connect menu
        connectMenu = new JMenu("Connect");
        String[] portList;
        connectGroup = new ButtonGroup();
        portList = theDriver.getPortNames();
        if (portList.length > 0) portText=null;
        System.out.println("Portlist len="+portList.length);
        for (String p : portList) {
        	JRadioButtonMenuItem item = new JRadioButtonMenuItem(p);
        	item.addItemListener(this);
        	connectGroup.add(item);
        	connectMenu.add(item);
        }
        if ((portText != null) && (portText.length() > 0)) {
        	JRadioButtonMenuItem item = new JRadioButtonMenuItem(portText);
        	item.addItemListener(this);;
        	connectGroup.add(item);;
        	connectMenu.add(item);
        }
        menuBar.add(connectMenu);
        
        //Help menu
        menuBar.add(Box.createHorizontalGlue());
        menu = new JMenu("Help");
        mi = new JMenuItem("Instructions");
        mi.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Connect using a serial (COM) port to CAN-USB or CAN-RS.\n"
						+ "Connect using TCP/IP to CANETHER.\n"
						+ "Construct message events to send and press the Send button.\n"
						+ "The log screen shows all messages sent (in black) and received (in green).\n\n"
						+ "Accepts TCP/IP connections on port "+port+".\n\n"
						+ "Automatic operation can be defined in scripts.");
			}
        	
        });
        
        menu.add(mi);
        mi = new JMenuItem("About");
        mi.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "CBUS I/O program.\nVersion:"+VERSION+"\nPart of the CCMR suite by Ian Hogg");	
			}});
        menu.add(mi);
        menuBar.add(menu);
        
        panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(1200, 450));
        
        // the buttons to construct a message
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel label = new JLabel("Mj Pri:");
        buttons.add(label);
        mjPriCombo = new JComboBox<MjPri>(MjPri.values());
        buttons.add(mjPriCombo);
		
		label = new JLabel("Min Pri:");
		buttons.add(label);
		minPriCombo = new JComboBox<MinPri>(MinPri.values());
		buttons.add(minPriCombo);
        
		label = new JLabel("CAN_ID:");
		buttons.add(label);
		if (OptionsImpl.getOptions(log, null).getBase() == 16) {
			canid = new JTextField("7E", 3);
		} else {
			canid = new JTextField("126", 3);
		}
		buttons.add(canid);
        
		label = new JLabel("OPC:");
		buttons.add(label);
		opcCombo = new JComboBox<Opc>(Opc.values());
		buttons.add(opcCombo);
		opcCombo.addActionListener(this);
		
		params = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(params);
		
		setupParams(Opc.ACK);
		JButton button = new JButton("Send");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				/* Pressing the send button transmits the event */
				String text = null;
				try {
					Opc opc = (Opc) opcCombo.getSelectedItem();
					int idx = 0;
					CbusEvent cmd = new CbusEvent();
					cmd.setOpc(opc);
					text = canid.getText();
					cmd.setCANID(Util.getNum(text, OptionsImpl.getOptions(log, null).getBase()));
					cmd.setMjPri((MjPri)(mjPriCombo.getSelectedItem()));
					cmd.setMinPri((MinPri)(minPriCombo.getSelectedItem()));
					idx = 0;
					int di = 0;
					for (ParamNameAndLen p : opc.getParams()) {
						JTextField tf = textFields.get(idx);
						text = tf.getText();
						int v = Util.getNum(text, OptionsImpl.getOptions(log, null).getBase());
						for (int l=p.getLen()-1; l>=0; l--) {
							int vv = v >> (l*8);
							cmd.setData(di, vv&0xFF);
							di++;
						}
						idx++;
					}
				
					theDriver.queueForTransmit(cmd);
				} catch (NumberFormatException nfe) {
					try {
						log.insertString(log.getLength(), "Invalid number format:"+text+"\n", redAset);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}

		});
		buttons.add(button);
		
		buttons.add(Box.createHorizontalStrut(20));
		
		macroCombo = new JComboBox<ASTMacro>();
		buttons.add(macroCombo);
		button = new JButton("Do");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// do macro defined in macroCombo.
				scriptHandler.runMacro((ASTMacro) macroCombo.getSelectedItem());
			}});
		buttons.add(button);
		
		panel.add(buttons, BorderLayout.NORTH);
		
		
		textPane.setPreferredSize(new Dimension(1100,450));
		DefaultCaret caret = (DefaultCaret)textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scroll = new JScrollPane(textPane);
		panel.add(scroll, BorderLayout.CENTER);
 
        //Set the menu bar and add the label to the content pane.
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    	//theDriver = new SerialCbusDriver();
    	//theDriver.init(0, log, OptionsImpl.getOptions());
    	theDriver.addListener(this);
    	
    	try {
			tcpServer = TcpServer.getInstance(theDriver, port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	scriptHandler = new ScriptHandler(theDriver, frame, log);
		scriptHandler.addMacroChangeListener(this);
		theDriver.addListener(scriptHandler);
		
		String scriptFilename = System.getProperty("user.dir");
		scriptFilename += DEFAULT_SCRIPT;
		scriptHandler.load(new File(scriptFilename), log);
    }
 
	/** 
	 * Main method just constructs the UI and let it do its job.
	 * 
	 * @param args command line arguments are ignored
	 */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	if (args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	CbusIO cb = new CbusIO(null);
                cb.createAndShowGUI();
            }
        });
    }

    /**
     * Called when the OPC is changed.
     * Rebuild the OPC's parameters on the UI.
     */
	@Override
	public void actionPerformed(ActionEvent event) {
		Opc opc = (Opc) opcCombo.getSelectedItem();
		setupParams(opc);
	}
	
	/**
	 * Rebuild the OPC's parameters on the UI.
	 * Get the parameter list from the OPC and rebuild the UI based on the parameters.
	 * @param opc
	 */
	private void setupParams(Opc opc) {
		params.removeAll();
		textFields = new ArrayList<JTextField>();
		for (ParamNameAndLen pp : opc.getParams()) {
			JLabel label = new JLabel(pp.getName());
			JTextField text = new JTextField(pp.getLen() *2 + 2);
			params.add(label);
			params.add(text);
			textFields.add(text);
		}
		frame.pack();
	}

	/**
	 * Called when serial port is changed.
	 * If a serial port is currently open then close it. Open the new one selected.
	 * If there currently isn't a reader thread then create one and run it.
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		JRadioButtonMenuItem rb = (JRadioButtonMenuItem) event.getItem();
		String portName = rb.getText();
		
		switch(event.getStateChange()) {
		case ItemEvent.SELECTED:
			try {
				log.insertString(log.getLength(), "Opening port "+portName+"\n", redAset);
			} catch (BadLocationException e2) {
				e2.printStackTrace();
			}
			
			
			try {
				theDriver.connect(portName);
			} catch (CbusDriverException e2) {
				e2.printStackTrace();
				try {
					log.insertString(log.getLength(), e2.getMessage(), null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				
				rb.setSelected(false);
			}
			break;
		case ItemEvent.DESELECTED:
			theDriver.close();
			break;
		}
		
	}

	@Override
	public void receiveMessage(CbusEvent cmd) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, new Color(0,0x80,0));
		try {
			log.insertString(log.getLength(), "< "+cmd.toString()+"\n", aset);
			log.insertString(log.getLength(), "< "+cmd.dump(OptionsImpl.getOptions(log, null).getBase())+"\n", aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (tcpServer != null) tcpServer.receiveString(cmd.toString());
	}
	@Override
	public void receiveString(String input) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
    	AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
    	                                        StyleConstants.Foreground, new Color(0x80,0,0));
		try {
			log.insertString(log.getLength(), "< "+input+"\n", aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}

	public void macrosChanged() {
		System.out.println("Updating macro list");
		macroCombo.removeAllItems();
		for (ASTMacro m : scriptHandler.getMacros()) {
			macroCombo.addItem(m);
		}
	}


}
