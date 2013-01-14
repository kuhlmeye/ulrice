package net.ulrice.recorder;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ulrice.recorder.api.RecorderAPI;
import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;
import net.ulrice.recorder.domain.RecordingInfo;
import net.ulrice.recorder.gui.RecorderView;
import net.ulrice.recorder.gui.RecordingsSelectionDialog;

public class Recorder {

	private Recording recording;
	private RecorderView view = new RecorderView();
	private AWTEventListener eventListener;
	private RecordedScreen currentScreen;
	
	public Recorder(final File outputDirectory, final Window component) {
		this(outputDirectory, component, KeyEvent.VK_F12, new ExceptionHandler() {
			
			@Override
			public void handleException(Throwable th) {
				th.printStackTrace();
			}
		});
	}
	
	public Recorder(final File outputDirectory, final Window component, final int keyCode, final ExceptionHandler exceptionHandler) {

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
				save(outputDirectory, exceptionHandler);
			}

		});

		view.getExportButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<RecordingInfo> recordingInfos = RecorderAPI.loadRecordingInfos(outputDirectory);
					RecordingsSelectionDialog dialog = new RecordingsSelectionDialog(component, recordingInfos, true);
					dialog.pack();
					dialog.setVisible(true);
					
					List<RecordingInfo> recordings = dialog.getSelectedRecordings();
					List<File> recordingFiles = new ArrayList<File>();
					for(RecordingInfo recordingInfo : recordings) {
						recordingFiles.add(recordingInfo.getFile());
					}					
					RecorderAPI.exportRecordingsToHtml(null, outputDirectory, recordingFiles.toArray(new File[recordingFiles.size()]));
					
				} catch(Throwable th) {
					exceptionHandler.handleException(th);
				}				
			}

		});

		view.getLoadButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<RecordingInfo> recordingInfos = RecorderAPI.loadRecordingInfos(outputDirectory);
					RecordingsSelectionDialog dialog = new RecordingsSelectionDialog(component, recordingInfos, false);
					dialog.pack();
					dialog.setVisible(true);
					
					List<RecordingInfo> recordings = dialog.getSelectedRecordings();
					if(recordings.size() > 0) {
						Recording loadedRecording = RecorderAPI.loadRecording(recordings.get(0).getFile());
						recording = loadedRecording;
						if(recording.getScreens().isEmpty()) {
							currentScreen = null;
						} else {
							currentScreen = recording.getScreens().get(0);
						}
						view.showRecording(recording);
					}

				} catch(Throwable th) {
					exceptionHandler.handleException(th);
				}	
			}

		});

		view.getPrevButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = recording.getScreens().indexOf(currentScreen);
				if(idx > 0) {
					updateScreenTexts();
					currentScreen = recording.getScreens().get(idx-1);
					view.showScreen(currentScreen);
				}
			}

		});

		view.getNextButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = recording.getScreens().indexOf(currentScreen);
				if(idx < recording.getScreens().size() - 1) {
					updateScreenTexts();
					currentScreen = recording.getScreens().get(idx+1);
					view.showScreen(currentScreen);
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
						currentScreen = screen;
						recording.getScreens().add(screen);
						view.showScreen(screen);
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
		
		view.getRecordButton().setSelected(true);
		
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		Toolkit.getDefaultToolkit().addAWTEventListener(eventListener, AWTEvent.KEY_EVENT_MASK);
	}

	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(eventListener);
		updateScreenTexts();			
		view.getRecordButton().setSelected(false);
		
		recording.setTitle(view.getTitleField().getText());
		recording.setCategory(view.getCategoryField().getText());
		recording.setDescription(view.getDescriptionArea().getText());		
	}

	public void save(final File outputDirectory, final ExceptionHandler exceptionHandler) {
		try {
			stop();
			RecorderAPI.saveRecording(outputDirectory, recording);
			view.resetFields();
		} catch (Throwable th) {
			exceptionHandler.handleException(th);
		}
	}

	private void updateScreenTexts() {
		if (currentScreen != null) {
			currentScreen.setTitle(view.getScreenTitle().getText());
			currentScreen.setDescription(view.getScreenDescription().getText());
			currentScreen.setClipX(view.getScreenshot().getClipRect().x);
			currentScreen.setClipY(view.getScreenshot().getClipRect().y);
			currentScreen.setClipW(view.getScreenshot().getClipRect().width);
			currentScreen.setClipH(view.getScreenshot().getClipRect().height);
		}
	}

	public RecorderView getView() {
		return view;
	}
	
	public Recording getRecording() {
		return recording;
	}
}
