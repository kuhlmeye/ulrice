package net.ulrice.recorder.api;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;
import net.ulrice.recorder.domain.RecordingInfo;

import org.xml.sax.SAXException;

public class RecorderAPI {

	public static RecordedScreen recordScreen(Component component, int smallImageWidth, int smallImageHeight) throws AWTException, IOException {
		BufferedImage screenCapture = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
		component.paint(screenCapture.getGraphics());

		Image cursor = ImageIO.read(RecorderAPI.class.getResourceAsStream("pointer.png"));
		Point mousePointer = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePointer, component);

		Graphics2D graphics2D = screenCapture.createGraphics();
		graphics2D.drawImage(cursor, mousePointer.x, mousePointer.y, 25, 39, null);

		RecordedScreen screen = new RecordedScreen();
		screen.setFullImage(screenCapture);

		return screen;
	}
	
	public static File saveRecording(File outputDirectory, Recording recording) throws IOException {

		if (!outputDirectory.isDirectory()) {
			throw new IOException("Working Directory (" + outputDirectory + ") is not a directory.");
		}
		
		if(!outputDirectory.canRead() || !outputDirectory.canWrite()) {
			throw new IOException("No read/write access in working directory (" + outputDirectory + ").");
		}

		if (recording.getScreens().isEmpty()) {
			return null;
		}
		
		List<File> files = new ArrayList<File>();
		File xmlFile = new File(outputDirectory, "recording.xml");
		files.add(xmlFile);

		XmlIO.storeRecordingAsXML(recording, files, xmlFile);
		File outputFile = new File(outputDirectory, recording.getTitle().replace(" ", "_") + ".rec.zip");
		ZipUtils.compressOutput(outputFile, files);
		
		return outputFile;
	}

	public static Recording loadRecording(File recordingsFile) throws IOException {
		return loadRecordingInternally(recordingsFile, true);		
	}
	
	public static List<RecordingInfo> loadRecordingInfos(File directory) throws IOException {
		if (!directory.isDirectory()) {
			throw new IOException("Given Directory (" + directory + ") is not a directory.");
		}
		
		if(!directory.canRead()) {
			throw new IOException("No read access in  directory (" + directory + ").");
		}
		
		File[] recordingFiles = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".rec.zip");
			}
		});
		
		List<RecordingInfo> result = new ArrayList<RecordingInfo>(recordingFiles.length);
		for(File recordingFile : recordingFiles) {
			result.add(loadRecordingInfo(recordingFile));
		}
		return result;
	}

	public static RecordingInfo loadRecordingInfo(File recordingFile) throws IOException {
		Recording recording = loadRecordingInternally(recordingFile, false);
		
		RecordingInfo result = new RecordingInfo();
		result.setCategory(recording.getCategory());
		result.setTitle(recording.getTitle());
		result.setDescription(recording.getDescription());
		result.setFile(recordingFile);
		
		return result;
	}

	private static Recording loadRecordingInternally(File recordingsFile, boolean loadScreens) throws FileNotFoundException, IOException {
		if(!recordingsFile.exists()) {
			throw new FileNotFoundException(recordingsFile.getPath());
		}
				
		
		File tmpOutputDirectory = File.createTempFile(recordingsFile.getName() + "-dir", "");
		tmpOutputDirectory.delete();
		if (!tmpOutputDirectory.mkdirs()) {
			throw new IOException("Could not create temporary directory " + tmpOutputDirectory.getPath());
		}
		tmpOutputDirectory.deleteOnExit();

		if(loadScreens) {
			ZipUtils.decompressFile(recordingsFile, tmpOutputDirectory, "xml", "jpg");
		} else {
			ZipUtils.decompressFile(recordingsFile, tmpOutputDirectory, "xml");
		}

		File xmlFile = new File(tmpOutputDirectory, "recording.xml");
		if(!xmlFile.exists()) {
			throw new IOException("File recording.xml not found. Not a valid recording.");
		}
		
		try {
			return XmlIO.parseRecording(xmlFile, loadScreens);
		} catch (ParserConfigurationException e) {
			throw new IOException("Could not parse recording.xml", e);
		} catch (SAXException e) {
			throw new IOException("Could not parse recording.xml", e);
		}
	}
	
	public static void exportRecordingsToHtml(InputStream cssFile, File outputDirectory, File... recordingFiles) throws IOException {				
		
		// Load recordings
		List<Recording> recordings = new ArrayList<Recording>(recordingFiles.length);
		for(File recordingFile : recordingFiles) {
			Recording recording = loadRecording(recordingFile);
			recordings.add(recording);
		}

		// Sort recordings
		Collections.sort(recordings);
		
		// Export css file
		BufferedReader cssFileReader = null;
		if(cssFile != null) {
			cssFileReader = new BufferedReader(new InputStreamReader(cssFile));	
		} else {
			cssFileReader = new BufferedReader(new InputStreamReader(RecorderAPI.class.getResourceAsStream("style.css")));
		}
		
		PrintWriter pw = new PrintWriter(new File(outputDirectory, "style.css"));
		String line = null;
		while((line = cssFileReader.readLine()) != null) {
			pw.println(line);
		}
		pw.close();

		// Export index
		pw = new PrintWriter(new File(outputDirectory, "index.html"), "UTF-8");
		pw.println("<html>");
		pw.println("\t<head>");
		pw.println("\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
		pw.println("\t</head>");
		pw.println("\t<body>");
		pw.println("\t\t<table id=\"index\">");
		for(int i = 0; i < recordings.size(); i++) {
			Recording recording = recordings.get(i);
			
			pw.println("\t\t\t<tr class=\"indexTable\">");			
			pw.println("\t\t\t\t<td class=\"categoryCell\">" + fixHTML(recording.getCategory()) + "</td>");			
			pw.println("\t\t\t\t<td class=\"titleCell\"><a href=\"recording_" + i + "_0.html\">" + fixHTML(recording.getTitle()) + "</a></td>");			
			pw.println("\t\t\t</tr>");
		}
		pw.println("\t\t</table>");
		pw.println("\t</body>");
		pw.println("</html>");
		pw.close();
		
		// Export recordings
		for(int i = 0; i < recordings.size(); i++) {
			Recording recording = recordings.get(i);
			
			for(int j = 0; j < recording.getScreens().size(); j++) {
				RecordedScreen screen = recording.getScreens().get(j);
			
				pw = new PrintWriter(new File(outputDirectory, "recording_" + i + "_" + j + ".html"), "UTF-8");
	
				pw.println("<html>");
				pw.println("\t<head>");
				pw.println("\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
				pw.println("\t</head>");
				pw.println("\t<body>");
				pw.println("\t\t<div id=\"recordingInformation\">");
				pw.println("\t\t\t<div id=\"category\">" + fixHTML(recording.getCategory()) + "</div>");
				pw.println("\t\t\t<div id=\"title\">" + fixHTML(recording.getTitle()) + "</div>");
				pw.println("\t\t\t<div id=\"description\">" + fixHTML(recording.getDescription()) + "</div>");
				pw.println("\t\t</div>");
				pw.println("\t\t<div id=\"screen\">");
				pw.println("\t\t\t<div id=\"screenTitle\">" + fixHTML(screen.getTitle()) + "</div>");
				pw.println("\t\t\t<div id=\"screenImage\"><img src=\"screen_" + i + "_" + j + ".jpg\"></div>");
				if(j-1 < 0) {
					pw.println("\t\t\t<div id=\"leftLink\"><a href=\"index.html\">&lt;</a></div>");
				} else {
					pw.println("\t\t\t<div id=\"leftLink\"><a href=\"recording_" + i + "_" + (j-1) + ".html\">&lt;</a></div>");
				}
				pw.println("\t\t\t<div id=\"screenDescription\">" + fixHTML(screen.getDescription()) + "</div>");
				if(j+1 >= recording.getScreens().size()) {
					pw.println("\t\t\t<div id=\"rightLink\"><a href=\"index.html\">&gt;</a></div>");
				} else {
					pw.println("\t\t\t<div id=\"rightLink\"><a href=\"recording_" + i + "_" + (j+1) + ".html\">&gt;</a></div>");
				}
				pw.println("\t\t</div>");
				pw.println("\t\t<div id=\"pictures\">");
				pw.println("\t\t\t<ul id=\"smallPictureList\">");
				for(int k = 0; k < recording.getScreens().size(); k++) {
					if(k == j) {
						pw.println("\t\t\t\t<li id=\"current\" class=\"smallPicture\"><img src=\"screen_" + i + "_" + k + ".jpg\"></li>");
					} else {
						pw.println("\t\t\t\t<li class=\"smallPicture\"><a href=\"recording_" + i + "_" + k + ".html\"><img src=\"screen_" + i + "_" + k + ".jpg\"></a></li>");
					}
				}
				pw.println("\t\t\t</li>");
				pw.println("\t\t</div>");
				pw.println("\t</body>");
				pw.println("</html>");
				
				pw.close();

				BufferedImage clippedImage = ImageUtils.clipImage(screen.getFullImage(), screen.getClipX(), screen.getClipY(), screen.getClipW(), screen.getClipH());
				ImageIO.write(clippedImage, "JPG", new File(outputDirectory, "screen_" + i + "_" + j + ".jpg"));
			}			
		}
	}

	private static String fixHTML(String text) {
		text = text.replace("ü", "&uuml;");
		text = text.replace("Ü", "&Uuml;");

		text = text.replace("ä", "&auml;");
		text = text.replace("Ä", "&Auml;");

		text = text.replace("ö", "&ouml;");
		text = text.replace("Ö", "&Ouml;");
		
		text = text.replace("\n", "<br>");

		return text;
	}
}
