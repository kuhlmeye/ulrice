package net.ulrice.recorder;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog for controlling the recorder
 * 
 * @author christof
 */
public class RecorderDialog extends JDialog {
	
	private JTextField title;
	private JTextArea description;
	
	private JLabel screenshot;
	private JTextField screenTitle;
	private JTextArea screenDescription;
	
	private JButton recordButton;
	private JButton stopButton;
	private JButton saveButton;

}
