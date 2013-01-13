package net.ulrice.recorder.gui;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import net.ulrice.Ulrice;
import net.ulrice.recorder.api.RecorderAPI;
import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;

public class Recorder {

	private Recording recording;
	private RecorderDialog dialog = new RecorderDialog();
	private AWTEventListener eventListener;
	
	public Recorder(final File outputDirectory, final Component component) {
		this(outputDirectory, component, KeyEvent.VK_F12);
	}

	public Recorder(final File outputDirectory, final Component component, final int keyCode) {

		dialog.getStopButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}

		});

		dialog.getRecordButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}

		});

		dialog.getSaveButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
					RecorderAPI.saveRecording(outputDirectory, recording);
				} catch (IOException e1) {
					Ulrice.getMessageHandler().handleException(e1);
				}
			}

		});
		
		eventListener = new AWTEventListener() {
			
			@Override
			public void eventDispatched(AWTEvent e) {
				KeyEvent keyEvent = (KeyEvent) e;

				if (keyEvent.getID() == KeyEvent.KEY_RELEASED && keyCode == keyEvent.getKeyCode()) {
					try {
						updateScreenTexts();

						RecordedScreen screen = RecorderAPI.recordScreen(component, 320, 200);
						recording.getScreens().add(screen);
						dialog.getScreenTitle().setEnabled(true);
						dialog.getScreenDescription().setEnabled(true);

						dialog.getScreenshot().setIcon(new ImageIcon(screen.getSmallImage()));
						dialog.getScreenTitle().setText("");
						dialog.getScreenDescription().setText("");
						dialog.getContentPane().invalidate();
						dialog.getContentPane().repaint();

					} catch (AWTException e1) {
						Ulrice.getMessageHandler().handleException(e1);
					} catch (IOException e1) {
						Ulrice.getMessageHandler().handleException(e1);
					}
				}	
			}
		};

		dialog.pack();
		dialog.setVisible(true);
	}

	public void start() {
		recording = new Recording();
		recording.setScreens(new LinkedList<RecordedScreen>());

		dialog.getRecordButton().setSelected(true);

		dialog.getRecordButton().setSelected(false);
		dialog.getScreenTitle().setEnabled(false);
		dialog.getScreenDescription().setEnabled(false);
		
		dialog.getTitleField().setText("");
		dialog.getCategoryField().setText("");
		dialog.getDescriptionArea().setText("");
		dialog.getScreenshot().setIcon(null);
		dialog.getScreenTitle().setText("");
		dialog.getScreenDescription().setText("");
		
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		Toolkit.getDefaultToolkit().addAWTEventListener(eventListener, AWTEvent.KEY_EVENT_MASK);
	}

	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		updateScreenTexts();
		
		recording.setTitle(dialog.getTitleField().getText());
		recording.setCategory(dialog.getCategoryField().getText());
		recording.setDescription(dialog.getDescriptionArea().getText());		
	}

	private void updateScreenTexts() {
		if (recording.getScreens().size() > 0) {
			RecordedScreen lastScreen = recording.getScreens().get(recording.getScreens().size() - 1);
			lastScreen.setTitle(dialog.getScreenTitle().getText());
			lastScreen.setDescription(dialog.getScreenDescription().getText());
		}
	}

	public Recording getRecording() {
		return recording;
	}
}
