
package me.hwr.ICQ.Client;

import java.util.Date;
import java.util.logging.*;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Java2_Ch4
 * 
 * @author HowardWu
 */
public class ClientUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public ClientUI() {
		initComponents();
	}

	private void initComponents() {

		jScrollPaneMessages = new JScrollPane();
		jTextAreaMessages = new JTextArea();
		jScrollPaneInput = new JScrollPane();
		jTextAreaInput = new JTextArea();
		jButtonSend = new JButton();
		jButtonConnect = new JButton();
		jTextFieldIP = new JTextField();
		jTextFieldPort = new JTextField();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Client");
		setLocationByPlatform(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				formWindowClosing();
			}
		});

		jTextAreaMessages.setEditable(false);
		jTextAreaMessages.setColumns(20);
		jTextAreaMessages.setLineWrap(true);
		jTextAreaMessages.setRows(5);
		jScrollPaneMessages.setViewportView(jTextAreaMessages);

		jTextAreaInput.setEditable(false);
		jTextAreaInput.setColumns(20);
		jTextAreaInput.setLineWrap(true);
		jTextAreaInput.setRows(5);
		jTextAreaInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				jTextAreaInputKeyPressed();
			}
		});

		jScrollPaneInput.setViewportView(jTextAreaInput);

		jButtonSend.setText("Send");
		jButtonSend.setEnabled(false);
		jButtonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonSendActionPerformed();
			}
		});

		jButtonConnect.setText("Connect");
		jButtonConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jButtonConnectActionPerformed();
			}
		});

		jTextFieldIP.setText("127.0.0.1");

		jTextFieldPort.setText("60987");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addContainerGap()
								.addGroup(layout.createParallelGroup(Alignment.LEADING)
										.addComponent(jScrollPaneMessages)
										.addComponent(jScrollPaneInput, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
										.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
												.addComponent(jTextFieldIP, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(jTextFieldPort, GroupLayout.PREFERRED_SIZE, 48,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(jButtonConnect)
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(jButtonSend)))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(jScrollPaneMessages, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(jScrollPaneInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(jButtonSend)
						.addComponent(jButtonConnect)
						.addComponent(jTextFieldIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(jTextFieldPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addGap(12, 12, 12)));

		pack();
	}

	private void jTextAreaInputKeyPressed() {
		// start our set up of key bindings

		// to get the correct InputMap
		int condition = JComponent.WHEN_FOCUSED;
		// get our maps for binding from the chatEnterArea JTextArea
		InputMap inputMap = jTextAreaInput.getInputMap(condition);
		ActionMap actionMap = jTextAreaInput.getActionMap();

		// the key stroke we want to capture
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		// tell input map that we are handling the enter key
		inputMap.put(enterStroke, enterStroke.toString());

		// tell action map just how we want to handle the enter key
		actionMap.put(enterStroke.toString(), new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Connected) {
					getInput();
				} else if (!Connected)
					startClient();
			}
		});
	}

	static boolean endTheWorld;

	static boolean Connected;

	static void setConnected(boolean Connected) {
		ClientUI.Connected = Connected;
	}

	private static void formWindowClosing() {
		if (Connected)
			ClientWriter.inputMessage("ZW5kVGhlV29ybGQ=");
		endTheWorld = true;
	}

	private static void startClient() {
		String IP = jTextFieldIP.getText();
		int Port = Integer.parseInt(jTextFieldPort.getText());
		me.hwr.ICQ.Client.Client.startClient(IP, Port);
	}

	private void jButtonConnectActionPerformed() {
		startClient();
		jTextAreaInput.requestFocus();// requestFocus
	}

	private static void getInput() {
		String message = jTextAreaInput.getText();
		if (Connected && !(message.length() == 0)) {
			ClientWriter.inputMessage(message);
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
			DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
			Date date = new Date();
			String time = dateFormat.format(date) + " " + timeFormat.format(date);
			jTextAreaMessages.setText(jTextAreaMessages.getText() + time + "\nClient:\n" + message + "\n");
			jTextAreaInput.setText(null);
		}
	}

	private static void jButtonSendActionPerformed() {
		if (Connected) {
			getInput();
			jTextAreaInput.requestFocus();// requestFocus
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		EventQueue.invokeLater(() -> {
			new ClientUI().setVisible(true);
			jTextAreaInput.requestFocus();// requestFocus
		});
	}

	public static JButton jButtonConnect;
	static JButton jButtonSend;
	private JScrollPane jScrollPaneInput;
	private JScrollPane jScrollPaneMessages;
	static JTextArea jTextAreaInput;
	static JTextArea jTextAreaMessages;
	private static JTextField jTextFieldIP;
	private static JTextField jTextFieldPort;

	public ClientUI(String message) {
		setMessage(message);
	}

	final static void setMessage(String message) {
		jTextAreaMessages.setText(message);
		jTextAreaMessages.setCaretPosition(jTextAreaMessages.getText().length());
	}
}