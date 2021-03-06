package net.ulrice.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.AbstractButton;
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

import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;

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

	private ScreenView screenshot;
	private JTextField screenTitle;
	private JTextArea screenDescription;

	private JToggleButton recordButton;
	private JButton stopButton;
	private JButton saveButton;
	private JButton loadButton;
	private JButton deleteButton;
	private JButton exportButton;
	private JButton nextButton;
	private JButton prevButton;

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

		screenshot = new ScreenView();
		screenshot.setOpaque(true);
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
		loadButton = new JButton(createIcon("load.png"));
		deleteButton = new JButton(createIcon("delete.png"));
		exportButton = new JButton(createIcon("export.png"));
		prevButton = new JButton(createIcon("prev.png"));
		nextButton = new JButton(createIcon("next.png"));
		
		modifyButton(stopButton, recordButton, saveButton, loadButton, deleteButton, exportButton, prevButton, nextButton);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(recordButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(prevButton);
		buttonPanel.add(nextButton);
		
		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(screenPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		resetFields();
		getTitleField().setEnabled(false);
		getCategoryField().setEnabled(false);
		getDescriptionArea().setEnabled(false);
	}
	
	public void modifyButton(AbstractButton...buttons) {
		for(AbstractButton button : buttons) {
			button.setRolloverEnabled(false);
			button.setContentAreaFilled(true);
			button.setBackground(Color.DARK_GRAY);
		}
	}

	public void resetFields() {
		getRecordButton().setSelected(false);
		getTitleField().setEnabled(false);
		getCategoryField().setEnabled(false);
		getDescriptionArea().setEnabled(false);
		getScreenTitle().setEnabled(false);
		getScreenDescription().setEnabled(false);
		stopButton.setEnabled(false);

		getTitleField().setText("");
		getCategoryField().setText("");
		getDescriptionArea().setText("");
		getScreenshot().setImage(null);
		getScreenTitle().setText("");
		getScreenDescription().setText("");
	}

	public void showScreen(RecordedScreen screen) {
		getScreenTitle().setEnabled(true);
		getScreenDescription().setEnabled(true);

		getScreenshot().setImage(screen.getFullImage());
		getScreenshot().setClipRect(new Rectangle(screen.getClipX(), screen.getClipY(), screen.getClipW(), screen.getClipH()));
		
		getScreenTitle().setText(screen.getTitle());
		getScreenDescription().setText(screen.getDescription());
		invalidate();
		repaint();
	}

	public void showRecording(Recording recording) {
		resetFields();		
		if(recording != null) {

			getPrevButton().setEnabled(false);
			getNextButton().setEnabled(!recording.getScreens().isEmpty());	
			
			getTitleField().setEnabled(true);
			getDescriptionArea().setEnabled(true);
			getCategoryField().setEnabled(true);
			
			titleField.setText(recording.getTitle());
			descriptionArea.setText(recording.getDescription());
			categoryField.setText(recording.getCategory());
			
			if(!recording.getScreens().isEmpty()) {
				showScreen(recording.getScreens().get(0));
			}
		}
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

	public ScreenView getScreenshot() {
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

	public JButton getLoadButton() {
		return loadButton;
	}

	public JButton getExportButton() {
		return exportButton;
	}

	public JButton getNextButton() {
		return nextButton;
	}

	public JButton getPrevButton() {
		return prevButton;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}

}
