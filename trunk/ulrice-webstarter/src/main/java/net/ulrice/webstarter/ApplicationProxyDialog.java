package net.ulrice.webstarter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new GridLayout(6, 2));
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
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
