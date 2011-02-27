package net.ulrice.webstarter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * Main frame of the client application displaying the webstarter process.
 * 
 * @author christof
 */
public class ApplicationFrame extends JFrame implements ActionListener, ItemListener {
	
	private static final long serialVersionUID = 5214287073693526828L;

	public static final String START_CMD = "START";
	public static final String CANCEL_CMD = "CANCEL";

	private JProgressBar globalProgress;
	private JProgressBar taskProgress;
	private JTextArea messageArea;
	private JLabel applicationLabel;
	private JComboBox applicationChooser;
	private JLabel userIdLabel;
	private JTextField userIdField;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton startButton;
	private JButton cancelButton;
	private JButton proxyButton;
	private JScrollPane messageAreaScroller;
	private ImageIcon defaultAppImage;
	private JToggleButton messageToggleButton;

	public ApplicationFrame() {
		this.globalProgress = new JProgressBar();
		this.globalProgress.setString("");
		this.globalProgress.setStringPainted(true);

		this.taskProgress = new JProgressBar();
		this.taskProgress.setString("");
		this.taskProgress.setStringPainted(true);

		this.messageArea = new JTextArea();
		this.messageArea.setEditable(false);
		this.messageArea.setEnabled(false);

		this.applicationLabel = new JLabel();
		this.applicationLabel.setPreferredSize(new Dimension(210, 210));
		this.applicationLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		this.applicationLabel.setBackground(Color.WHITE);
		this.applicationLabel.setOpaque(true);
		this.applicationLabel.setHorizontalAlignment(JLabel.CENTER);
		this.applicationLabel.setHorizontalTextPosition(JLabel.CENTER);

		this.applicationChooser = new JComboBox();
		this.applicationChooser.addItemListener(this);

		this.userIdLabel = new JLabel("User-Id");
		this.userIdField = new JTextField();

		this.passwordLabel = new JLabel("Password");
		this.passwordField = new JPasswordField();

		this.startButton = new JButton("Start");
		this.startButton.setActionCommand(START_CMD);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.setActionCommand(CANCEL_CMD);

		this.proxyButton = new JButton("...");
		this.proxyButton.setToolTipText("Proxy Settings");

		JPanel applicationChooserPanel = new JPanel();
		applicationChooserPanel.setLayout(new BorderLayout(5, 5));
		applicationChooserPanel.add(applicationChooser, BorderLayout.CENTER);
		applicationChooserPanel.add(proxyButton, BorderLayout.EAST);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridBagLayout());
		gbc.weightx = 1.0d;
		gbc.anchor = GridBagConstraints.EAST;

		gbc.insets = new Insets(30, 2, 2, 2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		loginPanel.add(userIdLabel, gbc);
		gbc.gridx = 1;
		loginPanel.add(userIdField, gbc);

		gbc.insets = new Insets(2, 2, 10, 2);
		gbc.gridy = 1;
		gbc.gridx = 0;
		loginPanel.add(passwordLabel, gbc);
		gbc.gridx = 1;
		loginPanel.add(passwordField, gbc);

		gbc.insets = new Insets(2, 20, 2, 10);
		gbc.gridx = 0;
		gbc.gridy = 2;
		loginPanel.add(startButton, gbc);
		gbc.insets = new Insets(2, 10, 2, 20);
		gbc.gridx = 1;
		loginPanel.add(cancelButton, gbc);

		gbc = new GridBagConstraints();

		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new GridBagLayout());
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.weightx = 1.0d;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		upperPanel.add(applicationChooserPanel, gbc);

		gbc.weightx = 0.0d;
		gbc.gridwidth = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		upperPanel.add(applicationLabel, gbc);
		gbc.weightx = 1.0d;
		gbc.gridheight = 1;

		gbc.gridx = 1;
		gbc.gridy = 1;
		upperPanel.add(globalProgress, gbc);
		gbc.gridy = 2;
		upperPanel.add(taskProgress, gbc);
		gbc.gridy = 3;
		upperPanel.add(loginPanel, gbc);

		messageAreaScroller = new JScrollPane(messageArea);
		messageAreaScroller.setPreferredSize(new Dimension(getWidth(), 150));

		messageToggleButton = new JToggleButton();
		messageToggleButton.setIcon(new ImageIcon(ApplicationFrame.class.getResource("up.png")));
		messageToggleButton.setSelectedIcon(new ImageIcon(ApplicationFrame.class.getResource("down.png")));
		messageToggleButton.addActionListener(this);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		getContentPane().add(messageToggleButton, BorderLayout.SOUTH);

		URL defaultIconUrl = ApplicationFrame.class.getResource("default_icon.png");
		if (defaultIconUrl != null) {
			setIconImage(new ImageIcon(defaultIconUrl).getImage());
		}
		
		URL defaultAppImageUrl = ApplicationFrame.class.getResource("default_image.png");
		if(defaultAppImageUrl != null) {
			defaultAppImage = new ImageIcon(defaultAppImageUrl);
		}
		applicationLabel.setIcon(defaultAppImage);
		
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EXIT");

        ActionMap actionMap = getRootPane().getActionMap();

        actionMap.put("EXIT", new AbstractAction() {
            /** Default generated serial version uid. */
            private static final long serialVersionUID = 1326732178398549894L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(startButton);
        
		pack();		
		setSize(500, getHeight());
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public JProgressBar getGlobalProgress() {
		return globalProgress;
	}

	public JProgressBar getTaskProgress() {
		return taskProgress;
	}

	/**
	 * Append the message in the log window. 
	 * 
	 * @param message The message that should be displayed in the text area.
	 */
	public void appendMessage(String message) {
		messageArea.append(message);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				messageArea.setCaretPosition(messageArea.getText().length());
			}
		});
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}

	public JButton getStartButton() {
		return startButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public JButton getProxyButton() {
		return proxyButton;
	}

	public JTextField getUserIdField() {
		return userIdField;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(messageToggleButton.isSelected()) {
			getContentPane().add(messageAreaScroller, BorderLayout.CENTER);
		} else {
			getContentPane().remove(messageAreaScroller);	
		}
		setSize(500, getPreferredSize().height);
	}
	
	public ApplicationDescription getSelectedApplication() {
		return (ApplicationDescription) applicationChooser.getSelectedItem();
	}
	
	public void addApplication(ApplicationDescription appDescription) {
		applicationChooser.addItem(appDescription);
	}
	public void setSelectedApplication(String applicationId) {
		if(applicationId == null) {
			return;
		}
		
		for(int i = 0; i < applicationChooser.getItemCount(); i++) {
			ApplicationDescription app = (ApplicationDescription) applicationChooser.getItemAt(i);
			if(app.getId().equals(applicationId)) {
				applicationChooser.setSelectedIndex(i);
				return;
			}
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) {		
		if(ItemEvent.SELECTED == e.getStateChange()) {
			// Adapt display settings to selected applications.
			ApplicationDescription application = getSelectedApplication();
			userIdField.setEnabled(application.isNeedsLogin());
			passwordField.setEnabled(application.isNeedsLogin());
			setTitle("Start: " + application.getName());
			if(application.getIcon() != null) {
				applicationLabel.setIcon(application.getIcon());
			}
			else {
				applicationLabel.setIcon(defaultAppImage);
			}
			applicationLabel.invalidate();
		}
	}
}
