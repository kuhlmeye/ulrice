package net.ulrice.recorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;

import net.ulrice.recorder.api.RecorderAPI;
import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;
import net.ulrice.recorder.gui.RecorderView;

public class Recorder {

	private Recording recording;
	private RecorderView view = new RecorderView();
	private AWTEventListener eventListener;
	
	public Recorder(final File outputDirectory, final Component component) {
		this(outputDirectory, component, KeyEvent.VK_F12, new ExceptionHandler() {
			
			@Override
			public void handleException(Throwable th) {
				th.printStackTrace();
			}
		});
	}
	
	public Recorder(final File outputDirectory, final Component component, final int keyCode, final ExceptionHandler exceptionHandler) {

		view.getStopButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}

		});

		view.getRecordButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}

		});

		view.getSaveButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
					RecorderAPI.saveRecording(outputDirectory, recording);
				} catch (Throwable th) {
					exceptionHandler.handleException(th);
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
						view.showNewScreen(screen.getSmallImage());
					} catch (Throwable th) {
						exceptionHandler.handleException(th);
					}
				}	
			}
		};
	}

	public void start() {
		recording = new Recording();
		recording.setScreens(new LinkedList<RecordedScreen>());
		
		view.reinit();
		
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		Toolkit.getDefaultToolkit().addAWTEventListener(eventListener, AWTEvent.KEY_EVENT_MASK);
	}

	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		updateScreenTexts();
		
		recording.setTitle(view.getTitleField().getText());
		recording.setCategory(view.getCategoryField().getText());
		recording.setDescription(view.getDescriptionArea().getText());		
	}

	private void updateScreenTexts() {
		if (recording.getScreens().size() > 0) {
			RecordedScreen lastScreen = recording.getScreens().get(recording.getScreens().size() - 1);
			lastScreen.setTitle(view.getScreenTitle().getText());
			lastScreen.setDescription(view.getScreenDescription().getText());
		}
	}

	public RecorderView getView() {
		return view;
	}
	
	public Recording getRecording() {
		return recording;
	}
}
