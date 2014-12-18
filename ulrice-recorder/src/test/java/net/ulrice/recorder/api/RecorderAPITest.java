package net.ulrice.recorder.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;
import net.ulrice.recorder.domain.RecordingInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RecorderAPITest {

	private Recording recording;


	@Before
	public void setUp() {
		RecordedScreen screen0 = new RecordedScreen();
		screen0.setTitle("Screen 0");
		screen0.setDescription("Description of Screen 0");
		screen0.setFullImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
		
		RecordedScreen screen1 = new RecordedScreen();
		screen1.setTitle("Screen 1");
		screen1.setDescription("Description of Screen 1");
		screen1.setFullImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

		RecordedScreen screen2 = new RecordedScreen();
		screen2.setTitle("Screen 2");
		screen2.setDescription("Description of Screen 2");
		screen2.setFullImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

		RecordedScreen screen3 = new RecordedScreen();
		screen3.setTitle("Screen 3");
		screen3.setDescription("Description of Screen 3");
		screen3.setFullImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
		

		recording = new Recording();
		recording.setTitle("Test Recording");
		recording.setCategory("JUNIT");
		recording.setDescription("Test Description");
		recording.setScreens(new ArrayList<RecordedScreen>());
		recording.getScreens().add(screen0);
		recording.getScreens().add(screen1);
		recording.getScreens().add(screen2);
		recording.getScreens().add(screen3);
	}
	
	
	@Test
	@Ignore
	public void testSaveAndLoad() throws IOException {
		File outputFile = RecorderAPI.saveRecording(new File(System.getProperty("java.io.tmpdir")), recording);
		Assert.assertNotNull(outputFile);
		
		Recording loadedRecording = RecorderAPI.loadRecording(outputFile);		
		Assert.assertEquals(loadedRecording, recording);	
	}
	
	@Test 
	public void testLoadAllRecordingInfos() throws IOException {
		File outputFile = RecorderAPI.saveRecording(new File(System.getProperty("java.io.tmpdir")), recording);
		Assert.assertNotNull(outputFile);

		RecordingInfo recordingInfo = RecorderAPI.loadRecordingInfo(outputFile);		
		Assert.assertEquals(recording.getTitle(), recordingInfo.getTitle());
		Assert.assertEquals(recording.getCategory(), recordingInfo.getCategory());
		Assert.assertEquals(recording.getDescription(), recordingInfo.getDescription());				
	}
	
	@Test
	public void testExportToHtml() throws IOException {
		File outputFile = RecorderAPI.saveRecording(new File(System.getProperty("java.io.tmpdir")), recording);
		Assert.assertNotNull(outputFile);

		RecorderAPI.exportRecordingsToHtml(null, new File(System.getProperty("java.io.tmpdir")), outputFile);
	}
}
