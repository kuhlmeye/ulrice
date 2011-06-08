package net.ulrice.webstarter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ApplicationProxyDialog extends JDialog implements ActionListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -7573353168859554142L;

	private JLabel proxyHostLabel = new JLabel("Host");
	private JLabel proxyPortLabel = new JLabel("Port");
	private JLabel proxyUserLabel = new JLabel("User");
	private JLabel proxyPassLabel = new JLabel("Password");
	
	private JTextField proxyHost = new JTextField();
	private JTextField proxyPort = new JTextField();
	private JTextField proxyUser = new JTextField();
	private JPasswordField proxyPass = new JPasswordField();

	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");

	public ApplicationProxyDialog(ApplicationFrame applicationFrame) {
		super(applicationFrame);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		proxyHost.setText(System.getProperty("http.proxyHost"));
		proxyPort.setText(System.getProperty("http.proxyPort"));
		proxyUser.setText(System.getProperty("http.proxyUser"));
		proxyPass.setText(System.getProperty("http.proxyPassword"));
		
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new GridLayout(4, 2));
		
		fieldPanel.add(proxyHostLabel);
		fieldPanel.add(proxyHost);

		fieldPanel.add(proxyPortLabel);
		fieldPanel.add(proxyPort);

		fieldPanel.add(proxyUserLabel);
		fieldPanel.add(proxyUser);

		fieldPanel.add(proxyPassLabel);
		fieldPanel.add(proxyPass);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(fieldPanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setTitle("Proxy-Settings");
		setContentPane(panel);

		pack();
		setSize(320, 200);
		setLocationRelativeTo(applicationFrame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (okButton.equals(e.getSource())) {			
			System.setProperty("http.proxyHost", proxyHost.getText());
			System.setProperty("http.proxyPort", proxyPort.getText());
			System.setProperty("http.proxyUser", proxyUser.getText());
			System.setProperty("http.proxyPassword", String.valueOf(proxyPass.getPassword()));
			dispose();
		} else if (cancelButton.equals(e.getSource())) {
			dispose();
		}
	}

}
