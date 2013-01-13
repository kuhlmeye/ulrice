package net.ulrice.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

/**
 * Dialog for controlling the recorder
 * 
 * @author christof
 */
public class RecorderView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField titleField;
	private JTextField categoryField;
	private JTextArea descriptionArea;

	private JLabel screenshot;
	private JTextField screenTitle;
	private JTextArea screenDescription;

	private JToggleButton recordButton;
	private JButton stopButton;
	private JButton saveButton;

	public RecorderView() {
		super();

		titleField = new JTextField();
		categoryField = new JTextField();
		descriptionArea = new JTextArea();
		descriptionArea.setRows(5);

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2),
				BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2))));
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.add(createComponentWithLabel("Title", titleField, LEFT_ALIGNMENT));
		titlePanel.add(createComponentWithLabel("Category", categoryField, LEFT_ALIGNMENT));
		titlePanel.add(createComponentWithLabel("Description", new JScrollPane(descriptionArea), LEFT_ALIGNMENT));

		screenshot = new JLabel();
		screenshot.setOpaque(true);
		screenshot.setHorizontalAlignment(JLabel.CENTER);
		screenshot.setVerticalTextPosition(JLabel.BOTTOM);
		screenshot.setHorizontalTextPosition(JLabel.CENTER);
		screenshot.setBackground(Color.BLACK);
		screenshot.setAlignmentX(CENTER_ALIGNMENT);
		screenshot.setAlignmentY(CENTER_ALIGNMENT);
		screenshot.setMinimumSize(new Dimension(320, 200));
		screenshot.setPreferredSize(new Dimension(320, 200));

		screenTitle = new JTextField();
		screenDescription = new JTextArea();
		screenDescription.setRows(5);

		JPanel screenPanel = new JPanel(new BorderLayout());
		screenPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		screenPanel.add(createComponentWithLabel("Screen Title", screenTitle, LEFT_ALIGNMENT), BorderLayout.NORTH);
		screenPanel.add(screenshot, BorderLayout.CENTER);
		screenPanel.add(createComponentWithLabel("Screen Description", new JScrollPane(screenDescription), LEFT_ALIGNMENT), BorderLayout.SOUTH);

		stopButton = new JButton(createIcon("stop.png"));
		recordButton = new JToggleButton(createIcon("record.png"));
		saveButton = new JButton(createIcon("save.png"));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(recordButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(saveButton);

		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(screenPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		reinit();
	}
	
	public void reinit() {
		getRecordButton().setSelected(false);
		getScreenTitle().setEnabled(false);
		getScreenDescription().setEnabled(false);
		
		getTitleField().setText("");
		getCategoryField().setText("");
		getDescriptionArea().setText("");
		getScreenshot().setIcon(null);
		getScreenTitle().setText("");
		getScreenDescription().setText("");
	}

	public void showNewScreen(BufferedImage image) {
		getScreenTitle().setEnabled(true);
		getScreenDescription().setEnabled(true);

		getScreenshot().setIcon(new ImageIcon(image));
		getScreenTitle().setText("");
		getScreenDescription().setText("");
		invalidate();
		repaint();
	}

	private Icon createIcon(String iconName) {
		URL iconUrl = getClass().getResource(iconName);
		if (iconUrl != null) {
			return new ImageIcon(iconUrl);
		}

		return null;
	}

	public JPanel createComponentWithLabel(String labelText, JComponent component, float componentAlignment) {
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setLabelFor(component);

		component.setAlignmentX(componentAlignment);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(component);

		return panel;
	}

	public JLabel getScreenshot() {
		return screenshot;
	}

	public JTextField getTitleField() {
		return titleField;
	}

	public JTextField getCategoryField() {
		return categoryField;
	}

	public JTextArea getDescriptionArea() {
		return descriptionArea;
	}

	public JTextField getScreenTitle() {
		return screenTitle;
	}

	public JTextArea getScreenDescription() {
		return screenDescription;
	}

	public JToggleButton getRecordButton() {
		return recordButton;
	}

	public JButton getStopButton() {
		return stopButton;
	}

	public JButton getSaveButton() {
		return saveButton;
	}
}
