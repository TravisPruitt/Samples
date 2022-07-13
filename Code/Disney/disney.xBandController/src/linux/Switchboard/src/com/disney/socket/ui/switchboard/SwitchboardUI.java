package com.disney.socket.ui.switchboard;

import java.awt.EventQueue;
import com.disney.socket.library.*;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JTextField;

public class SwitchboardUI {

	private JFrame frame;
	private JTextField textField;
	private Client client;
	private JTextPane textpane;
	private String serverName;
	private int portNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwitchboardUI window = new SwitchboardUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public SwitchboardUI() throws IOException, InterruptedException {
		initialize();
		displayTextMessage ("Initialize complete.  Calling switchboard.");
		serverName = SwitchboardProperties.getPropertyValue("ServerName");
		portNumber = Integer.parseInt(SwitchboardProperties.getPropertyValue("Port"));
		SetupSwitchboardLib();
		
	}
	
	public void SetupSwitchboardLib() throws IOException, InterruptedException
	{
		client = new Client(serverName, portNumber, new int[]{0});
		client.addMyEventListener(new SwitchBoardMessageListener() {
			public  void switchBoardMessage(SwitchBoardMessageEvent event)
			{
				displayTextMessage(event.getMessage());
			}
		});
	}
	
	public void displayTextMessage(String message)
	{
		
		String s = textpane.getText();
		s += message + "\n";
		
		this.textpane.setText(s);
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 482, 485);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					client.Stop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frame.dispose();
			}
		});
		mnFile.add(mntmQuit);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 10);
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 282, 466, 146);
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(null);
		
		JLabel lblReaderId = new JLabel("Reader ID");
		lblReaderId.setBounds(12, 12, 70, 15);
		panel_1.add(lblReaderId);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(125, 7, 32, 24);
		panel_1.add(comboBox);
		
		JLabel lblLaneNumber = new JLabel("Lane Number");
		lblLaneNumber.setBounds(12, 48, 120, 15);
		panel_1.add(lblLaneNumber);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(125, 43, 32, 24);
		panel_1.add(comboBox_1);
		
		JLabel lblNewLabel = new JLabel("rfid Tag");
		lblNewLabel.setBounds(12, 83, 70, 15);
		panel_1.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(125, 81, 114, 19);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					client.Write("<message echo=\"true\" type=\"2\"><rfid reader=\"123\" lane=\"1\">555123</rfid><guest/><status valid=\"0\">DAP_PARK_ENT_ERRO</status></message>\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSend.setBounds(337, 112, 117, 25);
		panel_1.add(btnSend);
		
		JLabel lblNewLabel_1 = new JLabel("Message Type");
		lblNewLabel_1.setBounds(12, 117, 102, 15);
		panel_1.add(lblNewLabel_1);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(125, 112, 32, 24);
		panel_1.add(comboBox_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 10, 0, 198);
		frame.getContentPane().add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textpane = textPane;
		textPane.setBounds(10, 22, 444, 248);
		textPane.setName("messages");
		frame.getContentPane().add(textPane);
		
	}
}
